/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search.sample;

import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.storage.search.implementation.database.*;

/**
 * Sample code demonstrating basic usage of the <code>SearchQuery</code> 
 * interfaces.
 * <p>
 * Requires the folowing builders to be active: <code>builders</code> and 
 * <code>pools</code>.
 *
 * @author  Rob van Maris
 * @version $Revision: 1.2 $
 * @since MMBase-1.7
 */
public class QuerySampleCode {
    
    /**
     * Demo application, writes resulting SQL strings to Sytem.out.
     * <br>
     * Requires one commandline argument: the path to the MMBase config directory.
     * @param args Commandline arguments. 
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println(
            "Requires one commandline argument: the path to the MMBase config directory.");
            System.exit(1);
        }
        MMBaseContext.init(args[0], true);
        
        // Sql handler that generates SQL strings.
        SqlHandler sqlHandler 
            = new BasicSqlHandler(new java.util.HashMap());
        
        MMBase mmbase = MMBase.getMMBase();
        MMObjectBuilder images = mmbase.getBuilder("images");
        
        // Create query.
        BasicSearchQuery query = new BasicSearchQuery();
        BasicStep step1 = query.addStep(images);
        FieldDefs imagesTitle = images.getField("title");
        BasicStepField field1 = query.addField(step1, imagesTitle);
        
/* 
 Result:
        SELECT images.title AS title FROM <basename>_images images
 */
        System.out.println("Result:\n" + sqlHandler.toSql(query, sqlHandler));
        
        // Add a related step.
        InsRel insrel = mmbase.getInsRel();
        MMObjectBuilder pools = mmbase.getBuilder("pools");
        BasicRelationStep step2 = query.addRelationStep(insrel, pools);
        step2.setDirectionality(RelationStep.DIRECTIONS_DESTINATION);
        
/* 
 Result:
        SELECT images.title AS title
        FROM <basename>_images images,
            <basename>_insrel insrel,
            <basename>_pools pools
        WHERE (images.number=insrel.snumber
        AND pools.number=insrel.dnumber)
 */
        System.out.println("Result:\n" + sqlHandler.toSql(query, sqlHandler));
        
        // Add another field.
        BasicStep step3 = (BasicStep) step2.getNext();
        FieldDefs poolsName = pools.getField("name");
        BasicStepField field2 = query.addField(step3, poolsName);
        
/* 
 Result:
        SELECT images.title AS title,
            pools.name AS name
        FROM <basename>images images,
            <basename>insrel insrel,
            <basename>pools pools
        WHERE (images.number=insrel.snumber
        AND pools.number=insrel.dnumber)
 */
        System.out.println("Result:\n" + sqlHandler.toSql(query, sqlHandler));
        
        // Add nodenumber constraint.
        step3.addNode(100);
        
/* 
 Result:
        SELECT images.title AS title,
            pools.name AS name 
        FROM <basename>images images,
            <basename>insrel insrel,
            <basename>pools pools 
        WHERE pools.number IN (100) 
        AND (images.number=insrel.snumber 
        AND pools.number=insrel.dnumber)
 */
        System.out.println("Result:\n" + sqlHandler.toSql(query, sqlHandler));
        
        // Add field value constraint.
        BasicFieldValueConstraint constraint 
            = new BasicFieldValueConstraint(field1, "Logo");
        query.setConstraint(constraint);
        
/* 
 Result:
        SELECT images.title AS title,
            pools.name AS name 
        FROM <basename>images images,
            <basename>insrel insrel,
            <basename>pools pools 
        WHERE pools.number IN (100) 
        AND (images.number=insrel.snumber 
        AND pools.number=insrel.dnumber)
        AND images.title='Logo'
 */
        System.out.println("Result:\n" + sqlHandler.toSql(query, sqlHandler));
        
        // Add sort order.
        BasicSortOrder sortOrder = query.addSortOrder(field1);

/* 
 Result:
        SELECT images.title AS title,
            pools.name AS name 
        FROM test1_images images,
            test1_insrel insrel,
            test1_pools pools 
        WHERE pools.number IN (100) 
        AND (images.number=insrel.snumber 
        AND pools.number=insrel.dnumber) 
        AND images.title='Logo' 
        ORDER BY title ASC
 */
        System.out.println("Result:\n" + sqlHandler.toSql(query, sqlHandler));

        System.exit(0);
    }
    
}
