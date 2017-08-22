//=================================================================
//                   DatabaseProperties Class
//=================================================================
//    This class provides the collection & structure for the storage
// of database connection properties.
//
//                << DatabaseProperties.java >>
//
//=================================================================
// Copyright (C) 2016-2017 Dana M. Proctor
// Version 1.3 08/22/2017
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
// Version 1.0 Production DatabaseProperties Class.
//         1.1 Class javadoc Constructor Comment Correction.
//         1.2 Method init() db.substring() for Question Mark, Used
//             to Pass Connection URL Properties. Debug Output.
//         1.3 Method init() db String Removed v1.2 Change, But
//             Clarified for HSQL Semicolon.
//
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.datasource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
// import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.utilities.Utils;

/**
 *    The DatabaseProperties class provides the collection &amp; structure
 * for the storage of database connection properties.
 * 
 * @author Dana M. Proctor
 * @version 1.3 08/22/2017
 */

public class DatabaseProperties
{
   // Class Instances
   private ConnectionProperties connectionProperties;
   private String db, subProtocol;
   private ArrayList<String> schemas;
   private ArrayList<String> tables;
   private String[] tableTypes;
   
   private String dbType, catalog, schemaPattern, tableNamePattern;
   
   private String dbProductNameVersion;
   private String catalogSeparator;
   private String identifierQuoteString;
   private int maxColumnNameLength;
   
   // private String sqlKeyWords;
   // private String numericFunctions, stringFunctions, systemFunctions, timeDateFunctions;
   
   private boolean filter;
   
   // private static final String TABLE_CAT = "TABLE_CAT";
   private static final String TABLE_SCHEM = "TABLE_SCHEM";
   private static final String TABLE_NAME = "TABLE_NAME";
   private static final String TABLE_TYPE = "TABLE_TYPE";
   // private static final String REMARKS = "REMARKS";
   // private static final String TYPE_CAT = "TYPE_CAT";
   // private static final String TYPE_SCHEM = "TYPE_SCHEM";
   // private static final String TYPE_NAME = "TYPE_NAME";
   // private static final String SELF_REFERENCING_COL_NAME = "SELF_REFERENCING_COL_NAME";
   // private static final String REF_GENERATION = "REF_GENERATION";
   private static final String CONFIGURATION_FILE_NAME = "ajqvue.conf";
  
   //==============================================================
   // DatabaseProperties Constructors
   //==============================================================
   
   public DatabaseProperties(ConnectionProperties connectionProperties)
   {
      this(connectionProperties, true);
   }
   
   public DatabaseProperties(ConnectionProperties connectionProperties, boolean filter)
   {
      this.connectionProperties = connectionProperties;
      this.filter = filter;
      
      db = connectionProperties.getProperty(ConnectionProperties.DB);
      subProtocol = connectionProperties.getProperty(ConnectionProperties.SUBPROTOCOL);
      
      schemas = new ArrayList<String>();
      tables = new ArrayList<String>();
   }
   
   public void init(Connection dbConnection) throws SQLException
   {
      // Method Instances
      DatabaseMetaData dbMetaData;
      ResultSet db_resultSet;
      
      //======================================================
      // Collect the appropriate default database information.
      
      // HSQL database uses a semicolon to add properties
      // so remove.
      
      if (subProtocol.indexOf(ConnectionManager.HSQL) != -1
          && db.indexOf(";") != -1)
         db = db.substring(0, db.indexOf(";"));
      
      if (Ajqvue.getDebug())
         System.out.println("DatabaseProperties init() db: " + db);
      
      // HSQL
      if (subProtocol.indexOf(ConnectionManager.HSQL) != -1)
      {
         catalog = null;
         schemaPattern = "%";
         tableNamePattern = "%";
         dbType = ConnectionManager.HSQL;
         //db_resultSet = dbMetaData.getTables(null, "%", "%", tableTypes);
      }
      // Oracle
      else if (subProtocol.indexOf(ConnectionManager.ORACLE) != -1)
      {
         catalog = db;
         schemaPattern = "%";
         tableNamePattern = "%";
         dbType = ConnectionManager.ORACLE;
         //db_resultSet = dbMetaData.getTables(db, "%", "%", tableTypes);
      }
      // MySQL, MariaDB, & PostgreSQL
      else if (subProtocol.equals(ConnectionManager.MYSQL)
               || subProtocol.equals(ConnectionManager.MARIADB)
               || subProtocol.equals(ConnectionManager.POSTGRESQL))
      {
         catalog = db;
         schemaPattern = "";
         tableNamePattern = "%";
         if (subProtocol.equals(ConnectionManager.MYSQL))
            dbType = ConnectionManager.MYSQL;
         else if (subProtocol.equals(ConnectionManager.MARIADB))
            dbType = ConnectionManager.MARIADB;
         else
            dbType = ConnectionManager.POSTGRESQL;
         //db_resultSet = dbMetaData.getTables(db, "", "%", tableTypes);
      }
      // SQLite
      else if (subProtocol.equals(ConnectionManager.SQLITE))
      {
         catalog = db;
         schemaPattern = null;
         tableNamePattern = null;
         dbType = ConnectionManager.SQLITE;
         //db_resultSet = dbMetaData.getTables(db, null, null, tableTypes);
         
      }
      // Derby
      else if (subProtocol.equals(ConnectionManager.DERBY))
      {
         catalog = db;
         schemaPattern = null;
         tableNamePattern = "%";
         dbType = ConnectionManager.DERBY;
         //db_resultSet = dbMetaData.getTables(db, null, "%", tableTypes);
      }
      // H2
      else if (subProtocol.equals(ConnectionManager.H2))
      {
         catalog = null;
         schemaPattern = null;
         tableNamePattern = "%";
         dbType = ConnectionManager.H2;
         // db_resultSet = dbMetaData.getTables(null, null, "%", tableTypes);
      }
      // MSSQL
      else if (subProtocol.equals(ConnectionManager.MSSQL))
      {
         catalog = db;
         schemaPattern = null;
         tableNamePattern = "%";
         dbType = ConnectionManager.MSSQL;
         // db_resultSet = dbMetaData.getTables(db, null, "%", tableTypes);
      }
      // Unknown
      else
      {
         catalog = null;
         schemaPattern = null;
         tableNamePattern = null;
         dbType = ConnectionManager.OTHERDB;
         //db_resultSet = dbMetaData.getTables(null, null, null, tableTypes);
      }
      
      if (db.toLowerCase(Locale.ENGLISH).equals("null"))
         catalog = null;
      
      //======================================================
      // Collect the database meta data.
      
      try
      {
         dbMetaData = dbConnection.getMetaData();

         // =======================
         // Database Product Name & Version
         
         if (!dbMetaData.getDatabaseProductName().isEmpty())
            dbProductNameVersion = dbMetaData.getDatabaseProductName() + " ";
         else
         {
            if (subProtocol.equals(ConnectionManager.MYSQL))
               dbProductNameVersion = "MySQL ";
            else if (subProtocol.equals(ConnectionManager.MARIADB))
               dbProductNameVersion = "MarianDB ";
            else if (subProtocol.equals(ConnectionManager.POSTGRESQL))
               dbProductNameVersion = "PostgreSQL ";
            else if (subProtocol.indexOf(ConnectionManager.HSQL) != -1)
               dbProductNameVersion = "HSQL ";
            else if (subProtocol.indexOf(ConnectionManager.ORACLE) != -1)
               dbProductNameVersion = "Oracle ";
            else if (subProtocol.equals(ConnectionManager.SQLITE))
               dbProductNameVersion = "SQLite ";
            else if (subProtocol.equals(ConnectionManager.MSACCESS))
               dbProductNameVersion = "MS Access ";
            else if (subProtocol.equals(ConnectionManager.MSSQL))
               dbProductNameVersion = "Microsoft SQL Server ";
            else if (subProtocol.equals(ConnectionManager.DERBY))
               dbProductNameVersion = "Apache Derby ";
            else if (subProtocol.equals(ConnectionManager.H2))
               dbProductNameVersion = "H2 ";
            else
               dbProductNameVersion = "Unknown Data Source ";
         }
         if (!dbMetaData.getDatabaseProductVersion().isEmpty())
            dbProductNameVersion += dbMetaData.getDatabaseProductVersion();
         
         // Lots of debug info for gathering database information during
         // testing. Some of these will fail, throw exceptions/null pointers
         // for some databases.
         
         // =======================
         // Catalogs
         /*
         db_resultSet = dbMetaData.getCatalogs();
         while (db_resultSet.next())
            System.out.println("Catalogs: " + db_resultSet.getString(TABLE_CAT));
         */

         // =======================
         // Schemas
         /*
         db_resultSet = dbMetaData.getSchemas();
         while (db_resultSet.next())
            System.out.println("Table Scheme: " + db_resultSet.getString(1));
         */
         
         // =======================
         // Catalog Separator
         
         catalogSeparator = dbMetaData.getCatalogSeparator();
         if (catalogSeparator == null || catalogSeparator.equals(""))
            catalogSeparator = ".";
         // System.out.println("Catalog Separator: " + catalogSeparator);

         // =======================
         // SQL Key Words
         /*
         sqlKeyWords = dbMetaData.getSQLKeywords();
         System.out.println("SQL key Words:\n" + sqlKeyWords);
         */
         
         // =======================
         // SQL Identifier
         
         identifierQuoteString = dbMetaData.getIdentifierQuoteString();
         if (identifierQuoteString == null || identifierQuoteString.equals(" "))
            identifierQuoteString = "";
         // System.out.println("Identifier Quote String: " + identifierQuoteString);
          
         // =======================
         // Database Functions
         /*
         numericFunctions = dbMetaData.getNumericFunctions();
         System.out.println("Numeric Functions:\n" + numericFunctions);
         
         stringFunctions = dbMetaData.getStringFunctions();
         System.out.println("String Functions:\n" + stringFunctions);
         
         systemFunctions = dbMetaData.getSystemFunctions();
         System.out.println("System Functions:\n" + systemFunctions);
         
         timeDateFunctions = dbMetaData.getTimeDateFunctions();
         System.out.println("Time/Date Functions:\n" + timeDateFunctions);
         */
         
         // =======================
         // Max Column Name Length
         
         maxColumnNameLength = dbMetaData.getMaxColumnNameLength();
         // System.out.println("Max Column Name Length: " + maxColumnNameLength);
         
         // ========================
         // Table Types
         
         int i = 0;
         db_resultSet = dbMetaData.getTableTypes();
         while (db_resultSet.next())
            i++;

         tableTypes = new String[i];

         i = 0;
         db_resultSet = dbMetaData.getTableTypes();
         while (db_resultSet.next())
         {
            tableTypes[i] = db_resultSet.getString(TABLE_TYPE);
            // System.out.println("Table Types: " + tableTypes[i]);
            i++;
         }
         
         // ========================
         // Type Info
         /*
         db_resultSet = dbMetaData.getTypeInfo();
         while (db_resultSet.next())
         {
            System.out.println(db_resultSet.getString("TYPE_NAME") + ":" +
                               db_resultSet.getString("DATA_TYPE"));
         }
         */
         
         // Clean up.
         db_resultSet.close();  
      }
      catch (SQLException e)
      {
         throw new SQLException("DatabaseProperties init() " + e);
      }  
   }
   
   //==============================================================
   // Class method that provides the ability to load/reload the
   // database schemas & tables.
   //==============================================================
   
   public void loadDBTables(Connection dbConnection) throws SQLException
   {
      // Method Instances
      DatabaseMetaData dbMetaData;
      ResultSet db_resultSet;
      HashSet<String> oracleSystemSchemaHash;
      String tableSchem, tableName, tableType;
      // String tableCat, remarks, typeCat, typeSchem, typeName, selfReferencingColName, refGeneration;
      String grantee, user;
      
      try
      {
         dbMetaData = dbConnection.getMetaData();
         
         // ============================
         // Obtain the databases tables.
         
         //********************************************************
         // THIS IS WHERE EACH DATABASE'S TABLES/VIEWS ARE OBTAINED.
         // EACH DATABASE WILL NEED TO BE TESTED HERE TO PROPERLY
         // OBTAIN THE PROPER INPUT FOR the dbMetaData.getTables()
         // ARGUMENTS TO GET THINGS TO WORK.
         // *******************************************************
         
         // System.out.println("'" + catalog + "' '" + schemaPattern + "' '" + tableNamePattern + "'");
         db_resultSet = dbMetaData.getTables(catalog, schemaPattern, tableNamePattern, tableTypes);
         
         // Setup some Oracle system exclusion schema.
         oracleSystemSchemaHash = new HashSet <String>();
         String[] oracleSystemSchemas = {"CTXSYS", "DBSNMP", "DSSYS", "MDSYS",
                                         "ODM", "ODM_MTR", "OLAPSYS", "ORDPLUGINS",
                                         "ORDSYS", "OUTLN", "PERFSTAT", "REPADMIN",
                                         "SYS", "SYSTEM", "TRACESVR", "TSMSYS",
                                         "WKPROXY", "WKSYS", "WMSYS", "XDB"};

         for (int j = 0; j < oracleSystemSchemas.length; j++)
            oracleSystemSchemaHash.add(oracleSystemSchemas[j]);

         // This is where you can modifiy Ajqvue to obtain all the
         // available tables you want. Uncomment the System.out below
         // and run to see what is available.
         
         // Clear the tables vector and load it with the databases
         // tables.
         schemas.clear();
         tables.clear();
         
         /*
         System.out.print("Valid Table Columns: ");
         ResultSetMetaData rsmd = db_resultSet.getMetaData();
         
         for (int i = 1; i <= rsmd.getColumnCount(); i++)
            System.out.print(rsmd.getColumnName(i) + " ");
         System.out.println();
         */
         
         while (db_resultSet.next())
         {
            // tableCat = db_resultSet.getString(TABLE_CAT);
            tableSchem = db_resultSet.getString(TABLE_SCHEM);
            tableName = db_resultSet.getString(TABLE_NAME);
            tableType = db_resultSet.getString(TABLE_TYPE);
            // remarks = db_resultSet.getString(REMARKS);
            // typeCat = db_resultSet.getString(TYPE_CAT);
            // typeSchem = db_resultSet.getString(TYPE_SCHEM);
            // typeName = db_resultSet.getString(TYPE_NAME);
            // selfReferencingColName = db_resultSet.getString(SELF_REFERENCING_COL_NAME);
            // refGeneration = db_resultSet.getString(REF_GENERATION);
            
            // All information, could be to much.
            // System.out.println("Table CAT: " + tableCat
            //                     + " Table Schem: " + tableSchem
            //                     + " Table Name: " + tableName
            //                     + " Table Type: " + tableType
            //                     + " Remarks: " + remarks
            //                     + " Type Cat: " + typeCat
            //                     + " Type Schem: " + typeSchem
            //                     + " Type Name: " + typeName
            //                     + " Self Referencing Col Name: " + selfReferencingColName
            //                     + " refGeneration: " + refGeneration);

            // Filter, only TABLEs & VIEWs allowed in Ajqvue
            // application.
            
            if (tableType != null && !(tableType.indexOf("INDEX") != -1)
                && !(tableType.indexOf("SEQUENCE") != -1) && !(tableType.indexOf("SYNONYM") != -1)
                && (tableType.equals("TABLE") || tableType.equals("BASE TABLE")
                    || tableType.equals("VIEW") || !filter))
            {
               // Filter some more for Oracle & MSSQL.
               if ((subProtocol.indexOf(ConnectionManager.ORACLE) != -1 && filter)
                     && (oracleSystemSchemaHash.contains(tableSchem) || tableSchem.indexOf("FLOWS") != -1
                         || tableSchem.indexOf("APEX") != -1 || tableName.indexOf("BIN$") != -1))
                  continue;
               
               if ((subProtocol.indexOf(ConnectionManager.MSSQL) != -1 && filter)
                     && (tableSchem.toUpperCase(Locale.ENGLISH).indexOf("SYS") != -1
                         || tableSchem.toUpperCase(Locale.ENGLISH).indexOf("INFORMATION_SCHEMA") != -1))
                  continue;
               
               // Abreviated and filtered information.
               // System.out.println(tableType + " " + tableSchem + "." + tableName);

               if (tableSchem != null && !tableSchem.equals(""))
               {
                  tables.add(tableSchem + catalogSeparator + tableName);
               }
               else
                  tables.add(tableName);
            }
         }

         // ************************************************************
         // PostgreSQL databases may have schemas that limit access to
         // tables by users. So make a check and remove tables that are
         // not accessable by the user.
         
         if (subProtocol.equals(ConnectionManager.POSTGRESQL))
         {
            // This is temporary fix for ACL SQLException Thrown When
            // Using jre7. This is a bug in pgjdbc or server. A space
            // essentially returns no results so this is not used.
            
            //db_resultSet = dbMetaData.getTablePrivileges(db, "", "%");
            db_resultSet = dbMetaData.getTablePrivileges(db, " ", "%");
             
            while (db_resultSet.next())
            {
               tableName = db_resultSet.getString(TABLE_NAME);
               
               if (tables.contains(tableName))
               {
                  grantee = db_resultSet.getString("GRANTEE");
                  user = connectionProperties.getProperty(ConnectionProperties.USER);
                  
                  if (Ajqvue.getDebug())
                     System.out.println("Unauthorized Table Access: " + tableName + " : "
                        + grantee + " : " + user);

                  if (tables.contains(tableName) && !grantee.equals(user))
                     tables.remove(tableName);
               }
            }
         }
         
         // ============================
         // Obtain the databases schemas.
         
         Iterator<String> tablesIterator = tables.iterator();
         
         while (tablesIterator.hasNext())
         {
            tableName = tablesIterator.next();
            
            if (tableName.indexOf(".") != -1)
            {
               String schemasName = tableName.substring(0, tableName.indexOf("."));
               
               if (!schemas.contains(schemasName))
               {
                  schemas.add(schemasName);
                  // System.out.println(schemasName);
               }
            }
         }
         
         db_resultSet.close();
      }
      catch (SQLException e)
      {
         throw new SQLException("DatabaseProperties loadDBTables() " + e);
      }
   }
   
   //==============================================================
   // Class method that attempts to override the default table
   // loading parameters, with a configuration file.
   //==============================================================

   public void overideDefaults()
   {
      // Method Instances
      String configFileString;
      File configurationFile;
      String currentLine;
      
      FileReader fileReader;
      BufferedReader bufferedReader;
      
      // Create file name for retrieval.
      configFileString = Utils.getAjqvueConfDirectory()
                         + Utils.getFileSeparator() + CONFIGURATION_FILE_NAME;
      fileReader = null;
      bufferedReader = null;
      
      try
      {
         // Check to see if file exists.
         configurationFile = new File(configFileString);
         try
         { 
            if (!configurationFile.exists())
               return;
         }
         catch (SecurityException e)
         {
            //System.out.println("SecurityException " + e);
            return;
         }
         
         // Looks good so create reader and buffer to read
         // in the lines from the file.
         fileReader = new FileReader(configFileString);
         bufferedReader = new BufferedReader(fileReader);
            
         while ((currentLine = bufferedReader.readLine()) != null)
         {
            currentLine = currentLine.trim();
            
            if (!currentLine.startsWith("#"))
            {
               // Filter Parameter
               if (currentLine.toLowerCase(Locale.ENGLISH).indexOf("filter") != -1)
               {
                  if (currentLine.toLowerCase(Locale.ENGLISH).indexOf("on") != -1)
                     filter = true;
                  else if (currentLine.toLowerCase(Locale.ENGLISH).indexOf("off") != -1)
                     filter = false;
                  
                  // System.out.println("filter=" + filter);
               }
               
               if (currentLine.toLowerCase(Locale.ENGLISH).indexOf(dbType) != -1)
               {
                  // System.out.println(currentLine);
                  
                  // schemaPattern Parameter
                  if (currentLine.toLowerCase(Locale.ENGLISH).indexOf("schemapattern") != -1)
                  {
                     if (currentLine.indexOf("=") != -1)
                     {
                        schemaPattern = currentLine.substring(currentLine.indexOf("=") + 1).trim();
                        if (schemaPattern.equals("null"))
                           schemaPattern = null;
                     }
                  }
                  
                  // tableNamePattern Parameter
                  if (currentLine.toLowerCase(Locale.ENGLISH).indexOf("tablenamepattern") != -1)
                  {
                     if (currentLine.indexOf("=") != -1)
                     {
                        tableNamePattern = currentLine.substring(currentLine.indexOf("=") + 1).trim();
                        if (tableNamePattern.equals("null"))
                           tableNamePattern = null;
                     }
                  }
                  
                  // tableTypes Parameter
                  if (currentLine.toLowerCase(Locale.ENGLISH).indexOf("types") != -1)
                  {
                     if (currentLine.indexOf("=") != -1)
                     {
                        currentLine = currentLine.substring(currentLine.indexOf("=") + 1).trim();
                        if (currentLine.equals("null"))
                           tableTypes = null;
                        else
                           tableTypes = currentLine.split(",");
                     }
                  }
               }
            }
         }
      }
      catch (IOException ioe) 
      {
         if (Ajqvue.getDebug())
            System.out.println("DatabaseProperties overideDefaults() File I/O Problem. " + ioe);
      }
      finally
      {
         try
         {
            if (bufferedReader != null)
               bufferedReader.close();
         }
         catch (IOException ioe)
         {
            if (Ajqvue.getDebug())
               System.out.println("DatabaseProperties overideDefaults() Failed to Close BufferedReader. "
                                  + ioe);
         }
         finally
         {
            try
            {
               if (fileReader != null)
                  fileReader.close();
            }
            catch (IOException ioe)
            {
               if (Ajqvue.getDebug())
                  System.out.println("DatabaseProperties overideDefaults() Failed to Close FileReader. "
                                     + ioe);
            }
         }
      }
   }
   
   //==============================================================
   // Class method to return the schemas pattern that will derive
   // no restriction on tables collect with a DatabaseMetaData
   // getTables().
   //==============================================================
   
   public String getAllSchemasPattern()
   {
      String dataSourceType;
      
      dataSourceType = getDataSourceType();
      
      if (dataSourceType.equals(ConnectionManager.HSQL)
          || dataSourceType.equals(ConnectionManager.ORACLE))
         return "%";
      else if (dataSourceType.equals(ConnectionManager.MYSQL)
               || dataSourceType.equals(ConnectionManager.MARIADB)
               || dataSourceType.equals(ConnectionManager.POSTGRESQL))
         return "";
      else
         return null;
   }
   
   //==============================================================
   // Class method to get the current database catalog separator.
   //==============================================================

   public String getCatalogSeparator()
   {
      return catalogSeparator;
   }
   
   //==============================================================
   // Class method to get the current data source type given by a
   // ConnectionProperties & DatabaseProperties Classes.
   //==============================================================

   public String getDataSourceType()
   {  
      if (subProtocol.equals(ConnectionManager.MYSQL))
         return ConnectionManager.MYSQL;
      else if (subProtocol.equals(ConnectionManager.MARIADB))
         return ConnectionManager.MARIADB;
      else if (subProtocol.equals(ConnectionManager.POSTGRESQL))
         return ConnectionManager.POSTGRESQL;
      else if (subProtocol.indexOf(ConnectionManager.HSQL) != -1)
      {
         if (dbProductNameVersion.indexOf(" 2.") != -1)
            return ConnectionManager.HSQL2;
         else
            return ConnectionManager.HSQL;
      }
      else if (subProtocol.indexOf(ConnectionManager.ORACLE) != -1)
         return ConnectionManager.ORACLE;
      else if (subProtocol.equals(ConnectionManager.SQLITE))
         return ConnectionManager.SQLITE;
      else if (subProtocol.equals(ConnectionManager.MSACCESS))
         return ConnectionManager.MSACCESS;
      else if (subProtocol.equals(ConnectionManager.MSSQL))
         return ConnectionManager.MSSQL;
      else if (subProtocol.equals(ConnectionManager.DERBY))
         return ConnectionManager.DERBY;
      else if (subProtocol.equals(ConnectionManager.H2))
         return ConnectionManager.H2;
      else
         return ConnectionManager.OTHERDB; 
   }
   
   //==============================================================
   // Class method to return the current database product name &
   // version.
   //==============================================================

   public String getDBProductName_And_Version()
   {
      return dbProductNameVersion;
   }
   
   //==============================================================
   // Class method to return the current identifier quote string
   // that is used by the database.
   //==============================================================

   public String getIdentifierQuoteString()
   {
      return identifierQuoteString;
   }
   
   //==============================================================
   // Class method to return the max column name length that is
   // used by the database.
   //==============================================================

   public int getMaxColumnNameLength()
   {
      return maxColumnNameLength;
   }
   
   //==============================================================
   // Class method to return a copy of the available database
   // schemas names.
   //==============================================================

   public ArrayList<String> getSchemas()
   {
      ArrayList<String> schemasList = new ArrayList <String>();
      Iterator<String> schemasIterator = schemas.iterator();
      
      while (schemasIterator.hasNext())
         schemasList.add(schemasIterator.next());
      
      return schemasList;
   }
   
   //==============================================================
   // Class method to return a copy of the default database table
   // names.
   //==============================================================

   public ArrayList<String> getTableNames()
   {
      ArrayList<String> tablesList = new ArrayList <String>();
      Iterator<String> tablesIterator = tables.iterator();
      
      while (tablesIterator.hasNext())
         tablesList.add(tablesIterator.next());
      
      return tablesList;
   }
   
   //==============================================================
   // Class method to set the schemaPattern.
   //==============================================================
   
   public void setSchemaPattern(String pattern)
   {
      schemaPattern = pattern;
   }
}
