package com.acme.spring.hibernate.batch.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.acme.spring.hibernate.batch.integration.service.StockServiceInt;
import com.acme.spring.hibernate.domain.Stock;
import com.acme.spring.hibernate.service.StockService;

public class Application {

        private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String args[]) {
        try {
            ApplicationContext ctx = new ClassPathXmlApplicationContext("/stock-application-int-batch.xml");
            StockService stockService = ctx.getBean(StockService.class);
            StockServiceInt stockServiceInt = ctx.getBean(StockServiceInt.class);

            runIntegration(stockService, stockServiceInt);
        }
        catch(Exception x) {
        LOG.error("Integration run failed", x);
        System.exit(-1);
        }
    }

    private static void runIntegration(StockService stockService, StockServiceInt stockServiceInt) {
        for (Stock stock: stockService.getAll()) {
            stockServiceInt.integrate(stock);
        }
    }
}
