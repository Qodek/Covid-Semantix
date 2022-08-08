package als.saude.covid.business

import als.saude.covid.business.transform.CovidCardTransform
import als.saude.covid.database.schema.colCovidReduced
import org.apache.spark.sql.{Dataset, SparkSession}

/**
 * Consolida as transformações do card.
 */


  class CovidCard(spark:SparkSession) {
    val covidTransform = new CovidCardTransform(spark)
    def getRecentReduced: Dataset[colCovidReduced] = {
      covidTransform()
    }
  }

