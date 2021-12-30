# tJDBCUniqRow

Talend component that can be used to filter rows based on database entries. It uses JDBC to connect to a database and determine if a record has already been processed. Because the database can be shared by multiple jobs/instances it can be used to split workload over several jobs without processing the same row twice.
