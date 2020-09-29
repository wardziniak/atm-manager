# atm-manager

Sample implementation of ATM Manager

Business logic is implemented in AtmTopologyBuilder.

There is assumption that, applications that produce messages with data - Transaction (com.wardziniak.atm.dto.AtmRequest) or Account information (com.wardziniak.atm.model.Account)
use proper partitioner (com.wardziniak.atm.partitioners.AccountsPartitioner) to distribute data properly for AtmApp. 
It is important for keeping order of requests and proper calculation of Account Balance. Output of messages also keeps the input partitioning of data.



#### Dockerization

Application can be dockerized using following command:

```sbt docker:publishLocal```

And started with:

```docker run -e BOOTSTRAP_SERVER="192.168.8.101:9092" --rm atm-manager:1.0```


Also application can be scale up with using docker-compose:
```docker-compose up --scale atm-manager=3 -d```
