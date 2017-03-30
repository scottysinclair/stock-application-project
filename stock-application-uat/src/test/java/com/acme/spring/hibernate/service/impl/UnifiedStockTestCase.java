/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.acme.spring.hibernate.service.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.spring.integration.test.annotation.SpringConfiguration;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.acme.spring.hibernate.Deployments;
import com.acme.spring.hibernate.IntegrationHelper;
import com.acme.spring.hibernate.UnifiedTestHelper;
import com.acme.spring.hibernate.domain.Stock;
import com.acme.spring.hibernate.service.StockService;

/**
 * <p>Tests the {@link com.acme.spring.hibernate.service.impl.DefaultStockService} class.</p>
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 */
@RunWith(Arquillian.class)
@SpringConfiguration("applicationContext.xml")
public class UnifiedStockTestCase  {

    /**
     * <p>Creates the test deployment.</p>
     *
     * @return the test deployment
     */
    @Deployment
    public static Archive<?> createTestArchive() {
        return Deployments.createDeployment();
    }

    @Rule
    public final UnifiedTestHelper unifiedTestHelper = new UnifiedTestHelper();

    @Autowired
    @Qualifier("dataSource")
    private DataSource ds;

    @Autowired
    @Qualifier("dataSourceInt")
    private DataSource dsInt;

    /**
     * <p>Injected {@link com.acme.spring.hibernate.service.impl.DefaultStockService}.</p>
     */
    @Autowired
    private StockService stockService;

    @PostConstruct
    public void init() {
        unifiedTestHelper.initializePostgres(ds).initializeDb2(dsInt);
    }


    /**
     * Test case: http://beitrag-confluence/VVL/testcases/testcase1
     *
     */
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


    /**
     * Test case: http://beitrag-confluence/VVL/testcases/testcase2
     *
     */
    @Test
    public void test_case_2() throws Exception  {
      /*
       * perform some business logic in the new application
       */
      Stock acme = createStock("ABC", "ABC", 999.21D, new Date());
      stockService.save(acme);

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

    /**
     * <p>Creates new stock instance</p>
     *
     * @param name   the stock name
     * @param symbol the stock symbol
     * @param value  the stock value
     * @param date   the stock date
     *
     * @return the created stock instance
     */
    private static Stock createStock(String name, String symbol, double value, Date date) {
        Stock result = new Stock();
        result.setName(name);
        result.setSymbol(symbol);
        result.setValue(new BigDecimal(value));
        result.setDate(date);
        return result;
    }

}
