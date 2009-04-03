package nl.didactor.component.metadata.constraints;

import org.mmbase.bridge.*;
import nl.didactor.metadata.util.*;
import nl.didactor.taglib.*;

/**
 * This class contains error report
 *
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
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
 * @author Alex Zemskov
 * @version $Id: Error.java,v 1.3 2007-05-11 12:34:03 michiel Exp $
 */





public class Error {

    public final static int FORBIDDEN = 0;
    public final static int MANDATORY = 1;
    public final static int LIMITED = 2;
    public final static int FORMAT = 3;



    private int type;
    private Constraint constraint;
    private Node nodeMetaDefinition;
    private TranslateTable tt = null;
    private String sLocale = "nl"; // WTF!

    public Error(Node nodeMetaDefinition, int type, Constraint constraint){
        this.nodeMetaDefinition = nodeMetaDefinition;
        this.type = type;
        this.constraint = constraint;
    }


    public int getType() {
        return type;
    }

    public int getMin() {
        return constraint.getMin();
    }

    public int getMax() {
        return constraint.getMax();
    }



    public Node getMetaDefinition() {
        return nodeMetaDefinition;
    }


    public String getErrorReport(){
        if(tt == null){
            this.tt = new TranslateTable(org.mmbase.util.LocalizedString.getLocale(this.sLocale));
        }

        String sBundleKey = "metadata.form_error";

        if(constraint.getEvent() == Constraint.EVENT_VOCABULARY_TO_VOCABULARY_RELATION){
            sBundleKey += "_vocabulary_to_vocabulary";
        }
        else{
            switch(nodeMetaDefinition.getIntValue("type")){
                case MetaDataHelper.DATE_TYPE:{
                    sBundleKey += "_date";
                    break;
                }
                case MetaDataHelper.DURATION_TYPE:{
                    sBundleKey += "_duration";
                    break;
                }
                case MetaDataHelper.LANGSTRING_TYPE:{
                    sBundleKey += "_langstring";
                    break;
                }
                case MetaDataHelper.VOCABULARY_TYPE:{
                    sBundleKey += "_vocabulary";
                    break;
                }
                default:{
                    new Exception("wrong metadefinition type in node ID=" + this.nodeMetaDefinition.getNumber());
                }
            }

            if(constraint.getEvent() == Constraint.EVENT_VOCABULARY_CONSTRAINT_RELATION){
                sBundleKey += "_event_vocabulary";
            }
        }

        if(this.type == Error.FORBIDDEN){
            sBundleKey += "_forbidden";
        }
        if(this.type == Error.MANDATORY){
            sBundleKey += "_required";
        }
        if(this.type == Error.LIMITED){
            sBundleKey += "_limited";
        }

//        System.out.println(sBundleKey);
        String sErrorReport = nodeMetaDefinition.getStringValue("name") + ": " + tt.translate(sBundleKey);
//        System.out.println(sErrorReport);

        if(this.type == Error.LIMITED){
            sErrorReport = sErrorReport.replaceFirst("\\{\\$\\$\\$\\}", "" + this.getMin());
            sErrorReport = sErrorReport.replaceFirst("\\{\\$\\$\\$\\}", "" + this.getMax());
        }

        if(constraint.getEvent() == Constraint.EVENT_VOCABULARY_CONSTRAINT_RELATION){
            Node[] eventObject = (Node[]) constraint.getEventObject();
            sErrorReport = sErrorReport.replaceFirst("\\{\\*\\*\\*\\}", eventObject[0].getStringValue("name"));
            sErrorReport = sErrorReport.replaceFirst("\\{\\*\\*\\*\\}", eventObject[1].getStringValue("value"));
        }

        if(constraint.getEvent() == Constraint.EVENT_VOCABULARY_TO_VOCABULARY_RELATION){
            Node[] eventObject = (Node[]) constraint.getEventObject();
            sErrorReport = sErrorReport.replaceFirst("\\{\\*\\*\\*\\}", eventObject[1].getStringValue("name"));
            sErrorReport = sErrorReport.replaceFirst("\\{\\*\\*\\*\\}", eventObject[2].getStringValue("value"));
            sErrorReport = sErrorReport.replaceFirst("\\{###\\}", eventObject[0].getStringValue("value"));
        }

//        System.out.println(sErrorReport);
        return sErrorReport;
    }

}
