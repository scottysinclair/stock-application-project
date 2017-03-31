# Testing POC project with Arquillian, Arquillian Cube and Arquillian Persistence
## Goals
Execute automated tests against a real project infrastructure, in this case:
1. Call business logic in Spring Services backed by hibernate/JPA
2. Use PostgreSQL as the application database.
3. Execute batch jobs which sync data between the PostgreSQL database and a DB2 database.

*Arquillian* was chosen as it allows test suites to be built and deployed on various servlet containers (including EAP7).   *Aquillian Cube* suports docker which is used to launch various containers for EAP7, PostgreSQL, DB2 and batch processing.  
*Arquillian Spring* supports using Spring inside the Arqulllian Test Suite.  
*Arquillian Persistence* provide support for using DBUnit inside the Arquillian Test Suite.  


