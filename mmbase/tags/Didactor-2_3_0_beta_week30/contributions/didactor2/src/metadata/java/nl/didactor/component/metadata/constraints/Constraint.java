package nl.didactor.component.metadata.constraints;

import java.util.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Unificated constrain for all constraint-modes of the metaeditor
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company: Avantlab.com</p>
 * <p>
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * </p>
 * @author Alex Zemskov, Henk Hangyi
 * @version 1.0
*/


public class Constraint {
    private static Logger log = Logging.getLoggerInstance(Constraint.class);

    public final static int NOT_USED = 0;
    public final static int MANDATORY = 1;
    public final static int LIMITED = 2;
    public final static int FORBIDDEN = 3;
    public final static int HIDDEN = 3;
    public final static String [] typeString = { "NOT_USED", "FORBIDDEN", "MANDATORY", "LIMITED" };

    public final static int EVENT_METADEFINITION_ITSELF = 0;
    public final static int EVENT_METASTANDART_CONSTRAINT_RELATION = 1;
    public final static int EVENT_VOCABULARY_CONSTRAINT_RELATION = 2;
    public final static int EVENT_VOCABULARY_TO_VOCABULARY_RELATION = 3;
    public final static String [] eventString = { "EVENT_METADEFINITION_ITSELF", "EVENT_METASTANDART_CONSTRAINT_RELATION", "EVENT_VOCABULARY_CONSTRAINT_RELATION", "EVENT_VOCABULARY_TO_VOCABULARY_RELATION" };

    private int min = 0;
    private int max = 9999;
    private int type;
    private int position;
    private int event;
    private Object eventObject;



    /**
     * Contains link to other constraints, so they can compose a chain
     */
    private ArrayList constraintsChain = null;




    public Constraint(int type, int event){
/*
        log.debug("creating constraint " + typeString[type] + " for event " +  eventString[event]);
        if(type<MANDATORY || type>FORBIDDEN) {
          log.error("Not supported type " + type);
        }
        if(event<EVENT_METADEFINITION_ITSELF || event>EVENT_VOCABULARY_TO_VOCABULARY_RELATION) {
          log.error("Not supported event " + event);
        }
*/
        this.type = type;
        this.event = event;
    }




    public void setMax(int max) {
        this.max = max;
    }
    public int getMax() {
        return max;
    }



    public void setMin(int min) {
        this.min = min;
    }
    public int getMin() {
        return min;
    }



    public void setPosition(int position) {
        this.position = position;
    }
    public int getPosition() {
        return position;
    }




    public int getType() {
        return type;
    }
    public int getEvent(){
        return event;
    }


    public void setEventObject(Object eventObject){
        this.eventObject = eventObject;
    }
    public Object getEventObject(){
        return eventObject;
    }



    public void setConstraintsChain(ArrayList constraintsChain){
        this.constraintsChain = constraintsChain;
    }
    public ArrayList getConstraintsChain(){
        return constraintsChain;
    }


}



