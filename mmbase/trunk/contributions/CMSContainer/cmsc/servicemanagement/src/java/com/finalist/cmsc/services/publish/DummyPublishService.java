/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.services.publish;

import org.mmbase.bridge.Node;


public class DummyPublishService extends PublishService {

    @Override
    public boolean isPublishable(Node node) {
        return false;
    }

    @Override
    public boolean isPublished(Node node) {
        return false;
    }

    @Override
    public void publish(Node node) {
        // nothing
    }

    @Override
    public void remove(Node node) {
        // nothing
    }

    @Override
    public void unpublish(Node node) {
        // nothing
    }

}
