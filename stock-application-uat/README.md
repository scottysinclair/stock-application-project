# Testing POC project with Arquillian, Arquillian Cube and Arquillian Persistence
## Goals
Execute automated tests against a real project infrastructure, in this case:
1. Call business logic in Spring Services backed by hibernate/JPA
2. Use PostgreSQL as the application database.
3. Execute batch jobs which sync data between the PostgreSQL database and a DB2 database.

*Arquillian* was chosen as it allows test suites to be built and deployed on various servlet containers (including EAP7).   *Aquillian Cube* suports docker which is used to launch various containers for EAP7, PostgreSQL, DB2 and batch processing.  
*Arquillian Spring* supports using Spring inside the Arqulllian Test Suite.  
*Arquillian Persistence* provide support for using DBUnit inside the Arquillian Test Suite.  

## Steps in the automated test process

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
DBUnit will perform a **CLEAN_INSERT** for PostgreSQL by finding a file [input_ds.xml](./src/test/resources/datasets/test_case_1/input_ds.xml)
DBUnit will perform a **CLEAN_INSERT** for DB2 by finding a file [input_dsInt.xml](./src/test/resources/datasets/test_case_1/input_dsInt.xml)

If DBUnit should just clean the tables and not perform any inserts then the XML file should just refer to the table but not specify any data. DBUnit will then only peform the clean (this is what [input_dsInt.xml](./src/test/resources/datasets/test_case_1/input_dsInt.xml) does).

### The test method is lauched
```Java
    @Test
    public void test_case_1() throws Exception  {
      /*
       * perform some business logic in the new application
       */
      Stock acme = createStock("Acme", "ACM", 123.21D, new Date());
      stockService.save(acme);

      Stock redhat = createStock("Red Hat", "RHC", 59.61D, new Date());
      stockService.save(redhat);

      /*
       * assert the state of the new application database.
       */
      unifiedTestHelper.assertNewTestData(new String[]{"date"});

      /*
       * execute the integration job.
       */
      IntegrationHelper.executeIntegration();

      /*
       * assert the state of the DB2 database after integration.
       */
      unifiedTestHelper.assertFirstIntegration(new String[]{"date"});
    }
```

### The test method executes some business logic
In this case the test method creates two new Stocks.

### The test method asserts the state of the PostgreSQL database
DBUnit will compare the PostgreSQL database tables with the content of file [expected_result_1.xml](src/test/resources/datasets/test_case_1/expected_result_1.xml)

### The test method executes the integration batch job which syncs data to the DB2 database
The batch job is hosted in the *batch* docker container. The [Java Docker client](https://github.com/docker-java/docker-java) is used to execute the batch job on the batch container using the docker **EXEC** command.

Note. The Java Docker client has a hard dependency to the Jersey JAX-RS implementation for communicating with the docker daemon. EAP7 on the other hand uses the [RestEasy JAX-RS](http://resteasy.jboss.org/) implementation and due to the class loading architecture of JBOSS it is not easy to override the implementation.

For this reason a simple Java application [docker-client-commandline](https://github.com/scottysinclair/docker-client-commandline) which wraps the Java Docker Client with command-line execution is installed in the EAP7 Docker container. The test harness then executes an OS process to launch the commandline Docker Client to execute the batch job on the container.

For this to work the EAP7 docker container must have the following enviornment variable set `DOCKER_SERVER_URI` and it must point to the Docker daeomon which was used to launch the test containers.

### The test method asserts the state of the DB2 database
DBUnit will compare the DB2 database tables with the content of file [expected_result_2.xml](src/test/resources/datasets/test_case_1/expected_result_2.xml)

### On test case failure
If a test case fails then both the PostgreSQL and the DB2 databse contents are dumped to /tmp/target/test_case_1/new_application.xml and /tmp/target/test_case_1/int_tables.xml respectively.
