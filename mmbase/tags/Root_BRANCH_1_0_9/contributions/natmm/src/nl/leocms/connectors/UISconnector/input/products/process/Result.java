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
 * The Initial Developer of the Original Code is 'Media Competence'
 *
 * See license.txt in the root of the LeoCMS directory for the full license.
 */
package nl.leocms.connectors.UISconnector.input.products.process;

import org.mmbase.bridge.*;
import nl.leocms.connectors.UISconnector.input.products.model.*;

public class Result
{
   public static final int EXCEPTION = -1;
   public static final int ADDED = 0;
   public static final int UPDATED = 1;

   private Exception exception;
   private int status;
   private Product product;
   private Node evenementNode;

   public Result()
   {
   }

   public void setException(Exception exception)
   {
      this.exception = exception;
   }

   public void setStatus(int status)
   {
      this.status = status;
   }

   public void setProduct(Product product)
   {
      this.product = product;
   }

   public void setEvenementNode(Node evenementNode)
   {
      this.evenementNode = evenementNode;
   }

   public Exception getException()
   {
      return exception;
   }

   public int getStatus()
   {
      return status;
   }

   public Product getProduct()
   {
      return product;
   }

   public Node getEvenementNode()
   {
      return evenementNode;
   }

}
