package com.sbbs.snippet

import com.sbbs.model.PostItem
import com.sbbs.service.UserService
import net.liftweb.http._
import net.liftweb.util
import util.Helpers._

import scala.xml.NodeSeq

/**
 * Created by baifuyou on 14-9-19.
 */
class MainPage extends StatefulSnippet {

  override def dispatch: DispatchIt = {
    case "render" => render
  }

  def render = {
    UserService.autoLogin()
    var pageIndex: Int = S.param("px").openOr("1").toInt
    val pageCount: Int = UserService.getPostPageCount()
    if (pageCount != 0) {
      if (pageIndex <= 0) {
        S.redirectTo("index?px=1")
      } else if (pageIndex > pageCount) {
        S.redirectTo("index?px=" + pageCount)
      }
    } else {
      pageIndex = 1
    }
    val uidBox = S.getSessionAttribute("uid")
    val uid = uidBox.openOr(null)
    ".welcome *+" #> generateWelcomeHtml(uid) &
      ".post-list *+" #> generatePostListHtml(UserService.getPosts(pageIndex)) &
      ".pagination *+" #> generatePageIndexHtml(pageIndex) &
      ".new-post-btn" #> SHtml.onSubmitUnit(addPost)
  }

  def addPost() = {
    if (S.getSessionAttribute("uid").isEmpty) {
      S.error("not-login", "您还没有登录")
    } else {
      UserService.addPost()
    }
  }

  def generatePostListHtml(items: List[PostItem]): NodeSeq = {
    val itemTags = items.map((item) => {
      <div class="list-group-item">
        <div class="post-title">
          <a href={"post?pid=" + item.getPostId}>
            {item.getTitle}
          </a>
        </div>
        <div class="post-info">
          <span>
            <a href={"user/" + item.getUid}>
              {item.getNickname}
            </a>
          </span> <span class="addDate">
          {item.getAddDate}
        </span>
        </div>
      </div>
    })
    var itemsTag: NodeSeq = <div></div>
    itemTags.foreach((tag) => {
      itemsTag = itemsTag ++ tag
    })
    itemsTag
  }

  def generateWelcomeHtml(uid: String): NodeSeq = {
    if (uid == null || uid == "") {
      <a href="login">登录</a>
    } else {
      val user = UserService.getUserByUid(uid)
      //println("mainpage uid: " + uid)
      <p>欢迎您，
        <a href="userController">
          {user.getNickname}
        </a>
        </p>
    }
  }

  def generatePageIndexHtml(nowPageIndex: Int): NodeSeq = {
    val pageCount = UserService.getPostPageCount()
    val pageIndexsShowed: List[Int] = generateIndexSeq(nowPageIndex, pageCount)
    var pageIndexHtml: NodeSeq = <li>
      <a href="index?px=1">首页</a>
    </li>
    val priorIndex = if (nowPageIndex - 1 <= 0) 1 else nowPageIndex - 1
    val nextIndex = if (nowPageIndex + 1 > pageCount) pageCount else nowPageIndex + 1
    pageIndexHtml = pageIndexHtml ++ <li>
      <a href={"index?px=" + priorIndex}>
        &laquo;
      </a>
    </li>;
    pageIndexsShowed.foreach((index) => {
      val classStr = if (index == nowPageIndex) "active" else ""
      pageIndexHtml = pageIndexHtml ++ <li class={classStr}>
        <a href={"index?px=" + index}>
          {index}
        </a>
      </li>
    })
    pageIndexHtml = pageIndexHtml ++ <li>
      <a href={"index?px=" + nextIndex}>
        &raquo;
      </a>
    </li>
    pageIndexHtml = pageIndexHtml ++ <li>
      <a href={"index?px=" + pageCount}>尾页</a>
    </li>
    pageIndexHtml
  }

  def generateIndexSeq(nowPageIndex: Int, pageCount: Int): List[Int] = {
    if (nowPageIndex > 5) {
      var endIndex = nowPageIndex + 5
      endIndex = if (endIndex > pageCount) pageCount else endIndex
      ((nowPageIndex - 5) to (nowPageIndex + 5)).toList
    } else {
      val start: Int = 1
      val end: Int = if (pageCount <= 11) pageCount else 11
      (start to end).toList
    }
  }

  def process() = {
    val uidBox = S.getSessionAttribute("uid")
    val uid = uidBox.openOr(null)
    val user = UserService.getUserByUid(uid)
    var welcomeText = ""
    if (uid == null) {
      welcomeText = <a href="login">
        登录
      </a>.toString()
    } else {
      welcomeText = "欢迎您，" + user.getNickname
    }
  }
}

