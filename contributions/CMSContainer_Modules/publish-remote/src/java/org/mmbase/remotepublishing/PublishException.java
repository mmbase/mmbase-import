/*
 * MMBase Remote Publishing
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 */
package org.mmbase.remotepublishing;


public class PublishException extends Exception {
    
   private static final long serialVersionUID = -5600625093903580389L;

   /**
    * @param message exception message
    */
    public PublishException(final String message) {
        super(message);
    }
}
