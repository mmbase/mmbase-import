/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.mynews;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import org.mmbase.util.transformers.CharTransformer;
import org.mmbase.util.transformers.Identifier;
import org.mmbase.util.DynamicDate;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.storage.search.*;
import org.mmbase.framework.*;
import org.mmbase.framework.basic.DirectoryUrlConverter;
import org.mmbase.framework.basic.BasicFramework;
import org.mmbase.framework.basic.Url;
import org.mmbase.framework.basic.BasicUrl;
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
 * @version $Id: MyNewsUrlConverter.java,v 1.32 2009-01-16 22:35:44 michiel Exp $
 * @since MMBase-1.9
 */
public class MyNewsUrlConverter extends DirectoryUrlConverter {
    private static final Logger log = Logging.getLoggerInstance(MyNewsUrlConverter.class);

    private static CharTransformer trans = new Identifier();
    private boolean useTitle = false;
    private int dateDepth  = 0;

    public MyNewsUrlConverter(BasicFramework fw) {
        super(fw);
        setDirectory("/magazine/");
        addComponent(ComponentRepository.getInstance().getComponent("mynews"));
    }

    public void setUseTitle(boolean t) {
        useTitle = t;
    }
    public void setDateDepth(int d) {
        dateDepth = d;
    }

    @Override public int getDefaultWeight() {
        int q = super.getDefaultWeight();
        return Math.max(q, q + 1000);
    }

    /**
     * Generates a nice url for 'mynews'. This basicly looks like
     * &lt;directory&gt;/[&lt;year&gt;[/&lt;month&gt;[&lt;day of month&gt;]]][/&lt;identifier of
     * news article (number or title)&gt;]
     *
     * How many elements of the 'date' are produces depends on the 'date depth', and can be 0, 1, 2
     * or 3.
     */
    @Override protected void getNiceDirectoryUrl(StringBuilder b,
                                                 Block block,
                                                 Parameters parameters,
                                                 Parameters frameworkParameters,  boolean action) throws FrameworkException {
        if (log.isDebugEnabled()) {
            log.debug("" + parameters + frameworkParameters);
            log.debug("Found mynews block " + block);
        }
        if(block.getName().equals("article")) {
            Node n = parameters.get(Framework.N);
            parameters.set(Framework.N, null);
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
    }


    /**
     * Translates the result of {@link #getNiceUrl} back to an actual JSP which can render the block
     */
    @Override public Url getFilteredInternalDirectoryUrl(List<String>  path, Map<String, ?> params, Parameters frameworkParameters) throws FrameworkException {
        StringBuilder result = new StringBuilder("/mmbase/framework/render.jspx?component=mynews");
        if (path.size() == 0) {
            result.append("&block=magazine");
        } else {
            result.append("&block=article&n=");
            // article mode
            String id = path.get(path.size() - 1); // last element in the list identifies the article
            String n;
            if (useTitle) {
                Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase");
                NodeManager news = cloud.getNodeManager("news");
                NodeQuery q = news.createQuery();
                String like = id;
                Queries.addConstraint(q, Queries.createConstraint(q, "title", Queries.getOperator("LIKE"), like));
                if (path.size() > 1) {
                    String[] date = new String[] {path.get(0), "01", "01"};
                    String offset = "1 year";
                    if (path.size() > 2) {
                        date[1] = path.get(1);
                        offset = "1 month";
                    }
                    if (path.size() > 3) {
                        date[2] = path.get(2);
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

            result.append(n);
        }
        return new BasicUrl(this, result.toString());
    }

}
