package nl.didactor.builders;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;
import java.util.*;

/**
 * Some didactor specific Node functions (implemented as 'bean')
 * @author Michiel Meeuwissen
 * @version $Id: Functions.java,v 1.1 2007-05-11 10:14:05 michiel Exp $
 */
public class Functions {
    protected final static Logger log = Logging.getLoggerInstance(Functions.class);
    
    private Node node;

    public Functions() {
    }

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
}
