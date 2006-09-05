/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.services.sitemanagement;

import java.io.Serializable;

import net.sf.mmapps.commons.beans.MMBaseNodeMapper;

import org.mmbase.bridge.*;
import org.mmbase.core.event.NodeEvent;
import org.mmbase.core.event.RelationEvent;

import com.finalist.cmsc.beans.om.*;
import com.finalist.cmsc.navigation.PortletUtil;


public class PortletCacheEntryFactory extends MMBaseCacheEntryFactory {
    
    public PortletCacheEntryFactory() {
        super(PortletUtil.PORTLET);
        registerListener(PortletUtil.PORTLETPARAMETER);
        registerListener(PortletUtil.NODEPARAMETER);
    }

    protected Serializable loadEntry(Serializable key) throws Exception {
        return loadPortlet((Integer) key);
    }

    private Portlet loadPortlet(Integer key) {
        Node portletNode = getNode(key);

        Portlet portlet = (Portlet) MMBaseNodeMapper.copyNode(portletNode, Portlet.class);
        
        Node definition = PortletUtil.getDefinition(portletNode);
        portlet.setDefinition(definition.getNumber());

        NodeList vlist = PortletUtil.getPortletViews(portletNode);
        for (NodeIterator iter = vlist.nodeIterator(); iter.hasNext();) {
            Node viewNode = iter.nextNode();
            portlet.addView(viewNode.getNumber());
        }

        NodeList plist = PortletUtil.getPortletParameters(portletNode);
        for (NodeIterator iter = plist.nodeIterator(); iter.hasNext();) {
            Node paramNode = iter.nextNode();
            PortletParameter param = (PortletParameter) MMBaseNodeMapper.copyNode(paramNode, PortletParameter.class);
            portlet.addPortletparameter(param);
        }

        NodeList pnodeslist = PortletUtil.getNodeParameters(portletNode);
        for (NodeIterator iter = pnodeslist.nodeIterator(); iter.hasNext();) {
            Node paramNode = iter.nextNode();
            NodeParameter param = (NodeParameter) MMBaseNodeMapper.copyNode(paramNode, NodeParameter.class);
            portlet.addPortletparameter(param);
        }
        return portlet;
    }

    /**
     * @see net.sf.mmapps.commons.portalImpl.services.sitemanagement.MMBaseCacheEntryFactory#getKey(org.mmbase.core.event.NodeEvent)
     */
    @Override
    protected Integer getKey(NodeEvent event) {
        int nodeNumber = event.getNodeNumber();
        if (isNodeEvent(event, PortletUtil.PORTLET)) {
            return new Integer(nodeNumber);
        }
        if (isNodeEvent(event, PortletUtil.PORTLETPARAMETER)
                || isNodeEvent(event, PortletUtil.NODEPARAMETER)) {
            
            if (event.getType() !=  NodeEvent.TYPE_DELETE) {
                Node parameter = getNode(nodeNumber);
                if (parameter != null) {
                    Node portlet = PortletUtil.getPortletForParameter(parameter);
                    if (portlet != null) {
                        return new Integer(portlet.getNumber());
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    protected Integer getKey(RelationEvent event) {
        int nodeNumber = event.getRelationSourceNumber();
        if (isRelationEvent(event, PortletUtil.PORTLET)) {
            return new Integer(nodeNumber);
        }
        if (isRelationEvent(event, PortletUtil.PORTLETPARAMETER)
                || isRelationEvent(event, PortletUtil.NODEPARAMETER)) {
            
            Node parameter = getNode(nodeNumber);
            if (parameter != null) {
                Node portlet = PortletUtil.getPortletForParameter(parameter);
                if (portlet != null) { 
                    return new Integer(portlet.getNumber());
                }
            }
        }
        return null;
    }
    
    /**
     * @see net.sf.mmapps.commons.portalImpl.services.sitemanagement.MMBaseCacheEntryFactory#isNodeEvent(org.mmbase.core.event.NodeEvent)
     */
    @Override
    protected boolean isNodeEvent(NodeEvent event) {
        return isNodeEvent(event, PortletUtil.PORTLET)
            || isNodeEvent(event, PortletUtil.PORTLETPARAMETER)
            || isNodeEvent(event, PortletUtil.NODEPARAMETER);
    }

    /**
     * @see net.sf.mmapps.commons.portalImpl.services.sitemanagement.MMBaseCacheEntryFactory#isRelationEvent(org.mmbase.core.event.RelationEvent)
     */
    @Override
    protected boolean isRelationEvent(RelationEvent event) {
        return isRelationEvent(event, PortletUtil.PORTLET)
            || isRelationEvent(event, PortletUtil.PORTLETPARAMETER)
            || isRelationEvent(event, PortletUtil.NODEPARAMETER);
    }
}
