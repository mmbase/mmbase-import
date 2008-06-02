package nl.vpro.redactie.util;

import java.util.StringTokenizer;

import nl.vpro.redactie.cache.BasicOSCachNameResolver;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This bean is used in the templates of the vpro-wizards. it is used to add node numbers to templates of flushnames. It also has a method
 * for cleaning the templates out of the flushnames. this is used by {@link BasicOSCachNameResolver} for information about this class see
 * the apidoc of {@link BasicOSCachNameResolver}
 *
 * @author ebunders
 *
 */
public class FlushNameTemplateBean {
    private String template;

    private String type;

    private String nodenr;

    private Cloud cloud;

    private static final Logger log = Logging.getLoggerInstance(FlushNameTemplateBean.class);

    /**
     * @param nodenr
     *            the num ber of the node appended to the tempate for matches with 'type'
     */
    public void setNodenr(String nodenr) {
        this.nodenr = nodenr;
    }

    /**
     *
     * @param template
     *            flushname with placeholders to be replaced
     */
    public void setTemplate(String template) {
        this.template = template;
    }

    /**
     * @param type
     *            the nodetype to add the nodenumber to.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * this method processes the template and appends the nodenumber to all matching placeholders in the templates it also removes
     * preveously added nodenumbers. (templates can be reused)
     *
     * @return
     */
    public String getProcessedTemplate() {
        int from = 0;
        while (template.substring(from).matches("^.*\\[[a-zA-Z0-9\\.:]+\\].*$")) {
            // \\[[a-zA-Z0-9]+\\]
            log.debug("evaluating: " + template.substring(from) + ", from: " + from);
            int begin = template.indexOf("[", from);
            int end = template.indexOf("]", from);
            String t = template.substring(begin + 1, end);
            log.debug("begin: " + begin + ", end: " + end + ", template: " + t);

            // maybe this template was used before, in that case we have to clean the old value out of it
            if (t.indexOf(":") > -1) {
                t = t.substring(0, t.indexOf(":"));
                log.debug("template reuse. after cleaning: " + t);
            }

            //is the template a path?
            String thistipe = null, relation = null, destination = null;
            boolean isQuery = false;
            if(t.indexOf(".") > -1){
                StringTokenizer st = new StringTokenizer(t, ".");
                thistipe = st.nextToken();
                relation = st.nextToken();
                destination = st.nextToken();
                isQuery = true;
            }else{
                thistipe = t;
            }

            // if the template matches the given type, add the node number
            if (thistipe.equals(type)) {

                //we copy the nodenumber field becouse if there are more templates then one we need the original again.
                String copyNodeNumber = nodenr;
                if(isQuery){
                    Node node = cloud.getNode(nodenr);
                    NodeList nl = node.getRelatedNodes(destination, relation, "both");
                    if(nl.size() > 0){
                        copyNodeNumber = ""+nl.getNode(0).getNumber();
                    }else{
                        log.warn("could not find 'parent' node with path " + t + " and root node "+copyNodeNumber);
                        copyNodeNumber= "!notfound!";
                    }
                }
                template = template.substring(0, begin + 1) + t + ":" + copyNodeNumber + template.substring(end);
                // adjust the end index
                end = template.indexOf("]", from);
            }
            from = end + 1;
        }
        return template;
    }

    /**
     * this method strips the templates away from the flushnames and just leaves the nodenumbers. This should yield the actual flushnames.
     *
     * @param flushname
     * @return
     * @throws Exception when there is a problem with parsing the template
     */
    public static String stripTemplates(String flushname) throws Exception {
        log.debug("before. template: "+flushname);
        // decode the templates out of the flushname
        int from = 0;
        while (flushname.substring(from).matches("^.*\\[[a-zA-Z0-9\\.]+:[a-zA-Z0-9]+\\].*$")) {
            log.debug("evaluating: " + flushname.substring(from) + ", from: " + from);
            int begin = flushname.indexOf("[", from);
            int end = flushname.indexOf("]", from);
            String t = flushname.substring(begin + 1, end);
            log.debug("begin: " + begin + ", end: " + end + ", flushname: " + t);

            if (t.indexOf(":") == -1) {
                // when this happens there is a template in the flushname that has not been suffixed
                // with an actual nodenumber. this is an application error!
                throw new Exception("flushname '" + flushname
                        + "' illegal. some temlates have not been suffixed with ':<nodenr>'");
            }
            String nodenr = t.substring(t.indexOf(":") + 1);

            flushname = flushname.substring(0, begin) + nodenr + flushname.substring(end + 1);
            from = begin + 1;
        }
        log.debug("after. tempate: " + flushname);
        return flushname;
    }

    public void setCloud(Cloud cloud) {
        this.cloud = cloud;
    }

    public static void main(String[] args) {
        String test = "plaatsen_[memoryresponse,related,memorylocation:8080]";
        try {
            System.out.println(FlushNameTemplateBean.stripTemplates(test));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
