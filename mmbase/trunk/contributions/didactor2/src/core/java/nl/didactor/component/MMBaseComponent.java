
package nl.didactor.component;
import org.mmbase.framework.*;
import org.mmbase.bridge.Node;
import org.mmbase.module.core.MMObjectNode;
import java.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;



/**
 * The didactor component wrapping an mmbase component.
 * @author Michiel Meeuwissen
 * @version $Id: MMBaseComponent.java,v 1.4 2008-08-21 09:08:46 michiel Exp $
 */

public class MMBaseComponent extends nl.didactor.component.Component {
    private static final Logger log = Logging.getLoggerInstance(MMBaseComponent.class);

    enum Scope {
        COMPONENT("component"),
            PROVIDERS("provider"),
            EDUCATIONS("education", PROVIDERS),
            CLASSES("class", EDUCATIONS),
            PEOPLE("people");
        private final String ref;
        private final Scope implies;
        Scope(String r) {
            ref = r;
            implies = null;
        }
        Scope(String r, Scope i) {
            ref = r;
            implies = i;
        }

        public void put(Map<String, String> scopes) {
            scopes.put(toString().toLowerCase(), ref);
            if (implies != null) implies.put(scopes);
        }
    }

    private final String name;
    private final int number;

    public MMBaseComponent(Node node) {
        name = node.getStringValue("name");
        number = node.getNumber();
    }
    public MMBaseComponent(MMObjectNode node) {
        name = node.getStringValue("name");
        number = node.getNumber();
    }

    @Override
    public String getVersion() {
        return "" + ComponentRepository.getInstance().getComponent(name).getVersion();
    }
    @Override
    public String getName() {
        return name;
    }
    @Override
    public Component[] dependsOn() {
        return new Component[0];
    }
    @Override
    public void init() {
    }


    protected org.mmbase.framework.Component getComponent() {
        return ComponentRepository.getInstance().getComponent(name);
    }


    @Override
    public Map<String, String> getScopesMap() {
        Map<String, String> scopes = new HashMap<String, String>();
        for (Block block : getComponent().getBlocks()) {
            CLASS:
            for (Block.Type type : block.getClassification()) {
                if (type.getParent() == null) continue;
                while (! type.getParent().getName().equals("didactor")) {
                    type = type.getParent();
                    if (type.getParent() == null) continue CLASS;
                }
                try {
                    Scope.valueOf(type.getName().toUpperCase()).put(scopes);
                } catch (IllegalArgumentException iae) {
                    // never mind. Not a recognized scope.
                }
            }
        }
        log.info("Found " + scopes);
        return scopes;
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public String getTemplateBar() {
        Framework fw = Framework.getInstance();
        return (String) fw.getSettingValue(getComponent().getSetting("didactor_templatebar"), fw.createSettingValueParameters());
    }

}

