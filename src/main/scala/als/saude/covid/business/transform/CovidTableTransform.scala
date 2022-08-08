package als.saude.covid.business.transform

import als.saude.covid.dao.CovidDAO
import als.saude.covid.database.CovidDatabase
import als.saude.covid.database.schema._
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

/**
 * Define as transformações a serem realizadas em cima de um dataset.
 * Transformações para obter dados da tabela, que mostra os dados de cada região e estado na data mais recente.
 */


class CovidTableTransform(spark:SparkSession) {
  import spark.implicits._

  def apply(): Dataset[colCovidReducedTable] = {
    /**
     * Aplica as transformações.
     */

    getFile
      .transform(FilterDataset)
      .transform(ReduceDataset)
      .transform(mostRecent)
      .transform(sorted)
      .as[colCovidReducedTable]
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
      .filter(col("codmun").isNull)
  }

  def ReduceDataset(table: Dataset[colCovid]): DataFrame = {
    /**
     * Remove colunas desnecessárias nessa análise.
     */
    table
      .drop("municipio", "coduf", "codmun", "codRegiaoSaude", "nomeRegiaoSaude", "Interior_metropolitana", "Recuperadosnovos", "emAcompanhamentoNovos")
  }

  def mostRecent(table: DataFrame): DataFrame = {
    /**
     * Filtra a data mais recente.
     */

    table
      .filter(col("data")===CovidDatabase.DATE)
  }

  def sorted(table: DataFrame): DataFrame = {
    /**
     * classifica, para manter organizado.
     */
    table
      .sort(col("regiao"))
  }
}
