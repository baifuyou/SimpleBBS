package com.sbbs.dao

import com.sbbs.commons.SbbsConfig
import com.sbbs.model._
import net.liftweb.common.Logger
import org.mongodb.morphia._
import java.util.Date

import org.mongodb.morphia.query.Query

/**
 * Created by baifuyou on 14-9-16.
 */
object UserDao {

  private val datastore = DataSource.getDatastore()
  private val logger = Logger(this.getClass)

  def getRepliesCount(postId: String) = {
    datastore.createQuery(classOf[ReplyItem])
      .filter("postId =", postId)
      .countAll()
      .toInt
  }

  def getReplies(postId: String, index: Int): List[ReplyItem] = {
    val query = datastore.createQuery(classOf[ReplyItem])
      .filter("postId =", postId)
      .offset(SbbsConfig.replyPageSize * (index - 1))
      .limit(SbbsConfig.replyPageSize)
    query.asList().toArray().toList.asInstanceOf[List[ReplyItem]]
  }

  def getPostByPostId(pid: String) = {
    datastore.find(classOf[PostItem], "postId =", pid).get()
  }

  def getAutoLoginUser(uid: String, autoLoginConfirmCode: String): Option[User] = {
    val query = datastore.createQuery(classOf[User])
      .filter("uid =", uid)
      .filter("autoLoginConfirmCode =", autoLoginConfirmCode)
      .filter("autoLoginEndTime >", new Date(System.currentTimeMillis()))
    Option[User](query.get())
  }

  def getPosts(index: Int): List[PostItem] = {
    val query: Query[PostItem] = datastore.createQuery(classOf[PostItem])
      .offset(SbbsConfig.pageSize * (index - 1))
      .limit(SbbsConfig.pageSize)
    query.asList().toArray().toList.asInstanceOf[List[PostItem]]
  }

  def getPostCount() = {
    datastore.getCount(classOf[PostItem])
  }

  def getWaitConfirmedUser(emailRecognitionCode: String): Option[WaitConfirmedUser] = {
    val query = datastore.createQuery(classOf[WaitConfirmedUser])
      .filter("emailRecognitionCode =", emailRecognitionCode)
      .filter("deadline >", new Date(System.currentTimeMillis()))
    Option[WaitConfirmedUser](query.get())
  }

  def saveObject[T](obj: T) = {
    datastore.save[T](obj)
  }

  def deleteObject[T](obj: T) = {
    datastore.delete(obj)
  }

  def getUserByUid(uid: String): Option[User] = {
    Option[User](datastore.find(classOf[User], "uid =", uid).get().asInstanceOf[User])
  }

  def isUidConfirmed(uid: String): Boolean = {
    datastore.getCount(datastore.find(classOf[User], "uid =", uid)) != 0
  }

  def isUidWaitConfirmed(uid: String): Boolean = {
    datastore.getCount(datastore.find(classOf[WaitConfirmedUser], "uid =", uid)) != 0
  }
}
