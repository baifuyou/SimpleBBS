package com.sbbs.snippet

import net.liftweb._
import http._
import util.Helpers._
import com.sbbs.service._
import com.sbbs.model.AuthResult._

/**
 * Created by baifuyou on 14-9-16.
 */
class UserLogin extends StatefulSnippet {

  private var uid: String = ""
  private var password: String = ""
  private var rememberMe = false

  override def dispatch: DispatchIt = {
    case
      "render" => render
  }

  def render = {
    if (UserService.autoLogin()) {
      S.redirectTo(S.referer.openOr("index"))
    }
    "name=uid" #> SHtml.text(uid, uid = _) &
      "name=password" #> SHtml.password(password, password = _) &
      "name=rememberMe" #> SHtml.checkbox(rememberMe, rememberMe = _) &
    "type=submit" #> SHtml.onSubmitUnit(process)
  }

  private def process() = {
    if (rememberMe) {
      UserService.rememberMe(uid)
    } else {
      UserService.forgiveMe(uid)
    }
    val authResult = UserService.authUser(uid, password)
    authResult match {
      case SUCCESS => {
        S.setSessionAttribute("uid", uid)
        S.redirectTo("index")
      }
      case PASSWORD_WRONG => S.error("password", "密码不正确！")
      case UID_NO_EXIST => S.error("password", "用户名不存在！")
    }
  }
}
