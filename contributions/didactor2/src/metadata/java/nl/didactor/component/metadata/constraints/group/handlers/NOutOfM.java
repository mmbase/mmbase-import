package nl.didactor.component.metadata.constraints.group.handlers;

import java.util.ArrayList;

import org.mmbase.bridge.*;
import nl.didactor.taglib.*;

import nl.didactor.metadata.util.*;
import nl.didactor.component.metadata.constraints.group.HandlerInterface;


import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Checks the number of not empty metadefinitions
 *
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
 * @version $Id: NOutOfM.java,v 1.2 2007-05-11 12:34:03 michiel Exp $
*/


public class NOutOfM implements HandlerInterface{

    private static Logger log = Logging.getLoggerInstance(MetaDataHelper.class);

    public ArrayList check(Node nodeGroupConstraint, Node nodeObject, String sLocale){
        log.debug("NOutOfM.check() for GroupConstraint=" + nodeGroupConstraint.getNumber() + ", Object=" + nodeObject.getNumber() + ", locale=" + sLocale);

        ArrayList arliResult = new ArrayList();

        Cloud cloud = nodeGroupConstraint.getCloud();

        int iMin = nodeGroupConstraint.getIntValue("minvalues");
        int iMax = nodeGroupConstraint.getIntValue("maxvalues");

        //Fields from MetaDefinition node
        NodeList nl = cloud.getList("" + nodeGroupConstraint.getNumber(),
                                    "group_constraints,metadefinition",
                                    "metadefinition.number",
                                    null,
                                    null, null, null, true);

        int iCounter = 0;
        String sMetaDefinitionsList = "";
        for(int n = 0; n < nl.size(); n++){
            Node nodeMetaDefinition = cloud.getNode(nl.getNode(n).getStringValue("metadefinition.number"));

            if(!"".equals(sMetaDefinitionsList)){
                sMetaDefinitionsList += ",";
            }
            sMetaDefinitionsList += nodeMetaDefinition.getStringValue("name");

            if (!MetaDataHelper.metaHelpers[MetaDataHelper.getIType(nodeMetaDefinition)].isEmpty(nodeMetaDefinition, nodeObject)){
                iCounter++;
            }
        }
        log.debug("NOutOfM.check(): " + iCounter + " MetaDefinitions are filled in. Constraints [" + iMin + "," + iMax + "]");

        if((iCounter < iMin) || (iCounter > iMax)){
            TranslateTable tt = new TranslateTable(org.mmbase.util.LocalizedString.getLocale(sLocale));

            String sErrorReport = null;

            if(iCounter < iMin){
                sErrorReport = tt.translate("metadata.form_error_group_constraints_min_NOutOfM");
                sErrorReport = sErrorReport.replaceFirst("\\{\\$\\$\\$\\}", "" + iMin);
            }

            if(iCounter > iMax){
                sErrorReport = tt.translate("metadata.form_error_group_constraints_max_NOutOfM");
                sErrorReport = sErrorReport.replaceFirst("\\{\\$\\$\\$\\}", "" + iMax);
            }

            sErrorReport = sErrorReport.replaceFirst("\\{\\*\\*\\*\\}", sMetaDefinitionsList);
            log.debug("NOutOfM.check(): Error report=" + sErrorReport);

            arliResult.add(sErrorReport);
        }

        return arliResult;
    }
}
