/*
 * Copyright (c) 2006 Levi9 Global Sourcing. All Rights Reserved.
 * This software is the confidential and proprietary information of
 * Levi9 Global Sourcing. ("Confidential Information"). You shall
 * not disclose such Confidential Information and shall use it
 * only in accordance with the terms of the license agreement you
 * entered into with Levi9 Global Sourcing.
 * Levi9 Global Sourcing makes no representations or warranties about the
 * suitability of the software, either express or implied,
 * including but not limited to the implied warranties of merchantability,
 * fitness for a particular purpose, or non-infringement. Levi9 Global Sourcing
 * shall not be liable for any damages suffered by licensee as a
 * result of using, modifying or distributing this software or its
 * derivatives.
 */

package nl.didactor.proactivemail.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import nl.didactor.events.*;

import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.corebuilders.RelDef;
import org.mmbase.module.corebuilders.TypeDef;
import org.mmbase.storage.search.StepField;
import org.mmbase.storage.search.implementation.BasicFieldValueConstraint;
import org.mmbase.storage.search.implementation.BasicCompositeConstraint;
import org.mmbase.storage.search.implementation.NodeSearchQuery;
import org.mmbase.util.logging.*;

/**
 * @author p.becic
 * @javadoc
 */
public class EventManager implements DidactorEventListener {
    private static final  Logger log = Logging.getLoggerInstance(nl.didactor.proactivemail.util.EventManager.class);
    
    public void notify(Event event) {
        
        try {
            if ( event == null ) return;
            String eType = event.getEventType();
            String eValue = event.getEventValue();
            if ( eType == null || eType.length() == 0 )
                return;
            Integer number = null;
            if ( eType.compareToIgnoreCase("LOGIN") == 0 ) {
                eType = "firstuserlogin";
                if ( eValue != null ) number = Integer.valueOf(eValue);
            } else if ( eType.compareToIgnoreCase("peopleaccountcreated") == 0 ) {
                if ( eValue != null ) number = Integer.valueOf(eValue);
            } else
                return;
            createAndStoreEvent(number, eType, new Long(0), event.getUsername());
        } catch (Exception exc) {
            if ( event != null )
                log.error("Can not write event "+event.getNote()+". \r\n"+exc.toString());
            else
                log.error("Can not write null event.");
        }
    }
    
    public static void createAndStoreEvent(Integer number, String eventtype, Long eventvalue, String username) {
        if ( number == null || eventtype == null || eventtype.length() == 0 )
            return;
        if ( username == null ) username = "";
        if ( eventvalue == null ) eventvalue = new Long(0);
        MMBase mmb = (MMBase) org.mmbase.module.Module.getModule("mmbaseroot");
        try {
            //first get number of eventtype -> eventid
            MMObjectBuilder builder = mmb.getBuilder("eventtypes");
            NodeSearchQuery nsQuery = new NodeSearchQuery(builder);
            StepField field = nsQuery.getField(builder.getField("name"));
            BasicFieldValueConstraint constraint = new BasicFieldValueConstraint(field, eventtype);
            nsQuery.setConstraint(constraint);
            List list = builder.getNodes(nsQuery);
            Integer eventtypeNumber = null;
            if ( list.size() > 0) {
                eventtypeNumber = ((MMObjectNode)list.get(0)).getIntegerValue("number");
                //now get relation for people 'number' and 'eventid'
                
                builder = mmb.getBuilder("eventdatarel");
                nsQuery = new NodeSearchQuery(builder);
                BasicCompositeConstraint compConst = new BasicCompositeConstraint(BasicCompositeConstraint.LOGICAL_AND);
                field = nsQuery.getField(builder.getField("eventid"));
                constraint = new BasicFieldValueConstraint(field, eventtypeNumber);
                StepField field1 = nsQuery.getField(builder.getField("snumber"));
                BasicFieldValueConstraint constraint1 = new BasicFieldValueConstraint(field1, number);
                compConst.addChild(constraint);
                compConst.addChild(constraint1);
                nsQuery.setConstraint(compConst);
                list = builder.getNodes(nsQuery);
                if ( list.size() == 0) {
                    // if not exist, create eventdata and than relation 'eventdatarel' between 'people' and 'eventdata'
                    
                    String systemname = "system";
                    String admin = "admin";
                    MMObjectBuilder builderdata = mmb.getBuilder("eventdata");
                    MMObjectNode data = builderdata.getNewNode(systemname);
                    data.setValue("timestamp", System.currentTimeMillis()/1000);
                    data.setValue("value", eventvalue.toString());
                    data.setValue("stringvalue", username);
                    int dataNumber = builderdata.insert(systemname, data);
                    

                    RelDef reldef = mmb.getRelDef();
                    int reldefnumber = reldef.getNumberByName("eventdatarel");
                    MMObjectNode relnode = builder.getNewNode(systemname);
                    relnode.setValue("snumber", number);
                    relnode.setValue("dnumber", dataNumber);
                    relnode.setValue("eventid", eventtypeNumber);
                    relnode.setValue("rnumber", reldefnumber);
                    relnode.setValue("dir", 2);
                    builder.insert(systemname, relnode);
                } 
            } 
        } catch (Exception e) {
            log.error("Can't read eventdata table. \r\n"+e.toString());
        }
    }
}
