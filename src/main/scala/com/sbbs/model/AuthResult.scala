package com.sbbs.model

/**
 * Created by baifuyou on 14-9-16.
 */
object AuthResult extends Enumeration{
  type AuthResult = Value
  val SUCCESS, PASSWORD_WRONG, UID_NO_EXIST = Value
}
