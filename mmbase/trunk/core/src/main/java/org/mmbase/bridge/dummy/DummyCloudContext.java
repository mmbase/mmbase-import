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
 * The 'dummy' cloud context is mainly meant for useage in junit test cases. It provides a
 * functional bridge implementation, without the backing of the MMBase core classes. This means on
 * one hand that it takes much less time to initialize, on the other hand that all data created is only
 * availabe in memory and is in no way persistent.
 *
 * Also, at the moment, it is only <em>partially</em> working. You will easily encounter many {@link
 * UnsupportedOperationExceptions}s and other kind of errors. For several simple tests it is however useful already.

 * The object model must be manually set up using the several <code>addNodeManager</code>
 * methods. The 'core' model is created with {@link #addCore}. Current implementation will basicly allow all 'related' and 'posrel' relations, but this
 * part has still to be elaborated on. This may change in future versions of this.
 *
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
    final Map<String,  Map<String, Field>> nodeManagers       = new ConcurrentHashMap<String, Map<String, Field>>();

    final Map<String,  DummyBuilderReader>    builders           = new ConcurrentHashMap<String, DummyBuilderReader>();


    public DummyCloudContext() {
        clouds.add("mmbase");
    }

    public void clear() {
        nodes.clear();
        nodeTypes.clear();
        nodeManagers.clear();
        builders.clear();
    }

    /**
     * Adds the 'core' builders to the object model.
     * It may also add a core relation model. Like 'related' and 'posrel' roles.
     */
    public void addCore() throws java.io.IOException {
        DummyCloudContext.getInstance().addNodeManager(DummyBuilderReader.getBuilderLoader().getInputSource("core/typedef.xml"));
        DummyCloudContext.getInstance().addNodeManager(DummyBuilderReader.getBuilderLoader().getInputSource("core/typerel.xml"));
        DummyCloudContext.getInstance().addNodeManager(DummyBuilderReader.getBuilderLoader().getInputSource("core/reldef.xml"));
        DummyCloudContext.getInstance().addNodeManager(DummyBuilderReader.getBuilderLoader().getInputSource("core/object.xml"));
        DummyCloudContext.getInstance().addNodeManager(DummyBuilderReader.getBuilderLoader().getInputSource("core/insrel.xml"));
    }

    public void addNodeManager(String name, Map<String, DataType> map) {
        Map<String, Field> m = new HashMap<String, Field>();
        for (Map.Entry<String, DataType> e : map.entrySet()) {
            m.put(e.getKey(), new DummyField(e.getKey(), null, e.getValue()));
        }
        nodeManagers.put(name, m);
    }

    public void addNodeManager(InputSource source) {
        synchronized(builders) {
            DummyBuilderReader reader = new DummyBuilderReader(source);
            addNodeManager(reader);

        }
    }

    protected void addNodeManager(DummyBuilderReader reader) {
        Map<String, Field> map = new HashMap<String, Field>();
        for (Field f : reader.getFields()) {
            map.put(f.getName(), f);
        }
        nodeManagers.put(reader.getName(), map);
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
