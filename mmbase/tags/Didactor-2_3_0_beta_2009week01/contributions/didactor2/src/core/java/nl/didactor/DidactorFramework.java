/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package nl.didactor;

import nl.didactor.component.MMBaseComponent;
import org.mmbase.framework.basic.BasicFramework;
import org.mmbase.framework.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.SearchUtil;

import org.w3c.dom.Element;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**

 *
 * @author Michiel Meeuwissen
 * @version $Id: DidactorFramework.java,v 1.6 2009-01-02 09:36:10 michiel Exp $
 * @since Didactor-2.3
 */
public class DidactorFramework extends BasicFramework {
    private static final Logger log = Logging.getLoggerInstance(DidactorFramework.class);


    public DidactorFramework(Element el) {
        super(el);
        org.mmbase.util.ThreadPools.jobsExecutor.execute(new Runnable() {public void run() {DidactorFramework.this.dispatchComponents() ;}});
    }

    protected void dispatchComponents() {
        // make sure every component has a corresponding component object
        // This ought to make any mmbase component useable in didactor.

        Cloud cloud = ContextProvider.getDefaultCloudContext().assertUp().getCloud("mmbase", "class", null);

        NodeManager nm = cloud.getNodeManager("components");

        ComponentRepository rep = ComponentRepository.getInstance();
        log.info("Checking components " + rep.toMap().keySet());

        COMPONENT:
        for (Component comp : rep.getComponents()) {
            Node node = SearchUtil.findNode(cloud, "components", "name", comp.getName());
            if (comp.getSetting("has_didactor_component") != null) continue;
            if (node == null) {
                for (Block block : comp.getBlocks()) {
                    CLASS:
                    for (Block.Type type :  block.getClassification()) {
                        while (type.getParent() != Block.Type.ROOT) {
                            type = type.getParent();
                            if (type == null) continue CLASS;
                        }
                        if (type.getName().equals("didactor")) {
                            log.info("No object found for " + comp.getName() + " wich has blocks classified as didactor. createing one now");
                            node = nm.createNode();
                            node.setStringValue("name", comp.getName());
                            node.setStringValue("classname", MMBaseComponent.class.getName());
                            node.commit();
                            nl.didactor.component.Component.register(comp.getName(), new MMBaseComponent(node));
                            continue COMPONENT;
                        }
                    }

                }
                log.debug("No blocks classified as didactor found for " + comp);
            } else {

            }
        }
    }

    @Override
    public String getName() {
        return "DidactorFramework";
    }


    @Override
    protected String getComponentClass() {
        return "mm_fw_didactor";
    }

}
