package als.saude.covid.database

  object CovidDatabase {

    /**
     * Define o nome da tabela e banco de dados a ser chamado no arquivo. A utilizada será Covid.mainCovid.
     */

    val DB = "covid."
    val MAIN = "maincovid"
    val DB_MAIN = s"${DB}${MAIN}"

    val PERIOD = "timeperiod"
    val DB_PERIOD = s"${DB}${PERIOD}"

    /**
     * Define o caminho onde serão salvas as tabelas do modelo. Definido para hdfs://namenode/project/tables
     */

    val PATH = "hdfs://namenode"
    val FILE = "/project/tables/"
    val PATH_FILE = s"${PATH}${FILE}"

    /**
     * Variável com a data mostrada. Automação planejada.
     */

    val DATE = "2022-08-07"
  }

