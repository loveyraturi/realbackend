# File versioning

It is created to maintain file versions along and switching back to any version whenever needed.

## Installation

### For Development
1>clone the repository.
2> upload it as a maven project in you eclipse
3> change database configuration 
4> run and enjoy

### For production
1> Clone the repository.
2> Form project root run command ``` docker build -t=fileversioning:v1 .```
3> run docker command docker run -p 6001:6001 -e MYSQL_JDBC_URL: -e MYSQL_PASSWORD: -e MYSQL_USERNAME: -e
4> run and enjoy
