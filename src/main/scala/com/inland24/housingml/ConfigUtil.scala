package com.inland24.housingml

import java.io.File
import com.typesafe.config.{Config, ConfigFactory}


object ConfigUtil {

  sealed trait ConfigSource
  object ConfigSource {
    case class FromFile(path: String) extends ConfigSource
    case class FromResource(name: String) extends ConfigSource
  }

  def getConfigSource: ConfigSource =
    Option(System.getProperty("config.file")) match {
      case Some(path) if new File(path).exists() =>
        ConfigSource.FromFile(path)

      case _ =>
        val opt1 = Option(System.getProperty("ENV", "")).filter(_.nonEmpty)
        val opt2 = Option(System.getProperty("env", "")).filter(_.nonEmpty)

        opt1.orElse(opt2) match {
          case Some(envName) =>
            val name = s"application.${envName.toLowerCase}.conf"
            ConfigSource.FromResource(name)
          case None =>
            ConfigSource.FromResource("application.conf")
        }
    }

  def loadFromEnv(): Config = {
    getConfigSource match {
      case ref @ ConfigSource.FromFile(path) =>
        ConfigFactory.parseFile(new File(path)).resolve()

      case ref @ ConfigSource.FromResource(name) =>
        ConfigFactory.load(name).resolve()
    }
  }
}