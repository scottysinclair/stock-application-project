# Testing POC project with Arquillian, Arquillian Cube and Arquillian Persistence
## Goals
Execute automated tests against a real project infrastructure, in this case:
1. Call business logic in Spring Services backed by hibernate/JPA
2. Use PostgreSQL as the application database.
3. Execute batch jobs which sync data between the PostgreSQL database and a DB2 database.

*Arquillian* was chosen as it allows test suites to be built and deployed on various servlet containers (including EAP7).   *Aquillian Cube* suports docker which is used to launch various containers for EAP7, PostgreSQL, DB2 and batch processing.  
*Arquillian Spring* supports using Spring inside the Arqulllian Test Suite.  
*Arquillian Persistence* provide support for using DBUnit inside the Arquillian Test Suite.  

## Steps in the automate test process
Launch the tests by running `mvn clean install -Puat`

Arquillian Cube fires up the docker containers defined in Arqullian.xml:
1. web - contains the EAP7 image and a command line Java Docker client.
2. db - contains the PostgreSQL image.
3. olddb - contains the ibm DB2 image (based on https://hub.docker.com/r/ibmcom/db2express-c/)
4. batch - contains the batch Java application which syncs from PostgreSQL to DB2.



