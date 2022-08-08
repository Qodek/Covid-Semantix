docker exec namenode hdfs dfs -put namenode:/input/HIST_PAINEL_COVIDBR_* /project/data
docker cp /home/qodek/proj/hivesql.sql hive-server:/home/
docker exec hive-server hive -f /home/hivesql.sql
docker cp /home/qodek/proj/covid-semantix/target/scala-2.11/covid-semantix-assembly-0.1.jar spark:/home/
docker exec spark spark-submit --master local /home/covid-semantix-assembly-0.1.jar
