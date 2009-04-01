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
package org.mmbase.remotepublishing.builders;

import org.mmbase.remotepublishing.CloudManager;
import org.mmbase.remotepublishing.util.PublishUtil;

import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.corebuilders.InsRel;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class PublishRelationBuilder extends InsRel {

    private static Logger log = Logging.getLoggerInstance(PublishRelationBuilder.class.getName());

    private boolean publish = false;

    @Override
    public boolean init() {
        String publishStr = getInitParameter("publish");
        if (publishStr == null || "".equals(publishStr.trim()) 
                || "false".equalsIgnoreCase(publishStr)) {
            log.info(getTableName() + " is inactive");
        }
        else {
            if ("true".equalsIgnoreCase(publishStr)) {
                publish = true;
                log.info(getTableName() + " is active");
            }
            else {
                String defaultCloud = CloudManager.getDefaultCloudName();
                if (publishStr.equals(defaultCloud)) {
                    publish = true;
                    log.info(getTableName() + " is active for local mmbase instance");
                }
            }
        }
        return super.init();
    }

    @Override
    public int insert(String owner, MMObjectNode node) {
        int number = super.insert(owner, node);
        if (number != -1) {
            if (publish) {
                PublishUtil.publishOrUpdateNode(number);
            }
        }
        return number;
    }

    @Override
    public boolean commit(MMObjectNode objectNode) {
        boolean retval = super.commit(objectNode);

        if (publish) {
            PublishUtil.publishOrUpdateNode(objectNode);
        }
        return retval;
    }

    @Override
    public void removeNode(MMObjectNode objectNode) {
        if (publish) {
            PublishUtil.removeNode(objectNode);
        }
        super.removeNode(objectNode);
    }

}
