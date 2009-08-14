/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.dummy;

import java.util.*;
import java.util.concurrent.*;
import org.mmbase.bridge.*;
import org.mmbase.datatypes.DataType;
import org.mmbase.bridge.implementation.*;
import org.mmbase.security.*;
import org.mmbase.util.*;
import org.mmbase.util.functions.*;
import org.xml.sax.InputSource;
import org.w3c.dom.Document;

/**
 * @author  Michiel Meeuwissen
 * @version $Id: MapNode.java 36154 2009-06-18 22:04:40Z michiel $
 * @since   MMBase-1.9.2
 * @todo    EXPERIMENTAL
 */

public class DummyCloudContext implements CloudContext {

    private static final DummyCloudContext virtual = new DummyCloudContext();
    private static int lastNodeNumber = 0;

    public static DummyCloudContext getInstance() {
        return virtual;
    }

    private final Authentication authentication = new NoAuthentication();
    private final BasicStringList clouds = new BasicStringList();


    final Map<Integer, Map<String, Object>>   nodes              = new ConcurrentHashMap<Integer, Map<String, Object>>();
    final Map<Integer, String>                nodeTypes          = new ConcurrentHashMap<Integer, String>();
    final Map<String,  Map<String, DataType>> nodeManagers       = new ConcurrentHashMap<String, Map<String, DataType>>();

    final Map<String,  DummyBuilderReader>    builders           = new ConcurrentHashMap<String, DummyBuilderReader>();


    DummyCloudContext() {
        clouds.add("mmbase");
    }

    public void addCore() throws java.io.IOException {
        DummyCloudContext.getInstance().addNodeManager(DummyBuilderReader.getBuilderLoader().getInputSource("core/typedef.xml"));
        DummyCloudContext.getInstance().addNodeManager(DummyBuilderReader.getBuilderLoader().getInputSource("core/typerel.xml"));
        DummyCloudContext.getInstance().addNodeManager(DummyBuilderReader.getBuilderLoader().getInputSource("core/reldef.xml"));
        DummyCloudContext.getInstance().addNodeManager(DummyBuilderReader.getBuilderLoader().getInputSource("core/object.xml"));
        DummyCloudContext.getInstance().addNodeManager(DummyBuilderReader.getBuilderLoader().getInputSource("core/insrel.xml"));
    }

    public void addNodeManager(String name, Map<String, DataType> map) {
        nodeManagers.put(name, map);
    }

    public void addNodeManager(InputSource source) {
        synchronized(builders) {
            DummyBuilderReader reader = new DummyBuilderReader(source);
            addNodeManager(reader);

        }
    }

    protected void addNodeManager(DummyBuilderReader reader) {
        Map<String, DataType> map = new HashMap<String, DataType>();
        for (Field f : reader.getFields()) {
            map.put(f.getName(), f.getDataType());
        }
        addNodeManager(reader.getName(), map);
        builders.put(reader.getName(), reader);
    }
    public void addNodeManagers(ResourceLoader directory) throws java.io.IOException {
        for (String builder : directory.getResourcePaths(ResourceLoader.XML_PATTERN, true)) {
            synchronized(builders) {
                DummyBuilderReader reader = new DummyBuilderReader(directory.getInputSource(builder));
                if (reader.getRootElement().getTagName().equals("builder")) {
                    try {
                        addNodeManager(reader);
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }
            }
        }
    }



    public synchronized int addNode(Map<String, Object> map, String type) {
        int number = ++lastNodeNumber;
        virtual.nodes.put(number, map);
        virtual.nodeTypes.put(number, type);
        return number;
    }

    public ModuleList getModules() {
        return new BasicModuleList();
    }

    public Module getModule(String name) throws NotFoundException {
        throw new NotFoundException();
    }

    public boolean hasModule(String name) {
        return false;
    }

    public Cloud getCloud(String name) {
        return new DummyCloud(name, this, new BasicUser("anonymous"));
    }

    public Cloud getCloud(String name, String authenticationType, Map<String, ?> loginInfo) throws NotFoundException {
        return new DummyCloud(name, this, new BasicUser(authenticationType));
    }

    public Cloud getCloud(String name, org.mmbase.security.UserContext user) throws NotFoundException {
        return new DummyCloud(name, this, user);
    }

    public StringList getCloudNames() {
        return clouds;
    }

    public String getDefaultCharacterEncoding() {
        return "UTF-8";
    }


    public Locale getDefaultLocale() {
        return Locale.getDefault();
    }

    public TimeZone getDefaultTimeZone() {
        return TimeZone.getDefault();
    }


    public FieldList createFieldList() {
        return new BasicFieldList();
    }

    public NodeList createNodeList() {
        return getCloud("mmbase").createNodeList();
    }

    public RelationList createRelationList() {
        return getCloud("mmbase").createRelationList();
    }


    public NodeManagerList createNodeManagerList() {
        return getCloud("mmbase").createNodeManagerList();
    }

    public RelationManagerList createRelationManagerList() {
        return getCloud("mmbase").createRelationManagerList();
    }
    public ModuleList createModuleList() {
        return new BasicModuleList();
    }

    public StringList createStringList() {
        return new BasicStringList();
    }

    public AuthenticationData getAuthentication() {
        return authentication;
    }

    public ActionRepository getActionRepository() {
        return ActionRepository.getInstance();
    }

    public boolean isUp() {
        return true;
    }

    public CloudContext assertUp() {
        return this;
    }


    public String getUri() {
        return "dummy://localhost";
    }
 }
