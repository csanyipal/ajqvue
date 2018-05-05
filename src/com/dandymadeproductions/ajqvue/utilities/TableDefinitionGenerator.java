//=================================================================
//                  TableDefinitionGenerator
//=================================================================
//   This class provides a common focus for creating the various
// database table definitions for table structures that output
// via the SQL data export feature.
//
//              << TableDefinitionGenerator.java >>
//
//=================================================================
// Copyright (C) 2016-201 Dana M. Proctor
// Version 1.6 05/05/2018
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
// Version 1.0 Original TableDefinitionGenerator Class.
//         1.1 Corrected Import Package DataExportProperties.
//         1.2 Class Method createDerbyTableDefinition() Eliminated Redundant
//             Conditional for Character Types, Along With Correction to Not
//             Include Size for Long Varchar for Bit Data.
//         1.3 Method createOracleTableDefinition() Insured resultSet Closed
//             After Each Use.
//         1.4 Constructor Clarified Removal of Semicolon for databaseName.
//         1.5 Reverted v1.4, Multiple Databases Tag Properties on Connection
//             URL, databaseName.
//         1.6 Method createHSQLTableDefinition() Modified Handling of Binary
//             Types to Assign Size, Either Binary, Default, or 1.
//             
//-----------------------------------------------------------------
//                    danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.utilities;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.dandymadeproductions.ajqvue.Ajqvue;
import com.dandymadeproductions.ajqvue.datasource.ConnectionManager;
import com.dandymadeproductions.ajqvue.datasource.ConnectionProperties;
import com.dandymadeproductions.ajqvue.gui.panels.DBTablesPanel;
import com.dandymadeproductions.ajqvue.structures.DataExportProperties;

/**
 *    The TableDefinitionGenerator class provides a common focus
 * for creating the various database table definitions for table
 * structures that output via the SQL data export feature.
 * 
 * @author Dana Proctor
 * @version 1.6 05/05/2017
 */

public class TableDefinitionGenerator
{
   // Class Instances.
   private String databaseName, schemaTableName, schemaName, tableName;
   private Connection dbConnection;
   private String dbIdentifierQuoteString;
   private String identifierQuoteString;
   private DataExportProperties sqlDataExportOptions;
   private StringBuffer tableDefinition;

   //==============================================================
   // TableDefinitionGenerator Constructor.
   //==============================================================

   public TableDefinitionGenerator(Connection dbConnection, String table)
   {
      // Common class Instances.
      this.dbConnection = dbConnection;
      this.schemaTableName = table;
      // System.out.println(schemaTableName);
      
      databaseName = ConnectionManager.getConnectionProperties().getProperty(
         ConnectionProperties.DB);
      
      if (databaseName.indexOf(";") != -1)
         databaseName = databaseName.substring(0, databaseName.indexOf(";"));

      // Setting up required instances.
      dbIdentifierQuoteString = ConnectionManager.getIdentifierQuoteString();
      sqlDataExportOptions = DBTablesPanel.getDataExportProperties();
      identifierQuoteString = sqlDataExportOptions.getIdentifierQuoteString();
      
      if (schemaTableName.indexOf(".") != -1)
      {
         schemaName = schemaTableName.substring(0, schemaTableName.indexOf("."));
         schemaName = schemaName.replaceAll(dbIdentifierQuoteString, "");
         tableName = schemaTableName.substring(schemaTableName.indexOf(".") + 1);
         tableName = tableName.replaceAll(dbIdentifierQuoteString, "");
      }
      else
      {
         schemaName = "";
         tableName = schemaTableName.replaceAll(dbIdentifierQuoteString, "");
      }
      tableDefinition = new StringBuffer("");
   }

   //==============================================================
   // Class method for creating a given MySQL/MariaDB TABLE
   // definition.
   //==============================================================

   private void createSQLTableDefinition() throws SQLException
   {
      // Class Method Instances.
      String sqlStatementString;
      Statement sqlStatement;
      ResultSet resultSet;

      // Beginning the creation of the string description
      // of the table Structure.
      
      sqlStatement = null;
      resultSet = null;
      
      try
      {
         // Setup a connection statement.
         sqlStatement = dbConnection.createStatement();

         // MySQL does all the work here with the handy
         // SHOW SQL Command.

         sqlStatementString = "SHOW CREATE TABLE " + schemaTableName;
         resultSet = sqlStatement.executeQuery(sqlStatementString);
         
         // Drop Table Statements As Needed.
         if (DBTablesPanel.getDataExportProperties().getTableStructure())
         {
            tableDefinition.append("DROP "
                                   + resultSet.getMetaData().getColumnName(1).toUpperCase(
                                     Ajqvue.getLocale())
                                   + " IF EXISTS " + schemaTableName + ";\n");
         }
         // Create Table column
         resultSet.next();
         tableDefinition.append(resultSet.getString(2) + ";\n");
      }
      catch (SQLException e)
      {
         ConnectionManager.displaySQLErrors(e,
            "TableDefinitionGenerator createMySQLTableDefinition()");
      }
      finally
      {
         try
         {
            if (resultSet != null)
               resultSet.close();
         }
         catch (SQLException sqle)
         {
            ConnectionManager.displaySQLErrors(sqle,
               "TableDefinitionGenerator createMySQLTableDefinition()");
         }
         finally
         {
            if (sqlStatement != null)
               sqlStatement.close();
         }
      }
   }

   //==============================================================
   // Class method for creating a given PostgreSQL TABLE definition.
   //==============================================================

   private void createPostgreSQLTableDefinition() throws SQLException
   {
      // Class Method Instances.
      String tableType;
      String columnName, columnType, constraint;
      StringBuffer primaryKeys, uniqueKeys;
      HashMap<String, String> foreignKeys;
      String foreignKey;
      String referenceSchemaName, referenceTableName;
      StringBuffer referenceColumnName;
      String onDeleteRule;

      String sqlStatementString;
      Statement sqlStatement, sqlStatement2;
      ResultSet resultSet, resultSet2;

      // Beginning the creation of the string description
      // of the table Structure.
      
      sqlStatement = null;
      sqlStatement2 = null;
      
      try
      {
         // Setup a connection statement.
         sqlStatement = dbConnection.createStatement();
         sqlStatement2 = dbConnection.createStatement();

         // No easy method here for PostgreSQL have to create each
         // field name, type, defaults, etc. Also Keys. As of 2.81
         // Keys are used instead of sequences, this may need to
         // change.
         
         // Collect table type for the table.
         
         sqlStatementString = "SELECT table_type FROM information_schema.tables WHERE "
                               + "table_catalog='" + databaseName + "' AND "
                               + "table_schema='" + schemaName + "' AND "
                               + "table_name='" + tableName + "'";
         // System.out.println(sqlStatementString);
         
         resultSet = sqlStatement.executeQuery(sqlStatementString);
         
         if (resultSet.next())
         {
            if (resultSet.getString(1).equals("BASE TABLE"))
               tableType = "TABLE";
            else
               tableType = resultSet.getString(1);
         }
         else
            tableType = "TABLE";
         
         // Drop Table Statements As Needed.
         if (DBTablesPanel.getDataExportProperties().getTableStructure())
         {
            tableDefinition.append("DROP " + tableType + " IF EXISTS " + schemaTableName + ";\n");
         }
         
         // Table Creation Statement.
         if (tableType.equals("VIEW"))
         {
            sqlStatementString = "SELECT view_definition FROM information_schema.views WHERE "
                                 + "table_catalog='" + databaseName + "' AND "
                                 + "table_schema='" + schemaName + "' AND "
                                 + "table_name='" + tableName + "'";
            // System.out.println(sqlStatementString);

            resultSet = sqlStatement.executeQuery(sqlStatementString);

            if (resultSet.next())
            {
               tableDefinition.append("CREATE " + tableType + " " + schemaTableName
                                      + " AS " + resultSet.getString(1) + "\n");
            }
            resultSet.close();
            return;
         }
         // TABLE
         else
            tableDefinition.append("CREATE " + tableType + " " + schemaTableName + " (\n    ");

         // Begin by creating the individual column field definitions.
         // Column name, data type, default, and isNullable.

         // Use information_schema view.
         sqlStatementString = "SELECT table_catalog, table_name, column_name, ordinal_position, "
                              + "column_default, is_nullable, data_type, character_maximum_length, "
                              + "numeric_precision, numeric_scale, datetime_precision, udt_name FROM "
                              + "information_schema.columns " + "WHERE table_catalog='"
                              + databaseName + "' AND " + "table_schema='" + schemaName
                              + "' AND table_name='" + tableName + "' ORDER BY ordinal_position";
         // System.out.println(sqlStatementString);

         resultSet.close();
         resultSet = sqlStatement.executeQuery(sqlStatementString);

         while (resultSet.next())
         {
            columnName = resultSet.getString("column_name");
            columnType = resultSet.getString("data_type");
            
            // =============
            // Column name.

            tableDefinition.append(identifierQuoteString + columnName + identifierQuoteString + " ");

            // =============
            // Column type.

            // Integer & Sequences
            if (columnType.indexOf("int") != -1)
            {
               if (columnType.equals("integer") && resultSet.getString("column_default") != null
                   && resultSet.getString("column_default").indexOf("nextval") != -1)
                  tableDefinition.append("serial");
               else if (columnType.equals("bigint") && resultSet.getString("column_default") != null
                        && resultSet.getString("column_default").indexOf("nextval") != -1)
                  tableDefinition.append("bigserial");
               else
                  tableDefinition.append(columnType);
            }
            // Numeric Types
            else if (columnType.equals("numeric"))
            {
               if (resultSet.getString("numeric_precision") != null
                   || resultSet.getString("numeric_scale") != null)
                  tableDefinition.append(columnType + "(" + resultSet.getString("numeric_precision") + ","
                                         + resultSet.getString("numeric_scale") + ")");
               else
                  tableDefinition.append(columnType);

            }
            // Character Types
            else if (columnType.indexOf("character") != -1)
            {
               if (columnType.equals("character"))
                  tableDefinition.append("char(" + resultSet.getString("character_maximum_length") + ")");
               else
                  tableDefinition.append("varchar(" + resultSet.getString("character_maximum_length") + ")");
            }
            // Time & Timestamp
            else if (columnType.indexOf("time") != -1)
            {
               if (resultSet.getString("datetime_precision") != null)
               {
                  if (columnType.indexOf("timestamp") != -1)
                  {
                     if (columnType.indexOf("without") != -1)
                        tableDefinition.append("timestamp(" + resultSet.getString("datetime_precision")
                                               + ") without time zone");
                     else
                        tableDefinition.append("timestamp(" + resultSet.getString("datetime_precision")
                                               + ") with time zone");
                  }
                  else
                  {
                     if (columnType.indexOf("without") != -1)
                        tableDefinition.append("time(" + resultSet.getString("datetime_precision")
                                               + ") without time zone");
                     else
                        tableDefinition.append("time(" + resultSet.getString("datetime_precision")
                                               + ") with time zone");
                  }
               }
               else
                  tableDefinition.append(columnType);
            }
            // Interval
            else if (columnType.equals("interval") && resultSet.getString("datetime_precision") != null)
            {
               tableDefinition.append(columnType + "(" + resultSet.getString("datetime_precision") + ")");
            }
            // Bit
            else if (columnType.indexOf("bit") != -1
                     && resultSet.getString("character_maximum_length") != null)
            {
               tableDefinition.append(columnType + "(" + resultSet.getString("character_maximum_length")
                                      + ")");
            }
            // Arrays
            else if (columnType.equals("ARRAY"))
            {
               sqlStatementString = "SELECT format_type(atttypid, atttypmod) FROM pg_attribute WHERE "
                                    + "attrelid='" + tableName + "'::regclass " + "AND attname='"
                                    + columnName + "'";
               // System.out.println(sqlStatementString);

               resultSet2 = sqlStatement2.executeQuery(sqlStatementString);
               resultSet2.next();
               tableDefinition.append(resultSet2.getString(1));
               resultSet2.close();
            }
            // User Defined
            else if (columnType.equals("USER-DEFINED"))
            {
               if (resultSet.getString("udt_name") != null)
                  tableDefinition.append(resultSet.getString("udt_name"));
               else
                  tableDefinition.append(columnType);
            }
            else
               tableDefinition.append(columnType);

            // ==========================
            // Column Default & NOT NULL

            if (resultSet.getString("column_default") != null
                && resultSet.getString("column_default").indexOf("nextval") == -1)
            {
               String defaultString = resultSet.getString("column_default");

               if (defaultString.indexOf("::") != -1)
                  tableDefinition.append(" DEFAULT "
                                         + defaultString.substring(0, defaultString.indexOf("::")));
               else
                  tableDefinition.append(" DEFAULT " + defaultString);

               if (resultSet.getString("is_nullable").equals("NO"))
                  tableDefinition.append(" NOT NULL,\n    ");
               else
                  tableDefinition.append(",\n    ");
            }
            else
            {
               if (resultSet.getString("column_default") == null
                   && resultSet.getString("is_nullable").equals("YES"))
                  tableDefinition.append(" DEFAULT NULL,\n    ");
               else
               {
                  if (resultSet.getString("is_nullable").equals("NO"))
                     tableDefinition.append(" NOT NULL,\n    ");
                  else
                     tableDefinition.append(",\n    ");
               }
            }
         }

         // Create the keys for the table. Again use the
         // information_schema.

         sqlStatementString = "SELECT table_catalog, table_name, column_name, constraint_name FROM "
                              + "information_schema.key_column_usage " + "WHERE table_catalog='"
                              + databaseName + "' AND " + "table_schema='" + schemaName
                              + "' AND table_name='" + tableName + "'";
         // System.out.println(sqlStatementString);

         resultSet = sqlStatement.executeQuery(sqlStatementString);

         primaryKeys = new StringBuffer();
         uniqueKeys = new StringBuffer();
         foreignKeys = new HashMap<String, String> ();
         foreignKey = "";
         referenceSchemaName = "";
         referenceTableName = "";
         referenceColumnName = new StringBuffer();
         constraint = "";
         onDeleteRule = "";

         while (resultSet.next())
         {
            constraint = resultSet.getString("constraint_name");
            columnName = resultSet.getString("column_name");

            // Unique Keys
            if (constraint.indexOf("_key") != -1)
               uniqueKeys.append(identifierQuoteString + columnName + identifierQuoteString + ",");

            // Primary Keys
            else if (constraint.indexOf("pkey") != -1)
               primaryKeys.append(identifierQuoteString + columnName + identifierQuoteString + ",");
            
            // Foreign Keys. There should be only one, NOT.
            else if (constraint.indexOf("fkey") != -1)
            {
               if (foreignKeys.containsKey(constraint))
                  foreignKeys.put(constraint, foreignKeys.get(constraint) + "," + identifierQuoteString +
                     columnName + identifierQuoteString);
               else
                  foreignKeys.put(constraint, identifierQuoteString + columnName + identifierQuoteString);
            }
         }

         // Create the appropriate SQL for the keys. Chose to
         // use a breviated form. A future version may need to
         // more closely/duplicate pg_dump. The issue is the same
         // for all these databases with the CONSTRAINTS. If a
         // table is not yet created then can not create the
         // constraint. This class works fine for individual 
         // table dumps, but a schema needs to dump tables then
         // add constraints.

         // Add Unique & Primary Keys.
         if (!(uniqueKeys.toString()).equals(""))
            tableDefinition.append("UNIQUE (" 
                                   + uniqueKeys.substring(0, uniqueKeys.length() - 1) 
                                   + "),\n    ");

         if (!(primaryKeys.toString()).equals(""))
            tableDefinition.append("PRIMARY KEY (" 
                                   + primaryKeys.substring(0, primaryKeys.length() - 1)
                                   + "),\n    ");

         // Add Foreign Keys. There should be only one? NOT!
         if (!foreignKeys.isEmpty())
         {
            Set<Map.Entry<String, String>> foreignKeysSet = foreignKeys.entrySet();
            Iterator<Map.Entry<String, String>> foreignKeyConstraintIterator = foreignKeysSet.iterator();
            
            while (foreignKeyConstraintIterator.hasNext())
            {
               Map.Entry<String, String> currentEntry = (Map.Entry<String, String>) foreignKeyConstraintIterator.next();
               foreignKey = currentEntry.getValue();
               
               // Obtaining the table who owns the foreign key
               // and its name.

               sqlStatementString = "SELECT table_catalog, table_schema, table_name, " 
                                    + "column_name, constraint_name FROM "
                                    + "information_schema.constraint_column_usage "
                                    + "WHERE table_catalog='"
                                    + databaseName
                                    + "' AND "
                                    + "constraint_name='"
                                    + constraint + "'";
               // System.out.println(sqlStatementString);

               resultSet = sqlStatement.executeQuery(sqlStatementString);
               
               while (resultSet.next())
               {
                  referenceSchemaName = identifierQuoteString + resultSet.getString("table_schema")
                                        + identifierQuoteString;
                  referenceTableName = identifierQuoteString + resultSet.getString("table_name")
                                       + identifierQuoteString;
                  
                  if (!resultSet.isLast())
                     referenceColumnName.append(identifierQuoteString + resultSet.getString("column_name")
                                           + identifierQuoteString + ",");
                  else
                     referenceColumnName.append(identifierQuoteString + resultSet.getString("column_name")
                                           + identifierQuoteString);  
               }

               // Obtaining the constraint for ON DELETE.

               sqlStatementString = "SELECT constraint_catalog, constraint_name, delete_rule FROM "
                                    + "information_schema.referential_constraints "
                                    + "WHERE constraint_catalog='" + databaseName
                                    + "' AND " + "constraint_name='" + constraint + "'";
               // System.out.println(sqlStatementString);

               resultSet = sqlStatement.executeQuery(sqlStatementString);
               
               resultSet.next();
               onDeleteRule = resultSet.getString("delete_rule");

               // Finally create the SQL for the key.
               tableDefinition.append("FOREIGN KEY ("
                                      + foreignKey
                                      + ") REFERENCES " + referenceSchemaName + "." + referenceTableName + "("
                                      + referenceColumnName + ") ON DELETE " + onDeleteRule);
               
               if (foreignKeyConstraintIterator.hasNext())
                  tableDefinition.append(",\n    ");
               else
                  tableDefinition.append(" \n    ");
               referenceColumnName.delete(0, referenceColumnName.length());
            }  
         }
         tableDefinition.delete(tableDefinition.length() - 6, tableDefinition.length());
         tableDefinition.append("\n);\n");

         resultSet.close();
      }
      catch (SQLException e)
      {
         ConnectionManager.displaySQLErrors(e, "TableDefinitionGenerator createPostgreSQLTableDefinition()");
      }
      finally
      {
         try
         {
            if (sqlStatement != null)
               sqlStatement.close();
         }
         catch (SQLException e)
         {
            ConnectionManager.displaySQLErrors(e,
               "TableDefinitionGenerator createPostgreSQLTableDefinition()");
         }
         finally
         {
            if (sqlStatement2 != null)
               sqlStatement2.close();
         }   
      }
   }

   //================================================================
   // Class method for creating a given HSQL TABLE definition.
   //================================================================

   private void createHSQLTableDefinition() throws SQLException
   {
      // Class Method Instances.
      String dataSourceType;
      String tableType;
      String catalogName;
      String autoIncrementColumnName;
      HashMap<String, String> timeStampTMZFieldsHashMap;
      HashMap<String, String> timeTMZFieldsHashMap;
      HashMap<String, String> intervalFieldsHashMap;
      HashMap<String, Integer> columnPrecisionHashMap;
      String columnName, columnType, columnSize;
      int columnPrecision;
      String defaultString;
      StringBuffer uniqueKeys;
      String primaryKeys, foreignKeys;
      String referenceTableName, referenceColumnName;
      String onDeleteRule;

      DatabaseMetaData dbMetaData;
      ResultSetMetaData tableMetaData;

      String sqlStatementString;
      Statement sqlStatement;
      ResultSet resultSet;
      
      // Beginning the creation of the string description
      // of the table Structure.
      
      dataSourceType = ConnectionManager.getDataSourceType();
      timeStampTMZFieldsHashMap = new HashMap<String, String>();
      timeTMZFieldsHashMap = new HashMap<String, String>();
      intervalFieldsHashMap = new HashMap<String, String>();
      columnPrecisionHashMap = new HashMap<String, Integer>();
      sqlStatement = null;
      resultSet = null;
      
      try
      {
         sqlStatement = dbConnection.createStatement();
         
         // Collect the table type for the table.
         
         sqlStatementString = "SELECT " + dbIdentifierQuoteString + "TABLE_TYPE"
                               + dbIdentifierQuoteString + " FROM " + dbIdentifierQuoteString
                               + "INFORMATION_SCHEMA" + dbIdentifierQuoteString + "."
                               + dbIdentifierQuoteString + "SYSTEM_TABLES" + dbIdentifierQuoteString
                               + " WHERE " + dbIdentifierQuoteString + "TABLE_SCHEM"
                               + dbIdentifierQuoteString + "='" + schemaName
                               + "' AND TABLE_NAME='" + tableName + "'";
         // System.out.println(sqlStatementString);
         
         resultSet = sqlStatement.executeQuery(sqlStatementString);
         
         if (resultSet.next())
            tableType = resultSet.getString(1);
         else
            tableType = "TABLE";
         
         // Drop Table Statements As Needed.
         if (DBTablesPanel.getDataExportProperties().getTableStructure())
         {
            tableDefinition.append("DROP " + tableType + " IF EXISTS " + schemaTableName + ";\n");
         }
         
         // Table Creation Statement.
         if (tableType.equals("VIEW"))
         {
            if (dataSourceType.equals(ConnectionManager.HSQL))
            {
               sqlStatementString = "SELECT " + dbIdentifierQuoteString + "VIEW_DEFINITION"
                                    + dbIdentifierQuoteString + " FROM " + dbIdentifierQuoteString
                                    + "INFORMATION_SCHEMA" + dbIdentifierQuoteString + "."
                                    + dbIdentifierQuoteString + "SYSTEM_VIEWS" + dbIdentifierQuoteString
                                    + " WHERE " + dbIdentifierQuoteString + "TABLE_SCHEMA"
                                    + dbIdentifierQuoteString + "='" + schemaName
                                    + "' AND TABLE_NAME='" + tableName + "'";
               // System.out.println(sqlStatementString);
            }
            // HSQL2
            else
            {
               sqlStatementString = "SELECT " + dbIdentifierQuoteString + "VIEW_DEFINITION"
                                    + dbIdentifierQuoteString + " FROM " + dbIdentifierQuoteString
                                    + "INFORMATION_SCHEMA" + dbIdentifierQuoteString + "."
                                    + dbIdentifierQuoteString + "VIEWS" + dbIdentifierQuoteString
                                    + " WHERE " + dbIdentifierQuoteString + "TABLE_SCHEMA"
                                    + dbIdentifierQuoteString + "='" + schemaName
                                    + "' AND TABLE_NAME='" + tableName + "'";
               // System.out.println(sqlStatementString);
            }
            
            resultSet.close();
            resultSet = sqlStatement.executeQuery(sqlStatementString);

            if (resultSet.next())
            {
               tableDefinition.append("CREATE " + tableType + " " + schemaTableName
                                       + " AS " + resultSet.getString(1) + ";\n");
            }
            resultSet.close();
            return;
         }
         // TABLE
         else
            tableDefinition.append("CREATE " + tableType + " " + schemaTableName + " (\n    ");

         // Begin by creating the individual column field definitions.
         // Column name, data type, default, and isNullable.
         
         // Special Field Collection.
         
         sqlStatementString = "SELECT " + dbIdentifierQuoteString + "COLUMN_NAME" + dbIdentifierQuoteString
                              + "," + dbIdentifierQuoteString + "DATA_TYPE" + dbIdentifierQuoteString
                              + "," + dbIdentifierQuoteString + "DATETIME_PRECISION" + dbIdentifierQuoteString
                              + "," + dbIdentifierQuoteString + "DTD_IDENTIFIER" + dbIdentifierQuoteString
                              + " FROM " + dbIdentifierQuoteString
                              + "INFORMATION_SCHEMA" + dbIdentifierQuoteString + "."
                              + dbIdentifierQuoteString + "COLUMNS" + dbIdentifierQuoteString
                              + " WHERE " + dbIdentifierQuoteString + "TABLE_SCHEMA"
                              + dbIdentifierQuoteString + "='" + schemaName
                              + "' AND TABLE_NAME='" + tableName + "'";
         // System.out.println(sqlStatementString);
         
         resultSet.close();
         resultSet = sqlStatement.executeQuery(sqlStatementString);

         while (resultSet.next())
         {
            if (resultSet.getString("DATA_TYPE").equals("TIMESTAMP WITH TIME ZONE"))
               timeStampTMZFieldsHashMap.put(resultSet.getString("COLUMN_NAME"),
                                         "TIMESTAMP(" + resultSet.getString("DATETIME_PRECISION")
                                         + ") WITH TIME ZONE");
            
            if (resultSet.getString("DATA_TYPE").equals("TIME WITH TIME ZONE"))
               timeTMZFieldsHashMap.put(resultSet.getString("COLUMN_NAME"),
                                         resultSet.getString("DTD_IDENTIFIER"));
            
            if (resultSet.getString("DATA_TYPE").equals("INTERVAL"))
               intervalFieldsHashMap.put(resultSet.getString("COLUMN_NAME"),
                                         resultSet.getString("DTD_IDENTIFIER"));
         }
         
         dbMetaData = dbConnection.getMetaData();

         sqlStatementString = "SELECT LIMIT 0 1 * FROM " + schemaTableName;
         // System.out.println(sqlStatementString);

         resultSet.close();
         resultSet = sqlStatement.executeQuery(sqlStatementString);
         tableMetaData = resultSet.getMetaData();
         
         // Fix for HSQLDB 2.x
         catalogName = tableMetaData.getCatalogName(1);
         
         if (catalogName != null)
            if (catalogName.equals(""))
               catalogName = null;

         resultSet.close();
         resultSet = dbMetaData.getColumns(catalogName,
                                           tableMetaData.getSchemaName(1),
                                           tableMetaData.getTableName(1), "%");

         // Obtain IDENTITY column if there is one and at
         // the same time precision information for TIME &
         // TIMESTAMP fields.
         
         autoIncrementColumnName = "";
         for (int i = 1; i < tableMetaData.getColumnCount() + 1; i++)
         {
            columnName = tableMetaData.getColumnName(i);
            // columnType = (tableMetaData.getColumnTypeName(i)).toUpperCase(Locale.ENGLISH);
            
            if (tableMetaData.isAutoIncrement(i))
               autoIncrementColumnName = columnName;
            
            columnPrecisionHashMap.put(columnName, Integer.valueOf(tableMetaData.getPrecision(i)));
         }

         // Now proceed with rest of structure.
         while (resultSet.next())
         {
            columnName = resultSet.getString("COLUMN_NAME");
            columnType = resultSet.getString("TYPE_NAME");
            columnSize = resultSet.getString("COLUMN_SIZE");
            
            if (columnPrecisionHashMap.get(columnName) != null)
               columnPrecision = (columnPrecisionHashMap.get(columnName)).intValue();
            else
               columnPrecision = 0;
            
            // =============
            // Column name.

            tableDefinition.append(identifierQuoteString + columnName
                                   + identifierQuoteString + " ");

            // =============
            // Column type.

            // Character Types
            if (columnType.indexOf("CHAR") != -1)
            {
               if (columnType.equals("CHAR") || columnType.equals("CHARACTER"))
                  tableDefinition.append("CHAR(" + columnSize + ")");
               else
               {
                  if (ConnectionManager.getDataSourceType().equals(ConnectionManager.HSQL))
                  {
                     if (columnType.equals("VARCHAR") || columnType.equals("VARCHAR_IGNORECASE"))
                        tableDefinition.append("VARCHAR(" + columnSize + ")");
                     else
                        tableDefinition.append(columnType);
                  }
                  else
                  {
                     if (columnSize.equals("16777216"))
                        tableDefinition.append("LONGVARCHAR");
                     else
                        tableDefinition.append("VARCHAR(" + columnSize + ")");      
                  }
               }
            }
            // HSQL 2.x Binary Types
            else if (columnType.indexOf("BINARY") != -1 &&
                     ConnectionManager.getDataSourceType().equals(ConnectionManager.HSQL2))
            {
               if (columnType.equals("BINARY"))
               {
                  boolean parseError;
                  
                  try
                  {
                     parseError = false;
                     Integer.parseInt(columnSize);
                  }
                  catch (NumberFormatException nfe)
                  {
                     parseError = true;
                  }
                  
                  if (parseError)
                     tableDefinition.append("BINARY");
                  else if (Integer.parseInt(columnSize) == 0)
                     tableDefinition.append("BINARY(1)");
                  else
                     tableDefinition.append("BINARY(" + columnSize + ")");
               }
               else
               {
                  if (columnSize.equals("16777216"))
                     tableDefinition.append("LONGVARBINARY");
                  else
                     tableDefinition.append("VARBINARY(" + columnSize + ")");     
               }
            }
            // Integer/BigInt Types
            else if (columnType.equals("INTEGER") || columnType.equals("BIGINT"))
            {
               tableDefinition.append(columnType);

               // Assign IDENTITY as needed.
               if (columnName.equals(autoIncrementColumnName))
                  tableDefinition.append(" IDENTITY");
            }
            // Double Types
            else if (columnType.equals("DOUBLE"))
            {
               // Manual Indicates a precision, but
               // can not create DOUBLE(p).
               tableDefinition.append(columnType);
            }
            // Decimal & Numeric Types
            else if (columnType.equals("DECIMAL") || columnType.equals("NUMERIC"))
            {
               if (dataSourceType.equals(ConnectionManager.HSQL) && columnType.equals("NUMERIC"))
                  tableDefinition.append(columnType);
               else
                  tableDefinition.append(columnType + "(" + columnPrecision
                                         + "," + resultSet.getString("DECIMAL_DIGITS") + ")");
            }
            // Time With Time Zone
            else if (columnType.equals("TIME WITH TIME ZONE"))
            {
               // HSQL2 Defines, precision but can not determine.
               // tableDefinition.append(columnType + "(" + columnPrecision + ")");
               // so....
               
               if (timeTMZFieldsHashMap.get(columnName) != null)
                  tableDefinition.append(timeTMZFieldsHashMap.get(columnName));
               else
                  tableDefinition.append(columnType);
            }
            // Timestamp
            else if (columnType.indexOf("TIMESTAMP") != -1)
            {
               // Unable to tell where the precision is located in system
               // table. Column_Size of 29 Seems to Indicate Timestamp(0) for HSQL.
               
               if (dataSourceType.equals(ConnectionManager.HSQL))
               {
                  if (columnSize.equals("29"))
                     tableDefinition.append(columnType + "(0)");
                  else
                     tableDefinition.append(columnType);
               }
               else
               {
                  // HSQL2 Defines, precision but can not determine.
                  // tableDefinition.append(columnType + "(" + columnPrecision + ")");
                  // so...
                  
                  if (timeStampTMZFieldsHashMap.get(columnName) != null)
                     tableDefinition.append(timeStampTMZFieldsHashMap.get(columnName));
                  else
                     tableDefinition.append(columnType);
               }           
            }
            // Interval
            else if (columnType.indexOf("INTERVAL") != -1)
            {
               if (intervalFieldsHashMap.get(columnName) != null)
                  tableDefinition.append(intervalFieldsHashMap.get(columnName));
               else
                  tableDefinition.append(columnType);
            }
            // Bit Varying
            else if (columnType.equals("BIT VARYING")
                     && columnSize != null)
            {
               tableDefinition.append(columnType + "(" + columnSize + ")");
            }
            // All Others.
            else
            {
               tableDefinition.append(columnType);
            }

            // ==========================
            // Column Default & NOT NULL
            
            defaultString = resultSet.getString("COLUMN_DEF");
            
            if (defaultString != null && defaultString.equals("NULL"))
               defaultString = null;

            if (defaultString != null)
            {
               defaultString = defaultString.trim();
               
               if (defaultString.indexOf("::") != -1)
                  tableDefinition.append(" DEFAULT " 
                                         + defaultString.substring(0, defaultString.indexOf(":")));
               else
                  tableDefinition.append(" DEFAULT " + defaultString);

               if (resultSet.getString("IS_NULLABLE").equals("NO"))
                  tableDefinition.append(" NOT NULL,\n    ");
               else
                  tableDefinition.append(",\n    ");
            }
            else
            {
               if (resultSet.getString("IS_NULLABLE").equals("YES"))
                  tableDefinition.append(" DEFAULT NULL,\n    ");
               else
               {
                  if (resultSet.getString("IS_NULLABLE").equals("NO"))
                     tableDefinition.append(" NOT NULL,\n    ");
                  else
                     tableDefinition.append(",\n    ");
               }
            }
         }

         // Create the keys for the table.
         columnName = "";
         primaryKeys = "";
         uniqueKeys = new StringBuffer();
         foreignKeys = "";
         referenceTableName = "";
         referenceColumnName = "";
         // onUpdateRule = "";
         onDeleteRule = "";

         // Primary Keys
         resultSet.close();
         resultSet = dbMetaData.getPrimaryKeys(catalogName,
                                               tableMetaData.getSchemaName(1),
                                               tableMetaData.getTableName(1));
         while (resultSet.next())
         {
            columnName = resultSet.getString("COLUMN_NAME");
            
            if (!columnName.equals(autoIncrementColumnName))
               primaryKeys += identifierQuoteString + columnName + identifierQuoteString + ",";
         }

         // Unique Keys
         resultSet.close();
         resultSet = dbMetaData.getIndexInfo(catalogName,
                                             tableMetaData.getSchemaName(1),
                                             tableMetaData.getTableName(1), true, false);
         while (resultSet.next())
         {
            columnName = resultSet.getString("COLUMN_NAME");

            // Only place unidentitied keys in uniqe string.
            if (primaryKeys.indexOf(columnName) == -1 && !columnName.equals(autoIncrementColumnName))
               uniqueKeys.append(identifierQuoteString + columnName + identifierQuoteString + ",");
         }

         // Add Unique & Primary Keys.
         if (!(uniqueKeys.toString()).equals(""))
            tableDefinition.append("UNIQUE ("
                                   + uniqueKeys.delete((uniqueKeys.length() - 1), uniqueKeys.length())
                                   + "),\n    ");

         if (!primaryKeys.equals(""))
            tableDefinition.append("PRIMARY KEY (" 
                                   + primaryKeys.substring(0, primaryKeys.length() - 1)
                                   + "),\n    ");

         // Foreign Keys
         resultSet.close();
         resultSet = dbMetaData.getImportedKeys(catalogName,
                                                tableMetaData.getSchemaName(1),
                                                tableMetaData.getTableName(1));
         while (resultSet.next())
         {
            columnName = resultSet.getString("FKCOLUMN_NAME");
            referenceTableName = resultSet.getString("PKTABLE_NAME");
            referenceColumnName = resultSet.getString("PKCOLUMN_NAME");

            // These rules return integer values that can not
            // be correlated to a specific rule. Default to
            // DELETE ON CASCADE.
            // onUpdateRule = resultSet.getString("UPDATE_RULE");
            // onDeleteRule = resultSet.getString("DELETE_RULE");
            onDeleteRule = "CASCADE";

            foreignKeys = identifierQuoteString + columnName + identifierQuoteString;

            tableDefinition.append("FOREIGN KEY (" + foreignKeys + ") REFERENCES "
                                   + identifierQuoteString + referenceTableName 
                                   + identifierQuoteString + "(" + identifierQuoteString
                                   + referenceColumnName + identifierQuoteString 
                                   + ") ON DELETE " + onDeleteRule);
            if (resultSet.isLast())
               tableDefinition.append(" \n    ");
            else
               tableDefinition.append(",\n    ");
         }
         tableDefinition.delete(tableDefinition.length() - 6, tableDefinition.length());
         tableDefinition.append("\n);\n");
      }
      catch (SQLException e)
      {
         ConnectionManager.displaySQLErrors(e,
            "TableDefinitionGenerator createHSQLTableDefinition()");
      }
      finally
      {
         try
         {
            if (resultSet != null)
               resultSet.close();
         }
         catch (SQLException sqle)
         {
            ConnectionManager.displaySQLErrors(sqle,
               "TableDefinitionGenerator createHSQLTableDefinition()");
         }
         finally
         {
            if (sqlStatement != null)
               sqlStatement.close();
         }
      }
   }

   //================================================================
   // Class method for creating a given Oracle TABLE definition.
   //================================================================

   private void createOracleTableDefinition() throws SQLException
   {
      // Class Method Instances.
      String tableType;
      String columnName, columnClass, columnType;
      int columnSize, columnDecimalDigits;
      String columnDefault, columnIsNullable;
      
      HashMap<String, String> autoIncrementColumnNameHashMap;
      HashMap<String, String> autoIncrementColumnSeqHashMap;
      
      String sequenceKeyPresent;
      double minValue, maxValue, incrementBy, cacheSize;
      boolean cycleFlag, orderFlag;

      StringBuffer primaryKeys, uniqueKeys;
      String foreignKeys;
      int foreignKeysCount;
      String referenceTableName, referenceColumnName;
      // String onUpdateRule;
      String onDeleteRule;

      DatabaseMetaData dbMetaData;
      ResultSetMetaData tableMetaData;

      String sqlStatementString;
      Statement sqlStatement, sqlStatement2;
      ResultSet resultSet, resultSet2;

      // Begin creating the table structure scheme.
      
      sqlStatement = null;
      sqlStatement2 = null;
      resultSet = null;
      resultSet2 = null;

      try
      {
         sqlStatement = dbConnection.createStatement();
         sqlStatement2 = dbConnection.createStatement();
         dbMetaData = dbConnection.getMetaData();
         
         // Collect table type for the table.
         
         sqlStatementString = "SELECT OBJECT_TYPE FROM USER_OBJECTS WHERE "
                               + "OBJECT_NAME='" + tableName + "'";
         // System.out.println(sqlStatementString);
         
         resultSet = sqlStatement.executeQuery(sqlStatementString);
         
         if (resultSet.next())
            tableType = resultSet.getString(1);
         else
            tableType = "TABLE";
         resultSet.close();
         
         // Drop Table Statements As Needed.
         if (DBTablesPanel.getDataExportProperties().getTableStructure())
         {
            if (tableType.equals("VIEW"))
               tableDefinition.append("DROP VIEW " + schemaTableName + ";\n");
            else
               tableDefinition.append("DROP " + tableType + " " + schemaTableName + ";\n");
         }
         
         // Table Creation Statement.
         if (tableType.equals("VIEW"))
         {
            
            sqlStatementString = "SELECT TEXT FROM ALL_VIEWS WHERE "
                                 + "VIEW_NAME='" + tableName + "'";
            // System.out.println(sqlStatementString);

            resultSet = sqlStatement.executeQuery(sqlStatementString);

            if (resultSet.next())
            {
               tableDefinition.append("CREATE " + tableType + " " + schemaTableName
                                      + " AS " + resultSet.getString(1) + ";\n");
            }
            resultSet.close();
            return;
         }
         // TABLE
         else
            tableDefinition.append("CREATE " + tableType + " " + schemaTableName + " (\n    ");
         
         // Obtain SEQUENCE column if there is one.

         sqlStatementString = "SELECT * FROM " + schemaTableName + " WHERE ROWNUM=1";
         // System.out.println(sqlStatementString);
         
         resultSet = sqlStatement.executeQuery(sqlStatementString);
         tableMetaData = resultSet.getMetaData();

         sequenceKeyPresent = "";
         autoIncrementColumnNameHashMap = new HashMap <String, String>();
         autoIncrementColumnSeqHashMap = new HashMap <String, String>();
         
         for (int i = 1; i < tableMetaData.getColumnCount() + 1; i++)
         {
            columnName = tableMetaData.getColumnName(i);
            // System.out.println("Sequence Checking: " + columnName);

            sqlStatementString = "SELECT USER_IND_COLUMNS.INDEX_NAME, ALL_SEQUENCES.MIN_VALUE,"
                  + " ALL_SEQUENCES.MAX_VALUE, ALL_SEQUENCES.INCREMENT_BY, ALL_SEQUENCES.CYCLE_FLAG,"
                  + " ALL_SEQUENCES.ORDER_FLAG, ALL_SEQUENCES.CACHE_SIZE FROM USER_IND_COLUMNS, "
                  + "ALL_SEQUENCES WHERE USER_IND_COLUMNS.INDEX_NAME="
                  + "ALL_SEQUENCES.SEQUENCE_NAME AND USER_IND_COLUMNS.TABLE_NAME='"
                  + tableName + "' AND USER_IND_COLUMNS.COLUMN_NAME='" + columnName + "'";
           // System.out.println(sqlStatementString);
           
            resultSet2 = sqlStatement2.executeQuery(sqlStatementString);

            if (resultSet2.next())
            {
               String sequenceName = resultSet2.getString("INDEX_NAME");
               // System.out.println("SEQUENCE FOUND: " + columnName + ": " + sequenceName);
               
               autoIncrementColumnNameHashMap.put(columnName, sequenceName);
               autoIncrementColumnSeqHashMap.put(columnName, resultSet2.getString("MIN_VALUE").trim()
                                                 + "," + resultSet2.getString("MAX_VALUE").trim()
                                                 + "," + resultSet2.getString("INCREMENT_BY").trim()
                                                 + "," + resultSet2.getString("CYCLE_FLAG").trim()
                                                 + "," + resultSet2.getString("ORDER_FLAG").trim()
                                                 + "," + resultSet2.getString("CACHE_SIZE").trim());
            }
            resultSet2.close();
         }
         sqlStatement2.close();
         
         // Create the individual column field definitions.
         // Column name, data type, default, and isNullable.

         resultSet.close();
         resultSet = dbMetaData.getColumns(databaseName, schemaName, tableName, "%");
         
         // System.out.println("Collecting Column Field Definitions");

         while (resultSet.next())
         {
            // Collect all necessary columns information from
            // the resultset

            columnName = resultSet.getString("COLUMN_NAME");
            columnClass = resultSet.getString("DATA_TYPE");
            columnType = resultSet.getString("TYPE_NAME");
            columnSize = resultSet.getInt("COLUMN_SIZE");
            columnDecimalDigits = resultSet.getInt("DECIMAL_DIGITS");
            columnDefault = resultSet.getString("COLUMN_DEF");
            columnIsNullable = resultSet.getString("IS_NULLABLE");
            // System.out.println(columnName + " " + columnClass + " " + columnType + " " + columnSize
            //                    + " " + columnDecimalDigits + " " + columnDefault + " " + columnIsNullable);

            // =============
            // Column name.

            tableDefinition.append(identifierQuoteString + columnName + identifierQuoteString + " ");

            // =============
            // Column type.

            // Character Types
            if (columnType.indexOf("CHAR") != -1)
            {
               if (columnType.equals("CHAR"))
                  tableDefinition.append("CHAR(" + columnSize + ")");
               else if (columnType.equals("NCHAR"))
                  tableDefinition.append("NCHAR(" + columnSize + ")");
               else if (columnType.equals("VARCHAR2"))
                  tableDefinition.append("VARCHAR2(" + columnSize + ")");
               else if (columnType.equals("NVARCHAR2"))
                  tableDefinition.append("NVARCHAR2(" + columnSize + ")");
               else
                  tableDefinition.append(columnType);
            }
            // Number Types
            else if (columnType.equals("NUMBER"))
            {
               if (columnClass.toLowerCase(Locale.ENGLISH).indexOf("double") != -1)
                  tableDefinition.append("FLOAT");
               else
               {
                  if (columnDecimalDigits < 0)
                     tableDefinition.append("NUMBER");
                  else
                     tableDefinition.append("NUMBER(" + columnSize + "," + columnDecimalDigits + ")");
               }
            }
            // Binary_Float Types
            else if (columnType.equals("BINARY_FLOAT"))
            {
               tableDefinition.append(columnType);
            }
            // Binary_Double Types
            else if (columnType.equals("BINARY_DOUBLE"))
            {
               tableDefinition.append(columnType);
            }
            // Raw Types
            else if (columnType.equals("RAW"))
            {
               tableDefinition.append("RAW(" + columnSize + ")");
            }
            // Timestamp
            else if (columnType.equals("TIMESTAMP"))
            {
               tableDefinition.append(columnType);
            }
            // TimestampTZ
            else if (columnType.equals("TIMESTAMPTZ")
                     || columnType.equals("TIMESTAMP WITH TIME ZONE"))
            {
               tableDefinition.append("TIMESTAMP WITH TIME ZONE");
            }
            // TimestampLTZ
            else if (columnType.equals("TIMESTAMPLTZ")
                     || columnType.equals("TIMESTAMP WITH LOCAL TIME ZONE"))
            {
               tableDefinition.append("TIMESTAMP WITH LOCAL TIME ZONE");
            }
            // Interval
            else if (columnType.indexOf("INTERVAL") != -1)
            {
               if (columnType.equals("INTERVALYM"))
               {
                  tableDefinition.append("INTERVAL YEAR TO MONTH");
               }
               else if (columnType.equals("INTERVALDS"))
               {
                  tableDefinition.append("INTERVAL DAY TO SECOND");
               }
               else
                  tableDefinition.append(columnType);
            }
            // Long, Long Raw, Blob, BFile,
            else
            {
               tableDefinition.append(columnType);
            }

            // ==========================
            // Column Default & NOT NULL

            if (columnDefault != null)
            {
               if (columnDefault.indexOf("::") != -1)
                  tableDefinition.append(" DEFAULT " 
                                         + (columnDefault.substring(0, columnDefault.indexOf(":"))).trim());
               else
                  tableDefinition.append(" DEFAULT " + columnDefault.trim());

               if (columnIsNullable.equals("NO"))
                  tableDefinition.append(" NOT NULL,\n    ");
               else
                  tableDefinition.append(",\n    ");
            }
            else
            {
               if (columnIsNullable.equals("YES"))
                  tableDefinition.append(" DEFAULT NULL,\n    ");
               else
               {
                  if (columnIsNullable.equals("NO"))
                     tableDefinition.append(" NOT NULL,\n    ");
                  else
                     tableDefinition.append(",\n    ");
               }
            }
         }

         // ===============================
         // Create the keys for the table.

         columnName = "";
         primaryKeys = new StringBuffer();
         uniqueKeys = new StringBuffer();
         foreignKeys = "";
         referenceTableName = "";
         referenceColumnName = "";
         // onUpdateRule = "";
         onDeleteRule = "";

         // Primary Keys
         resultSet.close();
         resultSet = dbMetaData.getPrimaryKeys(databaseName, schemaName, tableName);
         
         // System.out.println("Collecting Primary Keys Definitions");

         while (resultSet.next())
         {
            columnName = resultSet.getString("COLUMN_NAME");
            if (autoIncrementColumnNameHashMap.containsKey(columnName))
               sequenceKeyPresent = "primary_" + columnName;

            primaryKeys.append(identifierQuoteString + columnName + identifierQuoteString + ",");
         }

         // Unique Keys
         // Clueless why needs quotes?
         resultSet.close();
         resultSet = dbMetaData.getIndexInfo(databaseName, dbIdentifierQuoteString
                                             + schemaName + dbIdentifierQuoteString,
                                             dbIdentifierQuoteString + tableName
                                             + dbIdentifierQuoteString, false, false);
         
         // System.out.println("Collecting Unique Keys Definitions");

         while (resultSet.next())
         {
            columnName = resultSet.getString("COLUMN_NAME");

            // Only place unidentitied keys in uniqe string.
            if (columnName != null && primaryKeys.indexOf(columnName) == -1)
            {
               if (autoIncrementColumnNameHashMap.containsKey(columnName))
                  sequenceKeyPresent = "unique_" + columnName;

               uniqueKeys.append(identifierQuoteString + columnName + identifierQuoteString + ",");
            }
         }

         // Add Unique & Primary Keys.
         if (uniqueKeys.length() != 0 && sequenceKeyPresent.indexOf("unique") == -1)
            tableDefinition.append("UNIQUE (" 
                                   + uniqueKeys.substring(0, uniqueKeys.length() - 1)
                                   + "),\n    ");

         if (primaryKeys.length() != 0 && sequenceKeyPresent.indexOf("primary") == -1)
            tableDefinition.append("PRIMARY KEY (" 
                                   + primaryKeys.substring(0, primaryKeys.length() - 1)
                                   + "),\n    ");

         // Foreign Keys
         // The Oracle database is having considerable delay right here
         // with collecting the imported keys.
         resultSet.close();
         resultSet = dbMetaData.getImportedKeys(databaseName, schemaName, tableName);
         
         // System.out.println("Collecting Unique Keys Definitions");
         
         // Collect number of keys.
         
         foreignKeysCount = 0;
         
         while (resultSet.next())
            foreignKeysCount++;
         
         resultSet = dbMetaData.getImportedKeys(databaseName, schemaName, tableName);
        
         int i = 0;
         while (resultSet.next())
         {
            columnName = resultSet.getString("FKCOLUMN_NAME");
            referenceTableName = resultSet.getString("PKTABLE_NAME");
            referenceColumnName = resultSet.getString("PKCOLUMN_NAME");

            // These rules return integer values that can not
            // be correlated to a specific rule. Default to
            // DELETE ON CASCADE.
            // onDeleteRule = resultSet.getString("DELETE_RULE");
            onDeleteRule = "CASCADE";

            foreignKeys = identifierQuoteString + columnName + identifierQuoteString;

            tableDefinition.append("FOREIGN KEY (" + foreignKeys + ") REFERENCES " 
                                   + identifierQuoteString + referenceTableName 
                                   + identifierQuoteString + "(" + identifierQuoteString
                                   + referenceColumnName + identifierQuoteString + ") ON DELETE "
                                   + onDeleteRule);
            
            if (i < foreignKeysCount)
               tableDefinition.append(",\n    ");
            else
               tableDefinition.append(" \n    ");
            i++;
         }
         tableDefinition.delete(tableDefinition.length() - 6, tableDefinition.length());
         tableDefinition.append("\n);\n");

         // Add Sequence as needed.
         // System.out.println("Adding Sequences");
         
         if (!sequenceKeyPresent.equals(""))
         {
            columnName = sequenceKeyPresent.substring(sequenceKeyPresent.indexOf("_") + 1);
            
            try
            {
               String[] parameters = (autoIncrementColumnSeqHashMap.get(columnName)).split(",");
               
               minValue = 0; maxValue = 0; incrementBy = 1; cacheSize = 0;
               cycleFlag = false; orderFlag = false; 
               
               for (int k=0; k<parameters.length; k++)
               {
                  switch (k)
                  {
                     case 0:
                        minValue = Double.parseDouble(parameters[k]);
                        break;
                     case 1:
                        maxValue = Double.parseDouble(parameters[k]);
                        break;
                     case 2:
                        incrementBy = Double.parseDouble(parameters[k]);
                        break;
                     case 3:
                        cycleFlag = parameters[k].equals("N")?false:true;
                        break;
                     case 4:
                        orderFlag = parameters[k].equals("N")?false:true;
                        break;
                     case 5:
                        cacheSize = Double.parseDouble(parameters[k]);
                        break;
                     default:
                        continue;
                  } 
               }
               tableDefinition.append("CREATE SEQUENCE " + identifierQuoteString
                                      + autoIncrementColumnNameHashMap.get(columnName)
                                      + identifierQuoteString + "\n");
               
               tableDefinition.append("   INCREMENT BY " + incrementBy + "\n");
               
               if (incrementBy > 0)
                  tableDefinition.append("   START WITH " + minValue + "\n");
               else
                  tableDefinition.append("   START WITH " + maxValue + "\n");
               
               if (maxValue < 1.0E28)
                  tableDefinition.append("   MAXVALUE " + maxValue + "\n");
               else
                  tableDefinition.append("   NOMAXVALUE\n");
               
               if (minValue > -1.0E27)
                  tableDefinition.append("   MINVALUE " + minValue + "\n");
               else
                  tableDefinition.append("   NOMINVALUE\n");
               
               if (cycleFlag)
                  tableDefinition.append("   CYCLE\n");
               else
                  tableDefinition.append("   NOCYCLE\n");
               
               tableDefinition.append("   CACHE " + cacheSize + "\n");
               
               if (orderFlag)
                  tableDefinition.append("   ORDER\n");
               else
                  tableDefinition.append("   NOORDER;\n");
            }
            catch (NumberFormatException nfe)
            {
               // System.out.println(nfe);
               tableDefinition.append("CREATE SEQUENCE " + identifierQuoteString
                                      + autoIncrementColumnNameHashMap.get(columnName)
                                      + identifierQuoteString + ";\n");
            }
            
            // Add Constraints.
            
            tableDefinition.append("ALTER TABLE " + identifierQuoteString + tableName
                                   + identifierQuoteString + " ADD CONSTRAINT " 
                                   + identifierQuoteString
                                   + autoIncrementColumnNameHashMap.get(columnName) 
                                   + identifierQuoteString + " ");
            if (sequenceKeyPresent.indexOf("primary") != -1)
               tableDefinition.append("PRIMARY KEY (" 
                                      + primaryKeys.substring(0, primaryKeys.length() - 1)
                                      + ");\n\n  ");
            else
               tableDefinition.append("UNIQUE (" 
                                      + uniqueKeys.substring(0, uniqueKeys.length() - 1)
                                      + ");\n\n  ");
         }
      }
      catch (SQLException e)
      {
         ConnectionManager.displaySQLErrors(e,
            "TableDefinitionGenerator createOracleTableDefinition()");
      }
      finally
      {
         try
         {
            if (resultSet != null)
               resultSet.close();
         }
         catch (SQLException sqle)
         {
            ConnectionManager.displaySQLErrors(sqle,
               "TableDefinitionGenerator createOracleTableDefinition()");
         }
         finally
         {
            try
            {
               if (resultSet2 != null)
                  resultSet2.close();
            }
            catch (SQLException sqle)
            {
               ConnectionManager.displaySQLErrors(sqle,
                  "TableDefinitionGenerator createOracleTableDefinition()");
            }
            finally
            {
               try
               {
                  if (sqlStatement != null)
                     sqlStatement.close();
               }
               catch (SQLException sqle)
               {
                  ConnectionManager.displaySQLErrors(sqle,
                        "TableDefinitionGenerator createOracleTableDefinition()");
               }
               finally
               {
                  if (sqlStatement2 != null)
                     sqlStatement2.close();
               }
            }
         }   
      }
   }
   
   //==============================================================
   // Class method for creating a given SQLite TABLE definition.
   //==============================================================

   private void createSQLiteTableDefinition() throws SQLException
   {
      // Class Method Instances.
      String tableType;

      String sqlStatementString;
      Statement sqlStatement;
      ResultSet resultSet;

      // Beginning the creation of the string description
      // of the table Structure.
      
      sqlStatement = null;
      resultSet = null;
      
      try
      {
         // Setup a connection statement.
         sqlStatement = dbConnection.createStatement();

         // SQlite does all the work here with the handy
         // sqlite_master table.

         sqlStatementString = "SELECT type, sql FROM (SELECT * FROM sqlite_master UNION ALL " 
                              + "SELECT * FROM sqlite_temp_master) WHERE type!='meta' " 
                              + "AND sql NOT NULL AND name='" + tableName + "'";
         // System.out.println(sqlStatementString);
         
         resultSet = sqlStatement.executeQuery(sqlStatementString);
         
         if (resultSet.next())
         {
         
            // Drop Table Statements As Needed.
            if (DBTablesPanel.getDataExportProperties().getTableStructure())
            {
               tableType = resultSet.getString("type");
               
               if (tableType != null)
                  tableDefinition.append("DROP " + tableType.toUpperCase(Ajqvue.getLocale())
                                         + " IF EXISTS " + schemaTableName + ";\n");
            }
            // Create Table column
            tableDefinition.append(resultSet.getString("sql") + ";\n");
         }
         else
            tableDefinition.append("\n");
      }
      catch (SQLException e)
      {
         ConnectionManager.displaySQLErrors(e, "TableDefinitionGenerator createSQLiteTableDefinition()");
      }
      finally
      {
         try
         {
            if (resultSet != null)
               resultSet.close();
         }
         catch (SQLException sqle)
         {
            ConnectionManager.displaySQLErrors(sqle, "TableDefinitionGenerator createSQLiteTableDefinition()");
         }
         finally
         {
            if (sqlStatement != null)
               sqlStatement.close();
         }
      }
   }
   
   //==============================================================
   // Class method for creating a given MSAccess TABLE definition.
   //==============================================================

   private void createMSAccessTableDefinition()
   {
      // Class Method Instances.
      // Beginning the creation of the string description
      // of the table Structure.
      
      tableDefinition.append("-- MSAccess Table Definition, DDL, Not Supported At This Time.\n");
   }
   
   //==============================================================
   // Class method for creating a given MSSQL TABLE definition.
   //==============================================================

   private void createMSSQLTableDefinition() throws SQLException
   {
      // Class Method Instances.
      String tableType;
      String autoIncrementColumnName;
      HashMap<String, Integer> columnPrecisionHashMap;
      String columnName, columnType, columnSize;
      int columnPrecision, columnDateTimePrecision;
      String defaultString;
      StringBuffer uniqueKeys;
      String primaryKeys, foreignKeys;
      String referenceTableName, referenceColumnName;
      String onDeleteRule;

      DatabaseMetaData dbMetaData;
      ResultSetMetaData tableMetaData;

      String sqlStatementString;
      Statement sqlStatement;
      ResultSet resultSet, resultSet2;
      
      // Beginning the creation of the string description
      // of the table Structure.
      
      columnPrecisionHashMap = new HashMap<String, Integer>();
      sqlStatement = null;
      resultSet = null;
      resultSet2 = null;
      
      try
      {
         sqlStatement = dbConnection.createStatement();
         
         // Collect the table type for the table.
         
         sqlStatementString = "SELECT " + dbIdentifierQuoteString + "TABLE_TYPE"
                               + dbIdentifierQuoteString + " FROM " + dbIdentifierQuoteString
                               + "INFORMATION_SCHEMA" + dbIdentifierQuoteString + "."
                               + dbIdentifierQuoteString + "TABLES" + dbIdentifierQuoteString
                               + " WHERE " + dbIdentifierQuoteString + "TABLE_SCHEMA"
                               + dbIdentifierQuoteString + "='" + schemaName
                               + "' AND TABLE_NAME='" + tableName + "'";
         // System.out.println(sqlStatementString);
         
         resultSet = sqlStatement.executeQuery(sqlStatementString);
         
         if (resultSet.next())
         {
            tableType = resultSet.getString(1);
            
            if (tableType.equals("BASE TABLE"))
               tableType = "TABLE";
         }
         else
            tableType = "TABLE";
         
         resultSet.close();
         
         // Drop Table Statements As Needed.
         if (DBTablesPanel.getDataExportProperties().getTableStructure())
         {
            tableDefinition.append("DROP " + tableType + " " + schemaTableName + ";\n");
         }
         
         // Table Creation Statement.
         if (tableType.equals("VIEW"))
         {
            sqlStatementString = "SELECT " + dbIdentifierQuoteString + "VIEW_DEFINITION"
                                 + dbIdentifierQuoteString + " FROM " + dbIdentifierQuoteString
                                 + "INFORMATION_SCHEMA" + dbIdentifierQuoteString + "."
                                 + dbIdentifierQuoteString + "VIEWS" + dbIdentifierQuoteString
                                 + " WHERE " + dbIdentifierQuoteString + "TABLE_SCHEMA"
                                 + dbIdentifierQuoteString + "='" + schemaName
                                 + "' AND TABLE_NAME='" + tableName + "'";
            // System.out.println(sqlStatementString);
            
            resultSet = sqlStatement.executeQuery(sqlStatementString);

            if (resultSet.next())
            {
               String sqlDefinition = resultSet.getString(1).replaceAll("\n", " ");
               
               if (sqlDefinition.indexOf("/*") != -1)
                  tableDefinition.append(sqlDefinition.substring(0, sqlDefinition.indexOf("/*")).trim() + ";\n");
               else
                  tableDefinition.append(resultSet.getString(1) + ";\n");
            }
            return;
         }
         // TABLE
         else
            tableDefinition.append("CREATE " + tableType + " " + schemaTableName + " (\n    ");

         // Begin by creating the individual column field definitions.
         // Column name, data type, default, and isNullable.
         
         dbMetaData = dbConnection.getMetaData();

         sqlStatementString = "SELECT TOP 1 * FROM " + schemaTableName;

         resultSet.close();
         resultSet = sqlStatement.executeQuery(sqlStatementString);
         tableMetaData = resultSet.getMetaData();
         
         resultSet = dbMetaData.getColumns(databaseName, schemaName, tableName, "%");
         
         // Obtain IDENTITY column if there is one.
         
         autoIncrementColumnName = "";
         for (int i = 1; i < tableMetaData.getColumnCount() + 1; i++)
         {
            columnName = tableMetaData.getColumnName(i);
            // columnType = (tableMetaData.getColumnTypeName(i)).toUpperCase(Locale.ENGLISH);
            
            if (tableMetaData.isAutoIncrement(i))
               autoIncrementColumnName = columnName;
            
            columnPrecisionHashMap.put(columnName, Integer.valueOf(tableMetaData.getPrecision(i)));
         }

         // Now proceed with rest of structure.
         while (resultSet.next())
         {
            columnName = resultSet.getString("COLUMN_NAME");
            columnType = (resultSet.getString("TYPE_NAME")).toUpperCase(Locale.ENGLISH);
            columnSize = resultSet.getString("COLUMN_SIZE");
            
            if (columnPrecisionHashMap.get(columnName) != null)
               columnPrecision = (columnPrecisionHashMap.get(columnName)).intValue();
            else
               columnPrecision = 0;
            
            // =============
            // Column name.

            tableDefinition.append(identifierQuoteString + columnName
                                   + identifierQuoteString + " ");

            // =============
            // Column type.

            // Character Types
            if (columnType.indexOf("CHAR") != -1)
            {
               tableDefinition.append(columnType + "(" + columnSize + ")");
            }
            // Integer/BigInt Types
            else if (columnType.indexOf("INT") != -1 || columnType.indexOf("BIGINT") != -1)
            {
               tableDefinition.append(columnType);

               // Assign IDENTITY as needed.
               if (columnName.equals(autoIncrementColumnName))
               {
                  // Unable to determine the start/increment values
                  // so assigning to default 1,1.
                  
                  if (columnType.indexOf("IDENTITY") != -1)
                     tableDefinition.append("(1,1)");
                  else
                     tableDefinition.append(" IDENTITY(1,)");
                  
                  tableDefinition.append(",\n    ");
                  continue;
               }
            }
            // Decimal & Numeric Types
            else if (columnType.equals("DECIMAL") || columnType.equals("NUMERIC"))
            {
               tableDefinition.append(columnType + "(" + columnPrecision
                                      + "," + resultSet.getString("DECIMAL_DIGITS") + ")");
            }
            // Blob/Binary Types
            else if (columnType.equals("BLOB") || columnType.indexOf("BINARY") != -1)
            {
               tableDefinition.append(columnType + "(" + columnSize + ")");
            }
            // Clob Types
            else if (columnType.equals("CLOB"))
            {
               tableDefinition.append(columnType + "(" + columnSize + ")");
            }
            // DateTimeOffsett Types
            else if (columnType.equals("DATETIMEOFFSET"))
            {
               sqlStatementString = "SELECT " + dbIdentifierQuoteString + "DATETIME_PRECISION"
                                    + dbIdentifierQuoteString + " FROM " + dbIdentifierQuoteString
                                    + "INFORMATION_SCHEMA" + dbIdentifierQuoteString + "."
                                    + dbIdentifierQuoteString + "COLUMNS" + dbIdentifierQuoteString
                                    + " WHERE " + dbIdentifierQuoteString + "TABLE_SCHEMA"
                                    + dbIdentifierQuoteString + "='" + schemaName
                                    + "' AND TABLE_NAME='" + tableName
                                    + "' AND COLUMN_NAME='" + columnName + "'";;
               // System.out.println(sqlStatementString);
               
               resultSet2 = sqlStatement.executeQuery(sqlStatementString);
               
               if (resultSet2.next())
                  columnDateTimePrecision = resultSet2.getInt("DATETIME_PRECISION");
               else
                  columnDateTimePrecision = 0;
               resultSet2.close();
               
               tableDefinition.append(columnType + "(" + columnDateTimePrecision + ")");
            }
            // All Others.
            else
            {
               tableDefinition.append(columnType);
            }

            // ==========================
            // Column Default & NOT NULL
            
            defaultString = resultSet.getString("COLUMN_DEF");
            
            if (defaultString != null)
            {      
               defaultString = defaultString.trim();
               
               if (defaultString.indexOf("(") != -1)
                  defaultString = defaultString.substring(defaultString.lastIndexOf("(") + 1);
               
               if (defaultString.indexOf("") != -1)
                  defaultString = defaultString.substring(0, defaultString.indexOf(")"));
               
               if (defaultString.equals("NULL"))
                  defaultString = null;
            }

            if (defaultString != null)
            {
               if (resultSet.getString("IS_NULLABLE").equals("NO"))
               {
                  if (!defaultString.toUpperCase(Locale.ENGLISH).equals("NULL"))
                     tableDefinition.append(" DEFAULT " + defaultString);
                  
                  tableDefinition.append(" NOT NULL,\n    ");
               }
               else
               {
                  tableDefinition.append(" DEFAULT " + defaultString);
                  tableDefinition.append(",\n    ");
               }
            }
            else
            {
               if (resultSet.getString("IS_NULLABLE").equals("YES"))
                  tableDefinition.append(" DEFAULT NULL,\n    ");
               else
               {
                  if (resultSet.getString("IS_NULLABLE").equals("NO"))
                     tableDefinition.append(" NOT NULL,\n    ");
                  else
                     tableDefinition.append(",\n    ");
               }
            }
         }

         // Create the keys for the table.
         columnName = "";
         primaryKeys = "";
         uniqueKeys = new StringBuffer();
         foreignKeys = "";
         referenceTableName = "";
         referenceColumnName = "";
         // onUpdateRule = "";
         onDeleteRule = "";

         // Primary Keys
         resultSet.close();
         resultSet = dbMetaData.getPrimaryKeys(databaseName, schemaName, tableName);
         
         while (resultSet.next())
         {
            columnName = resultSet.getString("COLUMN_NAME");
            primaryKeys += identifierQuoteString + columnName + identifierQuoteString + ",";
         }

         // Unique Keys
         resultSet.close();
         resultSet = dbMetaData.getIndexInfo(databaseName, schemaName, tableName, true, false);
         
         while (resultSet.next())
         {
            columnName = resultSet.getString("COLUMN_NAME");
            
            if (columnName == null)
               continue;

            // Only place unidentitied keys in uniqe string.
            if (primaryKeys.indexOf(columnName) == -1 && !columnName.equals(autoIncrementColumnName))
               uniqueKeys.append(identifierQuoteString + columnName + identifierQuoteString + ",");
         }
         
         // Add Unique & Primary Keys.
         if (!(uniqueKeys.toString()).equals(""))
            tableDefinition.append("UNIQUE ("
                                   + uniqueKeys.delete((uniqueKeys.length() - 1), uniqueKeys.length())
                                   + "),\n    ");

         if (!primaryKeys.equals(""))
            tableDefinition.append("PRIMARY KEY (" 
                                   + primaryKeys.substring(0, primaryKeys.length() - 1)
                                   + "),\n    ");
         
         // Foreign Keys
         resultSet.close();
         resultSet = dbMetaData.getImportedKeys(databaseName, schemaName, tableName);
         
         while (resultSet.next())
         {
            columnName = resultSet.getString("FKCOLUMN_NAME");
            referenceTableName = resultSet.getString("PKTABLE_NAME");
            referenceColumnName = resultSet.getString("PKCOLUMN_NAME");

            // These rules return integer values that can not
            // be correlated to a specific rule. Default to
            // DELETE ON CASCADE.
            // onUpdateRule = resultSet.getString("UPDATE_RULE");
            // onDeleteRule = resultSet.getString("DELETE_RULE");
            onDeleteRule = "CASCADE";

            foreignKeys = identifierQuoteString + columnName + identifierQuoteString;

            tableDefinition.append("FOREIGN KEY (" + foreignKeys + ") REFERENCES "
                                   + identifierQuoteString + referenceTableName 
                                   + identifierQuoteString + "(" + identifierQuoteString
                                   + referenceColumnName + identifierQuoteString 
                                   + ") ON DELETE " + onDeleteRule);
            
            tableDefinition.append(",\n    ");
         }
         tableDefinition.delete(tableDefinition.length() - 6, tableDefinition.length());
         tableDefinition.append("\n);\n");
      }
      catch (SQLException e)
      {
         ConnectionManager.displaySQLErrors(e,
            "TableDefinitionGenerator createMSSQLTableDefinition()");
      }
      finally
      {
         try
         {
            try
            {
               if (resultSet != null)
                  resultSet.close();
            }
            catch (SQLException sqle)
            {
               ConnectionManager.displaySQLErrors(sqle,
                  "TableDefinitionGenerator createMSSQLTableDefinition()");
            }
            finally
            {
               if (resultSet2 != null)
                  resultSet2.close();  
            }   
         }
         catch (SQLException sqle)
         {
            ConnectionManager.displaySQLErrors(sqle,
               "TableDefinitionGenerator createMSSQLTableDefinition()");
         }
         finally
         {
            if (sqlStatement != null)
               sqlStatement.close();
         }
      }
   }
   
   //==============================================================
   // Class method for creating a given Derby TABLE definition.
   //==============================================================

   private void createDerbyTableDefinition() throws SQLException
   {
      // Class Method Instances.
      String schema_id, table_id, tableType;
      String columnName, columnType;
      int columnSize, columnPrecision, columnDecimalDigits;
      String columnDefault, columnIsNullable;
      HashMap<String, Integer> columnAutoIncrementStartHashMap;
      HashMap<String, Integer> columnAutoIncrementIncHashMap;
      HashMap<String, Integer> columnPrecisionHashMap;
      StringBuffer uniqueKeys;
      String primaryKeys, foreignKeys;
      String referenceTableName, referenceColumnName;
      String onDeleteRule;

      DatabaseMetaData dbMetaData;
      ResultSetMetaData tableMetaData;

      String sqlStatementString;
      Statement sqlStatement;
      ResultSet resultSet, resultSet2;
      
      // Setup then beginning the creation of the string
      // description of the table Structure.
      
      schema_id = "";
      table_id = "";
      tableType = "";
      columnPrecisionHashMap = new HashMap<String, Integer>();
      columnAutoIncrementStartHashMap = new HashMap<String, Integer>();
      columnAutoIncrementIncHashMap = new HashMap<String, Integer>();
      sqlStatement = null;
      resultSet = null;
      
      try
      {
         sqlStatement = dbConnection.createStatement();
         dbMetaData = dbConnection.getMetaData();
         
         // Collect the table type for the table.
         
         sqlStatementString = "SELECT SCHEMAID FROM SYS.SYSSCHEMAS WHERE SCHEMANAME='" + schemaName + "'";
         // System.out.println(sqlStatementString);
         
         resultSet = sqlStatement.executeQuery(sqlStatementString);
         
         if (resultSet.next())
         {
            schema_id = resultSet.getString(1);
            resultSet.close();
         }
         else
            return;
         
         sqlStatementString = "SELECT TABLEID, TABLETYPE FROM SYS.SYSTABLES WHERE TABLENAME='"
                              + tableName + "' AND SCHEMAID='" + schema_id + "'";
         // System.out.println(sqlStatementString);
         
         resultSet = sqlStatement.executeQuery(sqlStatementString);
         
         if (resultSet.next())
         {
            table_id = resultSet.getString(1);
            tableType = resultSet.getString(2);
            resultSet.close();
         }
         else
            return;
         
         if (tableType.equals("T"))
            tableType = "TABLE";
         else if (tableType.equals("V"))
            tableType = "VIEW";
         else
            tableType = "SYSTEM";
         
         // Drop Table Statements As Needed.
         if (DBTablesPanel.getDataExportProperties().getTableStructure())
         {
            tableDefinition.append("DROP " + tableType + " " + schemaTableName + ";\n");
         }
         
         // Table Creation Statement.
         if (tableType.equals("VIEW"))
         {
            sqlStatementString = "SELECT VIEWDEFINITION FROM SYS.SYSVIEWS WHERE "
                                 + "COMPILATIONSCHEMAID='" + schema_id + "' AND "
                                 + "TABLEID='" + table_id + "'";
            // System.out.println(sqlStatementString);
            
            resultSet = sqlStatement.executeQuery(sqlStatementString);

            if (resultSet.next())
            {
               tableDefinition.append("CREATE " + tableType + " " + schemaTableName
                                      + " AS " + resultSet.getString(1) + ";\n");
            }
            resultSet.close();
            return;
         }
         // TABLE
         else
            tableDefinition.append("CREATE " + tableType + " " + schemaTableName + " (\n    ");

         // Begin by creating the individual column field definitions.
         // Column name, data type, default, and isNullable.
         
         sqlStatementString = "SELECT * FROM " + schemaTableName + " FETCH FIRST ROW ONLY";
         // System.out.println(sqlStatementString);

         resultSet.close();
         resultSet = sqlStatement.executeQuery(sqlStatementString);
         tableMetaData = resultSet.getMetaData();
         resultSet.close();
         
         resultSet = dbMetaData.getColumns(tableMetaData.getCatalogName(1), tableMetaData.getSchemaName(1),
                                           tableMetaData.getTableName(1), "%");

         // Obtain IDENTITY column if there is one and at
         // the same time increment and precision information.
         
         for (int i = 1; i < tableMetaData.getColumnCount() + 1; i++)
         {
            columnName = tableMetaData.getColumnName(i);
            
            if (tableMetaData.isAutoIncrement(i))
            {
               // System.out.println("AutoIncrement: " + columnName);
               
               sqlStatementString = "SELECT AUTOINCREMENTSTART, AUTOINCREMENTINC FROM SYS.SYSCOLUMNS "
                                    + "WHERE REFERENCEID='" + table_id + "' AND  COLUMNNAME='"
                                    + columnName + "'";
               // System.out.println(sqlStatementString);
               
               resultSet2 = sqlStatement.executeQuery(sqlStatementString);
               
               if (resultSet2.next())
               {
                  columnAutoIncrementStartHashMap.put(columnName, resultSet2.getInt(1));
                  columnAutoIncrementIncHashMap.put(columnName, resultSet2.getInt(2));
               }
               resultSet2.close(); 
            }
            columnPrecisionHashMap.put(columnName, Integer.valueOf(tableMetaData.getPrecision(i)));
         }

         // Now proceed with rest of structure.
         while (resultSet.next())
         {
            columnName = resultSet.getString("COLUMN_NAME");
            columnType = resultSet.getString("TYPE_NAME");
            columnSize = resultSet.getInt("COLUMN_SIZE");
            columnDecimalDigits = resultSet.getInt("DECIMAL_DIGITS");
            columnDefault = resultSet.getString("COLUMN_DEF");
            columnIsNullable = resultSet.getString("IS_NULLABLE");
            // System.out.println(columnName + " " + columnClass + " " + columnType + " " + columnSize + " "
            //                    + columnDecimalDigits + " " + columnDefault + " " + columnIsNullable);
             
            if (columnPrecisionHashMap.get(columnName) != null)
               columnPrecision = (columnPrecisionHashMap.get(columnName)).intValue();
            else
               columnPrecision = 0;
            
            // =============
            // Column name.

            tableDefinition.append(identifierQuoteString + columnName
                                   + identifierQuoteString + " ");

            // =============
            // Column type.

            // Character Types
            if (columnType.indexOf("CHAR") != -1)
            {
               if (columnType.equals("CHAR"))
                  tableDefinition.append("CHAR(" + columnSize + ")");
               else if (columnType.equals("VARCHAR"))
                  tableDefinition.append("VARCHAR(" + columnSize + ")");
               else if (columnType.indexOf("FOR BIT DATA") != -1)
               {
                  if (columnType.indexOf("VARCHAR") != -1)
                  {
                     if (columnType.indexOf("LONG") != -1)
                        tableDefinition.append("LONG VARCHAR FOR BIT DATA");
                     else
                        tableDefinition.append("VARCHAR(" + columnSize + ") FOR BIT DATA");
                  }
                  else
                     tableDefinition.append("CHAR(" + columnSize + ") FOR BIT DATA");
               }
               else
                  tableDefinition.append(columnType);
            }
            
            // Blob Types
            else if (columnType.equals("BLOB"))
            {
               tableDefinition.append(columnType + "(" + columnSize + ")");
            }
            // Clob Types
            else if (columnType.equals("CLOB"))
            {
               tableDefinition.append(columnType + "(" + columnSize + ")");
            }
            // Decimal & Numeric Types
            else if (columnType.equals("DECIMAL"))
            {
               tableDefinition.append(columnType + "(" + columnPrecision
                                      + "," + columnDecimalDigits + ")");
            }
            // All Others.
            else
            {
               tableDefinition.append(columnType);
            }
            
            // ==========================
            // Column Default & NOT NULL
            
            if (columnDefault != null && columnDefault.equals("NULL"))
               columnDefault = null;

            if (columnDefault != null)
            {
               columnDefault = columnDefault.trim();
               
               if (columnDefault.indexOf("::") != -1)
                  tableDefinition.append(" WITH DEFAULT " 
                                         + (columnDefault.substring(0, columnDefault.indexOf(":"))).trim());
               else if (columnDefault.indexOf("AUTOINCREMENT") != -1)
               {
                  
                  tableDefinition.append(" GENERATED ALWAYS AS IDENTITY (START WITH "
                                         + columnAutoIncrementStartHashMap.get(columnName)
                                         + ", INCREMENT BY " + columnAutoIncrementIncHashMap.get(columnName)
                                         + ")");
               }
               else if (columnDefault.indexOf("GENERATED") != -1)
               {
                  tableDefinition.append(" GENERATED BY DEFAULT AS IDENTITY (START WITH "
                                         + columnAutoIncrementStartHashMap.get(columnName)
                                         + ", INCREMENT BY " + columnAutoIncrementIncHashMap.get(columnName)
                                         + ")");;
               }
               else
                  tableDefinition.append(" WITH DEFAULT " + columnDefault.trim());

               if (columnIsNullable.equals("NO"))
                  tableDefinition.append(" NOT NULL,\n    ");
               else
                  tableDefinition.append(",\n    ");
            }
            else
            {
               if (columnIsNullable.equals("YES"))
                  tableDefinition.append(" WITH DEFAULT NULL,\n    ");
               else
               {
                  if (columnIsNullable.equals("NO"))
                     tableDefinition.append(" NOT NULL,\n    ");
                  else
                     tableDefinition.append(",\n    ");
               }
            } 
         }
         resultSet.close();

         // Create the keys for the table.
         columnName = "";
         primaryKeys = "";
         uniqueKeys = new StringBuffer();
         foreignKeys = "";
         referenceTableName = "";
         referenceColumnName = "";
         // onUpdateRule = "";
         onDeleteRule = "";

         // Primary Keys
         resultSet = dbMetaData.getPrimaryKeys(tableMetaData.getCatalogName(1),
                                               tableMetaData.getSchemaName(1),
                                               tableMetaData.getTableName(1));
         while (resultSet.next())
         {
            columnName = resultSet.getString("COLUMN_NAME");
            primaryKeys += identifierQuoteString + columnName + identifierQuoteString + ",";
         }
         resultSet.close();

         // Unique Keys
         resultSet = dbMetaData.getIndexInfo(tableMetaData.getCatalogName(1),
                                             tableMetaData.getSchemaName(1),
                                             tableMetaData.getTableName(1), true, false);
         while (resultSet.next())
         {
            columnName = resultSet.getString("COLUMN_NAME");

            // Only place unidentitied keys in uniqe string.
            if (primaryKeys.indexOf(columnName) == -1)
               uniqueKeys.append(identifierQuoteString + columnName + identifierQuoteString + ",");
         }

         // Add Unique & Primary Keys.
         if (!(uniqueKeys.toString()).equals(""))
            tableDefinition.append("UNIQUE ("
                                   + uniqueKeys.delete((uniqueKeys.length() - 1), uniqueKeys.length())
                                   + "),\n    ");

         if (!primaryKeys.equals(""))
            tableDefinition.append("PRIMARY KEY (" 
                                   + primaryKeys.substring(0, primaryKeys.length() - 1)
                                   + "),\n    ");

         // Foreign Keys
         resultSet.close();
         resultSet = dbMetaData.getImportedKeys(tableMetaData.getCatalogName(1),
                                                tableMetaData.getSchemaName(1),
                                                tableMetaData.getTableName(1));
         
         while (resultSet.next())
         {
            columnName = resultSet.getString("FKCOLUMN_NAME");
            referenceTableName = resultSet.getString("PKTABLE_NAME");
            referenceColumnName = resultSet.getString("PKCOLUMN_NAME");

            // These rules return integer values that can not
            // be correlated to a specific rule. Default to
            // DELETE ON CASCADE.
            // onUpdateRule = resultSet.getString("UPDATE_RULE");
            // onDeleteRule = resultSet.getString("DELETE_RULE");
            onDeleteRule = "CASCADE";

            foreignKeys = identifierQuoteString + columnName + identifierQuoteString;

            tableDefinition.append("FOREIGN KEY (" + foreignKeys + ") REFERENCES "
                                   + identifierQuoteString + referenceTableName 
                                   + identifierQuoteString + "(" + identifierQuoteString
                                   + referenceColumnName + identifierQuoteString 
                                   + ") ON DELETE " + onDeleteRule);
            
            tableDefinition.append(",\n    ");
         }
         tableDefinition.delete(tableDefinition.length() - 6, tableDefinition.length());
         tableDefinition.append("\n);\n");  
      }
      catch (SQLException e)
      {
         ConnectionManager.displaySQLErrors(e,
            "TableDefinitionGenerator createDerbyTableDefinition()");
      }
      finally
      {
         try
         {
            if (resultSet != null)
               resultSet.close();
         }
         catch (SQLException sqle)
         {
            ConnectionManager.displaySQLErrors(sqle,
               "TableDefinitionGenerator createDerbyTableDefinition()");
         }
         finally
         {
            if (sqlStatement != null)
               sqlStatement.close();
         }
      }
   }
   
   //================================================================
   // Class method for creating a given H2 TABLE definition.
   //================================================================

   private void createH2TableDefinition() throws SQLException
   {
      // Class Method Instances.
      String tableType;
      String autoIncrementColumnName;
      HashMap<String, Integer> columnPrecisionHashMap;
      String columnName, columnType, columnSize;
      int columnPrecision;
      String defaultString;
      StringBuffer uniqueKeys;
      String primaryKeys, foreignKeys;
      String referenceTableName, referenceColumnName;
      String onDeleteRule;

      DatabaseMetaData dbMetaData;
      ResultSetMetaData tableMetaData;

      String sqlStatementString;
      Statement sqlStatement;
      ResultSet resultSet;
      
      // Beginning the creation of the string description
      // of the table Structure.
      
      columnPrecisionHashMap = new HashMap<String, Integer>();
      sqlStatement = null;
      resultSet = null;
      
      try
      {
         sqlStatement = dbConnection.createStatement();
         
         // Collect the table type for the table.
         
         sqlStatementString = "SELECT " + dbIdentifierQuoteString + "TABLE_TYPE"
                               + dbIdentifierQuoteString + " FROM " + dbIdentifierQuoteString
                               + "INFORMATION_SCHEMA" + dbIdentifierQuoteString + "."
                               + dbIdentifierQuoteString + "TABLES" + dbIdentifierQuoteString
                               + " WHERE " + dbIdentifierQuoteString + "TABLE_SCHEMA"
                               + dbIdentifierQuoteString + "='" + schemaName
                               + "' AND TABLE_NAME='" + tableName + "'";
         // System.out.println(sqlStatementString);
         
         resultSet = sqlStatement.executeQuery(sqlStatementString);
         
         if (resultSet.next())
            tableType = resultSet.getString(1);
         else
            tableType = "TABLE";
         
         // Drop Table Statements As Needed.
         if (DBTablesPanel.getDataExportProperties().getTableStructure())
         {
            tableDefinition.append("DROP " + tableType + " IF EXISTS " + schemaTableName + ";\n");
         }
         
         // Table Creation Statement.
         if (tableType.equals("VIEW"))
         {
            sqlStatementString = "SELECT " + dbIdentifierQuoteString + "SQL"
                                 + dbIdentifierQuoteString + " FROM " + dbIdentifierQuoteString
                                 + "INFORMATION_SCHEMA" + dbIdentifierQuoteString + "."
                                 + dbIdentifierQuoteString + "TABLES" + dbIdentifierQuoteString
                                 + " WHERE " + dbIdentifierQuoteString + "TABLE_SCHEMA"
                                 + dbIdentifierQuoteString + "='" + schemaName
                                 + "' AND TABLE_NAME='" + tableName + "'";
            // System.out.println(sqlStatementString);
            
            resultSet.close();
            resultSet = sqlStatement.executeQuery(sqlStatementString);

            if (resultSet.next())
            {
               String sqlDefinition = resultSet.getString(1).replaceAll("\n", " ");
               
               if (sqlDefinition.indexOf("/*") != -1)
                  tableDefinition.append(sqlDefinition.substring(0, sqlDefinition.indexOf("/*")).trim() + ";\n");
               else
                  tableDefinition.append(resultSet.getString(1) + ";\n");
            }
            return;
         }
         // TABLE
         else
            tableDefinition.append("CREATE " + tableType + " " + schemaTableName + " (\n    ");

         // Begin by creating the individual column field definitions.
         // Column name, data type, default, and isNullable.
         
         dbMetaData = dbConnection.getMetaData();

         sqlStatementString = "SELECT * FROM " + schemaTableName + " LIMIT 1";
         // System.out.println(sqlStatementString);

         resultSet.close();
         resultSet = sqlStatement.executeQuery(sqlStatementString);
         tableMetaData = resultSet.getMetaData();
         
         resultSet = dbMetaData.getColumns(tableMetaData.getCatalogName(1),
                                           tableMetaData.getSchemaName(1),
                                           tableMetaData.getTableName(1), "%");

         // Obtain IDENTITY column if there is one.
         
         autoIncrementColumnName = "";
         for (int i = 1; i < tableMetaData.getColumnCount() + 1; i++)
         {
            columnName = tableMetaData.getColumnName(i);
            // columnType = (tableMetaData.getColumnTypeName(i)).toUpperCase(Locale.ENGLISH);
            
            if (tableMetaData.isAutoIncrement(i))
               autoIncrementColumnName = columnName;
            
            columnPrecisionHashMap.put(columnName, Integer.valueOf(tableMetaData.getPrecision(i)));
         }

         // Now proceed with rest of structure.
         while (resultSet.next())
         {
            columnName = resultSet.getString("COLUMN_NAME");
            columnType = resultSet.getString("TYPE_NAME");
            columnSize = resultSet.getString("COLUMN_SIZE");
            
            if (columnPrecisionHashMap.get(columnName) != null)
               columnPrecision = (columnPrecisionHashMap.get(columnName)).intValue();
            else
               columnPrecision = 0;
            
            // =============
            // Column name.

            tableDefinition.append(identifierQuoteString + columnName
                                   + identifierQuoteString + " ");

            // =============
            // Column type.

            // Character Types
            if (columnType.indexOf("CHAR") != -1)
            {
               tableDefinition.append(columnType + "(" + columnSize + ")");
            }
            // Integer/BigInt Types
            else if (columnType.equals("INTEGER") || columnType.equals("BIGINT"))
            {
               tableDefinition.append(columnType);

               // Assign IDENTITY as needed.
               if (columnName.equals(autoIncrementColumnName))
               {
                  tableDefinition.append(" IDENTITY");
                  tableDefinition.append(",\n    ");
                  continue;
               }
            }
            // Double Types
            else if (columnType.equals("DOUBLE"))
            {
               // Docs indicate precision, but unable to determine.
               tableDefinition.append(columnType);
            }
            // Decimal & Numeric Types
            else if (columnType.equals("DECIMAL") || columnType.equals("NUMERIC"))
            {
               tableDefinition.append(columnType + "(" + columnPrecision
                                      + "," + resultSet.getString("DECIMAL_DIGITS") + ")");
            }
            // Blob Types
            else if (columnType.equals("BLOB") || columnType.equals("BINARY"))
            {
               tableDefinition.append(columnType + "(" + columnSize + ")");
            }
            // Clob Types
            else if (columnType.equals("CLOB"))
            {
               tableDefinition.append(columnType + "(" + columnSize + ")");
            }
            // All Others.
            else
            {
               tableDefinition.append(columnType);
            }

            // ==========================
            // Column Default & NOT NULL
            
            defaultString = resultSet.getString("COLUMN_DEF");
            
            if (defaultString != null && defaultString.equals("NULL"))
               defaultString = null;

            if (defaultString != null)
            {
               defaultString = defaultString.trim();
               
               if (defaultString.indexOf("::") != -1)
                  tableDefinition.append(" DEFAULT " 
                                         + defaultString.substring(0, defaultString.indexOf(":")));
               else
                  tableDefinition.append(" DEFAULT " + defaultString);

               if (resultSet.getString("IS_NULLABLE").equals("NO"))
                  tableDefinition.append(" NOT NULL,\n    ");
               else
                  tableDefinition.append(",\n    ");
            }
            else
            {
               if (resultSet.getString("IS_NULLABLE").equals("YES"))
                  tableDefinition.append(" DEFAULT NULL,\n    ");
               else
               {
                  if (resultSet.getString("IS_NULLABLE").equals("NO"))
                     tableDefinition.append(" NOT NULL,\n    ");
                  else
                     tableDefinition.append(",\n    ");
               }
            }
         }

         // Create the keys for the table.
         columnName = "";
         primaryKeys = "";
         uniqueKeys = new StringBuffer();
         foreignKeys = "";
         referenceTableName = "";
         referenceColumnName = "";
         // onUpdateRule = "";
         onDeleteRule = "";

         // Primary Keys
         resultSet.close();
         resultSet = dbMetaData.getPrimaryKeys(tableMetaData.getCatalogName(1),
                                               tableMetaData.getSchemaName(1),
                                               tableMetaData.getTableName(1));
         while (resultSet.next())
         {
            columnName = resultSet.getString("COLUMN_NAME");
            primaryKeys += identifierQuoteString + columnName + identifierQuoteString + ",";
         }

         // Unique Keys
         resultSet.close();
         resultSet = dbMetaData.getIndexInfo(tableMetaData.getCatalogName(1),
                                             tableMetaData.getSchemaName(1),
                                             tableMetaData.getTableName(1), true, false);
         while (resultSet.next())
         {
            columnName = resultSet.getString("COLUMN_NAME");

            // Only place unidentitied keys in uniqe string.
            if (primaryKeys.indexOf(columnName) == -1 && !columnName.equals(autoIncrementColumnName))
               uniqueKeys.append(identifierQuoteString + columnName + identifierQuoteString + ",");
         }

         // Add Unique & Primary Keys.
         if (!(uniqueKeys.toString()).equals(""))
            tableDefinition.append("UNIQUE ("
                                   + uniqueKeys.delete((uniqueKeys.length() - 1), uniqueKeys.length())
                                   + "),\n    ");

         if (!primaryKeys.equals(""))
            tableDefinition.append("PRIMARY KEY (" 
                                   + primaryKeys.substring(0, primaryKeys.length() - 1)
                                   + "),\n    ");

         // Foreign Keys
         resultSet.close();
         resultSet = dbMetaData.getImportedKeys(tableMetaData.getCatalogName(1),
                                                tableMetaData.getSchemaName(1),
                                                tableMetaData.getTableName(1));
         while (resultSet.next())
         {
            columnName = resultSet.getString("FKCOLUMN_NAME");
            referenceTableName = resultSet.getString("PKTABLE_NAME");
            referenceColumnName = resultSet.getString("PKCOLUMN_NAME");

            // These rules return integer values that can not
            // be correlated to a specific rule. Default to
            // DELETE ON CASCADE.
            // onUpdateRule = resultSet.getString("UPDATE_RULE");
            // onDeleteRule = resultSet.getString("DELETE_RULE");
            onDeleteRule = "CASCADE";

            foreignKeys = identifierQuoteString + columnName + identifierQuoteString;

            tableDefinition.append("FOREIGN KEY (" + foreignKeys + ") REFERENCES "
                                   + identifierQuoteString + referenceTableName 
                                   + identifierQuoteString + "(" + identifierQuoteString
                                   + referenceColumnName + identifierQuoteString 
                                   + ") ON DELETE " + onDeleteRule);
            if (resultSet.isLast())
               tableDefinition.append(" \n    ");
            else
               tableDefinition.append(",\n    ");
         }
         tableDefinition.delete(tableDefinition.length() - 6, tableDefinition.length());
         tableDefinition.append("\n);\n");
      }
      catch (SQLException e)
      {
         ConnectionManager.displaySQLErrors(e,
            "TableDefinitionGenerator createH2TableDefinition()");
      }
      finally
      {
         try
         {
            if (resultSet != null)
               resultSet.close();
         }
         catch (SQLException sqle)
         {
            ConnectionManager.displaySQLErrors(sqle,
               "TableDefinitionGenerator createH2TableDefinition()");
         }
         finally
         {
            if (sqlStatement != null)
               sqlStatement.close();
         }
      }
   }

   //==============================================================
   // Class method for getting CREATE TABLE definition.
   //==============================================================

   public String getTableDefinition()
   {
      // Method Instances.
      String dataSourceType;
      
      // Determine the correct table structure method to
      // apply and proceed with creating a string of
      // the table definition.
      
      dataSourceType = ConnectionManager.getDataSourceType();
      tableDefinition.delete(0, tableDefinition.length());
      
      try
      {
         // MySQL/MariaDB
         if (dataSourceType.equals(ConnectionManager.MYSQL)
             || dataSourceType.equals(ConnectionManager.MARIADB))
         {
            createSQLTableDefinition();
         }

         // PostgreSQL
         else if (dataSourceType.equals(ConnectionManager.POSTGRESQL))
         {
            createPostgreSQLTableDefinition();
         }

         // HSQL
         else if (dataSourceType.indexOf(ConnectionManager.HSQL) != -1)
         {
            createHSQLTableDefinition();
         }

         // Oracle
         else if (dataSourceType.equals(ConnectionManager.ORACLE))
         {
            createOracleTableDefinition();
         }
         
         // SQLite
         else if (dataSourceType.equals(ConnectionManager.SQLITE))
         {
            createSQLiteTableDefinition();
         }
         
         // MSAccess
         else if (dataSourceType.equals(ConnectionManager.MSACCESS))
         {
            createMSAccessTableDefinition();
         }
         
         // MSSQL
         else if (dataSourceType.equals(ConnectionManager.MSSQL))
         {
            createMSSQLTableDefinition();
         }
         
         // Apache Derby
         else if (dataSourceType.equals(ConnectionManager.DERBY))
         {
            createDerbyTableDefinition();
         }
         
         // H2
         else if (dataSourceType.indexOf(ConnectionManager.H2) != -1)
         {
            createH2TableDefinition();
         }

         // Default
         else
            tableDefinition.append(";\n");
      }
      catch (SQLException e)
      {
         ConnectionManager.displaySQLErrors(e, "TableDefinitionGenerator getTableDefinition()");
      }
      
      return (tableDefinition.toString().replaceAll(dbIdentifierQuoteString, identifierQuoteString));
   }
}
