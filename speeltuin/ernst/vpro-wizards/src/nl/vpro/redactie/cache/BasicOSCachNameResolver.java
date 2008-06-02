package nl.vpro.redactie.cache;

import javax.servlet.http.HttpServletRequest;

import nl.vpro.redactie.util.FlushNameTemplateBean;

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
public class BasicOSCachNameResolver implements OSCacheNameResolver {

    private static final Logger log = Logging.getLoggerInstance(BasicOSCachNameResolver.class);

    public String resolveCacheGroupName(String flushname, HttpServletRequest request) {
        try {
            return FlushNameTemplateBean.stripTemplates(flushname);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return flushname;
    }

    public String resolveCacheKeyName(String flushname, HttpServletRequest request) {
        try {
            return FlushNameTemplateBean.stripTemplates(flushname);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return flushname;
    }

}
