Ajqvue Version 2.0

Copyright 2016-2018
by Dana Proctor
http://dandymadeproductions.com

What is Ajqvue?

   Ajqvue provides an easy to use Java based interface for viewing, adding,
editing, or deleting entries in the Derby, H2, HSQL, Microsoft SQL, MSAccess,
MariaDB, MySQL, Oracle, PostgreSQL, and SQLite databases. All tables and
fields are available for access in the selected database.

   Ajqvue is the result of a condensed version of a larger project that was
needed to access an inventory database. The project from its inception was to
be constructed from code that could be run on most OS, Operating Systems,
platform independent. In addition the selected database, MySQL, was also deemed
to be chosen because of its open source nature. The final key was network enabled.
The application would need to be able to connect to the database either locally,
same machine, or a server anywhere that was Internet accessable. Java and MySQL
fit these needs.
    Ajqvue has expanded beyond the scope of MySQL and now supports alternate
databases and plugins. The application only needs two other pieces of code, the
JRE, Java Runtime Environment, and a JDBC, Java Database Connectivity, driver.
The JDBC provides the driver interface between the Java SQL, structured query
language, statements, and the database. As of the release of this version of
Ajqvue the application has been tested with the Apache Derby, H2, HSQL, MS SQL,
MSAccess, MySQL, Oracle, PostgreSQL, and SQLite databases. The application once
installed can provide a much quicker access/update to a database than a web based
interface frontend and Ajqvue supports transaction locking. If your small business,
scientific community, government agency, or educational institute wants to quickly
access one of the supported databases for viewing, adding, editing, searching,
and analyzing data Ajqvue provides an alternative that is simple and easy to use.

Dana M. Proctor
Project Manager

Features:

    Plugin Framework.
    Internationalization Support.
    Simple Easy to Use Interface.
    User Connection Preferences Saving/Managing.
    User Preferences Summary Table Column Selection & Sizing.
    User Preferences Summary Table Row Sizing.
    Access to All User Tables in Database During Session.
    Simple and Advanced Table Sorting and Searching.
    Summary Table State Saving/Loading.
    Viewing, Adding, Editing, and Deleting All Table Fields.
    Update Multiple Rows of Selected Table Field.
    Support for Table Field Function Operations.
    Support for All Basic Data Types, Including Blob/Bytea/Binary.
    Data Type Checking During Adding or Updates.
    Support for All Table Types, Including Transaction Locking.
    Support for Flushing Privileges for User root on mysql database.
    Printing of Summary Table Data, Screen View.
    Export of CSV Summary or Complete Table Data.
    Export of SQL Summary or Complete Table Structure/Data.
    Export of PDF Summary Table.
    Export of SQL Current Open Database/Scheme.
    Import of SQL Statement File.
    Import of CSV data File Into Selected Database Table via Insert/Update.
    Query Bucket for Storing/Organizing SQL Statements.
    Query Frame for Building Complex SQL Statements.
    Search Frame for Running Generic LIKE Queries on All Tables in Database.
    In Memory Data Analysis Capability

Requirements:

    Microsoft® Windows 7, Vista ?, XP, 2000, 98, NT, ME, MAC?, Linux, & X-Window
    Environment.
    Java Runtime Environment, JRE 1.8 or Newer.
    Apache Derby 10.10.2.0 - 10.13.1.1.
    H2 1.3.173 - 1.4.197.
    HSQL 2.2.5 - 2.4.1. NOTE, 1.8.x No Longer Supported.
    MSAccess MS Access 97 - 2003, ODBC-JDBC Bridge, Control Panel Data Sources.
    Microsoft SQL Server 8? - 11.00.2100, JDBC 4.0.
    MySQL 5.0.7 - 5.1.61, JDBC 5.0.7 - 5.1.42.
    MariaDB 5.5.32 - 10.0.26, JDBC 1.1.6 - 2.0.1.
    Oracle 10g - 11g JDBC 14.
    PostgreSQL 8.2.5 - 8.4.4, 9.0.1 - 9.6.3, 10.4, JDBC4 8.2-506 - 8.4-702, 9.3,
    9.4, 42.1.0 - 42.2.4.
    Xerial SQLite JDBC 3.6.20 - 3.23.1. Note, SQLite need not be installed on
    the system.

Installation Notes:

   Ajqvue is a stand alone application. To get started download and unzip the
release file to your local hard drive using a program such as WinZip, 7-Zip, Tar
or similar compression/decompression program. Extract the Ajqvue files to the
desired location on the hard disk. On a Windows system this may be any location,
but on a Unix or Linux system it will be in the home directory of the user. If
multiple users are to access the application then consult with the system
administrator of your workstation. The likely location will be in the /usr/local
or a public directory, which all users have access to for application execution.

Update Notes:

   Ajqvue updates need no special attention. Just install the latest release into
your existing directory. The only other major concern of a new release that might
be of interest is obtaining new connection parameters. Each new release that
supports a additional database will have an example configuration for the connection
parameters in the advanced login parameters. Likely changes will be included in
the reference ajqvue.xml file located in the installation directory. Often the
Driver, Protocol, SubProtocol, and Port parameters will be different for the
various database servers. To gain access to any new connection parameters one may
just open the reference ajqvue.xml file and copy and paste any additional entries
desired into your own working copy of the ajqvue.xml file. See the General Setup
Instructions below for locating this file.

General Setup Instructions

    The Ajqvue application is a Java based program and does require the JRE, to
be installed. The minimum runtime environment needs to be JRE-1.7. In addition
to the JRE an extension, JDBC, needs to be installed that allows Java to communicate
to the database. The programs are available on the Internet free of charge. Check
with the sites MS SQL, mariadb.org, dev.mysql.com, oracle.com, jdbc.postgresql.org,
and the jar file for these extensions needs to be installed to the JRE /lib/ext
directory. On a Windows system this JRE directory in normally under C:/Windows/java.
On a Linux/Unix system the directory is normally under the /usr/lib directory,
and is most easily found by typing the command "which java" in a console. Please
consult with each vendor for specific installation instructions for these JDBC
pieces of code. Similarly if other databases are to be accessed via the application
then again installed the recommended driver(s) to the Java Runtime /lib/ext
directory. The exceptions to JDBC installations are for the Derby, H2, HSQL, and
SQLite databases. Derby requires the derby.jar, derbynet.jar, and derbyclient.jar
files to placed in the Java Extension directory. H2, and HSQL use an integrated
JDBC built into the application. Ajqvue includes these databases so no additional
installation need take place. Likewise the SQLite JDBC need also not be installed
as it is a included library along with native libaries for the SQLite database.

    The new login interface and Connection Manager use a XML configuration file
for saving user preferences. Upon first running Ajqvue a reference XML file,
ajqvue.xml, located in the installation directory is read then copied to the users
home directory under a newly created directory, .ajqvue. DO NOT MODIFY THIS
REFERENCE FILE! The newly created directory On Windows ® 98 will be
'C:Windows\.ajqvue'. On Windows ® XP, Vista, and Windows 7 the new directory
will be in the user's home folder. On a Linux system the new directory will be
'/home/user/.ajqvue'. All saved changes performed in site management will be
stored in the XML file located in the above referenced directory. The user is
encouraged to make a backup of this file occasionally. The Ajqvue application
also supports internationalization. The .ajqvue directory in addition to
retaining the XML configuration file will also house the file ajqvue_locale.txt
which holds the entry that allows control of language support.

    Ajqvue requires a database to be setup to communicate with in order to work.
It is beyond the scope of this document for installing and setting up a database,
but remember you must have user rights to the database tables you are trying to
access. Please consult with your database system administrator for proper grant
rights.

Adding a Shortcut

    To add a Ajqvue icon to your desktop, right click the mouse pointer on an
unused area of your desktop to display the pop up menu and select New | Shortcut.
Choose Browse, find and double click on Ajqvue.jar, then click on Next. Type in
"Ajqvue" for the name of the shortcut, then click Finish. To run Ajqvue, either
double click on the new Ajqvue icon or use the Start | Run command.

     On a Unix or Linux system a shortcut may be created in the KDE Desktop
environment by right clicking on the desktop and selecting Create New | File |
Link to Application. Give the application a name such as "Ajqvue" in the General
tab. Next select the Application tab and fill in the description desired then
browse to the location on the disk where Ajqvue was installed and select the
Ajqvue.jar file for the Command entry. The Command entry is still not complete
and must contain the Java command. Place before the quoted Ajqvue.jar entry,
"java -jar ". Do not place quotes of any kind around this command, the quotes
only contain the Jar file location. Example: java -jar '/home/~user/Ajqvue/Ajqvue.jar'.
Finally select the Work Path as the directory where installation took place.

     The Ajqvue application may also be run directly from a command/console
window by typing in the command "java -jar Ajqvue.jar" while in the installation
directory. A Linux/Unix environment will require the file location to be
specified by "./Ajqvue.jar", remember this must be done with a console that is
running within a X Window environment like KDE or Gnome.

Copyright Notice:

This program is licensed under the GNU GPL.

Credits (in chronological order):

    Dana Proctor - Project Manager

Special thanks to:
L.S. Proctor who provided support and mental clarity.

Version History:

Production (2016-09-17):
Version 1.0  Code Review and Cleanup. Libraries and TableFieldProfiler Plugin
             Updated. Bug Fixes for Query Bucket Feature.
        1.11 Minor Release to Mainly Highlight Updated QueryBuilder Plugin.
        1.15 Maintanence With Some Minor Fixes. Review/Testing Current Revisions
             of Supported Databases.
        2.0	 Major Maintenance, Cleanup, and Consolidation of the Code Base
             Driven a DB_To_FileMemoryDB Plugin and SQLite Affinity.
