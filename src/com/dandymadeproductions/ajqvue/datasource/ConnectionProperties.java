//=================================================================
//                 ConnectionProperties Class
//=================================================================
//    This class provides the structure for the connection properties
// storage.
//
//                << ConnectionProperties.java >>
//
//=================================================================
// Copyright (C) 2016-2017 Dana M. Proctor
// Version 1.2 08/23/2017
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
// Version 1.0 Production ConnectionProperties Class.
//         1.1 Added Class Instance connectionProperties With Getter
//             & Setter.
//         1.2 Changed getConnectionProperties() to protected. Added
//             connectionProperties to toString() Output.
//
//-----------------------------------------------------------------
//                 danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.datasource;

import java.util.Enumeration;
import java.util.Properties;

/**
 * 
 *    This ConnectionProperties class provides the structure for the
 * connection properties storage.
 * 
 * @author Dana M. Proctor
 * @version 1.2 08/23/2017
 */

public class ConnectionProperties
{
   // Class Instances.
   private String connectionURLString;
   private Properties connectionProperties;
   
   private String driver;
   private String protocol;
   private String subProtocol;
   private String host;
   private String port;
   private String db;
   private String user;
   private String password;
   private String ssh;
   
   public static final String DRIVER = "Driver";
   public static final String PROTOCOL = "Protocol";
   public static final String SUBPROTOCOL = "SubProtocol";
   public static final String HOST = "Host";
   public static final String PORT = "Port";
   public static final String DB = "DB";
   public static final String USER = "User";
   public static final String PASSWORD = "Password";
   public static final String SSH = "SSH";
   
   //==============================================================
   // ConnectionProperties Constructor
   //==============================================================

   public ConnectionProperties()
   {
      // These are additional properties passed with
      // the database name, so initialize data structure.
      // 
      // Standard: db?prop1=x&prop2=y
      //
      // HSQL Standard: db;prop1=x;prop1=y
      // HSQL Non-Standard: db;prop1=w;prop2=x?prop3=y&prop4=z
      
      connectionProperties = new Properties();
   }
   
   //==============================================================
   // Class methods to allow classes to get the connection property
   // objects.
   //==============================================================
   
   public String getConnectionURLString()
   {
      return connectionURLString;
   }
   
   protected Properties getConnectionProperties()
   {
      return connectionProperties;
   }
   
   public String getProperty(String property)
   {
      if (property.equals(DRIVER))
         return driver;
      else if (property.equals(PROTOCOL))
         return protocol;
      else if (property.equals(SUBPROTOCOL))
         return subProtocol;
      else if (property.equals(HOST))
         return host;
      else if (property.equals(PORT))
         return port;
      else if (property.equals(DB))
         return db;
      else if (property.equals(USER))
         return user;
      else if (property.equals(SSH))
         return ssh;
      else
         return "";  
   }
   
   protected String getPassword()
   {
      return password;
   }
   
   //==============================================================
   // Class methods to allow classes to set the connection property
   // objects.
   //==============================================================
   
   public void setConnectionURLString(String connectionURLString)
   {
      this.connectionURLString = connectionURLString;
   }
   
   public void setProperties(Properties properties)
   {
      connectionProperties = properties;
   }
   
   public void setProperty(String property, String value)
   {
      if (property.equals(DRIVER))
         driver = value;
      else if (property.equals(PROTOCOL))
         protocol = value;
      else if (property.equals(SUBPROTOCOL))
         subProtocol = value;
      else if (property.equals(HOST))
         host = value;
      else if (property.equals(PORT))
         port = value;
      else if (property.equals(DB))
         db = value;
      else if (property.equals(USER))
         user = value;
      else if (property.equals(PASSWORD))
         password = value;
      else if (property.equals(SSH))
         ssh = value;
      else
      {
         // Don't Know.
      }
   }
   
   //==============================================================
   // Class method to properly implement the toString() method
   // for the object. Local method overides.
   //==============================================================

   public String toString()
   {
      // Method Instances
      StringBuffer parameters;
      Enumeration<Object> propertyKeys;
      
      parameters = new StringBuffer("[ConnectionProperties: ");
      
      parameters.append("[" + ConnectionProperties.DRIVER + " = " + driver + "]");
      parameters.append("[" + ConnectionProperties.PROTOCOL + " = " + protocol + "]");
      parameters.append("[" + ConnectionProperties.SUBPROTOCOL + " = " + subProtocol + "]");
      parameters.append("[" + ConnectionProperties.HOST + " = " + host + "]");
      parameters.append("[" + ConnectionProperties.PORT + " = " + port + "]");
      parameters.append("[" + ConnectionProperties.DB + " = " + db + "]");
      parameters.append("[" + ConnectionProperties.USER + " = " + user + "]");
      parameters.append("[" + ConnectionProperties.SSH + " = " + ssh + "]");
      
      propertyKeys = connectionProperties.keys();
      
      while (propertyKeys.hasMoreElements())
      {
         String key = (String) propertyKeys.nextElement();
         
         if (!key.equals(ConnectionProperties.USER) && !key.equals(ConnectionProperties.PASSWORD))
            parameters.append("[" + key + " = " + connectionProperties.getProperty(key) + "]");
      }
      
      return parameters.toString();
   }
}