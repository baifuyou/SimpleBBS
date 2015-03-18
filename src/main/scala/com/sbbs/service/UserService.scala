package com.sbbs.service

import java.io.FileInputStream
import java.util.Date
import java.util
import javax.mail._
import javax.mail.internet.{InternetAddress, MimeMessage}
import com.sbbs.model._

import com.sbbs.commons._
import com.sbbs.dao.UserDao
import com.sbbs.model.AuthResult._
import com.sbbs.model.WaitConfirmedUser

import net.liftweb.common.Logger
import net.liftweb.http.S
import net.liftweb.http.provider.HTTPCookie

import scala.xml.NodeSeq

/**
 * Created by baifuyou on 14-9-16.
 */
object UserService {

  private val logger = Logger(this.getClass)
  private val confirmMills = 24 * 60 * 60 * 1000  // 1天
  private val autoLoginSeconds: Long = 30 * 24 * 60 * 60 // 30天
  private val confirmCodeLength = 6

  def resetPassword(uid: String, password: String): Boolean = {
    val user = UserDao.getUserByUid(uid)
    if(user.isEmpty) {
      false
    } else {
      val salt = CommonUtils.generateSalt()
      val encryptedPassword = CommonUtils.encrypt(password + salt)
      val userObj = user.get
      userObj.setSalt(salt)
      userObj.setEncryptedPwd(encryptedPassword)
      UserDao.saveObject(userObj)
      true
    }
  }

  def getReplyPageCount(postId: String) = {
    UserDao.getRepliesCount(postId: String)
    if ( (UserDao.getRepliesCount(postId: String) % SbbsConfig.replyPageSize) == 0)
      (UserDao.getRepliesCount(postId: String) / SbbsConfig.replyPageSize)
    else
      (UserDao.getRepliesCount(postId: String) / SbbsConfig.replyPageSize) + 1
  }

  def getPostByPostId(pid: String) = {
    UserDao.getPostByPostId(pid)
  }

  def setNickname(nickname: String) = {
    val uid = S.getSessionAttribute("uid").openOr("")
    val user = UserDao.getUserByUid(uid).get
    user.setNickname(nickname)
    UserDao.saveObject(user)
  }

  def getReplys(postId: String, pageIndex: Int): List[ReplyItem] = {
    UserDao.getReplies(postId, pageIndex);
  }

  def addReply(postId: String, replyContent: String) = {
    val uid = S.getSessionAttribute("uid").openOr("")
    val user = UserDao.getUserByUid(uid).get
    val reply = new ReplyItem()
    reply.setAddDate(new Date(System.currentTimeMillis()))
    reply.setContent(replyContent)
    reply.setPostId(postId)
    reply.setUid(uid)
    reply.setNickname(user.getNickname)
    UserDao.saveObject(reply)
  }

  def addPost() = {
    val post = new PostItem();
    post.setTitle(S.param("post-title").openOr("无标题"))
    post.setContent(S.param("post-content").openOr("无内容"))
    val uid = S.getSessionAttribute("uid").openOr("")
    val user = UserDao.getUserByUid(uid).get
    post.setNickname(user.getNickname)
    post.setAddDate(new Date(System.currentTimeMillis()))
    post.setPostId(uid + System.currentTimeMillis())
    UserDao.saveObject(post)
  }


  def getPostPageCount() = {
    if ( (UserDao.getPostCount() % SbbsConfig.pageSize).asInstanceOf[Int] == 0)
      (UserDao.getPostCount() / SbbsConfig.pageSize).asInstanceOf[Int]
    else
      (UserDao.getPostCount() / SbbsConfig.pageSize).asInstanceOf[Int] + 1

  }

  def getUserByUid(uid: String) = {
    UserDao.getUserByUid(uid).get
  }

  def getPosts(pageIndex: Int): List[PostItem] = {
    UserDao.getPosts(pageIndex)
  }

  def forgiveMe(uid: String) = {
    println("userService Uid: " + uid)
    val user: User = UserDao.getUserByUid(uid).get
    user.setAutoLoginConfirmCode("")
    user.setAutoLoginEndTime(new Date(0))
    UserDao.saveObject(user)
    S.deleteCookie("rememberMe")
  }

  def rememberMe(uid: String) = {
    val autoLoginConfirmCode = CommonUtils.generateRandomCode(20)
    val user: User = UserDao.getUserByUid(uid).get
    user.setAutoLoginConfirmCode(autoLoginConfirmCode)
    user.setAutoLoginEndTime(new Date(System.currentTimeMillis() + autoLoginSeconds * 1000L))
    UserDao.saveObject(user)
    S.addCookie(HTTPCookie("rememberMe", uid + ":" + autoLoginConfirmCode).setMaxAge(autoLoginSeconds.asInstanceOf[Int]))
  }

  def autoLogin(): Boolean = {
    val rememberMe = S.cookieValue("rememberMe").openOr(null)
    if(rememberMe == null) {
      false
    } else {
      val inUid = rememberMe.split(":")(0)
      val autoLoginConfirmCode = rememberMe.split(":")(1)
      val isSuccess = !UserDao.getAutoLoginUser(inUid, autoLoginConfirmCode).isEmpty
      if(isSuccess) {
        S.setSessionAttribute("uid", inUid)
      }
      isSuccess
    }
  }

  def changeWaitConfirmUserToUser(waitConfirmedUser: WaitConfirmedUser) = {
    val user = new User()
    user.setEncryptedPwd(waitConfirmedUser.getEncryptedPwd)
    user.setNickname(waitConfirmedUser.getNickname)
    user.setSalt(waitConfirmedUser.getSalt)
    user.setUid(waitConfirmedUser.getUid)
    UserDao.saveObject(user)
    UserDao.deleteObject(waitConfirmedUser)
  }

  def confirmRegistedUser(emailRecognitionCode: String, confirmCode: String): Boolean = {
    val optionUser = UserDao.getWaitConfirmedUser(emailRecognitionCode)
    optionUser match {
      case Some(user) => {
        val rightConfirmCode = user.getConfirmCode
        if (rightConfirmCode == null || rightConfirmCode != confirmCode) {
          logger.info("随机码比对失败")
          false
        } else {
          logger.info("验证通过")
          changeWaitConfirmUserToUser(user)
          true
        }
      }
      case _ => false
    }
  }

  def sendConfirmEmail(uid: String, emailRecognitionCode: String, confirmCode: String, emailAddress: String, confirmPath: String) = {
    val topic = "SBBS注册-邮箱验证"
    val finalPath = confirmPath +"?recoCode=" + emailRecognitionCode + "&confirmCode=" + confirmCode
    val content: NodeSeq = <p>尊敬的用户，您好！您收到本邮件是因为您在SBBS注册了会员。请点击以下链接激活您的账号，您也可以复制该链接在浏览器中打开<br/>
      <a href="finalPath">{finalPath}</a><br/>
      如果非本人操作，请忽略此邮件。</p>
    val serverConfigStream = new FileInputStream("src/main/resources/props/mailServerConfig.properties")
    val serverConfig = new util.Properties()
      serverConfig.load(serverConfigStream)
      val auth = new Authenticator {
        override def getPasswordAuthentication(): PasswordAuthentication = {
          new PasswordAuthentication(EmailConfig.username, EmailConfig.password)
        }
      }
      val session = Session.getInstance(serverConfig, auth)
      val message = new MimeMessage(session)
      message.setSubject(topic)
      message.setText(content.toString())
      println(content.toString())
      message.setContent(content.toString(), "text/html;charset = UTF-8")
      val mailAccount = EmailConfig.username
      val mailPassword = EmailConfig.password
      message.setFrom(new InternetAddress(mailAccount))
      message.setRecipient(Message.RecipientType.TO, new InternetAddress(
        emailAddress))
      Transport.send(message)
  }

  def register(uid: String, nickname: String, password: String) = {
    val salt = CommonUtils.generateSalt()
    val emailRecognitionCode = uid.replaceAll("[@.]", "")
    val confirmCode = CommonUtils.generateRandomCode(confirmCodeLength)
    val encryptedPwd = CommonUtils.encrypt(password + salt)
    val user = new WaitConfirmedUser()

    user.setUid(uid)
    user.setEncryptedPwd(encryptedPwd)
    user.setSalt(salt)
    user.setNickname(nickname)
    user.setEmailRecognitionCode(emailRecognitionCode)
    user.setConfirmCode(confirmCode)
    user.setDeadline(new Date(System.currentTimeMillis() + confirmMills))

    UserDao.saveObject(user)

    val domain = WebAppConfig.domain
    val confirmPath = domain + "confirmRegisterEmail"
    sendConfirmEmail(uid, emailRecognitionCode, confirmCode, uid, confirmPath);
  }

  def authUser(uid: String, password: String): AuthResult = {
    var authResult = SUCCESS
    val option = UserDao.getUserByUid(uid)
    option match {
      case Some(user) => {
        val salt = user.getSalt
        val encryptedPwd = CommonUtils.encrypt(password + salt)
        if (user.getEncryptedPwd == encryptedPwd)
          authResult = SUCCESS
        else
          authResult = PASSWORD_WRONG
      }
      case _ => authResult = UID_NO_EXIST
    }
    authResult
  }

  def isRegistered(uid: String): Boolean = {
    UserDao.isUidConfirmed(uid) || UserDao.isUidWaitConfirmed(uid)
  }
}
