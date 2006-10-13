/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;

import java.util.*;
import org.w3c.dom.*;
import org.mmbase.util.LocalizedString;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.*;

/**
 * A component is a piece of pluggable functionality that typically has dependencies on other
 * components, and may be requested several blocks.
 *
 * @author Michiel Meeuwissen
 * @version $Id: BasicComponent.java,v 1.4 2006-10-13 13:36:54 johannes Exp $
 * @since MMBase-1.9
 */
public class BasicComponent implements Component {
    private static final Logger log = Logging.getLoggerInstance(BasicComponent.class);

    private final String name;
    private final LocalizedString description;
    private final Map<String, Block> blocks = new HashMap();

    public BasicComponent(String name) {
        this.name = name;
        this.description = new LocalizedString(name);
    }

    public String getName() {
        return name;
    }

    public LocalizedString getDescription() {
        return description;
    }

    public void configure(Element el) {
        log.warn("Start configure()");
        description.fillFromXml("description", el);
        NodeList blocks = el.getElementsByTagName("block");
        log.warn("Found description: " + description);
        log.warn("Found number of blocks: " + blocks);
        for (int i = 0 ; i < blocks.getLength(); i++) {
            Element block = (Element) blocks.item(i);
            String name = block.getAttribute("name");
            String mimetype = block.getAttribute("mimetype");
            Block b = new Block(name, mimetype);
            log.warn("Found block: " + name);

            b.head = getRenderer("head", block);
            b.body = getRenderer("body", block);
            b.processor = getProcessor("process", block);

            this.blocks.put(name, b);
        }
    }

    private Renderer getRenderer(String name, Element block) {
        NodeList heads = block.getElementsByTagName(name);
        log.warn("Number of [" + name + "] elements: " + heads.getLength());
        if (heads.getLength() == 1) {
            Element head = (Element) heads.item(0);
            String jsp = head.getAttribute("jsp");
            String cls = head.getAttribute("class");
            log.warn("jsp: [" + jsp + "], class: [" + cls + "]");
            if (jsp != null && !"".equals(jsp)) {
                return new JspRenderer(name.toUpperCase(), jsp);
            } else if (cls != null && !"".equals(cls)) {
                try {
                    return (Renderer)Class.forName(cls).newInstance();
                } catch (Exception e) {
                    log.error(e);
                }
            } else {
                log.error("JSP and CLASS are null!");
            }
        } else {
            log.warn("No [" + name + "] element found");
        }
        return null;
    }

    private Processor getProcessor(String name, Element block) {
        NodeList heads = block.getElementsByTagName(name);
        if (heads.getLength() == 1) {
            Element head = (Element) heads.item(0);
            String jsp = head.getAttribute("jsp");
            String cls = head.getAttribute("class");
            if (jsp != null && !"".equals(jsp)) {
                return new JspProcessor(jsp);
            } else if (cls != null && !"".equals(cls)) {
                try {
                    return (Processor)Class.forName(cls).newInstance();
                } catch (Exception e) {
                    log.error(e);
                }
            } else {
                log.error("JSP and CLASS are null!");
            }
        }
        return null;
    }

    public String toString() {
        return getName();
    }

    public Map<String, Block> getBlocks() {
        return Collections.unmodifiableMap(blocks);
    }

}
