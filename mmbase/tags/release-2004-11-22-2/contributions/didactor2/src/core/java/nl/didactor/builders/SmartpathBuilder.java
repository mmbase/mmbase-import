package nl.didactor.builders;

import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.util.logging.*;
import java.util.StringTokenizer;

/**
 * Extension of the AbstractSmartpathBuilder, which reads the fields to
 * use as smartpath fields from the builder.xml &lt;properties&gt; section.
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public class SmartpathBuilder extends AbstractSmartpathBuilder {
    protected static Logger log = Logging.getLoggerInstance("nl.didactor.builders.SmartpathBuilder") ;

    public SmartpathBuilder() {
    }

    /**
     * Initialization of the builder
     * @return Boolean indicating whether the builder was successfully initialized.
     */
    public boolean init() {
        String spFieldName = getInitParameter("smartpathfield");

        if (spFieldName == null || spFieldName.equals("")) {
            log.error("You must specify the 'smartpathfield' property in your <properties> block for this builder");
            return false;
        } 

        StringTokenizer st = new StringTokenizer(spFieldName, ",");
        spFieldNames = new String[st.countTokens()];
        log.debug("There apparently are '" + st.countTokens() + "' fieldnames");
        int i=0;
        while (st.hasMoreTokens()) {
            String fieldname = st.nextToken();
            log.debug("Adding fieldname[" + i + "] = '" + fieldname + "' to my list");
            spFieldNames[i] = fieldname;
            i++;
        }

        spPathPrefix = getInitParameter("pathprefix");
        if (spPathPrefix == null) spPathPrefix = "";

        if (super.init()) {
            return true;
        } else {
            return false;
        }
    }
}
