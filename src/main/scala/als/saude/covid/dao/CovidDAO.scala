package als.saude.covid.dao
import org.apache.spark.sql.{DataFrame, Dataset, Encoder, Encoders, SparkSession}

/**
 * Responsável pelas interações com o banco de dados, de salvar e ler tabelas.
 */

  class CovidDAO(spark: SparkSession) {
    import spark.implicits._
    def readMain[schemaTable:Encoder](tbName: String): Dataset[schemaTable] = {
      spark.read.table(tbName).as[schemaTable]
    }

    def saveParquet[T](ds: Dataset[T], path: String) = {
      ds.coalesce(1)
        .write
        .mode(saveMode = "overwrite")
        .option("compression", "snappy")
        .parquet(path)
    }

    def saveHive[T](ds: Dataset[T], name: String) = {
      ds.coalesce(1)
        .write
        .mode(saveMode = "overwrite")
        .saveAsTable(name)
    }
     def readKafkaTopic(topic: String): DataFrame = {
       spark.read
         .format("kafka")
         .option("kafka.bootstrap.servers", "kafka:9092")
         .option("subscribe", topic)
         .load()
     }

    def writeKafkaTopic[T](ds: Dataset[T], name: String) = {
      ds.selectExpr("to_json(struct(*)) AS value")
        .write
        .format("kafka")
        .option("kafka.bootstrap.servers", "kafka:9092")
        .option("topic", "Deaths_Total")
        .save()
  }
    }