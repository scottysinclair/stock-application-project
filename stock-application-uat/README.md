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

### Launch the test
Launch the tests by running `mvn clean install -Puat`

### Docker containers started 
Arquillian Cube fires up the docker containers defined in Arqullian.xml:
1. web - contains the EAP7 image and a command line Java Docker client.
2. db - contains the PostgreSQL image.
3. olddb - contains the ibm DB2 image (based on https://hub.docker.com/r/ibmcom/db2express-c/)
4. batch - contains the batch Java application which syncs from PostgreSQL to DB2.

The DOOCKER_HOST is currently assumed to be http://localhost:4243 but this restriction will be removed soon.

### WAR file deployed on server
Once the containers are up and running Arquillian deploys the Shrinkwrapped WAR file containing the application + Test Suite onto the EAP7 server.

### Test Suite is launched
The [UnifiedStockTestCase](./src/test/java/com/acme/spring/hibernate/service/impl/UnifiedStockTestCase.java) is launched.

A custom Junit rule (UnifiedTestWatcher) is used to managed the state of the PostgreSQL and DB2 databases before and after each test run. The UnifiedTestWatcher manages the following:
1. Initializes the PostgreSQL database according to the DBUnit dataset defined for the test case.
2. Initializes the DB2 database according to the DBUnit dataset defined for the test case.
3. Dumps the PostgreSQL and DB2 database contents to a DBUnit XML file when a test fails.

### The PostgreSQL and DB2 databases are initialized as per the UnifiedTestWatcher Rule
Each test case has it's own directory under `/datasets/` which contains DBUnit datasets.
DBUnit will perform a **CLEAN_INSERT** for PostgreSQL by finding a file [/datasets/test_case_1/input_ds.xml](./src/test/resources/datasets/test_case_1/input_ds.xml)


