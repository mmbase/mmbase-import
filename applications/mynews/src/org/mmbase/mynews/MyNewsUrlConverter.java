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
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.framework.*;
import org.mmbase.framework.basic.UrlConverter;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.*;

/**
 * The UrlConverter that can filter and create urls for the MyNews example application.
 * Links start with '/magazine/'.
 *
 *
 * @author Michiel Meeuwissen
 * @version $Id: MyNewsUrlConverter.java,v 1.9 2007-12-26 17:08:02 michiel Exp $
 * @since MMBase-1.9
 */
public class MyNewsUrlConverter implements UrlConverter {
    private static final Logger log = Logging.getLoggerInstance(MyNewsUrlConverter.class);

    private static CharTransformer trans = new Identifier();
    private boolean useTitle = false;
    private boolean useDate  = true;
    private String  directory = "/magazine";
    private final Framework framework;

    public MyNewsUrlConverter(Framework fw) {
        framework = fw;
    }

    public void setUseTitle(boolean t) {
        useTitle = t;
    }
    public void setUseDate(boolean d) {
        useDate = d;
    }
    public void setDir(String d) {
        directory = d;
    }


    public Parameter[] getParameterDefinition() {
        return new Parameter[] {};
    }

    public StringBuilder getUrl(String path,
                                Map<String, Object> parameters,
                                Parameters frameworkParameters, boolean escapeAmps) {
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
            if (block.getComponent().getName().equals("mynews")) {
                block = renderingBlock.getComponent().getBlock(path);
                log.debug("Found mynews block " + block);
                Node n = (Node) parameters.get(Framework.N.getName());
                StringBuilder b = new StringBuilder(directory);
                if(block.getName().equals("article")) {
                    b.append("/");
                    if (useDate) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(n.getDateValue("date"));
                        b.append(cal.get(Calendar.YEAR));
                        b.append('/');
                        b.append(cal.get(Calendar.MONTH) + 1);
                        b.append('/');
                    }

                    if (useTitle) {
                        b.append(trans.transform(n.getStringValue("title")));
                    } else {
                        b.append(n);
                    }
                }
                return b;
            } else {
                log.debug("No mynews block found");
                return null;
            }
        }
    }

    public StringBuilder getInternalUrl(String page, Map<String, Object> params, Parameters frameworkParameters) {
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
                StringBuilder result = new StringBuilder("/mmbase/components/mynews/render.jspx");
                assert path[0].equals("");
                assert path[1].equals(directory.substring(1));
                if (path.length == 2) {
                    return result;
                } else {
                    String n;
                    if (useTitle) {
                        Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase");
                        NodeManager news = cloud.getNodeManager("news");
                        NodeQuery q = news.createQuery();
                        String like = path[2];//.replaceAll("_", "?");
                        Queries.addConstraint(q, Queries.createConstraint(q, "title", Queries.getOperator("LIKE"), like));
                        NodeList list = news.getList(q);
                        Node node;
                        if (list.size() > 0) {
                            node = list.getNode(0);
                        } else {
                            node = cloud.getNode(path[2]);
                        }
                        n = "" + node.getNumber();

                    } else {
                        n = path[2];
                    }
                    result.append("?block=article&n=" + n);
                    return result;
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
