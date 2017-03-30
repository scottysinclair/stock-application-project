package com.acme.spring.hibernate;

import javax.sql.DataSource;

/**
 * Extends the DatabaseHelper for any DB2 specific requirements.
 * @author scott
 *
 */
public class Db2Helper extends DatabaseHelper {

    public Db2Helper(DataSource dataSource) {
        super(dataSource);
    }

}
