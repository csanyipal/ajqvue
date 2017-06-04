//=================================================================
//                   ConnectionInstance
//=================================================================
//    This class provides a generic instance to manage connections
// to a distinct set of databases.
//
//               << ConnectionInstance.java >>
//
//=================================================================
// Copyright (C) 2016-2017 Dana M. Proctor
// Version 1.1 02/11/2017
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version
// 2 of the License, or (at your option) any later version. This
// program is distributed in the hope that it will be useful, 
// but WITHOUT ANY WARRANTY; without even the implied warranty
// of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
// the GNU General Public License for more details. You should
// have received a copy of the GNU General Public License along
// with this program; if not, write to the Free Software Foundation,
// Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
// (http://opensource.org)
//
//=================================================================
// Revision History
// Changes to the code should be documented here and reflected
// in the present version number. Author information should
// also be included with the original copyright author.
//=================================================================
// Version 1.0 09/17/2016 Initial ConnectionInstance Class.
//         1.1 02/11/2017 Made Default Derby, H2, SQLite, & HSQL2 Memory Connection Instances
//                        public Along With PROTOCOL. Changed Class Instance HOST to LOCALHOST
//                        & Also Made public.
//        
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;

import javax.sql.RowSet;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.FilteredRowSet;
import javax.sql.rowset.WebRowSet;
import javax.swing.JOptionPane;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.datasource.ConnectionManager;
import com.dandymadeproductions.ajqvue.datasource.ConnectionProperties;
import com.dandymadeproductions.ajqvue.datasource.DatabaseProperties;
import com.dandymadeproductions.ajqvue.utilities.Utils;
import com.sun.rowset.CachedRowSetImpl;
import com.sun.rowset.FilteredRowSetImpl;
import com.sun.rowset.WebRowSetImpl;

/**
 *    The ConnectionInstance class provides a generic instance to manage
 * connections to a distinct set of databases.
 * 
 * @author Dana M. Proctor
 * @version 1.1 02/11/2017
 */

public class ConnectionInstance
{
   // Class Instances.
   private Connection memoryConnection;
   private ConnectionProperties connectionProperties;
   private DatabaseProperties databaseProperties;

   private boolean connectionInitialized, debug;
   
   public static final String MYSQL = ConnectionManager.MYSQL;
   public static final String MARIADB = ConnectionManager.MARIADB;
   public static final String POSTGRESQL = ConnectionManager.POSTGRESQL;
   public static final String HSQL = ConnectionManager.HSQL;
   public static final String HSQL2 = ConnectionManager.HSQL2;
   public static final String ORACLE = ConnectionManager.ORACLE;
   public static final String SQLITE = ConnectionManager.SQLITE;
   public static final String MSACCESS = ConnectionManager.MSACCESS;
   public static final String MSSQL = ConnectionManager.MSSQL;
   public static final String DERBY = ConnectionManager.DERBY;
   public static final String H2 = ConnectionManager.H2;
   public static final String OTHERDB = ConnectionManager.OTHERDB;
   
   // Default Derby, H2, SQLite, & HSQL2 Memory Connection Instances.
   public static final String DERBY_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
   public static final String DERBY_SUBPROTOCOL = "derby";
   public static final String DERBY_PORT = "1527";
   public static final String DERBY_MEMORY_DB = "memory:dbname0";
   
   public static final String H2_DRIVER = "org.h2.Driver";
   public static final String H2_SUBPROTOCOL = "h2";
   public static final String H2_PORT = "9092";
   public static final String H2_MEMORY_DB = "mem:dbname1";
   
   public static final String SQLITE_DRIVER = "org.sqlite.JDBC";
   public static final String SQLITE_SUBPROTOCOL = "sqlite";
   public static final String SQLITE_PORT = "0000";
   public static final String SQLITE_MEMORY_DB = ":memory:";
   
   public static final String HSQL2_DRIVER = "org.hsqldb.jdbcDriver";
   public static final String HSQL2_SUBPROTOCOL = "hsqldb:hsql";
   public static final String HSQL2_PORT = "9001";
   public static final String HSQL2_MEMORY_DB = "mem:dbname2";
   
   public static final String PROTOCOL = "jdbc";
   public static final String LOCALHOST = "localhost";
   private static final String USER = "sa";
   private static final String PASSWORD = " ";
   private static final String SSH = "false";
   
   //==============================================================
   // ConnectionInstance Constructors
   //
   // *** Defaults HSQL2 Memory, Use the 2nd, 3rd, or 4th constructors
   // to specifiy a supported memory connection.
   //
   // Use the full ConnectionInstance(ConnectionProperties, boolean,
   // boolean) to specify a particular database connection.
   //
   //==============================================================
   
   public ConnectionInstance()
   {
      this(HSQL2, Ajqvue.getDebug(), true);
   }
   
   public ConnectionInstance(String dataSourceType)
   {
      this(dataSourceType, Ajqvue.getDebug(), true);
   }
   
   public ConnectionInstance(String dataSourceType, boolean filter)
   {
      this(dataSourceType, Ajqvue.getDebug(), filter);
   }
   
   public ConnectionInstance(String dataSourceType, boolean debug, boolean filter)
   {
      this.debug = debug;
      
      createDefaultMemoryConnectionProperties(dataSourceType);
      databaseProperties = new DatabaseProperties(connectionProperties, filter);
      connectionInitialized = initConnection(filter); 
   }
   
   public ConnectionInstance(ConnectionProperties connectionProperties, boolean debug, boolean filter)
   {
      this.debug = debug;
      this.connectionProperties = connectionProperties;
      
      if (connectionProperties.getConnectionURLString() == null)
         connectionProperties.setConnectionURLString(
            ConnectionManager.createConnectionURLString(connectionProperties));
      
      databaseProperties = new DatabaseProperties(connectionProperties, filter);
      connectionInitialized = initConnection(filter); 
   }
   
   //==============================================================
   // Class method to store a default memory setup connection
   // properties for use with a Derby, H2 or default HSQL2 database.
   //==============================================================
   
   private void createDefaultMemoryConnectionProperties(String dataSourceType)
   {
      // Method Instances.
      String driver, subProtocol, port, db;
      
      connectionProperties = new ConnectionProperties();
      
      if (dataSourceType.equals(DERBY))
      {
         driver = DERBY_DRIVER;
         subProtocol = DERBY_SUBPROTOCOL;
         port = DERBY_PORT;
         db = DERBY_MEMORY_DB;
      }
      else if (dataSourceType.equals(H2))
      {
         driver = H2_DRIVER;
         subProtocol = H2_SUBPROTOCOL;
         port = H2_PORT;
         db = H2_MEMORY_DB;
      }
      else if (dataSourceType.equals(SQLITE))
      {
         driver = SQLITE_DRIVER;
         subProtocol = SQLITE_SUBPROTOCOL;
         port = SQLITE_PORT;
         db = SQLITE_MEMORY_DB;
      }
      else
      {
         driver = HSQL2_DRIVER;
         subProtocol = HSQL2_SUBPROTOCOL;
         port = HSQL2_PORT;
         db = HSQL2_MEMORY_DB;
      }
      
      connectionProperties.setProperty(ConnectionProperties.DRIVER, driver);
      connectionProperties.setProperty(ConnectionProperties.PROTOCOL, PROTOCOL);
      connectionProperties.setProperty(ConnectionProperties.SUBPROTOCOL, subProtocol);
      connectionProperties.setProperty(ConnectionProperties.HOST, LOCALHOST);
      connectionProperties.setProperty(ConnectionProperties.PORT, port);
      connectionProperties.setProperty(ConnectionProperties.DB, db);
      connectionProperties.setProperty(ConnectionProperties.USER, USER);
      connectionProperties.setProperty(ConnectionProperties.PASSWORD, PASSWORD);
      connectionProperties.setProperty(ConnectionProperties.SSH, SSH);
      
      connectionProperties.setConnectionURLString(
         ConnectionManager.createConnectionURLString(connectionProperties));
   }
   
   //==============================================================
   // Class method to initailize the connection to insure proper
   // parameters and driver. The ConnectionProperties instance must
   // properly be setup in order for this to function properly.
   // This will be done in basic default constructors. If you are
   // passing this instance make sure the connectionURLString has
   // been set.
   //==============================================================
   
   private boolean initConnection(boolean filter)
   {
      // Method Instances
      String connectionURLString, subProtocol, db;
      Properties connectProperties;
      Connection dbConnection;
      
      // Load Driver.
      try
      {
         Class.forName(connectionProperties.getProperty(ConnectionProperties.DRIVER));
         if (debug)
            System.out.println("ConnectionInstance initConnection() Driver Loaded");
      }
      catch (ClassNotFoundException e)
      {
         // Alert Dialog Output.
         String exceptionString = e.getMessage();
         if (exceptionString != null && exceptionString.length() > 200)
            exceptionString = exceptionString.substring(0, 200);
         
         String javaExtDir = System.getProperty("java.ext.dirs");
         if (javaExtDir == null || javaExtDir.equals(""))
            javaExtDir = "Java JRE/lib/ext";

         String optionPaneStringErrors = "Unable to Find or Load JDBC Driver" + "\n"
                                         + "Insure the Appropriate JDBC Driver is "
                                         + "Located in the " + "\n"
                                         + javaExtDir + Utils.getFileSeparator() + " directory."
                                         + "\n"
                                         + "Exeception: " + exceptionString;
         JOptionPane.showMessageDialog(null, optionPaneStringErrors, "Alert", JOptionPane.ERROR_MESSAGE);
         return false;
      }

      // Connection Requirements.
      try
      {
         connectionURLString = connectionProperties.getConnectionURLString();
         
         if (debug)
            System.out.println("ConnectionInstance initConnection() " + connectionURLString);
         
         connectProperties = new Properties();
         connectProperties.setProperty("user",
                                       connectionProperties.getProperty(ConnectionProperties.USER));
         connectProperties.setProperty("password",
                                       connectionProperties.getPassword());
         
         dbConnection = DriverManager.getConnection(connectionURLString, connectProperties);

         databaseProperties.init(dbConnection);
         
         // Load database tables.
         databaseProperties.loadDBTables(dbConnection);

         // Must be good so close things out and create a
         // constant connection for memory database connections.
         
         subProtocol = connectionProperties.getProperty(ConnectionProperties.SUBPROTOCOL);
         db = connectionProperties.getProperty(ConnectionProperties.DB);
         
         if ((subProtocol.equals(SQLITE) && db.toLowerCase(Locale.ENGLISH).equals(":memory:"))
              || (subProtocol.indexOf(HSQL) != -1
                  && db.toLowerCase(Locale.ENGLISH).indexOf("mem:") != -1)    
              || (subProtocol.equals(DERBY)
                  && db.toLowerCase(Locale.ENGLISH).indexOf("memory:") != -1)
              || (subProtocol.equals(H2)
                  && db.toLowerCase(Locale.ENGLISH).indexOf("mem:") != -1))
         {
            setMemoryConnection(DriverManager.getConnection(connectionURLString, connectProperties));
         }
         dbConnection.close();
      }
      catch (SQLException e)
      {
         displaySQLErrors(e, "ConnectionInstance initConnection()", debug);
      }
      
      return true;
   }
   
   //==============================================================
   // Class method that provides the ability to make a valid
   // connection to the database based on the connection properties.
   // A test should be made for any class accessing this method for
   // a null return, no connection made.
   //==============================================================

   public Connection getConnection(String description)
   {
      // Method Instances.
      Properties connectProperties;
      String connectionURLString;
      String db, subProtocol;
      
      // Check for Initialization.
      if (!connectionInitialized)
         return null;
      
      // Setup.
      connectProperties = new Properties();
      
      connectionURLString = connectionProperties.getConnectionURLString();
      db = connectionProperties.getProperty(ConnectionProperties.DB);
      subProtocol = connectionProperties.getProperty(ConnectionProperties.SUBPROTOCOL);
      
      connectProperties.setProperty("user",
                                    connectionProperties.getProperty(ConnectionProperties.USER));
      connectProperties.setProperty("password",
                                    connectionProperties.getPassword());
      
      // Handle SSL
      if (subProtocol.indexOf(HSQL) != -1 || subProtocol.equals(MYSQL)
          || subProtocol.equals(POSTGRESQL))
      {
           connectProperties.setProperty("useSSL",
              connectionProperties.getProperty(ConnectionProperties.SSH));  
      }
            
      // Select and try to return an appropriate connection
      // type.
      
      try
      {
         if (debug)
            System.out.println(description + " (CI) Connection Created");
         
         // Create the appropriate connection as needed.
         
         // HSQL, SQLite, Derby, & H2 Memory Connections
         if ((memoryConnection != null)
              && (subProtocol.equals(SQLITE) && db.toLowerCase(Locale.ENGLISH).equals(":memory:"))
                  || (subProtocol.indexOf(HSQL) != -1
                      && db.toLowerCase(Locale.ENGLISH).indexOf("mem:") != -1)
                  || (subProtocol.equals(DERBY) && db.toLowerCase(Locale.ENGLISH).indexOf("memory:") != -1)
                  || (subProtocol.equals(H2) && db.toLowerCase(Locale.ENGLISH).indexOf("mem:") != -1))
            return memoryConnection;
         // All others
         else
         {
            return DriverManager.getConnection(connectionURLString, connectProperties);
         }
      }
      catch (SQLException e)
      {
         displaySQLErrors(e, "ConnectionInstance getConnection()", debug);
         return null;
      }
   }
   
   //==============================================================
   // Class method that provides the ability to close the connection
   // that was created to the database based on the initial login,
   // host, db, user and password.
   //==============================================================

   public void closeConnection(Connection dbConnection, String description)
   {
      // Method Instances.
      String db, subProtocol;
      
      // Setup.
      db = connectionProperties.getProperty(ConnectionProperties.DB);
      subProtocol = connectionProperties.getProperty(ConnectionProperties.SUBPROTOCOL);
      
      try
      {
         if (debug)
            System.out.println(description + " (CI) Connection Closed");
         
         // Close connection as needed.
         if ((memoryConnection != null)
              && (subProtocol.equals(SQLITE) && db.toLowerCase(Locale.ENGLISH).equals(":memory:"))
                  || (subProtocol.indexOf(HSQL) != -1 && db.toLowerCase(Locale.ENGLISH).indexOf("mem:") != -1)
                  || (subProtocol.equals(DERBY) && db.toLowerCase(Locale.ENGLISH).indexOf("memory:") != -1)
                  || (subProtocol.equals(H2) && db.toLowerCase(Locale.ENGLISH).indexOf("mem:") != -1))
            return;
         else
            dbConnection.close();
      }
      catch (SQLException e)
      {
         displaySQLErrors(e, "ConnectionInstance closeConnection()", debug);
      }
   }
   
   //==============================================================
   // Class method that provides the ability to attempt to shutdown
   // database activity appropriately.
   //==============================================================

   public void shutdown(String description)
   {
      closeMemoryConnection(description);
      shutdownDatabase(description);
   }
   
   //==============================================================
   // Class method that provides the ability to close the memory
   // connection upon termination of the application
   //==============================================================

   private void closeMemoryConnection(String description)
   {  
      try
      {
         // Close connection as needed.
         if (memoryConnection != null)
         {
            if (debug)
               System.out.println(description + " (CI) Memory Connection Closed");
            
            memoryConnection.close();  
         }
      }
      catch (SQLException e)
      {
         displaySQLErrors(e, "ConnectionInstance closeMemoryConnection()", debug);
      }
   }
   
   //==============================================================
   // Class method that provides the ability to shutdown Databases
   // that advocate such, file & embedded types.
   //==============================================================

   private void shutdownDatabase(String description)
   {
      // Method Instances.
      Connection dbConnection;
      String connectionURLString;
      // String driver;
      String subProtocol;
      String databaseShutdownString;
      
      // Setup.
      connectionURLString = connectionProperties.getConnectionURLString();
      // driver = connectionProperties.getProperty(ConnectionProperties.DRIVER);
      subProtocol = connectionProperties.getProperty(ConnectionProperties.SUBPROTOCOL);
      
      if (connectionURLString.indexOf(";") != -1)
         databaseShutdownString = connectionURLString.substring(0, connectionURLString.indexOf(";"));
      else
         databaseShutdownString = connectionURLString;
      
      dbConnection = null;
      
      try
      {
         // Try to shutdown Derby & HSQL database properly.
         
         /*
         Derby:
         
         Unable to get a drop or shutdown to function without the
         following error message. Even Derby examples give same error.
         
         SQLException: invalid database address: jdbc:derby:~~
         SQLState: null
         VendorError: 0
         
         if (subProtocol.equals(DERBY))
         {
            // Drop Memory Databases
            if (databaseShutdownString.toLowerCase(Locale.ENGLISH).indexOf("memory:") != -1)
            {
               if (debug)
                  System.out.println(description + " (CI) Dropping Derby Memory Database");
               
               dbConnection = DriverManager.getConnection(databaseShutdownString + ";drop=true");
               dbConnection.close();
            }
            
            // Shutdown Embedded Only
            if (driver.indexOf("EmbeddedDriver") != -1)
            {
               if (debug)
                  System.out.println(description + " (CI) Shutting Down Derby Embedded Database");
               
               dbConnection = DriverManager.getConnection("jdbc:derby:;shutdown=true");
               dbConnection.close();
            }
            return;
         }
         */
         
         if (subProtocol.indexOf(HSQL) != -1)
         {
            // Only Apply this to file databases.
            if (databaseShutdownString.toLowerCase(Locale.ENGLISH).indexOf("file:") != -1)
                // || databaseShutdownString.toLowerCase(Locale.ENGLISH).indexOf("mem:") != -1)
            {
               if (debug)
                  System.out.println(description + " (CI) Shutting Down HSQL File/Memory Database");
               
               dbConnection = DriverManager.getConnection(databaseShutdownString + ";shutdown=true");
               dbConnection.close();
            }
            return;
         }
      }
      catch (SQLException e)
      {
         if (subProtocol.equals(DERBY) && e.getSQLState() != null &&
             (e.getSQLState().equals("08006") || e.getSQLState().equals("XJ015")))
         {
            if (debug)
            {
               System.out.println("SQLException: " + e.getMessage());
               System.out.println("SQLState: " + e.getSQLState());
            }
         }
         else
         {
            if (debug)
               displaySQLErrors(e, "ConnectionInstance shutdownDatabase()", debug);
         }
      }
      finally
      {
         try
         {
            if (dbConnection != null)
               dbConnection.close();
         }
         catch (SQLException sqle)
         {
            displaySQLErrors(sqle, "finally{} ConnectionInstance shutdownDatabase()", debug);
         }
      }
   }
   
   //==============================================================
   // Class method to output to the console and a alert dialog the
   // errors that occured during a connection to the database.
   //==============================================================

   public static void displaySQLErrors(SQLException e, String classCaller, boolean debug)
   {
      String sqlExceptionString;

      // Standard Console Output.
      if (debug)
      {
         SQLException eDebug = e;
         
         System.out.println(classCaller);
         
         while (eDebug != null)
         {
            System.out.println("SQLException: " + eDebug.getMessage());
            System.out.println("SQLState: " + eDebug.getSQLState());
            System.out.println("VendorError: " + eDebug.getErrorCode());
            
            Throwable t = eDebug.getCause();
            
            while (t != null)
            {
               System.out.println("Cause: " + t);
               t = t.getCause();
            }
            
            eDebug = eDebug.getNextException();
         }
      }

      // Alert Dialog Output.
      sqlExceptionString = e.getMessage();
      if (sqlExceptionString.length() > 200)
         sqlExceptionString = e.getMessage().substring(0, 200);

      String optionPaneStringErrors = classCaller + "\n" + "SQLException: " + sqlExceptionString + "\n"
                                      + "SQLState: " + e.getSQLState() + ",  " + "VendorError: "
                                      + e.getErrorCode();
      JOptionPane.showMessageDialog(null, optionPaneStringErrors, "Alert", JOptionPane.ERROR_MESSAGE);
   }
   
   //==============================================================
   // Class method that provides the ability to load/reload the
   // database schemas & tables.
   //==============================================================
   
   public void loadDBTables(Connection dbConnection) throws SQLException
   {
      databaseProperties.loadDBTables(dbConnection);
   }
   
   //==============================================================
   // Class method to get the current database catalog separator.
   //==============================================================

   public String getCatalogSeparator()
   {
      return databaseProperties.getCatalogSeparator();
   }
   
   //==============================================================
   // Class method to get the current connection properties.
   //==============================================================

   public ConnectionProperties getConnectionProperties()
   {
      return connectionProperties;
   }
   
   //==============================================================
   // Class method to get the current data source type given by a
   // ConnectionProperties & DatabaseProperties Classes.
   //==============================================================
   
   public String getDataSourceType()
   {
      return databaseProperties.getDataSourceType();
   }
   
   //==============================================================
   // Class method to return the current database product name &
   // version.
   //==============================================================

   public String getDBProductName_And_Version()
   {
      return databaseProperties.getDBProductName_And_Version();
   }
   
   //==============================================================
   // Class method to return the current identifier quote string
   // that is used by the database.
   //==============================================================

   public String getIdentifierQuoteString()
   {
      return databaseProperties.getIdentifierQuoteString();
   }
   
   //==============================================================
   // Class method to return the max column name length that is
   // used by the database.
   //==============================================================

   public int getMaxColumnNameLength()
   {
      return databaseProperties.getMaxColumnNameLength();
   }

   //==============================================================
   // Class method to return a copy of the available database
   // schemas names.
   //==============================================================

   public ArrayList<String> getSchemas()
   {
      return databaseProperties.getSchemas();
   }
   
   //==============================================================
   // Class method to return the schemas pattern that will derive
   // no restriction on tables collect with a DatabaseMetaData
   // getTables().
   //==============================================================

   public String getAllSchemasPattern()
   {
      return databaseProperties.getAllSchemasPattern();
   }
   
   //==============================================================
   // Class method to return a copy of the default database table
   // names.
   //==============================================================

   public ArrayList<String> getTableNames()
   {
      return databaseProperties.getTableNames();
   }
   
   //==============================================================
   // Class methods to return various rowsets of the current database
   // connection.
   //==============================================================

   public CachedRowSet getCachedRowSet() throws SQLException
   {
      CachedRowSet cachedRowSet = new CachedRowSetImpl();
      setRowSet(cachedRowSet);
      return cachedRowSet;
   }
   
   public FilteredRowSet getFilteredRowSet() throws SQLException
   {
      FilteredRowSet filteredRowSet = new FilteredRowSetImpl();
      setRowSet(filteredRowSet);
      return filteredRowSet;
   }
   
   public WebRowSet getWebRowSet() throws SQLException
   {
      WebRowSet webRowSet = new WebRowSetImpl();
      setRowSet(webRowSet);
      return webRowSet;
   }
   
   private void setRowSet(RowSet rowSet) throws SQLException
   {
      rowSet.setUrl(connectionProperties.getConnectionURLString());
      rowSet.setUsername(connectionProperties.getProperty(ConnectionProperties.USER));
      rowSet.setPassword(connectionProperties.getPassword());
   }

   // ==============================================================
   // Class method to set the connection properties.
   // ==============================================================

   public void setConnectionProperties(ConnectionProperties properties)
   {
      connectionProperties = properties;
   }
   
   public void setDatabaseProperties(DatabaseProperties properties)
   {
      databaseProperties = properties;
   }
   
   //==============================================================
   // Class method to set a memory type connection.
   //==============================================================

   private void setMemoryConnection(Connection connection)
   {
      memoryConnection = connection;
   }

   // ==============================================================
   // Class method to set the schemaPattern.
   // ==============================================================

   public void setSchemaPattern(String pattern)
   {
      databaseProperties.setSchemaPattern(pattern);
   }
}
