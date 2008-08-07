
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
 * @version $Id: MMBaseComponent.java,v 1.1 2008-08-07 16:33:49 michiel Exp $
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



    @Override
    public Map<String, String> getScopesMap() {
        Map<String, String> scopes = new HashMap<String, String>();
        for (Block block : ComponentRepository.getInstance().getComponent(name).getBlocks()) {
            CLASS:
            for (Block.Type type : block.getClassification()) {
                while (! type.getParent().getName().equals("didactor")) {
                    type = type.getParent();
                    if (type.getParent() == null) continue CLASS;
                }
                Scope.valueOf(type.getName().toUpperCase()).put(scopes);
            }
        }
        log.info("Found " + scopes);
        return scopes;
    }

    public int getNumber() {
        return number;
    }

}

