/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search;

import org.mmbase.storage.search.FieldCompareConstraint;

/**
 * This class can solve the following.

   = PROBLEM ==

The following query will be fired upon the database when somebody
tries to login:

  8000ms:
    SELECT otype,owner,number,firstname,account,lastname,email,description,password
    FROM vpro4_users users WHERE lowerEmail(email)='<USER>' AND lowerEmail(password)='<PASSWORD>'

The lower-function is slowing down the login-procedure, because the lower-function
will force a sequential-scan.

So an functional index should be used to query the table, but informix can't put an index
on a table with a function which is not variant;

-= SOLUTION =-

Use a wrapper to facilitate the variant version of lower and use this to query the database.
Squirrel-the-database-client seems to have a problem with these kinds of queries; use the
utility classes in cinema-importers -> importer -> CreateProcedure

 - create an notvariant function of lower:
   javac CreateProcedure.java && java -cp /usr/local/SQuirreL\ SQL\ Client/lib/ifxjdbc.jar:. CreateProcedure

   CREATE FUNCTION lowerNotVariant(field VARCHAR(255))
    RETURNING VARCHAR(255) WITH (NOT VARIANT);
          RETURN LOWER(field);
   END FUNCTION;

 - set an index on the field to be queried:
   CREATE INDEX vpro4_users_email_lower on vpro4_users(lowerNotVaraint(email));

 - now query the table with full-speed:

   33ms: SELECT otype,owner,number,firstname,account,lastname,email,description,password
         FROM vpro4_users users WHERE lowerNotVariant(email)='<USER>' AND lowerNotVariant(password)='<PASSWORD>'
README.txt (END)
 *
 * @author Marcel Maatkamp
 * @version $Id$
 * @since MMBase-1.8.5
 */
public interface FunctionValueConstraint extends FieldCompareConstraint {

    public String getFunction();

    /**
     * Gets the value to compare with.
     * Depending on the field type, the value is of type
     * <code>String</code> or <code>Number</code>.
     * <p>
     * If the associated field type is of string type, when used in
     * combination with the operator <code>LIKE</code>, this may contain the
     * following wildcard characters as well:
     * <ul>
     * <li>% for any string
     * <li>_ for a single character
     * </ul>
     */
    Object getValue();

    /**
     * Returns a string representation of this FunctionValueConstraint.
     * The string representation has the form
     * "FunctionValueConstraint(inverse:&lt:inverse&gt;, field:&lt;field&gt;,
     *  casesensitive:&lt;casesensitive&gt;, operator:&lt;operator&gt;,
     *  value:&lt;value&gt;)"
     * where
     * <ul>
     * <li><em>&lt;inverse&gt;</em>is the value returned by
     *      {@link #isInverse isInverse()}
     * <li><em>&lt;field&gt;</em> is the field alias returned by
     *     <code>FieldConstraint#getField().getAlias()</code>, or
     *     <code>FieldConstraint#getField().getFieldName()</code>
     *      when the former is <code>null</code>.
     * <li><em>&lt;casesensitive&gt;</em> is the value returned by
     *     {@link FieldConstraint#isCaseSensitive isCaseSensitive()}
     * <li><em>&lt;operator&gt;</em> is the value returned by
     *     (@link FieldCompareConstraint#getOperator getOperator()}
     * <li><em>&lt;value&gt;</em> is the value returned by
     *     {@link #getValue getValue()}
     * </ul>
     *
     * @return A string representation of this FunctionValueConstraint.
     */
    public String toString();

}
