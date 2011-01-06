/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.util;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.net.*;

// used for resolving in MMBase database
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.storage.search.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * @since MMBase-2.0
 * @author Michiel Meeuwissen
 */
public class NodeURLStreamHandlerFactory extends ResourceLoader.URLStreamHandlerFactory {

    private static Logger log = Logging.getLoggerInstance(NodeURLStreamHandlerFactory.class);
    // these should perhaps be configurable:
    public static final String    RESOURCENAME_FIELD  = "name";
    public static final String    TYPE_FIELD          = "type";
    public static final String    FILENAME_FIELD      = "filename";
    public static final String    HANDLE_FIELD        = "handle";
    public static final String    LASTMODIFIED_FIELD  = "lastmodified";
    public static final String    DEFAULT_CONTEXT     = "admin";

    /**
     * Protocol prefix used by URL objects in this class.
     */
    public static final URL NODE_URL_CONTEXT;
    static {
        URL temp = null;
        try {
            temp = new URL("http", "localhost", "/node/");
        } catch (MalformedURLException mfue) {
            assert false : mfue;
        }
        NODE_URL_CONTEXT = temp;
    }



    private static NodeManager resourceNodeManager;


    /**
     * @since MMBase-1.9.2
     */
    static NodeManager getResourceBuilder() {
        if (ResourceWatcher.resourceBuilder == null) return null;
        if (resourceNodeManager == null) {
            Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null);
            resourceNodeManager = cloud.getNodeManager(ResourceWatcher.resourceBuilder);
        }
        return resourceNodeManager;
    }



    @Override
    public ResourceLoader.PathURLStreamHandler[] createURLStreamHandler(ResourceLoader root, ResourceLoader.Type type) {
        return new ResourceLoader.PathURLStreamHandler[] {new NodeURLStreamHandler(root, type.ordinal())};
    }
    /**
     * URLStreamHandler for NodeConnections.
     */
    protected static class NodeURLStreamHandler extends ResourceLoader.PathURLStreamHandler {
        private final int type;
        NodeURLStreamHandler(ResourceLoader parent, int type) {
            super(parent);
            this.type    = type;
        }
        @Override
        public NodeURLStreamHandler createSubHandler(ResourceLoader parent) {
            return new NodeURLStreamHandler(parent, type);
        }

        @Override
        protected String getName(URL u) {
            return u.getPath().substring(NODE_URL_CONTEXT.getPath().length());
        }
        @Override
        public NodeConnection openConnection(String name) {
            URL u;
            while (name.startsWith("/")) {
                name = name.substring(1);
            }
            try {
                u = new URL(NODE_URL_CONTEXT, name, this);
            } catch (MalformedURLException mfue) {
                throw new AssertionError(mfue.getMessage());
            }
            return new NodeConnection(parent, u, name, type);
        }
        @Override
        public Set<String> getPaths(final Set<String> results, final Pattern pattern,  final boolean recursive, final boolean directories) {
            if (NodeURLStreamHandlerFactory.getResourceBuilder() != null) {
                try {
                    NodeManager nm = NodeURLStreamHandlerFactory.getResourceBuilder();
                    NodeQuery query = nm.createQuery();
                    Constraint typeConstraint = Queries.createConstraint(query, TYPE_FIELD, Queries.getOperator("="),  type);
                    Constraint nameConstraint = Queries.createConstraint(query, RESOURCENAME_FIELD, Queries.getOperator("LIKE"),  parent.getContext().getPath().substring(1) + "%");

                    BasicCompositeConstraint constraint = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND);

                    constraint.addChild(typeConstraint).addChild(nameConstraint);

                    query.setConstraint(constraint);
                    for (Node node :  nm.getList(query)) {
                        String url = node.getStringValue(RESOURCENAME_FIELD);
                        String subUrl = url.substring(parent.getContext().getPath().length() - 1);
                        int pos = subUrl.indexOf('/');

                        if (directories) {
                            if (pos < 0) continue; // not a directory
                            do {
                                String u = subUrl.substring(0, pos);
                                if (pattern != null && ! pattern.matcher(u).matches()) {
                                    continue;
                                }
                                results.add(u);
                                pos = subUrl.indexOf('/', pos + 1);
                            } while (pos > 0 && recursive);
                        } else {
                            if (pos > 0 && ! recursive) continue;
                            if (pattern != null && ! pattern.matcher(subUrl).matches()) {
                                continue;
                            }
                            results.add(subUrl);
                        }

                    }
                } catch (BridgeException sqe) {
                    log.warn(sqe);
                }
            }
            return results;
        }
        @Override
        public Integer getResourceNode(String name) {
            Node n = openConnection(name).getResourceNode();
            return n == null ? null : n.getNumber();
        }

        @Override
        public String toString() {
            return "nodes of type " + type;
        }

    }

    /**
     * A URLConnection based on an MMBase node.
     * @see FileConnection
     */
    private static class NodeConnection extends URLConnection {
        Node node;
        final String name;
        final int type;
        final ResourceLoader parent;
        NodeConnection(ResourceLoader parent, URL url, String name, int t) {
            super(url);
            this.name = name;
            this.type = t;
            this.parent = parent;
        }
        @Override
        public void connect() throws IOException {
            if (NodeURLStreamHandlerFactory.getResourceBuilder() == null) {
                throw new IOException("No resources builder available.");
            }
            connected = true;
        }
        /**
         * Gets the Node associated with this URL if there is one.
         * @return MMObjectNode or <code>null</code>
         */
        public  Node getResourceNode() {
            if (node != null) return node;
            if (name.equals("")) return null;
            String realName = (parent.getContext().getPath() + name).substring(1);
            if (NodeURLStreamHandlerFactory.getResourceBuilder() != null) {
                try {
                    NodeManager nm = NodeURLStreamHandlerFactory.getResourceBuilder();

                    NodeQuery query = nm.createQuery();
                    Constraint constraint1 = Queries.createConstraint(query, RESOURCENAME_FIELD, Queries.getOperator("="), realName);
                    Constraint constraint2 = Queries.createConstraint(query, TYPE_FIELD, Queries.getOperator("="), type);

                    BasicCompositeConstraint  constraint  = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND);
                    constraint.addChild(constraint1);
                    constraint.addChild(constraint2);

                    query.setConstraint(constraint);

                    Iterator<Node> i = nm.getList(query).iterator();
                    if (i.hasNext()) {
                        node = i.next();
                        return node;
                    }
                } catch (BridgeException sqe) {
                    log.warn(sqe);
                }
            }
            return null;
        }

        @Override
        public boolean getDoInput() {
            return getResourceNode() != null;
        }

        @Override
        public boolean getDoOutput() {
            getResourceNode();
            return
                (node != null && node.mayWrite()) ||
                (ResourceWatcher.resourceBuilder != null && ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null).getNodeManager(ResourceWatcher.resourceBuilder).mayCreateNode());
        }

        @Override
        public InputStream getInputStream() throws IOException {
            getResourceNode();
            if (node != null) {
                return node.getInputStreamValue(HANDLE_FIELD);
            } else {
                throw new IOException("No such (node) resource for " + name);
            }
        }
        @Override
        public OutputStream getOutputStream() throws IOException {
            if (getResourceNode() == null) {
                if (ResourceWatcher.resourceBuilder == null) return null;

                Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null);
                NodeManager nm = cloud.getNodeManager(ResourceWatcher.resourceBuilder);
                node = nm.createNode();
                node.setContext(DEFAULT_CONTEXT);
                String resourceName = (parent.getContext().getPath() + name).substring(1);
                node.setStringValue(RESOURCENAME_FIELD, resourceName);
                node.setIntValue(TYPE_FIELD, type);
                log.info("Creating node " + resourceName + " " + name + " " + type);
                node.commit();
            }
            return new OutputStream() {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                @Override
                public void close() throws IOException {
                    byte[] b = bytes.toByteArray();
                    node.setValue(HANDLE_FIELD, b);
                    String mimeType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(b));
                    if (mimeType == null) {
                        URLConnection.guessContentTypeFromName(name);
                    }
                    node.setValue("mimetype", mimeType);
                    node.commit();
                }
                @Override
                public void write(int b) {
                    bytes.write(b);
                }
                @Override
                public void write(byte[] b) throws IOException {
                    if (b == null) {
                        node.delete();
                        node = null;
                    } else {
                        super.write(b);
                    }
                }
            };
        }
        @Override
        public long getLastModified() {
            getResourceNode();
            if (node != null) {
                Date lm = node.getDateValue(LASTMODIFIED_FIELD);
                if (lm != null) {
                    return lm.getTime();
                }
            }
            return -1;
        }

        @Override
        public String toString() {
            return "NodeConnection " + node;
        }

    }


}