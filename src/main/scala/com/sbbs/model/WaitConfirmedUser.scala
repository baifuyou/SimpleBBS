package com.sbbs.model

import java.util.Date

import org.bson.types.ObjectId
import org.mongodb.morphia.annotations._

/**
 * Created by baifuyou on 14-9-17.
 */
@Entity("waitConfirmedUsers")
class WaitConfirmedUser {
  @Id
  private var id: ObjectId = null
  private var uid: String = ""
  private var salt: String = ""
  private var encryptedPwd: String = ""
  private var nickname: String = ""
  private var emailRecognitionCode: String = ""
  private var confirmCode: String = ""
  private var deadline: Date = null

  def getId = id

  def setId(id: ObjectId) = {
    this.id = id
  }

  def getUid = uid

  def setUid(uid: String) = {
    this.uid = uid
  }

  def getSalt = salt

  def setSalt(salt: String) = {
    this.salt = salt
  }

  def getEncryptedPwd = encryptedPwd

  def setEncryptedPwd(encryptedPwd: String) = {
    this.encryptedPwd = encryptedPwd
  }

  def getNickname = nickname

  def setNickname(nickname: String) = {
    this.nickname = nickname
  }

  def getEmailRecognitionCode = emailRecognitionCode

  def setEmailRecognitionCode(emailRecognitionCode: String) = {
    this.emailRecognitionCode = emailRecognitionCode
  }

  def getConfirmCode = confirmCode

  def setConfirmCode(confirmCode: String) = {
    this.confirmCode = confirmCode
  }

  def getDeadline = deadline

  def setDeadline(deadline: Date) = {
    this.deadline = deadline
  }
}
