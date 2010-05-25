package org.mmbase.applications.vprowizards.spring.cache;

import javax.servlet.http.HttpServletRequest;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * This basic flushname resolver uses the {@link FlushNameTemplateBean} to clear the templates
 * out of the flushnames. So what are templates:
 * in order to create flushname values dynamically there is a simpel template system in place.
 * Templates have this format:<br>
 * <i>"somecachename, type[page], something[page]"</i><br>
 * The listresult tag checks for the values between square brackets, and then appends the nodenumber
 * of the current row's node type matches it. The result is:<br>
 * <i>"somecachename, type[page:120], something[page:120]"</i><br>
 * This template string is not simply replaced by the nodenumber becouse when you go back to the list using the
 * path, this flushname will be in the session (the original flushname was probably given as a parameter from the editors menu)
 * , so by appending the nodenumber the template can be reused.<br>
 * When the flushname is finally used to flush the caches, the template is cleared from the flushname string.
 * @author ebunders
 *
 */
public class BasicOSCacheNameResolver implements OSCacheNameResolver {

    private static final Logger log = Logging.getLoggerInstance(BasicOSCacheNameResolver.class);

    /**
     * this method strips the templates away from the flushnames and just leaves the nodenumbers. This should yield the actual flushnames.
     *
     * @param flushname
     * @return
     * @throws Exception when there is a problem with parsing the template
     */
    static String stripTemplates(String flushname) throws Exception {
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


    public String resolveCacheGroupName(String flushname, HttpServletRequest request) {
        try {
            return stripTemplates(flushname);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return flushname;
    }

    public String resolveCacheKeyName(String flushname, HttpServletRequest request) {
        try {
            return stripTemplates(flushname);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return flushname;
    }

}
