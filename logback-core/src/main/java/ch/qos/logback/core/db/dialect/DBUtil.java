/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2009, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.core.db.dialect;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import ch.qos.logback.core.spi.ContextAwareBase;

/**
 * 
 * @author Ceki Gulcu
 * 
 */
public class DBUtil extends ContextAwareBase {
  private static final String POSTGRES_PART = "postgresql";
  private static final String MYSQL_PART = "mysql";
  private static final String ORACLE_PART = "oracle";
  // private static final String MSSQL_PART = "mssqlserver4";
  private static final String MSSQL_PART = "microsoft";
  private static final String HSQL_PART = "hsql";

  public static SQLDialectCode discoverSQLDialect(DatabaseMetaData meta) {
    SQLDialectCode dialectCode = SQLDialectCode.UNKNOWN_DIALECT;

    try {

      String dbName = meta.getDatabaseProductName().toLowerCase();

      if (dbName.indexOf(POSTGRES_PART) != -1) {
        return SQLDialectCode.POSTGRES_DIALECT;
      } else if (dbName.indexOf(MYSQL_PART) != -1) {
        return SQLDialectCode.MYSQL_DIALECT;
      } else if (dbName.indexOf(ORACLE_PART) != -1) {
        return SQLDialectCode.ORACLE_DIALECT;
      } else if (dbName.indexOf(MSSQL_PART) != -1) {
        return SQLDialectCode.MSSQL_DIALECT;
      } else if (dbName.indexOf(HSQL_PART) != -1) {
        return SQLDialectCode.HSQL_DIALECT;
      } else {
        return SQLDialectCode.UNKNOWN_DIALECT;
      }
    } catch (SQLException sqle) {
      // we can't do much here
    }

    return dialectCode;
  }

  public static SQLDialect getDialectFromCode(SQLDialectCode sqlDialectType) {
    SQLDialect sqlDialect = null;

    switch (sqlDialectType) {
    case POSTGRES_DIALECT:
      sqlDialect = new PostgreSQLDialect();

      break;
    case MYSQL_DIALECT:
      sqlDialect = new MySQLDialect();

      break;
    case ORACLE_DIALECT:
      sqlDialect = new OracleDialect();

      break;
    case MSSQL_DIALECT:
      sqlDialect = new MsSQLDialect();

      break;
    case HSQL_DIALECT:
      sqlDialect = new HSQLDBDialect();

      break;
    }
    return sqlDialect;
  }

  /**
   * This method handles cases where the
   * {@link DatabaseMetaData#supportsGetGeneratedKeys} method is missing in the
   * JDBC driver implementation.
   */
  public boolean supportsGetGeneratedKeys(DatabaseMetaData meta) {
    try {
      //
      // invoking JDK 1.4 method by reflection
      //
      return ((Boolean) DatabaseMetaData.class.getMethod(
          "supportsGetGeneratedKeys", (Class[]) null).invoke(meta,
          (Object[]) null)).booleanValue();
    } catch (Throwable e) {
      addInfo("Could not call supportsGetGeneratedKeys method. This may be recoverable");
      return false;
    }
  }

  /**
   * This method handles cases where the
   * {@link DatabaseMetaData#supportsBatchUpdates} method is missing in the JDBC
   * driver implementation.
   */
  public boolean supportsBatchUpdates(DatabaseMetaData meta) {
    try {
      return meta.supportsBatchUpdates();
    } catch (Throwable e) {
      addInfo("Missing DatabaseMetaData.supportsBatchUpdates method.");
      return false;
    }
  }
}
