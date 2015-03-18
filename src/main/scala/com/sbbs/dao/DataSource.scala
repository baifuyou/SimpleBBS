package com.sbbs.dao

import java.io.FileInputStream
import java.util
import java.util.Properties

import com.mongodb._
import org.mongodb.morphia._

/**
 * Created by baifuyou on 14-9-16.
 */
object DataSource {
  private val mongoConfigPath = "src/main/resources/props/mongoConfig.properties"
  private val toEnsureIndexs = false // 是否确认索引存在，默认false
  private val toEnsureCaps = false // 是否确认caps存在，默认false
  private val mapPackages = Array("code.model")
  private val ignoreInvalidClasses = true
  private val datastore: Datastore = newDatastore()

  def getDatastore(): Datastore = {
    datastore
  }

  def newDatastore(): Datastore = {
    val fis = new FileInputStream(mongoConfigPath)
    val mongoConfig = new Properties()
    mongoConfig.load(fis)
    val database = mongoConfig.getProperty("database")
    val morphia = getMorphia()
    val mongo = getMongo()
    val datastore = morphia.createDatastore(mongo, database)
    if (toEnsureIndexs)
      datastore.ensureIndexes()
    if (toEnsureCaps)
      datastore.ensureCaps()
    datastore
  }

  private def getMorphia(): Morphia = {
    val morphia = new Morphia()
      for (pack <- mapPackages) {
        morphia.mapPackage(pack, ignoreInvalidClasses)
      }
    morphia
  }

  private def getMongo(): Mongo = {
    val configStream = new FileInputStream(mongoConfigPath);
    val mongoConfig = new Properties()
    mongoConfig.load(configStream)
    val username = mongoConfig.getProperty("username")
    val password = mongoConfig.getProperty("password")
    val database = mongoConfig.getProperty("database");
    val serverAddressStr = mongoConfig.getProperty("server_address");
    val serverPort = mongoConfig.getProperty("server_port").toInt;
    val mongoCredential = MongoCredential
      .createMongoCRCredential(username, database,
        password.toCharArray());
    val credentials = new util.ArrayList[MongoCredential]();
    credentials.add(mongoCredential);
    val serverAddress = new ServerAddress(serverAddressStr,
      serverPort);
    val mongo = new MongoClient(serverAddress, credentials);
    mongo
  }
}
