package com.acme.spring.hibernate;

import javax.sql.DataSource;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class UnifiedTestHelper extends TestWatcher {

  private PostgresqlHelper pgHelper;
  private Db2Helper db2Helper;
  private String testName;

  public UnifiedTestHelper initializePostgres(DataSource ds) {
    if (pgHelper == null)  {
      pgHelper = new PostgresqlHelper( ds );
   }
    return this;
  }

  public UnifiedTestHelper initializeDb2(DataSource ds) {
    if (db2Helper == null) {
      db2Helper = new Db2Helper( ds );
    }
    return this;
  }

  @Override
  protected void failed(Throwable e, Description description) {
    pgHelper.dumpDatabase( testName );
    db2Helper.dumpDatabase( testName );
  }

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


}
