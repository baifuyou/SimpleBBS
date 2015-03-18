package com.sbbs.commons

import java.util.Random

import org.apache.commons.codec.digest.DigestUtils

/**
 * Created by baifuyou on 14-9-16.
 */
object CommonUtils {
  private val symbols = "qwertyuiopasdfghjklzxcvbnm1234567890"
  private val symbolsLength = symbols.length
  def encrypt(str: String): String = {
    DigestUtils.sha1Hex(str)
  }

  def generateRandomCode(length: Int): String = {
    val random = new Random(System.currentTimeMillis())
    val randomCode = new Array[Char](length)
    for (i <- 0 until length) {
      randomCode(i) = symbols(random.nextInt(symbolsLength));
    }
    randomCode.mkString("")
  }

  def generateSalt() = generateRandomCode(16)
}
