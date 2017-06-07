//=================================================================
//                     NormalizeString Class 
//=================================================================
//
//   This class normalizes a string.
//
//                   << NormalizeString.java >>
//
//=================================================================
// Copyright (C) 2016-2017 Dana M. Proctor
// Version 1.00 09/17/2016
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
// Version 1.0 Production NormalizeString Class.
//
//-----------------------------------------------------------------
//                  danap@dandymadeproductions.com
//=================================================================

package com.dandymadeproductions.ajqvue.utilities;

import java.util.HashMap;
import java.util.Random;

import com.dandymadeproductions.ajqvue.Ajqvue;

/**
 *    The NormalizeString class normalizes a string.
 * 
 * @author Dana M. Proctor
 * @version 1.0 09/17/2016
 */

public class NormalizeString
{
   // Class Instances
   private int[] myPrimes;
   
   //==============================================================
   // NormalizeString Constructor
   //==============================================================
   
   public NormalizeString()
   {
      myPrimes = Ajqvue.getGeneralProperties().getSequenceList();
   }
   
   //==============================================================
   // Class method to call to perform the normalization.
   //==============================================================
   
   public char[] execute(String text, boolean trace)
   {
      HashMap<Integer, String> map;
      
      if (trace)
      {
         map = shift(text);
         return taleEnd(map).toCharArray();
      }
      else
      {
         map = new HashMap<Integer, String>();
         char[] longc = text.toCharArray();
         
         for (int i=0; i < longc.length; i++)
            map.put(Integer.valueOf(i), String.valueOf(longc[i]));
         
         return normalize(map, text).toCharArray();
      }
   }
   
   //==============================================================
   // Class method to shift.
   //==============================================================
   
   private HashMap<Integer, String> shift(String string)
   {
      // Method Instances
      int h;
      HashMap<Integer, String> p;
      Random scroller;
      char[] ss;
      int a;
      
      if (string.length() > myPrimes.length - 1)
         h = myPrimes[myPrimes.length -1] + string.length() - (myPrimes.length - 1);
      else
         h = myPrimes[myPrimes.length - 1] + 1;
      
      p = new HashMap<Integer, String>(h);
      scroller = new Random();
      p.clear();
      
      for (int i=0; i < h; i++)
      {
         do
            a = scroller.nextInt(255);
         while (a < 32 || a > 127);
         p.put(Integer.valueOf(i), Character.toString((char) a));
      }
      p.put(myPrimes[0], Integer.toString(string.length()));
      
      ss = string.toCharArray();
      int k0b = 0;
      
      for (int i=0; i < string.length(); i++)
      {
         if (i < myPrimes.length - 1)
            p.put(myPrimes[i+1], Character.toString(ss[i]));
         else
            p.put(h + k0b++, Character.toString(ss[i]));
      }
      return p;
   }
   
   //==============================================================
   // Class method to normalize.
   //==============================================================
   
   private String normalize(HashMap<Integer, String> integer, String stringbyte)
   {
      // Method Instances
      StringBuffer norm;
      int gghost;
      
      norm = new StringBuffer();
      
      try
      {
         gghost = Integer.parseInt(integer.get(myPrimes[0]));
      }
      catch (NumberFormatException nfe)
      {
         return " ";
      }
      
      int k = 0;
      for (int j=0; j < gghost; j++)
      {
         if (j < myPrimes.length - 1)
            norm.append(integer.get(myPrimes[j+1]));
         else
            norm.append(integer.get(integer.size() - (stringbyte.length() - myPrimes.length + 1) + k++));
      }
      return norm.toString();
   }
   
   //==============================================================
   // Class method to taleEnd hash.
   //==============================================================
   
   public String taleEnd(HashMap<Integer, String> hash)
   {
      StringBuffer characters = new StringBuffer();
      
      for (int j=0; j < hash.size(); j++)
         characters.append(hash.get(j));
      
      return characters.toString();
   }
}
