set hive.exec.dynamic.partition=true;
set hive.exec.dynamic.partition.mode=nonstrict;
Set hive.exec.parallel = true;
set hive.vectorized.execution.enabled = true;
set hive.vectorized.execution.reduce.enabled = true;

create external table if not exists tempCovid(
regiao string,
estado string,
municipio string,
coduf int,
codmun int,
codRegiaoSaude int,
nomeRegiaoSaude string,
data string,
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
stored as textfile
location '/project/data'
tblproperties("skipheader.line.count"="1");


create table if not exists covid.mainCovid(
regiao string,
estado string,
municipio string,
codmun int,
codRegiaoSaude int,
nomeRegiaoSaude string,
data string,
semanaEpi int,
populacaoTCU2019 int,
casosAcumulado int,
casosNovos int,
obitosAcumulado int,
obitosNovos int,
Recuperadosnovos int,
emAcompanhamentoNovos int,
interior_metropolitana int)
comment "Tabela principal, com todos os dados do relatorio e com otimização em formato orc" 
Partitioned by (coduf int)
row format delimited
fields terminated by ';'
stored as orc
tblproperties(
"orc.compress"="SNAPPY",
"orc.create.index"="true",
"skipheader.line.count"="1");


insert overwrite table tempCovid  
select * from tempCovid
where regiao<>"regiao"; 



insert into table covid.mainCovid
partition(coduf)
SELECT regiao,
estado,
municipio,
codmun,
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
coduf FROM covid.tempCovid;

