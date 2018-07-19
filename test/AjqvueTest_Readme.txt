		      Ajqvue Test Outline
			
   The basic requirements for performing these various tests involve the
setting up of two databases for each supported database application, ie,
MariaDB, PostgreSQL, HSQL, Oracle etc. The two databases for the purpose
of this outline shall be called key_tables and datatypes. To be more helpful
in differentiation in the Ajqvue connection Manager it might be useful
to prefix the database with the database application name. Example:
mariadb_key_tables and mariadb_datatypes. Of course any existing database
could be used, and then just set up the needed tables for the testing.
The installation directory for Ajqvue holds a test directory which holds
this test outline file and contains all the SQL script files that may be
used to create the databases tables used during the test phase. To outline
as an example for setup of database tables prior to testing with MySQL:

Create databases key_tables and datatypes.
Load the script mysql_keyTables.sql > into the key_tables database.
Load the script mysql_loadTest.sql > into the datatypes database.
Load the script mysqlTypes.sql > into the datatypes database.

Though the above test database tables provide the data configuration
for testing Ajqvue this does not address the various platforms that may
be used to run the application. Therefore the tests should be performed
on various platforms to insure any peculiarities are discovered. 

Note: Insure for each test case that involves a menu command that the 
      corresponding toolbar command also executes the action.

1. Database Testing (Each Supported Database Application)
 
	a. Test add, edit, delete, sort, search, advanced sort/search,
	   update field rows, and functions in the test database key_tables
	   for each of the seven key type tables. It is helpful for this
	   test to review the appropriate database key_tables SQL file to
	   understand the purpose for each of the test tables. Insure that
	   each supported action is also properly executed from the popup
	   menu in the Summary Table.
	   
	b. Test the history feature of the application for sort, search,
	   and advanced sort/search activity in the summary table.

	c. Perform a data types load test with test database datatypes table
	   xxxxtypes. Where xxxx stands for the associated database application,
	   ie. mysql, hsql.

	d. Test add, edit, sort, search, advanced sort/search, update field
	   rows, and functions in the test database datatypes table xxxxtypes.
	   Where xxxx stands for the associated database application, ie.
	   mysql, hsql.
	   
	e. Test for each database that support such, ie {H2, HSQL, SQLite, ?},
	   the creation of both memory and file databases.
	   
	f. Test for each database that support such feature the passing of
	   parameters to Database Name on login.
	   
	   ConnectionProperties.STD_PROPERTY_CHAR = "?";
	   ConnectionProperties.STD_PROPERTY_DELIMITER = "&";
	   
	   Example: dbname;?PARAMETER_NAME_1='xxx'&PARAMETER_NAME_2='xxx'.

2. Table Summary Printing

	a. Test printing of basic summary table data.
	
	b. Test page formatting, 

3. Cut, Copy, and Pasting Data

	a. Summary Table copying and pasting into external editor.
	
	b. Summary Table paste, CSV Import, from external editor.

	c. Edit/View Forms Cutting, Copying, and Pasting.
	
4. Test Query Frame Tool (Each Supported Database Application)

	a. Execute SELECT * FROM xxxxtypes. Where xxxx stands for the associated
	   database application, ie mysql, hsql.
	
	b. Execute various SELECT SQL statements on database tables. Insure
	   to try statements that contain WHERE, GROUP key words.
       
	c. Execute SELECT SQL statements on data. Example SELECT 2*2.
	   
	   Note: Oracle requires the selection from the table DUAL.
	   Note: Apache Derby Does Not Support Directly.
       
5. Test Search Database Tool (Each Supported Database Application)

	a. Execute a generic search on the each of the test databases.
    
	b. Insure tables in the list may be selected and automatically
	   displayed in the main application's window summary table.
       
	c. Insure clearing and canceling operations functions along with
	   copying, cutting, and pasting in the search phrase text field.
       
6. Preferences & Basic Import/Exports (Each Supported Database Application)

	a. Table Summary View - Change visible column fields.

	b. Table Summary View - Change number of rows shown in tab summary
	   tables.

	c. Data Export - Check the generic CSV export of table and summary
	   table. Verify the inclusion of TEXT, MEDIUMTEXT, & LONGTEXT is
	   enabled during CSV export from the Preferences | Data Export | CSV.

	d. Data Export CSV - Change the delimiter, date format and export CSV
	   data both table & summary table.
	   
	e. Data Export PDF - Check that summary data may be exported in PDF
	   format. Use the Preferences | Data Export | PDF to change various
	   parameters like Font Size/Color, Title, etc.
       
	f. Data Export SQL - Check the generic SQL export of table and summary
	   table for the xxxxtypes database table. Insure this is done for one
	   row of data and multiple rows of data. Use the Preferences Panel
	   Edit | Preferences | SQL Format options to exercise INSERT/UPDATE,
	   Singular/Plural/Explicit.
	   
	   Note: Manual gives information on the various databases support of
	         these options.
           
	g. Data Import CSV - Verify data exported in 5.c. & 5.d. can be imported
	   into same table. Change preferences import delimiter, date format
	   as needed. Check both insert and update.
       
	h. Data Import SQL - Verify data exported in 5.f. can be imported into
	   same table.

	i. Export Database and Database Scheme. Import both output files with
           Ajqvue. As an alternate check, use each database's standard management
           tool to import the database and database scheme created to verify
           conformity.
       
7.	Summary Table State Saving/Opening.

	a. Table Summary View - Insure that state of summary view table is
	   saved to directed file. Vary properties, table fields, row number,
	   sort fields, search fields, and advanced sort/search entries.
	   
	b. Table Summary View - Insure that state is restored for each saved
	   *.myj file from 7.a by opening with Ajqvue.
	   
8. Query Drop Bucket

	a. Test the addition of a Summary Table current SQL statement to the
	   Query Bucket. Insure capability to change parameters during this
	   add such as name, statement, limit, and color.
       
	b. Test the ability to view, add, edit, and delete SQL statements in
	   the list.
       
	c. Try moving multiple list items to various positions in the list
	   and also drag and drop into an external editor and one of Ajqvue's
	   plugins such as the TableFieldProfiler.
       
	d. Insure the list can be saved via the File | Save/Save As and then
	   re-opened, loaded, back into the Query Bucket. 