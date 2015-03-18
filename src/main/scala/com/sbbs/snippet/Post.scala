package com.sbbs.snippet

import com.sbbs.model.{ReplyItem, PostItem}
import com.sbbs.service.UserService
import net.liftweb.http._
import net.liftweb.util
import util.Helpers._

import scala.xml.NodeSeq

/**
 * Created by baifuyou on 14-9-22.
 */
class Post {

  def render = {
    UserService.autoLogin()
    var pageIndex = S.param("px").openOr("1").toInt
    if (S.param("pid").isEmpty) {
      S.redirectTo("index")
    }
    val postId = S.param("pid").openOr("")
    val pageCount: Int = UserService.getReplyPageCount(postId)
    if (pageCount != 0) {
      if (pageIndex <= 0) {
        S.redirectTo("post?pid=" + postId + "&px=1")
      } else if (pageIndex > pageCount) {
        S.redirectTo("post?pid=" + postId + "&px=" + pageCount)
      }
    } else {
      pageIndex = 1
    }
    val post = UserService.getPostByPostId(postId)
    val replyList = UserService.getReplys(postId, pageIndex)
    ".post-body" #> generatePostBodyHtml(post) &
      ".reply-list *+" #> generateReplyList(replyList) &
      "title *+" #> post.getTitle &
      ".pagination *+" #> generatePageIndexHtml(pageIndex, postId) &
      ".new-reply-btn" #> SHtml.onSubmitUnit(addReply)
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

  def generatePageIndexHtml(nowPageIndex: Int, postId: String): NodeSeq = {
    val pageCount = UserService.getReplyPageCount(postId)
    val pageIndexsShowed: List[Int] = generateIndexSeq(nowPageIndex, pageCount)
    var pageIndexHtml: NodeSeq = <li>
      <a href={"post?pid=" + postId + "&px=1"}>首页</a>
    </li>
    val priorIndex = if (nowPageIndex - 1 <= 0) 1 else nowPageIndex - 1
    val nextIndex = if (nowPageIndex + 1 > pageCount) pageCount else nowPageIndex + 1
    pageIndexHtml = pageIndexHtml ++ <li>
      <a href={"post?pid=" + postId + "&px=" + priorIndex}>
        &laquo;
      </a>
    </li>;
    pageIndexsShowed.foreach((index) => {
      val classStr = if (index == nowPageIndex) "active" else ""
      pageIndexHtml = pageIndexHtml ++ <li class={classStr}>
        <a href={"post?pid=" + postId + "&px=" + index}>
          {index}
        </a>
      </li>
    })
    pageIndexHtml = pageIndexHtml ++ <li>
      <a href={"post?pid=" + postId + "&px=" + nextIndex}>
        &raquo;
      </a>
    </li>
    pageIndexHtml = pageIndexHtml ++ <li>
      <a href={"post?pid=" + postId + "&px=" + pageCount}>尾页</a>
    </li>
    pageIndexHtml
  }

  def addReply() = {
    if (S.getSessionAttribute("uid").isEmpty) {
      println("Post addReply error")
      S.error("not-login", "您还没有登录")
    } else {
      val postId = S.param("pid").openOr("")
      val replyContent = S.param("reply-content").openOr("")
      UserService.addReply(postId, replyContent)
    }
  }

  def generateReplyList(items: List[ReplyItem]): NodeSeq = {
    val itemTags = items.map((item) => {
      <div class="list-group-item">
        <div>
          {item.getContent}
        </div>
        <div>
          <span class="reply-nickname">
            {item.getNickname}
          </span> <span class="replay-date">
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

  def generatePostBodyHtml(post: PostItem): NodeSeq = {
    <div>
      <div class="post-title">
        <h3>
          {post.getTitle}
        </h3>
      </div>
      <div class="post-info">
        <span class="nickname">
          {post.getNickname}
        </span> <span class="post-add-date">
        {post.getAddDate}
      </span>
      </div>
      <div class="post-content">
        <span>
          {post.getContent}
        </span>
      </div>
    </div>
  }
}
