package com.inland24.housingml

import com.typesafe.config.Config

import scala.util.Try

final case class AppConfig(
  environment: String,
  sourceFileName: String,
  sourceFileUrl: String,
  targetFilePath: String
)
object AppConfig {

  def load(cfg: Config): Try[AppConfig] = Try {
    AppConfig(
      environment = cfg.getString("environment"),
      sourceFileName = cfg.getString("file.name"),
      sourceFileUrl = cfg.getString("file.source.url"),
      targetFilePath = cfg.getString("file.target.path")
    )
  }
}