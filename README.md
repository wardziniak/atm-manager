# atm-manager

Sample implementation of ATM Manager

Business logic is implemented in AtmTopologyBuilder.

There is assumption that, application that produce messages with data - Transaction (com.wardziniak.atm.dto.AtmRequest) or Account information (com.wardziniak.atm.model.Account)
use proper partitioner (com.wardziniak.atm.partitioners.AccountsPartitioner) to distribute data properly for AtmApp. 
It is important for keeping order of requests and proper calculation of Account Balance. Output of messages also keeps the input partitioning of data.




