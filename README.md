# Projeto Semantix - Big Data Engineering

O projeto consiste em utilizar os dados fornecidos no site https://covid.saude.gov.br/ para fazer alguns processos aprendidos ao longo do curso: Utilizar o docker e docker compose para criar e controlar o cluster de sistemas, utilizar o HDFS para gerenciamento dos dados no cluster, utilizar o HIVE com SQL para gerar um banco de dados, ler esses dados através do Spark, gerando tabelas com diferentes visualizações, e salvar as mesmas em diversos formatos, como tabela dentro do HIVE, um conjunto de arquivos em formato parquet, em um tópico do Kafka e do Elastic, e gerar Dashboards atrav

# Preparação do Ambiente
O projeto foi realizado utilizando o Ubuntu 22.04. Para instalação das aplicações, foram utilizadas as imagens do Docker fornecidas durante o curso, para garantir a compatibilidade com os comandos aprendidos. 
Para organização do cluster e facilidade de inicialização, foi utilizado o docker compose, com a estrutura conforme arquivo docker-compose.yml. Os seguintes comandos são utilizados para inicializar o cluster: 

`docker compose up -d`

`docker compose start`

# Ingestão de dados
Para a ingestão, usa-se o Hadoop Distributed File System, inserindo os dados em uma tabela hive através do SQOOP. Otimização feita dentro do hive. 
## Estrutura HDFS
A estrutura dentro do HDFS foi criada da seguinte forma:
    /project
        /data
        /tables
Para isso, os seguintes comandos foram executados: 

`docker exec -it namenode bash`

`hdfs dfs -mkdir -p /project/data`

`hdfs dfs -mkdir -p /project/tables`

O cluster está configurado para aceitar uma pasta externa (input, na pasta do docker compose) como interface entre o sistema e o cluster. Nessa pasta, estão os arquivos CSV analisados, que são histórico do COVID registrado no painel em https://covid.saude.gov.br/. Esses arquivos seguem o seguinte padrão:
 HIST_PAINEL_COVIDBR_YYYY_ParteX_timestamp.csv

Então, para trazer esses arquivos para o sistema do HDFS, o comando é: 

`hdfs dfs -put /input/HIST_PAINEL_COVIDBR_* /project/data`

## Banco de dados HIVE

No Hive, são criadas as tabelas com os dados. Primeiro, é criado um banco de dados para guardar as tabelas:

`create database covid comment "Contem os dados sobre casos de Covid no Brasil";`

`use covid;`

Depois, é criada uma tabela temporária, para trazer o CSV e uma tabela principal que traz os dados da primeira, porém de maneira otimizada:

```
create table tempCovid(
        regiao string,
        estado string,
        municipio string,
        coduf int,
        codmun int,
        codRegiaoSaude int,
        nomeRegiaoSaude string,
        data date,
        semanaEpi int,
        populacaoTCU2019 int,
        casosAcumulado int,
        casosNovos int,
        obitosAcumulado int,
        obitosNovos int,
        Recuperadosnovos int,
        emAcompanhamentoNovos int,
        interior_metropolitana int)
    comment "Tabela temporaria para leitura do CSV. Dados não otimizados"
    row format delimited
        fields terminated by ';'
    tblproperties("skipheader.line.count"="1");
```

```
create table mainCovid(
        regiao string,
        estado string,
        municipio string,
        coduf int,
        codRegiaoSaude int,
        nomeRegiaoSaude string,
        data date,
        semanaEpi int,
        populacaoTCU2019 int,
        casosAcumulado int,
        casosNovos int,
        obitosAcumulado int,
        obitosNovos int,
        Recuperadosnovos int,
        emAcompanhamentoNovos int,
        interior_metropolitana int
    )
    comment "Tabela principal, com todos os dados do relatorio e com otimização em formato orc"
    Partitioned by (codmun int)
    row format delimited
        fields terminated by ';'
    stored as orc
    tblproperties(
        "orc.compress"="SNAPPY",
        "orc.create.index"="true");
``` 
O formato ORC é utilizado para otimização, e a compressão SNAPPY por ser equilibrada. A tabela temporária é necessária para que seja possível fazer o particionamento conforme desejado. Após isso, fazemos o carregamento das informações na tabela temporária: 

`load data inpath '/project/data/*' into table tempCovid;`

O comando não ignora o cabeçalho em cada arquivo e a tabela só ignora o primeiro. Então, fazemos o overwrite: 
    insert overwrite table tempCovid  
    select * from tempCovid
    where regiao<>"regiao"; 

Define-se, então, os parâmetros do HIVE que permitem o particionamento dinâmico, para que não seja necessário inserir manualmente as partições:

```
set hive.exec.dynamic.partition=true;
set hive.exec.dynamic.partition.mode=nonstrict;
```

Já podemos aproveitar e definir mais alguns parâmetros que vão permitir queries mais eficientes. O primeiro libera a função de paralelização na execução de determinadas queries (As queries são feitas em diversos passos que, por padrão, são executados sequencialmente), e os outros dois permitem a vetorização de operações, fazendo com que sejam executadas em batches de 1024 linhas, ao invés de uma a uma. Não são tão relevantes no processo atual, mas caso seja necessário fazer alguma outra query vão permitir ter agilidade no processo. 

```
Set hive.exec.parallel = true;
set hive.vectorized.execution.enabled = true;
set hive.vectorized.execution.reduce.enabled = true;
```

Aqui, os dados da tabela temporária são inseridos na tabela principal, utilizando o código do município como partição. 

```
insert into table mainCovid
partition(codmun)
SELECT regiao,
estado,
municipio,
coduf,
codRegiaoSaude,
nomeRegiaoSaude,
data,
semanaEpi,
populacaoTCU2019,
casosAcumulado,
casosNovos,
obitosAcumulado,
obitosNovos,
Recuperadosnovos,
emAcompanhamentoNovos,
interior_metropolitana,
codmun FROM tempCovid WHERE regiao<>"regiao";
```

# Spark - Scala
O programa em Scala foi desenvolvido de acordo com o padrão Semantix disponibilizado no curso. Dentro do programa, cada arquivo está comentado de acordo com sua função e detalhes. Sua função principal é ler os dados da tabela do Hive, tratar, transformar e adequar os dados dessa tabela para exibição e para salvar de acordo com a solicitação dos exercícios do projeto. 

# Automação dos processos
Com os testes constantes e repetição de diversos passos do projeto, alguns scripts foram criados para automatizar esses testes. O hivesql.sql automatiza os processos explicados acima, sendo rodado dentro do container hive-server com o seguinte comando:

`hive -f /home/hivesql.sql`

E os comandos do docker, de copiar o programa e salvar dentro dos containers e rodar o spark-submit foram automatizados em um simples script sh. 
