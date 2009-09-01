/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.mock;

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.BridgeCollections;
import org.mmbase.datatypes.DataType;
import org.mmbase.bridge.implementation.*;
import org.mmbase.security.*;
import org.mmbase.util.*;
import org.xml.sax.InputSource;

/**
 * The 'dummy' cloud context is mainly meant for useage in junit test cases. It provides a
 * functional bridge implementation, without the backing of the MMBase core classes. This means on
 * one hand that it takes much less time to initialize, on the other hand that all data created is only
 * availabe in memory and is in no way persistent.
 *
 * Also, at the moment, it is only <em>partially</em> working. You will easily encounter many {@link
 * UnsupportedOperationException}s and other kind of errors. For several simple tests it is however useful already.

 * The object model must be manually set up using the several <code>addNodeManager</code>
 * methods. The 'core' model is created with {@link #addCore}. Current implementation will basicly allow all 'related' and 'posrel' relations, but this
 * part has still to be elaborated on. This may change in future versions of this.
 *
 * @author  Michiel Meeuwissen
 * @version $Id: MapNode.java 36154 2009-06-18 22:04:40Z michiel $
 * @since   MMBase-1.9.2
 * @todo    EXPERIMENTAL
 */

public class MockCloudContext implements CloudContext {

    private static final MockCloudContext virtual = new MockCloudContext();
    public static MockCloudContext getInstance() {
        return virtual;
    }

    public static final String CLOUD = "mmbase";

    /*
    private static SearchQueryHandler searchQueryHandler =
        new org.mmbase.storage.search.implementation.database.BasicQueryHandler(new org.mmbase.storage.search.implementation.database.BasicSqlHandler() {
                @Override
                public String getAllowedValue(String value) {
                    return value;
                }
                @Override
                protected void appendTableName(StringBuilder sb, Step step) {
                    sb.append(step.getTableName());
                    appendTableAlias(sb, step);
                }
                @Override
                public String forceEncode(String st) {
                    return st;
                }
            });
    */
    static class NodeManagerDescription {
        public final String name;
        public final Map<String, Field> fields;
        public final MockBuilderReader reader;
        public final Map<String, String> properties = new HashMap<String, String>();
        public NodeManagerDescription(String n, Map<String, Field> f, MockBuilderReader r) {
            name = n;
            fields = f;
            reader = r;
        }
        public String toString() {
            return name + ":" + fields;
        }
    }
    public static class NodeDescription {
        public final String type;
        public final Map<String, Object> values;
        public NodeDescription(String t, Map<String, Object> v) {
            type = t;
            values = v;
        }
        public String toString() {
            return type + ":" + values;
        }
    }


    private int lastNodeNumber = 0;
    private final Authentication authentication = new NoAuthentication();
    private final BasicStringList clouds        = new BasicStringList();



    final Map<Integer, NodeDescription>  nodes                 = Collections.synchronizedMap(new LinkedHashMap<Integer, NodeDescription>());
    final Map<String,  NodeManagerDescription> nodeManagers    = Collections.synchronizedMap(new LinkedHashMap<String, NodeManagerDescription>());


    public MockCloudContext() {
        clouds.add(CLOUD);
    }

    public Map<Integer, NodeDescription>  getNodes() {
        return nodes;
    }

    public void clear() {
        nodes.clear();
        nodeManagers.clear();
        lastNodeNumber = 0;
    }

    /**
     * Adds the 'core' builders to the object model.
     * It may also add a core relation model. Like 'related' and 'posrel' roles.
     */
    public void addCore() throws java.io.IOException {
        for (String buil : new String[] {"typedef", "typerel", "reldef", "object", "insrel"}) {
            addNodeManager(MockBuilderReader.getBuilderLoader().getInputSource("core/" + buil + ".xml"));
        }
    }

    public void addNodeManager(String name, Map<String, DataType> map) {
        Map<String, Field> m = new HashMap<String, Field>();
        for (Map.Entry<String, DataType> e : map.entrySet()) {
            m.put(e.getKey(), new MockField(e.getKey(), null, e.getValue()));
        }
        nodeManagers.put(name, new NodeManagerDescription(name, m, null));
    }

    public void addNodeManager(InputSource source) {
        synchronized(nodeManagers) {
            MockBuilderReader reader = new MockBuilderReader(source, this);
            addNodeManager(reader);

        }
    }

    protected void addNodeManager(MockBuilderReader reader) {
        Map<String, Field> map = new HashMap<String, Field>();
        for (Field f : reader.getFields()) {
            map.put(f.getName(), f);
        }
        nodeManagers.put(reader.getName(), new NodeManagerDescription(reader.getName(), map, reader));
    }

    public void addNodeManagers(ResourceLoader directory) throws java.io.IOException {
        for (String builder : directory.getResourcePaths(ResourceLoader.XML_PATTERN, true)) {
            synchronized(nodeManagers) {
                MockBuilderReader reader = new MockBuilderReader(directory.getInputSource(builder), this);
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



    public synchronized int addNode(String type, Map<String, Object> map) {
        int number = ++lastNodeNumber;
        nodes.put(number, new NodeDescription(type, map));
        return number;
    }

    public ModuleList getModules() {
        return BridgeCollections.EMPTY_MODULELIST;
    }

    public Module getModule(String name) throws NotFoundException {
        throw new NotFoundException();
    }

    public boolean hasModule(String name) {
        return false;
    }

    public Cloud getCloud(String name) {
        return new MockCloud(name, this, new BasicUser("anonymous"));
    }

    public Cloud getCloud(String name, String authenticationType, Map<String, ?> loginInfo) throws NotFoundException {
        return new MockCloud(name, this, new BasicUser(authenticationType));
    }

    public Cloud getCloud(String name, org.mmbase.security.UserContext user) throws NotFoundException {
        return new MockCloud(name, this, user);
    }

    public StringList getCloudNames() {
        return BridgeCollections.unmodifiableStringList(clouds);
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
        return getCloud(CLOUD).createNodeList();
    }

    public RelationList createRelationList() {
        return getCloud(CLOUD).createRelationList();
    }


    public NodeManagerList createNodeManagerList() {
        return getCloud(CLOUD).createNodeManagerList();
    }

    public RelationManagerList createRelationManagerList() {
        return getCloud(CLOUD).createRelationManagerList();
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
    /*
    public org.mmbase.storage.search.SearchQueryHandler getSearchQueryHandler() {
        return searchQueryHandler;
    }
    */
 }
