/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.implementation.*;
import org.mmbase.security.*;

/**
 * Abstract implementation of CloudContext which implements a number of methods with reasonable defaults, or based on other methods.
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.9.2
 */

public abstract class AbstractCloudContext implements CloudContext {

    protected final BasicStringList clouds        = new BasicStringList();
    private static final Authentication NOAUTHENTICATION = new NoAuthentication();


    static {
        org.mmbase.util.xml.AbstractBuilderReader.registerSystemIDs();
        org.mmbase.util.xml.AbstractBuilderReader.registerPublicIDs();
    }


    @Override
    public ModuleList getModules() {
        return BridgeCollections.EMPTY_MODULELIST;
    }

    @Override
    public Module getModule(String name) throws NotFoundException {
        throw new NotFoundException();
    }

    @Override
    public boolean hasModule(String name) {
        return false;
    }

    @Override
    public StringList getCloudNames() {
        return BridgeCollections.unmodifiableStringList(clouds);
    }

    @Override
    public String getDefaultCharacterEncoding() {
        return "UTF-8";
    }

    @Override
    public Locale getDefaultLocale() {
        return Locale.getDefault();
    }

    @Override
    public TimeZone getDefaultTimeZone() {
        return TimeZone.getDefault();
    }


    @Override
    public FieldList createFieldList() {
        return new BasicFieldList();
    }

    protected String getDefaultCloudName() {
        return getCloudNames().get(0);
    }


    @Override
    public Cloud getCloud(String name) {
        return getCloud(name, "anonymous", new HashMap<String, Object>());
    }

    @Override
    public Cloud getCloud(String name, String authenticationType, Map<String, ?> loginInfo) throws NotFoundException {
        UserContext uc =  ((Authentication) getAuthentication()).login(authenticationType, loginInfo, new Object[] {});
        return getCloud(name, uc);
    }


    @Override
    public NodeList createNodeList() {
        return getCloud(getDefaultCloudName()).createNodeList();
    }

    @Override
    public RelationList createRelationList() {
        return getCloud(getDefaultCloudName()).createRelationList();
    }


    @Override
    public NodeManagerList createNodeManagerList() {
        return getCloud(getDefaultCloudName()).createNodeManagerList();
    }

    @Override
    public RelationManagerList createRelationManagerList() {
        return getCloud(getDefaultCloudName()).createRelationManagerList();
    }
    @Override
    public ModuleList createModuleList() {
        return new BasicModuleList();
    }

    @Override
    public StringList createStringList() {
        return new BasicStringList();
    }

    @Override
    public AuthenticationData getAuthentication() {
        return NOAUTHENTICATION;
    }

    @Override
    public ActionRepository getActionRepository() {
        return ActionRepository.getInstance();
    }

    @Override
    public boolean isUp() {
        return true;
    }

    @Override
    public CloudContext assertUp() {
        return this;
    }


 }
