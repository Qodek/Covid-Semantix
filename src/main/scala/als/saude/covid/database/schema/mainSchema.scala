package als.saude.covid.database.schema

import java.sql.Date

/**
 * Define o Schema utilizado para ler uma tabela. Aqui são adicionados os schemas a serem utilizados.
 */

case class colCovid(regiao: String, estado: String, municipio: String, codmun: Integer, codRegiaoSaude: Integer, nomeRegiaoSaude: String, data: String, semanaEpi: Integer, populacaoTCU2019: Integer, casosAcumulado: Integer, casosNovos: Integer, obitosAcumulado: Integer, obitosNovos: Integer, Recuperadosnovos: Integer, emAcompanhamentoNovos: Integer, Interior_metropolitana: Integer)

case class colCovidReduced(regiao: String, data: String, semanaEpi: Integer, populacaoTCU2019: Integer, casosAcumulado: Integer, casosNovos: Integer, obitosAcumulado: Integer, obitosNovos: Integer, Recuperadosnovos: Integer, emAcompanhamentoNovos: Integer)

case class colCovidReducedTable(regiao: String, estado: String, data: String, semanaEpi: Integer, populacaoTCU2019: Integer, casosAcumulado: Integer, casosNovos: Integer, obitosAcumulado: Integer, obitosNovos: Integer)
//
//object mainSchema {
//  import org.apache.spark.sql.types.StructType
//  import org.apache.spark.sql.catalyst.ScalaReflection
//
//  val schemaCovid: StructType = ScalaReflection.schemaFor[colCovid].dataType.asInstanceOf[StructType]
//  val schemaCovidReduced: StructType = ScalaReflection.schemaFor[colCovidReduced].dataType.asInstanceOf[StructType]
//
//}