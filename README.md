# funelo
Big Data ingestion made easy.


Funelo ensures raw data automatically gets turned into actionable data sets as it lands first into messaging system like kafka and later in S3/HDFS for downstream applications to make sense out of it.

### Getting Started 

##### How to run
```shell
  git clone https://github.com/zalando/funelo.git
  cd funelo
  mvn clean package
  java -jar target/funelo-0.0.1-SNAPSHOT.jar -conf conf/funelo.json
```    

##### Hot to test
```shell
curl -vvv http://localhost:8081/v1/myfeed/view?creativeId=123456
```

### Performance 

##### Install wrk2
```shell
  git clone https://github.com/giltene/wrk2.git
  cd wrk2
  make
```

##### Run tests
```shell
wrk -t2 -c100 -d30s -R10000 --latency http://127.0.0.1:8080/
```
This runs a benchmark for 30 seconds, using 2 threads, keeping 100 HTTP connections open, and a constant throughput of 10,000 requests per second (total, across all connections combined).
