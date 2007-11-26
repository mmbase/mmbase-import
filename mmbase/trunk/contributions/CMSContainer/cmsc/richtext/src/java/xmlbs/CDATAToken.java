/*
 * xmlbs
 *
 * Copyright (C) 2002  R.W. van 't Veer
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA.
 */

package xmlbs;

/**
 * Token to represent and hold CDATA blocks.
 * 
 * @see <A href="http://www.w3.org/TR/REC-xml#sec-cdata-sect">XML: CDATA
 *      Sections</A>
 * @author R.W. van 't Veer
 * @version $Revision: 1.3 $
 */
public class CDATAToken implements Token {
   /** character data */
   private String data;


   /**
    * @param data
    *           CDATA content without &lt;![CDATA[ and ]]&gt;
    */
   public CDATAToken(String data) {
      this.data = data;
   }


   /**
    * @return CDATA content
    */
   public String getData() {
      return data;
   }


   /**
    * @return wellformed CDATA block
    */
   @Override
   public String toString() {
      return "<![CDATA[" + getData() + "]]>";
   }
}
