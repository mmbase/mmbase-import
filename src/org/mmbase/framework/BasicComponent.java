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
import org.mmbase.util.functions.Parameter;
import org.mmbase.util.logging.*;

/**
 * A component is a piece of pluggable functionality that typically has dependencies on other
 * components, and may be requested several blocks.
 *
 * @author Michiel Meeuwissen
 * @version $Id: BasicComponent.java,v 1.14 2006-10-23 17:17:20 michiel Exp $
 * @since MMBase-1.9
 */
public class BasicComponent implements Component {
    private static final Logger log = Logging.getLoggerInstance(BasicComponent.class);

    private final String name;
    private final LocalizedString description;
    private final Map<String, Block> blocks = new HashMap<String, Block>();
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
        log.debug("Configuring " + this);
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
            Block b = new Block(name, mimetype, this);
            b.getDescription().fillFromXml("description", element);
            log.trace("Found block: " + name);
            b.getRenderers().put(Renderer.Type.HEAD, getRenderer("head", element, b));
            b.getRenderers().put(Renderer.Type.BODY, getRenderer("body", element, b));
            b.processor = getProcessor("process", element, b);
            if (defaultBlock == null) defaultBlock = b;
            blocks.put(name, b);
        }
        String defaultBlockName = el.getAttribute("defaultblock");
        if (defaultBlockName != null && ! defaultBlockName.equals("")) {
            Block b = blocks.get(defaultBlockName);
            if (b == null) {
                log.error("There is no block '" + defaultBlockName + "' so, cannot take it as default. Taking " + defaultBlock + " in stead");
            } else {
                defaultBlock = b;
            }
        }
        if (defaultBlock == null) {
            log.warn("No blocks found.");
        } else {
            log.service("Default block: " + defaultBlock);
        }
    }

    private Renderer getRenderer(String name, Element block, Block b) {
        NodeList renderElements = block.getElementsByTagName(name);
        log.debug("Number of [" + name + "] elements: " + renderElements.getLength());
        if (renderElements.getLength() < 1) return null;
        Element renderElement = (Element) renderElements.item(0);
        String jsp = renderElement.getAttribute("jsp");
        Renderer renderer;
        if (jsp != null && !"".equals(jsp)) {
            renderer = new JspRenderer(name.toUpperCase(), jsp, b);
        } else {
            try {
                Element classElement = (Element) renderElement.getElementsByTagName("class").item(0);
                renderer = (Renderer) ComponentRepository.getInstance(classElement, name.toUpperCase(), b);
            } catch (Exception e) {
                log.error(e);
                return null;
            }
        }
        Parameter[] params = Parameter.readArrayFromXml(renderElement);
        if (params.length > 0) { // a bit to simple, how can you explicitely make a renderer parameter-less now?
            if (renderer instanceof AbstractRenderer) {
                ((AbstractRenderer) renderer).addParameters(params);
            } else {
                log.warn("Don't know how to add parameters to " + renderer);
            }
        }
        return renderer;
    }

    private Processor getProcessor(String name, Element block, Block b) {
        NodeList processorElements = block.getElementsByTagName(name);
        if (processorElements.getLength() < 1) return null;
        Element processorElement = (Element) processorElements.item(0);
        String jsp = processorElement.getAttribute("jsp");
        Processor processor;
        if (jsp != null && !"".equals(jsp)) {
            processor = new JspProcessor(jsp, b);
        } else {
            try {
                Element classElement = (Element) processorElement.getElementsByTagName("class").item(0);
                processor = (Processor) ComponentRepository.getInstance(classElement, name.toUpperCase(), b);
            } catch (Exception e) {
                log.error(e);
                return null;
            }
        }
        Parameter[] params = Parameter.readArrayFromXml(processorElement);
        if (params.length > 0) { // a bit to simple, how can you explicitely make a processor parameter-less now?
            if (processor instanceof AbstractProcessor) {
                ((AbstractProcessor) processor).addParameters(params);
            } else {
                log.warn("Don't know how to add parameters to " + processor);
            }
        }
        return processor;

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

    public String toString() {
        return getName();
    }

}
