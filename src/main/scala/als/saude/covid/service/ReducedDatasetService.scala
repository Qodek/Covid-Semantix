package als.saude.covid.service

import als.saude.covid.business.{CovidCard, CovidTable}
import als.saude.covid.dao.CovidDAO
import als.saude.covid.database.CovidDatabase
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{col, round}
import org.slf4j.LoggerFactory

object  ReducedDatasetService {
    var log = LoggerFactory.getLogger(this.getClass)
  /**
   * Ativa os Transforms, gera os Dataframes mostrados no Dashboard em terminal e salva eles nos respectivos formatos solicitados.
   */
  def apply(spark:SparkSession):Unit = {
      log.info("[*] *-----------------------------------------* Buscando dataset, transformando e salvando em tabela hive *------------------------------------------* ")
      val dao = new CovidDAO(spark)
      val cardInstance = new CovidCard(spark)
      val cardTable = cardInstance.getRecentReduced

    /**
     * Gera o Card de recuperações no país.
     */
      val cardRec = cardTable.select(
        col("Recuperadosnovos").as("Casos Recuperados"),
        col("emAcompanhamentoNovos").as("Em Acompanhamento")
      )

    /**
     * Gera o Card de casos acumulados, novos e incidência no país.
     */
      val cardCases = cardTable.select(
        col("casosAcumulado").as("Casos Acumulados"),
        col("casosNovos").as("Casos Novos"),
        round(col("casosAcumulado")/col("populacaoTCU2019")*100000,2).as("Incidencia")
      )

    /**
     * Gera o Card de óbitos acumulados, novos, mortalidade e letalidade no país.
     */
      val cardDeaths = cardTable.select(
        col("obitosAcumulado").as("Obitos Acumulados"),
        col("obitosNovos").as("Obitos Novos"),
        round(col("obitosAcumulado")/col("casosAcumulado")*100,2).as("Letalidade"),
        round(col("obitosAcumulado") / col("populacaoTCU2019") * 100000, 2).as("Mortalidade")
      )

    log.info("[*]\n *-------------------------------------* Relação de casos de Recuperação e casos em Acompanhamento *---------------------------------------------* ")
    cardRec.show()
    dao.saveHive(cardRec,"covid.Recup")
    log.info("[*]\n *-------------------------------------* Relação de casos totais até o momento analisado e Incidência (casos/pop)*100.000 *----------------------* ")
    cardCases.show()
    dao.saveParquet(cardCases,CovidDatabase.PATH_FILE)
    log.info("[*]\n *-------------------------------------* Relação de óbitos até o momento, letalidade (obitos/caso)*100 e Mortalidade (casos/pop)*100.000 *-------* ")
    cardDeaths.show()

    /**
     * Para escrever os dados no Kafka, chamamos a função criada:
     */
    dao.writeKafkaTopic(cardDeaths,"DeathsTotal")
    val deaths_topic = dao.readKafkaTopic("DeathsTotal")

    val tableInstance = new CovidTable(spark)
    val tableTable = tableInstance.getRecentReducedTable


    /**
     * Gera o Card de casos acumulados, novos e incidência no país.
     */
    val tableCases = tableTable.select(
      col("regiao").as("Região"),
      col("estado").as("Estado"),
      col("casosAcumulado").as("Casos Acumulados"),
      col("obitosAcumulado").as("Obitos Acumulados"),
      round(col("obitosAcumulado") / col("populacaoTCU2019") * 100000, 2).as("Mortalidade"),
      round(col("casosAcumulado") / col("populacaoTCU2019") * 100000, 2).as("Incidencia"),
      col("data").as("Atualização")
    )

    log.info("[*]\n *-------------------------------------* Tabela com os dados sobre o Covid no Brasil e suas Regiões *--------------------------------------------* ")
    tableCases.show(28)



    log.info("[*] *-----------------------------------------* Servico Finalizado *--------------------------------------------------------------------------------* ")


  }

}
