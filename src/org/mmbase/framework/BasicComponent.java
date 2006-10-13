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
 * @version $Id: BasicComponent.java,v 1.6 2006-10-13 17:22:15 michiel Exp $
 * @since MMBase-1.9
 */
public class BasicComponent implements Component {
    private static final Logger log = Logging.getLoggerInstance(BasicComponent.class);

    private final String name;
    private final LocalizedString description;
    private final Map<String, Block> blocks = new HashMap();
    private Block defaultBlock = null;

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
        log.service("Start configure()");
        description.fillFromXml("description", el);
        NodeList blockElements = el.getElementsByTagName("block");
        if (log.isDebugEnabled()) {
            log.debug("Found description: " + description);
            log.debug("Found number of blocks: " + blocks);
        }
        for (int i = 0 ; i < blockElements.getLength(); i++) {
            Element element = (Element) blockElements.item(i);
            String name = element.getAttribute("name");
            String mimetype = element.getAttribute("mimetype");
            Block b = new Block(name, mimetype);
            log.trace("Found block: " + name);
            b.getRenderers().put(Renderer.Type.HEAD, getRenderer("head", element));
            b.getRenderers().put(Renderer.Type.BODY, getRenderer("body", element));
            b.processor = getProcessor("process", element);
            if (defaultBlock == null) defaultBlock = b;
            blocks.put(name, b);
        }
        String defaultBlockName = el.getAttribute("defaultblock");
        if (defaultBlockName != null && ! defaultBlockName.equals("")) {
            Block b = blocks.get(defaultBlockName);
            if (b == null) {
                log.error("There is not block '" + defaultBlockName + "' so, cannot take it as default. Taking " + defaultBlock + " in stead");
            } else {
                defaultBlock = b;
            }
        }
        log.service("Default block: " + defaultBlock);
    }

    private Renderer getRenderer(String name, Element block) {
        NodeList heads = block.getElementsByTagName(name);
        log.debug("Number of [" + name + "] elements: " + heads.getLength());
        if (heads.getLength() == 1) {
            Element head = (Element) heads.item(0);
            String jsp = head.getAttribute("jsp");
            String cls = head.getAttribute("class");
            log.trace("jsp: [" + jsp + "], class: [" + cls + "]");
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
            log.trace("No [" + name + "] element found");
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

    public Collection<Block> getBlocks() {
        return Collections.unmodifiableCollection(blocks.values());
    }
    public Block getBlock(String name) {
        if (name == null) return getDefaultBlock();
        return blocks.get(name);
    }
    public Block getDefaultBlock() {
        return defaultBlock;
    }

}
