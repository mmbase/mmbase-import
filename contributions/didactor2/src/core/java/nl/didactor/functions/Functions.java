package nl.didactor.functions;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;
import java.util.*;

/**
 * Some didactor specific Node functions (implemented as 'bean')
 * @author Michiel Meeuwissen
 * @version $Id: Functions.java,v 1.1 2007-06-08 12:20:31 michiel Exp $
 */
public class Functions {
    protected final static Logger log = Logging.getLoggerInstance(Functions.class);
    
    private Node node;

    public void setNode(Node n) {
        node = n;
    }
    
    public Locale educationLocale() {
        NodeList providers = node.getRelatedNodes("providers");
        Node provider = providers.getNode(0);
        String providerPath = provider.getStringValue("path");
        String educationPath = node.getStringValue("path");
        Locale language = org.mmbase.util.LocalizedString.getLocale(provider.getStringValue("locale"));
        
        return new Locale(language.getLanguage(), language.getCountry(), 
                          providerPath + ("".equals(providerPath) || "".equals(educationPath) ? "" : "_") + educationPath);

    }

    /**
     * A node is active if it is related to an mmevent which is active.
     */
    public boolean active() {
        Date now = new Date();
        NodeList mmevents = node.getRelatedNodes("mmevents");
        NodeIterator ni = mmevents.nodeIterator();
        while (ni.hasNext()) {
            Node node = ni.nextNode();
            Date start = node.getDateValue("start");
            Date stop  = node.getDateValue("stop");
            if (start.before(now) && stop.after(now)) {
                return true;
            }
        }
        return false;
    }
}
