package als.saude.covid.business

import als.saude.covid.business.transform.CovidTableTransform
import als.saude.covid.database.schema.{colCovidReduced, colCovidReducedTable}
import org.apache.spark.sql.{Dataset, SparkSession}
/**
 * Consolida as transformações da tabela.
 */
class CovidTable(spark:SparkSession) {
  val covidTransform = new CovidTableTransform(spark)
  def getRecentReducedTable: Dataset[colCovidReducedTable] = {
    covidTransform()
  }
}

