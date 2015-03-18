package com.sbbs.snippet

import net.liftweb._
import http._
import net.liftweb.http.js.JE.ValById
import net.liftweb.http.js.JsCmds.{Alert, Run}
import util.Helpers._
import com.sbbs.service._

/**
 * Created by baifuyou on 14-9-16.
 */
class UserRegister extends StatefulSnippet {

  private var uid = ""
  private var password = ""
  private var nickname = ""
  private var rePassword = ""

  override def dispatch: DispatchIt = {
    case "render" => render
    case "confirm" => confirm
  }

  def confirm = {
    println("confirm")
    var content = ""
    val emailRecognitionCode: String = S.param("recoCode").openOr(null) match {
      case recoCode: String => recoCode
      case _ => ""
    }

    val confirmCode: String = S.param("confirmCode").openOr(null) match {
      case confirmCode: String => confirmCode
      case _ => ""
    }
    println("recoCode: " + emailRecognitionCode)
    println("confirmCode: " + confirmCode)
    if (emailRecognitionCode == null || confirmCode == null || (!UserService.confirmRegistedUser(emailRecognitionCode, confirmCode))) {
      content = "lift:embed?what=emailConfirmFailure"
    } else {
      content = "lift:embed?what=emailConfirmSuccess"
    }
    ".container [class+]" #> content
  }

  def render =
    "name=password" #> SHtml.password(password, password = _) &
      "name=nickname" #> SHtml.text(nickname, nickname = _) &
      "name=rePassword" #> SHtml.password(rePassword, rePassword = _) &
      "#inputEmail [value]" #> SHtml.text(uid, uid = _) &
      "#inputEmail [onchange]" #> SHtml.ajaxCall(ValById("inputEmail"), inUid => {
        println("inUid: " + inUid)
        uid = inUid
        val isRegistered = UserService.isRegistered(inUid)
        Run("checkEmail(" + isRegistered + ")")
      }) &
      "type=submit" #> SHtml.onSubmitUnit(process)

  private def process() = {
    println("uid: " + uid)
    if (UserService.isRegistered(uid)) {
      S.redirectTo("registerFailure")
    } else {
      UserService.register(uid, nickname, password)
      S.redirectTo("registerSuccess")
    }
  }
}
