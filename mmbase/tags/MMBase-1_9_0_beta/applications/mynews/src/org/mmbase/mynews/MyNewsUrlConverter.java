/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.mynews;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import org.mmbase.util.transformers.*;
import org.mmbase.util.DynamicDate;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.storage.search.*;
import org.mmbase.framework.*;
import org.mmbase.framework.basic.UrlConverter;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.*;

/**
 * The UrlConverter that can filter and create urls for the MyNews example application.
 * Links start with '/magazine/' (or another directory, which can be set with 'setDir')
 *
 * Links to articles have the form /magazine[/<year>[/<month>[/<day>]]]/<title of article>|<number
 * of the node>
 * How many of the date-parts are generated, and wether the title or the number of the articles are
 * produced, is controlled by 'setDateDepth' and 'setUseTitle'.
 *
 * These properties can be set by <param> tags in framework.xml.
 *
 *
 * @author Michiel Meeuwissen
 * @version $Id: MyNewsUrlConverter.java,v 1.17 2008-04-23 08:21:39 michiel Exp $
 * @since MMBase-1.9
 */
public class MyNewsUrlConverter implements UrlConverter {
    private static final Logger log = Logging.getLoggerInstance(MyNewsUrlConverter.class);

    private static CharTransformer trans = new Identifier();
    private boolean useTitle = false;
    private int dateDepth  = 0;
    private String  directory = "/magazine";
    private final Framework framework;

    public MyNewsUrlConverter(Framework fw) {
        framework = fw;
    }

    public void setUseTitle(boolean t) {
        useTitle = t;
    }
    public void setDateDepth(int d) {
        dateDepth = d;
    }
    public void setDir(String d) {
        directory = d;
    }


    public Parameter[] getParameterDefinition() {
        return new Parameter[] {};
    }

    protected String getUrl(String path,
                             Map<String, Object> parameters,
                             Parameters frameworkParameters, boolean escapeAmps, boolean action) {
        if (log.isDebugEnabled()) {
            log.debug("" + path + parameters + frameworkParameters);
        }
        Block renderingBlock = framework.getRenderingBlock(frameworkParameters);
        if (renderingBlock == null) {
            log.debug("No current block found for parameters " + frameworkParameters);
            return null;
        } else {
            HttpServletRequest request = frameworkParameters.get(Parameter.REQUEST);

            Block block = framework.getBlock(frameworkParameters);

            if (block != null && "mynews".equals(block.getComponent().getName())) {
                block = renderingBlock.getComponent().getBlock(path);
                log.debug("Found mynews block " + block);
                Node n = (Node) parameters.get(Framework.N.getName());
                StringBuilder b = new StringBuilder(directory);
                if(block.getName().equals("article")) {
                    //b.append("/");
                    if (dateDepth > 0) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(n.getDateValue("date"));
                        b.append(cal.get(Calendar.YEAR));
                        b.append('/');
                        if (dateDepth > 1) {
                            b.append(cal.get(Calendar.MONTH) + 1);
                            b.append('/');
                            if (dateDepth > 2) {
                                b.append(cal.get(Calendar.DAY_OF_MONTH));
                                b.append('/');
                            }
                        }
                    }

                    if (useTitle) {
                        b.append(trans.transform(n.getStringValue("title")));
                    } else {
                        b.append(n);
                    }
                }
                return b.toString();
            } else {
                log.debug("No mynews block found");
                return null;
            }
        }
    }

    public String getUrl(String path,
                         Map<String, Object> parameters,
                         Parameters frameworkParameters, boolean escapeAmps) {
        return getUrl(path, parameters, frameworkParameters, escapeAmps, false);
    }
    public String getProcessUrl(String path,
                                Map<String, Object> parameters,
                                Parameters frameworkParameters, boolean escapeAmps) {
        return getUrl(path, parameters, frameworkParameters, escapeAmps, true);
    }

    public String getInternalUrl(String page, Map<String, Object> params, Parameters frameworkParameters) throws FrameworkException {
        HttpServletRequest request = frameworkParameters.get(Parameter.REQUEST);
        if (page == null) throw new IllegalArgumentException();
        if (page.startsWith(directory)) {
            log.debug("Found a mynews url");
            String sp = FrameworkFilter.getPath(request);
            String[] path = sp.split("/");
            if (log.isDebugEnabled()) {
                log.debug("Going to filter " + Arrays.asList(path));
            }
            if (path.length >= 2) {
                StringBuilder result = new StringBuilder("/mmbase/mynews/render.jspx");
                assert path[0].equals("");
                assert path[1].equals(directory.substring(1));
                if (path.length == 2) {
                    // magazine mode.
                    return result.toString();
                } else {
                    // article mode
                    String id = path[path.length - 1];
                    String n;
                    if (useTitle) {
                        Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase");
                        NodeManager news = cloud.getNodeManager("news");
                        NodeQuery q = news.createQuery();
                        String like = id;//.replaceAll("_", "?");
                        Queries.addConstraint(q, Queries.createConstraint(q, "title", Queries.getOperator("LIKE"), like));
                        if (path.length > 3) {
                            String[] date = new String[] {path[2], "01", "01"};
                            String offset = "1 year";
                            if (path.length > 4) {
                                date[1] = path[3];
                                offset = "1 month";
                            }
                            if (path.length > 5) {
                                date[2] = path[4];
                                offset = "1 day";
                            }
                            String ds = date[0] + '-' + date[1] + '-' + date[2];
                            try {
                                Constraint start = Queries.createConstraint(q, "date", Queries.getOperator("ge"), DynamicDate.getInstance(ds));
                                Constraint end   = Queries.createConstraint(q, "date", Queries.getOperator("le"), DynamicDate.getInstance(ds + " + " + offset));
                                Queries.addConstraint(q, start);
                                Queries.addConstraint(q, end);
                            } catch (org.mmbase.util.dateparser.ParseException pe) {
                                throw new RuntimeException(pe);
                            }
                        }
                        NodeList list = news.getList(q);
                        Node node;
                        if (list.size() > 0) {
                            node = list.getNode(0);
                        } else {
                            if (cloud.hasNode(id)) {
                                // alias/nodenumbers work too
                                node = cloud.getNode(id);
                            } else {
                                throw new FrameworkException("" + q.toSql() + " gave no results");
                            }
                        }
                        n = "" + node.getNumber();
                    } else {
                        // node was specified by number. Date spec can be ignored.
                        n = id;
                    }
                    result.append("?block=article&n=" + n);
                    return result.toString();
                }
            } else {
                log.debug("path length " + path.length);
                return null;
            }
        } else {
            log.debug("Leaving unfiltered");
            return null;
        }
    }

    public String toString() {
        return directory;
    }


}
