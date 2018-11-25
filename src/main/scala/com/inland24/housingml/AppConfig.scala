package com.inland24.housingml

import com.typesafe.config.Config

import scala.util.Try


final case class AppConfig(
  environment: String,
  sourceFileName: String,
  sourceFileUrl: String,
  targetFilePath: String,
  testDataConfig: TestDataConfig
)

sealed trait DataCleansingStrategy
case object RemoveEmptyRows extends DataCleansingStrategy
case object Median extends DataCleansingStrategy

case class TestDataConfig(
  trainingSetRatio: Double = 0.2d,
  dataCleansingStrategy: DataCleansingStrategy = RemoveEmptyRows
)
object AppConfig {

  def load(cfg: Config): Try[AppConfig] = Try {
    AppConfig(
      environment = cfg.getString("environment"),
      sourceFileName = cfg.getString("file.name"),
      sourceFileUrl = cfg.getString("file.source.url"),
      targetFilePath = cfg.getString("file.target.path"),
      testDataConfig = TestDataConfig(
        trainingSetRatio = cfg.getDouble("training.ratio")
      )
    )
  }
}