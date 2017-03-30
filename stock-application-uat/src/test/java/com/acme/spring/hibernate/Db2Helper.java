package com.acme.spring.hibernate;

import java.io.File;

import javax.sql.DataSource;

/**
 * Extends the DatabaseHelper for any DB2 specific requirements.
 * @author scott
 *
 */
public class Db2Helper extends DatabaseHelper {

    public Db2Helper(DataSource dataSource) {
        super(dataSource, "input_dsInt.xml", "int_tables.xml");
    }

}
