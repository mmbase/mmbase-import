package org.mmbase.util.functions;

import java.util.*;
import java.util.regex.*;

import org.mmbase.cache.Cache;
import org.mmbase.core.event.NodeEvent;
import org.mmbase.core.event.NodeEventListener;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;
import org.mmbase.util.transformers.RomanTransformer;
import org.mmbase.module.core.*;
import org.mmbase.storage.search.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * The index node functions can be assigned to nodes which are connected by an 'index' relation. An
 * index relation is an extension of 'posrel', but also has an 'index' and a 'root' field. These are
 * used to calcaluate the 'number' of the connected nodes. The 'pos' field only serves to fix the order.
 *
 * The index field can be empty in which case the node with the lowest pos gets number 1, the
 * following number 2 and so on. But on any one of the index relations the index can be stated
 * explicitely. If e.g. the index is specified 'a' then the following node will be 'b'. You can also
 * arrange for 'i', 'ii', 'iii', 'iv' and so on.
 *
 * The root field (a node-type field) specifies to which tree the relations belong. So principaly
 * the same 'chapter' can exists with several different chapter numbers. Also, it is used to define
 * where counting must starts, because the complete number of a chapter consists of a chain of
 * numbers (like 2.3.4.iii).
 *
 * If the index is specified to be '-' then that means that it is explicitely empty.  That cannot be
 * '' or otherwise unfilled, because that means 'determin automaticly' already.
 *
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */
public class IndexFunction extends FunctionProvider {

    private static final Logger log = Logging.getLoggerInstance(IndexFunction.class);

    protected static Cache<String,String> indexCache = new Cache<String,String>(400) {
        @Override
            public  String getName() {
                return "IndexNumberCache";
            }
        @Override
            public String getDescription() {
                return "rootNumber/objectNumber -> Index";
            }

        };

    static {
        indexCache.putCache();

    }

    private static NodeEventListener observer = null;
    private static synchronized void initObserver() {
        if (observer == null) {
            NodeEventListener o = null;
            try {
                 o = new NodeEventListener() {
                        public void notify(NodeEvent event) {
                            nodeChanged(event.getMachine(), event.getNodeNumber(), event.getBuilderName());
                        }
                        public void nodeChanged(String machine, int number, String builder) {
                            log.info("Received change " + machine + "/" + number + "/" +  builder);
                            indexCache.clear(); // this could be done smarter.
                        }
                    };
                MMObjectBuilder indexRelation = MMBase.getMMBase().getBuilder("indexrel");
                indexRelation.addEventListener(o);
            } catch (Exception e) {
                log.service("" + e + " retrying later");
                return;
            }
            observer = o;
        }
    }

    /**
     * Returns the 'successor' of a string. Which means that e.g. after 'zzz' follows 'aaaa'.
     */
    public static String successor(String index) {
        StringBuilder buf = new StringBuilder(index);
        boolean lowercase = true;
        for (int i = index.length() - 1 ; i >= 0; i--) {
            char c = buf.charAt(i);
            if (c == '-') {
                // this special case means 'explicitely empty'.
                log.debug("Found explicit empty");
                if (i + 1 < index.length() && buf.charAt(i + 1) == '-') {
                    return "--";
                }
                break;
            } else if (c >= 'a' && c <= 'y') {
                buf.setCharAt(i, (char) (c + 1));
                return buf.toString();
            } else if (c == 'z') {
                buf.setCharAt(i, 'a');
                continue;
            } else if (c >= 'A' && c <= 'Y') {
                buf.setCharAt(i, (char) (c + 1));
                return buf.toString();
            } else if (c == 'Z') {
                lowercase = false;
                buf.setCharAt(i, 'A');
                continue;
            } else if (c < 128) {
                buf.setCharAt(i, (char) (c + 1));
                return buf.toString();
            } else {
                buf.setCharAt(i, (char) 65);
                continue;
            }
        }

        if (lowercase) {
            buf.insert(0, 'a');
        } else {
            buf.insert(0, 'A');
        }
        return buf.toString();
    }


    /**
     * Calculates the 'successor' of a roman number, preserving uppercase/lowercase.
     */
    protected static String romanSuccessor(String index) {
        if (index.equals("-")) return "i";
        if (index.equals("--")) return "--";
        boolean uppercase = index.length() > 0 && Character.isUpperCase(index.charAt(0));
        String res = RomanTransformer.decimalToRoman(RomanTransformer.romanToDecimal(index) + 1);
        return uppercase ? res.toUpperCase() : res;

    }
    /**
     * Calculates the 'successor' of an index String. Like '7.4.iii' of which the successor is
     * '7.4.iv'.
     *
     * @param index The string to succeed
     * @param separator Regular expression to split up the string first (e.g. "\\.")
     * @param joiner    String to rejoin it again (e.g. ".")
     * @param roman     Whether to consider roman numbers
     */
    protected static String successor(String index, String separator, String joiner, boolean roman) {
        if ("-".equals(index)) return roman ? "i" : "1";
        if ("--".equals(index)) return "--";
        String[] split = index.split(separator);
        //if (split.length == 0) return roman ? "i" : "1";

        String postfix = split[split.length - 1];
        if (RomanTransformer.NUMERIC.matcher(postfix).matches()) {
            postfix = "" + (Integer.parseInt(postfix) + 1);
        } else {
            if (! roman || ! RomanTransformer.ROMAN.matcher(postfix).matches()) {
            postfix = successor(postfix);
        } else {
            postfix = romanSuccessor(postfix);
        }
    }
    StringBuilder buf = new StringBuilder();
    for (int i = 0; i < split.length - 1; i++) {
            buf.append(split[i]);
            buf.append(joiner);
        }
        buf.append(postfix);
        return buf.toString();
    }
    private static Parameter<Node> ROOT        = new Parameter<Node>("root", Node.class, false);
    private static Parameter<Boolean> ROMAN    = new Parameter<Boolean>("roman", Boolean.class, Boolean.TRUE);
    private static Parameter<String> SEPARATOR = new Parameter<String>("separator", String.class, "\\.");
    private static Parameter<String> JOINER    = new Parameter<String>("joiner", String.class, ".");
    private static Parameter<String> ROLE      = new Parameter<String>("role", String.class, "index");

    private static Parameter<?>[] INDEX_ARGS = new Parameter[] {
        Parameter.CLOUD, ROOT, SEPARATOR, JOINER, ROMAN, ROLE
    };

    /**
     * calculates a key for the cache
     */
    private static String getKey(final Node node, final Parameters parameters) {
        Node root                = parameters.get(ROOT);
        final String role        = parameters.get(ROLE);
        final String join        = parameters.get(JOINER);
        final String separator   = parameters.get(SEPARATOR);
        final boolean roman      = parameters.get(ROMAN);
        return "" + node.getNumber() + "/" + (root == null ? "NULL" : "" + root.getNumber()) + "/" + role + "/" + join + "/" + separator  + "/" + roman;
    }


    protected static class Stack<C> extends ArrayList<C> {
        private static final long serialVersionUID = 0L;
        public void push(C o) {
            add(0, o);
        }
        public C pull() {
            return remove(0);
        }
    }

    protected static NodeFunction<String> index = new NodeFunction<String>("index", INDEX_ARGS, ReturnType.STRING) {
        private static final long serialVersionUID = 0L;

        {
            setDescription("Calculates the index of a node, using the surrounding 'indexrels'");
        }

        /**
         * complete bridge version of {@link #getFunctionValue}
         */
        public String getFunctionValue(final Node node, final Parameters parameters) {
            Node root = parameters.get(ROOT);
            final String role = parameters.get(ROLE);
            final String join = parameters.get(JOINER);
            final String separator = parameters.get(SEPARATOR);
            final Pattern indexPattern = Pattern.compile("(.+)" + separator + "(.+)");
            final boolean roman = parameters.get(ROMAN);

            final String key = getKey(node, parameters);

            initObserver();
            String result = indexCache.get(key);
            if (result != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Found index '" + result + "' for node " + node.getNumber() + " from cache (key " + key + ")");
                }
                return result;
            }
            log.debug("Determining index for node " + node.getNumber() + " with role " + role);

            final NodeManager nm = node.getNodeManager();

            // now we have to determine the path from node to root.

            GrowingTreeList tree = new GrowingTreeList(Queries.createNodeQuery(node), 10, nm, role, "source");
            NodeQuery template = tree.getTemplate();
            if (root != null) {
                StepField sf = template.addField(role + ".root");
                template.setConstraint(template.createConstraint(sf, root));
            }

            Stack<Node> stack = new Stack<Node>();
            TreeIterator it = tree.treeIterator();
            int depth = it.currentDepth();
            while (it.hasNext()) {
                Node n = it.nextNode();
                if (log.isDebugEnabled()) {
                    log.debug("Considering at " + it.currentDepth() + "/" + depth + " node " + n.getNodeManager().getName() + " " + n.getNumber());
                }
                if (it.currentDepth() > depth) {
                    stack.push(n);
                    depth = it.currentDepth();
                }
                if (indexCache.contains(getKey(n, parameters))) {
                    if (log.isDebugEnabled()) {
                        log.debug("Index for " + n.getNumber() + " is known already!, breaking");
                    }
                    break;
                }

                if (it.currentDepth() < depth) {
                    break;
                }
                //if (root == null) root = n.getNodeValue(role + ".root");
                if (root != null && n.getNumber() == root.getNumber()) {
                    break;
                }
            }

            if (stack.isEmpty()) {
                log.debug("Stack is empty, no root found, returning ''");
                indexCache.put(key, "");
                return "";
            }

            if (log.isDebugEnabled()) {
                log.debug("Now constructing index-number with " + stack.size() + " nodes on stack");
            }
            Node n = stack.pull(); // this is root, or at least _its_ index is known
            StringBuilder buf;
            if (!n.equals(node)) {
                buf = new StringBuilder(n.getFunctionValue("index", parameters).toString());
            } else {
                buf = new StringBuilder();
            }
            String j = buf.length() == 0 ? "" : join;
            OUTER:
            while (!stack.isEmpty()) {
                Node search = stack.pull();
                NodeQuery q = Queries.createRelatedNodesQuery(n, nm, role, "destination");
                StepField sf = q.addField(role + ".pos");
                q.addSortOrder(sf, SortOrder.ORDER_ASCENDING);
                q.addField(role + ".index");
                if (log.isDebugEnabled()) {
                    log.debug("Executing " + q.toSql() + " to search " + search.getNumber());
                }
                String index = null;
                NodeIterator ni = q.getCloud().getList(q).nodeIterator();
                boolean doRoman = roman;
                while (ni.hasNext()) {
                    Node clusterFound = ni.nextNode();
                    Node found = clusterFound.getNodeValue(q.getNodeStep().getAlias());
                    String i = clusterFound.getStringValue(role + ".index");
                    if (i == null || i.equals("")) {
                        i = index;
                    }
                    if (i == null) {
                        i = "1";
                    }
                    log.debug("Found index " + i);
                    Matcher matcher = indexPattern.matcher(i);
                    if (matcher.matches()) {
                        buf = new StringBuilder(matcher.group(1));
                        i = matcher.group(2);
                        log.debug("matched " + indexPattern + " --> " + i);
                    }
                    doRoman = doRoman && RomanTransformer.ROMAN.matcher(i).matches();

                    boolean explicitEmpty = "-".equals(i) || "--".equals(i);
                    if (found.getNumber() == search.getNumber()) {
                        log.debug("found sibling");
                        // found!
                        if (!explicitEmpty) {
                            buf.append(j).append(i);
                        } else {
                            buf.setLength(0);
                        }
                        j = join;
                        n = found;
                        continue OUTER;
                    }
                    index = successor(i, separator, join, doRoman);

                    String hapKey = getKey(found, parameters);
                    String value = explicitEmpty ? "" : buf.toString() + j + i;
                    log.debug("Caching " + key + "->" + value);
                    // can as well cache this one too.
                    indexCache.put(hapKey, value);
                }
                // not found
                buf.append(j).append("???");
                break;
            }
            String r = buf.toString();
            log.debug("Found '" + r + "' for " + key);
            indexCache.put(key, r);
            return r;
        }
    };
    {
        addFunction(index);
    }


    public static void main(String argv[]) {

        CloudContext cc = ContextProvider.getDefaultCloudContext();
        Cloud cloud = cc.getCloud("mmbase", "class", null);
        Node node = cloud.getNode(argv[0]);
        Node root = null;
        if (argv.length > 1) root = cloud.getNode(argv[1]);
        Parameters params = index.createParameters();
        params.set(ROOT, root);
        params.set(ROMAN, Boolean.TRUE);
        System.out.println("" + index.getFunctionValue(node, params));



    }

}
