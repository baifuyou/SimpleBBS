package com.sbbs.snippet

import com.sbbs.model.AuthResult
import com.sbbs.service.UserService
import net.liftweb.http._
import net.liftweb.http.js.JE.{JsArray, ValById}
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.{Run, SetElemById}
import net.liftweb.util
import util.Helpers._

/**
 * Created by baifuyou on 14-9-22.
 */
object UserControl {

  def render() = {
    UserService.autoLogin()
    if (S.getSessionAttribute("uid").isEmpty) {
      S.redirectTo("login")
    } else {
      val uid = S.getSessionAttribute("uid").openOr("")
      val user = UserService.getUserByUid(uid)
      "#nickname [value]" #> user.getNickname &
        "title *+" #> user.getNickname &
        "#save-profile [onclick]" #> SHtml.ajaxCall(ValById("nickname"), nickname => {
          resetNicknme(nickname)
        }) &
        "#save-password [onclick]" #> SHtml.ajaxCall(JsArray(ValById("old-password"), ValById("new-password"), ValById("new-password2")),
          args => {
            val elems = args.split(",")
            if (elems.length != 3) {
              Run("show('#request-format-error', 3000)")
            } else {
              val oldPassword = elems(0)
              val newPassword = elems(1)
              val newPassword2 = elems(2)
              resetPassword(oldPassword, newPassword, newPassword2)
            }
          })
    }
  }

  def resetPassword(oldPassword: String, newPassword: String, newPassword2: String): JsCmd = {
    val uid = S.getSessionAttribute("uid")
    if (uid.isEmpty) {
      Run("show('#not-login', 3000)")
    } else {
      val uidStr = uid.openOr("")
      if(UserService.authUser(uid.openOr(uidStr), oldPassword) != AuthResult.SUCCESS) {
        Run("show('#password-error', 3000)")
      } else {
        if(UserService.resetPassword(uidStr, newPassword)) {
          Run("show('#save-password-success', 3000)")
        } else {
          Run("show('#server-error', 3000")
        }
      }
    }
  }

  def resetNicknme(nickname: String) = {
    if (S.getSessionAttribute("uid").isEmpty) {
      Run("show('#save-profile-failure', 3000)")
    } else {
      UserService.setNickname(nickname)
      Run("show('#save-profile-success', 3000)")
    }
  }

}
