package als.saude.covid.business.transform

import org.apache.spark.sql.{DataFrame, Dataset, Encoder, Encoders, Row, SparkSession, functions}
import als.saude.covid.database.CovidDatabase
import als.saude.covid.dao.CovidDAO
import als.saude.covid.database.schema._
import org.apache.spark.sql.functions.{col, from_unixtime, unix_timestamp}
import org.apache.spark.sql.types.IntegerType

import java.sql.Date

/**
 * Define as transformações a serem realizadas em cima de um dataset.
 * Transformações para obter dados dos Cards gerais, que mostram os dados do país na data mais recente.
 */

class CovidCardTransform(spark:SparkSession) {
  import spark.implicits._

  def apply(): Dataset[colCovidReduced] = {
    /**
     * Aplica as transformações.
     */
    getFile
      .transform(FilterDataset)
      .transform(ReduceDataset)
      .transform(mostRecent)
      .as[colCovidReduced]

  }

  def getFile: Dataset[colCovid] = {
    /**
     * Traz o dataset usando a classe CovidDAO.
     */
    val daoFile = new CovidDAO(spark)
    import spark.implicits._

    daoFile.readMain(CovidDatabase.DB_MAIN)
  }

  def FilterDataset(table: Dataset[colCovid]): Dataset[colCovid] = {
    /**
     * Filtra os dados relevantes.
     */
    table
      .filter(col("regiao")==="Brasil")
  }


  def ReduceDataset(table: Dataset[colCovid]): DataFrame = {
    /**
     * Remove colunas desnecessárias nessa análise.
     */
    table
      .drop("municipio", "estado", "coduf", "codmun", "codRegiaoSaude", "nomeRegiaoSaude", "Interior_metropolitana")
      .filter(col("regiao")==="Brasil")
  }

  def mostRecent(table: DataFrame): DataFrame = {
    /**
     * Filtra a data mais recente.
     */
    table
      .filter(col("data")===CovidDatabase.DATE)
  }
}
