package com.sbbs.model

import java.util.Date

import org.bson.types.ObjectId
import org.mongodb.morphia.annotations._

/**
 * Created by baifuyou on 14-9-19.
 */
@Entity("replyItems")
class ReplyItem {
  @Id
  private var id: ObjectId = null
  private var content: String = ""
  private var uid: String = ""
  private var addDate: Date = null
  private var postId: String = ""
  private var nickname: String = ""

  def getNickname = nickname

  def setNickname(nickname: String) = {
    this.nickname = nickname
  }

  def getId = id

  def setId(id: ObjectId): Unit = {
    this.id = id
  }

  def getContent = content

  def setContent(content: String): Unit = {
    this.content = content
  }

  def getUid = uid

  def setUid(uid: String): Unit = {
    this.uid = uid
  }

  def getAddDate = addDate

  def setAddDate(addDate: Date) = {
    this.addDate = addDate
  }

  def getPostId = postId

  def setPostId(postId: String) = {
    this.postId = postId
  }
}
