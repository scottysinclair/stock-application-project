package com.acme.spring.hibernate;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Plugs in to the junit test lifecycle to prepare the databases before each test and to dump the databases to XML when a test fails.
 * @author scott
 *
 */
public class UnifiedTestWatcher extends TestWatcher {

  @Autowired
  @Qualifier("dataSource")
  private DataSource ds;

  @Autowired
  @Qualifier("dataSourceInt")
  private DataSource dsInt;

  private PostgresqlHelper pgHelper;

  private Db2Helper db2Helper;

  private String testName;

  @PostConstruct
  public void init() {
    pgHelper = new PostgresqlHelper(ds);
    db2Helper = new Db2Helper(dsInt);
  }

  /**
   * cleans then inserts the test data into PostgreSQL and DB2
   */
  @Override
  protected void starting(Description description) {
    try {
      this.testName = description.getMethodName();
      pgHelper.prepareDatabase( testName );
      db2Helper.prepareDatabase( testName );
    } catch (Exception x) {
      throw new RuntimeException("Failed preparing the database", x);
    }
  }

  public void assertNewTestData(String[] excludedColumns) throws Exception {
    pgHelper.assertTestData("asserting new application database", "/" + testName + "/expected_result_1.xml", null,
        excludedColumns);
  }

  public void assertFirstIntegration(String[] excludedColumns) throws Exception {
    db2Helper.assertTestData("asserting integration tables", "/" + testName + "/expected_result_2.xml", null, excludedColumns);
  }

  /**
   * The database is dumped when a test fails.
   */
  @Override
  protected void failed(Throwable e, Description description) {
    pgHelper.dumpDatabase( testName );
    db2Helper.dumpDatabase( testName );
  }

}
