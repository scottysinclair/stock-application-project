package com.acme.spring.hibernate;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Plugs in to the junit test lifecycle to prepare the databases before each test and to dump the databases to XML when a test fails.
 * @author scott
 *
 */
public class UnifiedTestWatcher extends TestWatcher {

  private static final Logger LOG = LoggerFactory.getLogger(UnifiedTestWatcher.class);

  @Autowired
  @Qualifier("dataSource")
  private DataSource ds;

  @Autowired
  @Qualifier("dataSourceInt")
  private DataSource dsInt;

  private PostgresqlHelper pgHelper;

  private Db2Helper db2Helper;

  private String testName;

  private ServicePlayer servicePlayer;

  private String excludedColumns[] = new String[0];

  @PostConstruct
  public void init() {
    pgHelper = new PostgresqlHelper(ds);
    db2Helper = new Db2Helper(dsInt);
  @Autowired
  public void setServicePlayer(ServicePlayer servicePlayer) {
    this.servicePlayer = servicePlayer;
  }

  public void setExcludedColumns(String excludedColumns[]) {
    this.excludedColumns = excludedColumns;
  }

  public void executeTestCase() throws Exception {

    String playerXml = loadPlayerXml( testName );

    servicePlayer.playTestCase(playerXml);

    /*
     * assert the state of the new application database.
     */
    assertNewTestData();

    /*
     * execute the integration job.

    IntegrationHelper.executeIntegration();

    /*
     * assert the state of the DB2 database after integration.

    assertFirstIntegration();
*/
    /*
     * execute the integration job.
     */
//    IntegrationHelper.executeIntegration();

    /*
     * assert the state of the DB2 database after integration.
     */
//    unifiedTestWatcher.assertFirstIntegration();

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

  public void assertNewTestData() throws Exception {
    pgHelper.assertTestData("asserting new application database", "/" + testName + "/expected_result_1.xml", null,
        excludedColumns);
  }

  public void assertFirstIntegration() throws Exception {
    //    db2Helper.assertTestData("asserting integration tables", "/" + testName + "/expected_result_2.xml", null, excludedColumns);
  }

  /**
   * The database is dumped when a test fails.
   */
  @Override
  protected void failed(Throwable e, Description description) {
    try {
      pgHelper.dumpDatabase( testName );
    }
    catch(Exception x) {
      LOG.error("Could not dump Postgres database", x);
    }
    try {
   //   db2Helper.dumpDatabase( testName );
    }
    catch(Exception x) {
      LOG.error("Could not dump DB2 database", x);
    }
  }

  private LineNumberReader openReaderToPlayerXml(String testName) throws FileNotFoundException {
    String filePath = "/datasets/" + testName + "/player.xml";
    InputStream in = UnifiedTestWatcher.class.getResourceAsStream( filePath );
    if (in == null) {
      throw new FileNotFoundException("Could not find the XML service player file for test case " + testName + " at '" + filePath + "'");
    }
    return new LineNumberReader(new InputStreamReader( in ));
  }

  private String loadPlayerXml(String testName)  {
    try {
      StringBuilder sb = new StringBuilder();
      try (LineNumberReader lin = openReaderToPlayerXml( testName );) {
        String line;
        while((line = lin.readLine()) != null) {
          sb.append(line);
          sb.append('\n');
        }
        return sb.toString();
      }
    }
    catch(IOException x) {
      throw new IllegalStateException("Could not load playerXml file for test case '" + testName + "'", x);
    }
  }


}
