package com.sbbs.model

import java.util.Date

import org.bson.types.ObjectId
import org.mongodb.morphia.annotations._

/**
 * Created by baifuyou on 14-9-16.
 */
@Entity("users")
class User {
  @Id
  private var id: ObjectId = null
  private var encryptedPwd: String = ""
  private var nickname: String = ""
  private var salt: String = ""
  private var uid: String = ""
  private var autoLoginConfirmCode: String = ""
  private var autoLoginEndTime: Date = new Date(0)

  def getAutoLoginEndTime = autoLoginEndTime

  def setAutoLoginEndTime(autoLoginEndTime: Date): Unit = {
    this.autoLoginEndTime = autoLoginEndTime
  }

  def getAutLoginoConfirmCode = autoLoginConfirmCode

  def setAutoLoginConfirmCode(autoLoginConfirmCode: String) = {
    this.autoLoginConfirmCode = autoLoginConfirmCode
  }

  def getId = id;

  def setId(id: ObjectId): Unit = {
    this.id = id
  }

  def getUid = uid

  def setUid(uid: String) = {
    this.uid = uid
  }

  def getEncryptedPwd = encryptedPwd

  def setEncryptedPwd(encryptedPwd: String) = {
    this.encryptedPwd = encryptedPwd
  }

  def getNickname = nickname

  def setNickname(nickname: String) = {
    this.nickname = nickname
  }

  def getSalt = salt

  def setSalt(salt: String) = {
    this.salt = salt
  }
}
