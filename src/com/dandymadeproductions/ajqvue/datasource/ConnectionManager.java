//=================================================================
//                     ConnectionManager
//=================================================================
//    This class provides a central class to manage all connections
// that are used by the Ajqvue application to access the various
// databases support.
//
//                 << ConnectionManager.java >>
//
//=================================================================
// Copyright (C) 2016-2017 Dana M. Proctor
// Version 1.0 09/17/2016
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
// Version 1.0 Production ConnectionManager Class.
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

import javax.sound.sampled.Clip;
import javax.sql.RowSet;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.FilteredRowSet;
import javax.sql.rowset.WebRowSet;
import javax.swing.JOptionPane;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.utilities.Utils;
import com.sun.rowset.CachedRowSetImpl;
import com.sun.rowset.FilteredRowSetImpl;
import com.sun.rowset.WebRowSetImpl;

/**
 *    The ConnectionManager class provides a central class to manage all
 * connections that are used by the Ajqvue application to access the
 * various databases support.   
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/17/2016
 */

public class ConnectionManager
{
   // Class Instances.
   private static Connection memoryConnection;
   private static ConnectionProperties connectionProperties = new ConnectionProperties();
   private static DatabaseProperties databaseProperties = new DatabaseProperties(connectionProperties);
   
   private static Clip errorSoundClip = Utils.getAudioClip("sounds"
                                                           + Utils.getFileSeparator()
                                                           + "huh.wav");
   public static final String MYSQL = "mysql";
   public static final String MARIADB = "mariadb";
   public static final String POSTGRESQL = "postgresql";
   public static final String HSQL = "hsql";
   public static final String HSQL2 = "hsql2";
   public static final String ORACLE = "oracle";
   public static final String SQLITE = "sqlite";
   public static final String MSACCESS = "odbc";
   public static final String MSSQL = "sqlserver";
   public static final String DERBY = "derby";
   public static final String H2 = "h2";
   public static final String OTHERDB = "other";
   
   //==============================================================
   // ConnectionManager Constructor
   //
   // !Caution: Nothing to Instantiate in this class.
   //==============================================================

   public ConnectionManager()
   {
      // Constructor Instances
      
      // Nothing to see here all aspects of
      // this class are static.
   }
   
   //==============================================================
   // Class method that provides the ability to make a valid
   // connection to the database based on the initial login, host,
   // db, user and password. A test should be made for any class
   // accessing this method for a null return, no connection made.
   //==============================================================

   public static Connection getConnection(String description)
   {
      // Method Instances.
      Properties connectProperties;
      String connectionURLString;
      String db, subProtocol;
      
      // Setup.
      connectProperties = new Properties();
      
      connectionURLString = connectionProperties.getConnectionURLString();
      db = connectionProperties.getProperty(ConnectionProperties.DB);
      subProtocol = connectionProperties.getProperty(ConnectionProperties.SUBPROTOCOL);
      
      connectProperties.setProperty("user", connectionProperties.getProperty(ConnectionProperties.USER));
      connectProperties.setProperty("password", connectionProperties.getPassword());
      
      // Handle SSL
      if (subProtocol.indexOf(HSQL) != -1 || subProtocol.equals(MYSQL)
          || subProtocol.equals(POSTGRESQL))
      {
         if (connectionProperties.getProperty(ConnectionProperties.SSH).equals("true"))
            connectProperties.setProperty("useSSL", "1");
      }
            
      // Select and try to return an appropriate connection
      // type.
      
      try
      {
         if (Ajqvue.getDebug())
         {
            System.out.println(description + " (CM) Connection Created");
            // System.out.println(" (CM) Connection Properties: " + connectProperties.toString());
         }
         
         // Create the appropriate connection as needed.
         
         // HSQL, SQLite, Derby, & H2 Memory Connections
         if ((memoryConnection != null)
              && (subProtocol.equals(SQLITE) && db.toLowerCase(Locale.ENGLISH).equals(":memory:"))
                  || (subProtocol.indexOf(HSQL) != -1 && db.toLowerCase(Locale.ENGLISH).indexOf("mem:") != -1)
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
         displaySQLErrors(e, "ConnectionManager getConnection()");
         return null;
      }
   }
   
   //==============================================================
   // Class method that provides the ability to close the connection
   // that was created to the database based on the initial login,
   // host, db, user and password.
   //==============================================================

   public static void closeConnection(Connection dbConnection, String description)
   {
      // Method Instances.
      String db, subProtocol;
      
      // Setup.
      db = connectionProperties.getProperty(ConnectionProperties.DB);
      subProtocol = connectionProperties.getProperty(ConnectionProperties.SUBPROTOCOL);
      
      try
      {
         if (Ajqvue.getDebug())
            System.out.println(description + " (CM) Connection Closed");
         
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
         displaySQLErrors(e, "ConnectionManager closeConnection()");
      }
   }
   
   //==============================================================
   // Class method that provides the ability to attempt to shutdown
   // database activity appropriately.
   //==============================================================

   public static void shutdown(String description)
   {
      closeMemoryConnection(description);
      shutdownDatabase(description);
   }
   
   //==============================================================
   // Class method that provides the ability to close the memory
   // connection upon termination of the application
   //==============================================================

   private static void closeMemoryConnection(String description)
   {  
      try
      {
         // Close connection as needed.
         if (memoryConnection != null)
         {
            if (Ajqvue.getDebug())
               System.out.println(description + " (CM) Memory Connection Closed");
            
            memoryConnection.close();  
         }
      }
      catch (SQLException e)
      {
         displaySQLErrors(e, "ConnectionManager closeMemoryConnection()");
      }
   }
   
   //==============================================================
   // Class method that provides the ability to shutdown Databases
   // that advocate such, file & embedded types.
   //==============================================================

   private static void shutdownDatabase(String description)
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
               if (Ajqvue.getDebug())
                  System.out.println(description + " (CM) Dropping Derby Memory Database");
               
               dbConnection = DriverManager.getConnection(databaseShutdownString + ";drop=true");
               dbConnection.close();
            }
            
            // Shutdown Embedded Only
            if (driver.indexOf("EmbeddedDriver") != -1)
            {
               if (Ajqvue.getDebug())
                  System.out.println(description + " (CM) Shutting Down Derby Embedded Database");
               
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
               if (Ajqvue.getDebug())
                  System.out.println(description + " (CM) Shutting Down HSQL File/Memory Database");
               
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
            if (Ajqvue.getDebug())
            {
               System.out.println("SQLException: " + e.getMessage());
               System.out.println("SQLState: " + e.getSQLState());
            }
         }
         else
         {
            if (Ajqvue.getDebug())
               displaySQLErrors(e, "ConnectionManager shutdownDatabase()");
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
            displaySQLErrors(sqle, "finally{} ConnectionManager shutdownDatabase()");
         }
      }
   }
   
   //==============================================================
   // Class method to output to the console and a alert dialog the
   // errors that occured during a connection to the database.
   //==============================================================

   public static void displaySQLErrors(SQLException e, String classCaller)
   {
      String sqlExceptionString;
      
      // Generated a sound warning.
      if (errorSoundClip != null)
      {
         errorSoundClip.setFramePosition(0);
         errorSoundClip.start();
      }

      // Standard Console Output.
      if (Ajqvue.getDebug())
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
   
   public static void loadDBTables(Connection dbConnection) throws SQLException
   {
      databaseProperties.loadDBTables(dbConnection);
   }
   
   //==============================================================
   // Class method to create a connection URL string based on the
   // given connection properties.
   //==============================================================

   public static String createConnectionURLString(ConnectionProperties properties)
   {
      // Method Instances
      String connectionURLString;
      String driver, protocol, subProtocol, host, port, db;
      
      // Collect Instances
      driver = properties.getProperty(ConnectionProperties.DRIVER);
      protocol = properties.getProperty(ConnectionProperties.PROTOCOL);
      subProtocol = properties.getProperty(ConnectionProperties.SUBPROTOCOL);
      host = properties.getProperty(ConnectionProperties.HOST);
      port = properties.getProperty(ConnectionProperties.PORT);
      db = properties.getProperty(ConnectionProperties.DB);
      
      // Take into consideration various database requirements.
      connectionURLString = protocol + ":";

      // Oracle
      if (subProtocol.indexOf(ConnectionManager.ORACLE) != -1)
      {
         if (subProtocol.indexOf("thin") != -1)
            connectionURLString += subProtocol + ":@//" + host + ":" + port + "/" + db;
         else
            connectionURLString += subProtocol + ":@" + db;
      }
      // SQLite
      else if (subProtocol.equals(ConnectionManager.SQLITE))
      {
         connectionURLString += subProtocol + ":" + db.replace("\\", "/");
      }
      // HSQL Memory, File, & Resource
      else if (subProtocol.indexOf(ConnectionManager.HSQL) != -1 &&
                (db.indexOf("mem:") != -1) || db.indexOf("file:") != -1 || db.indexOf("res:") != -1)
      {
         connectionURLString += "hsqldb:" + db;
      }
      // Derby Memory
      else if (subProtocol.indexOf(ConnectionManager.DERBY) != -1 &&
               db.indexOf("memory:") != -1)
      {
         if (db.toLowerCase(Locale.ENGLISH).indexOf(";create=true") == -1)
            db += ";create=true";
         
         if (driver.indexOf("EmbeddedDriver") != -1)
            connectionURLString += subProtocol + ":" + db;
         else
            connectionURLString += subProtocol + "://" + host + ":" + port + "/" + db;
      }
      // MS Access
      else if (subProtocol.equals(ConnectionManager.MSACCESS))
      {
         connectionURLString += subProtocol + ":" + db;
      }
      // MSSQL
      else if (subProtocol.equals(ConnectionManager.MSSQL))
      {
         if (db.isEmpty())
            connectionURLString += subProtocol + "://" + host + ":" + port;
         else
            connectionURLString += subProtocol + "://" + host + ":" + port + ";databaseName=" + db;
      }
      // H2
      else if (subProtocol.equals(ConnectionManager.H2))
      {
         if (db.indexOf("tcp:") != -1)
            connectionURLString += subProtocol + ":tcp://" + host + ":" + port + "/"
                             + db.substring(db.indexOf("tcp:") + 4);
         else if (db.indexOf("ssl:") != -1)
            connectionURLString += subProtocol + ":ssl://" + host + ":" + port + "/"
                             + db.substring(db.indexOf("ssl:") + 4);
         else
            connectionURLString += subProtocol + ":" + db;
            
      }
      // MySQL, MariaDB, PostgreSQL, HSQL, & Derby
      else
      {
         // The % character is interpreted as the start of a special escaped sequence,
         // two digit hexadeciaml value. So replace passwordString characters with that
         // character with that characters hexadecimal value as sequence, %37. Java
         // API URLDecoder.
         
         if (subProtocol.indexOf(ConnectionManager.DERBY) != -1 &&
               driver.indexOf("EmbeddedDriver") != -1)
            connectionURLString += subProtocol + ":" + db;
         else
            connectionURLString += subProtocol + "://" + host + ":" + port + "/" + db;
      }
      return connectionURLString;
   }
   
   //==============================================================
   // Class method to get the current database catalog separator.
   //==============================================================

   public static String getCatalogSeparator()
   {
      return databaseProperties.getCatalogSeparator();
   }
   
   //==============================================================
   // Class method to get the current connection properties.
   //==============================================================

   public static ConnectionProperties getConnectionProperties()
   {
      return connectionProperties;
   }
   
   //==============================================================
   // Class method to get the current data source type given by a
   // ConnectionProperties & DatabaseProperties Classes.
   //==============================================================
   
   public static String getDataSourceType()
   {
      return databaseProperties.getDataSourceType();
   }
   
   //==============================================================
   // Class method to return the current database product name &
   // version.
   //==============================================================

   public static String getDBProductName_And_Version()
   {
      return databaseProperties.getDBProductName_And_Version();
   }
   
   //==============================================================
   // Class method to return the current identifier quote string
   // that is used by the database.
   //==============================================================

   public static String getIdentifierQuoteString()
   {
      return databaseProperties.getIdentifierQuoteString();
   }
   
   //==============================================================
   // Class method to return the max column name length that is
   // used by the database.
   //==============================================================

   public static int getMaxColumnNameLength()
   {
      return databaseProperties.getMaxColumnNameLength();
   }

   //==============================================================
   // Class method to return a copy of the available database
   // schemas names.
   //==============================================================

   public static ArrayList<String> getSchemas()
   {
      return databaseProperties.getSchemas();
   }
   
   //==============================================================
   // Class method to return the schemas pattern that will derive
   // no restriction on tables collect with a DatabaseMetaData
   // getTables().
   //==============================================================

   public static String getAllSchemasPattern()
   {
      return databaseProperties.getAllSchemasPattern();
   }
   
   //==============================================================
   // Class method to return a copy of the default database table
   // names.
   //==============================================================

   public static ArrayList<String> getTableNames()
   {
      return databaseProperties.getTableNames();
   }
   
   //==============================================================
   // Class methods to return various rowsets of the current database
   // connection.
   //==============================================================

   public static CachedRowSet getCachedRowSet() throws SQLException
   {
      CachedRowSet cachedRowSet = new CachedRowSetImpl();
      setRowSet(cachedRowSet);
      return cachedRowSet;
   }
   
   public static FilteredRowSet getFilteredRowSet() throws SQLException
   {
      FilteredRowSet filteredRowSet = new FilteredRowSetImpl();
      setRowSet(filteredRowSet);
      return filteredRowSet;
   }
   
   public static WebRowSet getWebRowSet() throws SQLException
   {
      WebRowSet webRowSet = new WebRowSetImpl();
      setRowSet(webRowSet);
      return webRowSet;
   }
   
   private static void setRowSet(RowSet rowSet) throws SQLException
   {
      rowSet.setUrl(connectionProperties.getConnectionURLString());
      rowSet.setUsername(connectionProperties.getProperty(ConnectionProperties.USER));
      rowSet.setPassword(connectionProperties.getPassword());
   }
   
   //==============================================================
   // Class method to set the connection properties.
   //==============================================================

   public static void setConnectionProperties(ConnectionProperties properties)
   {
      connectionProperties = properties;
   }
   
   public static void setDatabaseProperties(DatabaseProperties properties)
   {
      databaseProperties = properties;
   }
   
   //==============================================================
   // Class method to set a memory type connection.
   //==============================================================

   public static void setMemoryConnection(Connection connection)
   {
      memoryConnection = connection;
   }
   
   //==============================================================
   // Class method to set the schemaPattern.
   //==============================================================
   
   public static void setSchemaPattern(String pattern)
   {
      databaseProperties.setSchemaPattern(pattern);
   }
}
