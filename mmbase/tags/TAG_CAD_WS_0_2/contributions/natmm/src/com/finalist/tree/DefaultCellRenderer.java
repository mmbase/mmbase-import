/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is LeoCMS.
 *
 * The Initial Developer of the Original Code is
 * 'De Gemeente Leeuwarden' (The dutch municipality Leeuwarden).
 *
 * See license.txt in the root of the LeoCMS directory for the full license.
 */
package com.finalist.tree;

import java.io.PrintWriter;

/**
 * @author Edwin van der Elst
 * Date :Sep 15, 2003
 * 
 */
public class DefaultCellRenderer extends TreeCellRendererAdapter {

   /* (non-Javadoc)
    * @see com.finalist.tree.TreeCellRenderer#render(java.lang.Object)
    */
   public void render(Object node, PrintWriter out) {
      // TODO Auto-generated method stub
     out.print( node.toString());
   }

}
