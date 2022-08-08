package als.saude.covid

import als.saude.covid.service.ReducedDatasetService
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.slf4j.LoggerFactory

object Covid {
  var log = LoggerFactory.getLogger(this.getClass)
  Logger.getLogger("org").setLevel(Level.ERROR)
  Logger.getLogger("als").setLevel(Level.INFO)

  def getSparkSession(): SparkSession = {
    val sparkSession = SparkSession.
      builder()
      .enableHiveSupport()
      .getOrCreate()
    sparkSession
  }

  def main(args: Array[String]) = {
    log.info("[*] *------------------------------------------------------------* Iniciando Aplicação*------------------------------------------------------------* ")
    val spark = getSparkSession()
    ReducedDatasetService(spark)
    log.info("[*] *------------------------------------------------------------* Finalizado*---------------------------------------------------------------------* ")
  }

}
