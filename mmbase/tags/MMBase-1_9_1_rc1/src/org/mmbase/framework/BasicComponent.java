/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;

import java.util.*;
import java.util.concurrent.*;
import org.w3c.dom.*;
import java.net.URI;
import org.mmbase.security.*;
import org.mmbase.util.xml.Instantiator;
import org.mmbase.util.LocalizedString;
import org.mmbase.util.functions.Parameter;
import org.mmbase.util.logging.*;

/**
 * A component is a piece of pluggable functionality that typically has dependencies on other
 * components, and may be requested several blocks.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9
 */
public class BasicComponent implements Component {
    private static final Logger log = Logging.getLoggerInstance(BasicComponent.class);


    private final String name;
    private String bundle;
    private final LocalizedString description;
    private final Map<String, Block> blocks        = new ConcurrentHashMap<String, Block>();
    private final Map<String, Setting<?>> settings = new ConcurrentHashMap<String, Setting<?>>();
    private Block defaultBlock = null;
    private URI uri;
    private float version = 0.0f;

    protected final Collection<Component> dependencies        = new CopyOnWriteArraySet<Component>();
    protected final Collection<VirtualComponent> unsatisfied  = new CopyOnWriteArraySet<VirtualComponent>();


    public BasicComponent(String name) {
        this.name = name;
        this.description = new LocalizedString(name);
        init();
    }

    /**
     * Called on initializion. Default implementation is empty.
     */
    protected void init() {
    }

    public String getName() {
        return name;
    }
    public URI getUri() {
        return uri;
    }
    public float getVersion() {
        return version;
    }


    public LocalizedString getDescription() {
        return description;
    }

    public void configure(Element el) {
        try {
            uri = new URI(el.getOwnerDocument().getDocumentURI());
        } catch (Exception e) {
            log.error(e);
        }
        log.debug("Configuring " + this);
        description.fillFromXml("description", el);

        version = Float.parseFloat(el.getAttribute("version"));

        {
            NodeList depElements = el.getElementsByTagName("dependency");
            for (int i = 0; i < depElements.getLength(); i++) {
                Element element = (Element) depElements.item(i);
                String name = element.getAttribute("component");
                float version = Float.parseFloat(element.getAttribute("version"));
                Component comp = ComponentRepository.getInstance().getComponent(name);
                if (comp != null && comp.getVersion() >= version) {
                    dependencies.add(comp);
                } else {
                    unsatisfied.add(new VirtualComponent(name, version));

                }
            }
        }
        {
            NodeList bundleElements = el.getElementsByTagName("bundle");
            if(bundleElements.getLength() > 0) {
                bundle = ((Element) bundleElements.item(0)).getAttribute("name");
            }
        }

        {
            NodeList settingElements = el.getElementsByTagName("setting");
            for (int i = 0; i < settingElements.getLength(); i++) {
                Element element = (Element) settingElements.item(i);
                Setting s = new Setting(this, element);
                settings.put(s.getName(), s);
            }
        }
        {
            NodeList actionElements = el.getElementsByTagName("action");
            for (int i = 0; i < actionElements.getLength(); i++) {
                try {
                    Element element = (Element) actionElements.item(i);
                    String actionName = element.getAttribute("name");
                    String rank = element.getAttribute("rank");
                    Object c = Instantiator.getInstanceWithSubElement(element);
                    Action a;
                    if (c != null) {
                        if (! "".equals(rank)) {
                            log.warn("Rank attribute ignored");
                        }
                        a = new Action(name, actionName, (ActionChecker) c);
                    } else {
                        if ("".equals(rank)) { rank = "basic user"; }
                        a = new Action(name, actionName, new ActionChecker.Rank(Rank.getRank(rank)));
                    }
                    a.getDescription().fillFromXml("description", element);
                    log.service("Registering action " + a);
                    ActionRepository.getInstance().add(a);
                } catch (Exception e) {
                    log.error(e);
                }
            }
        }

        {
            NodeList blockElements = el.getElementsByTagName("block");
            if (log.isDebugEnabled()) {
                log.debug("Found description: " + description);
                log.debug("Found number of blocks: " + blockElements.getLength());
            }
            for (int i = 0 ; i < blockElements.getLength(); i++) {
                Element element = (Element) blockElements.item(i);
                String blockName = element.getAttribute("name");
                String mimetype = element.getAttribute("mimetype");
                String classification = element.getAttribute("classification");
                // create types if missing
                Block.Type.getClassification(classification, true);
                Block b = new Block(blockName, mimetype, this, classification);
                b.getDescription().fillFromXml("description", element);
                b.getTitle().fillFromXml("title", element);
                log.trace("Found block: " + blockName);
                b.putRenderer(Renderer.Type.HEAD, getRenderer("head", element, b));
                b.putRenderer(Renderer.Type.BODY, getRenderer("body", element, b));
                b.setProcessor(getProcessor("process", element, b));
                if (defaultBlock == null) defaultBlock = b;
                blocks.put(blockName, b);
            }
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
            log.service("No blocks found for " + this + " " + uri);
        } else {
            log.debug("Default block: " + defaultBlock);
        }
    }

    private void addParameters(Element rendererElement, Block b) {
        Parameter[] params = Parameter.readArrayFromXml(rendererElement);
        boolean automatic = rendererElement.getAttribute("automaticParameters").equals("true");
        if (params.length == 0 && automatic) {
            // if addParameters function never called, it behaves with 'AutoDefinedParameters'.
        } else {
            if (automatic) {
                log.warn("Ignoring that automaticParameters, because parameters were explictely defined");
            }
            b.addParameters(params);
        }
    }

    private Renderer getRenderer(String name, Element block, Block b) {
        NodeList renderElements = block.getElementsByTagName(name);
        log.debug("Number of [" + name + "] elements: " + renderElements.getLength());
        if (renderElements.getLength() < 1) return null;
        Renderer renderer = null;
        for (int i = 0; i < renderElements.getLength(); i++) {
            Element renderElement = (Element) renderElements.item(i);
            String jsp = renderElement.getAttribute("jsp");

            Renderer subRenderer;
            if (!"".equals(jsp)) {
                subRenderer = new JspRenderer(name.toUpperCase(), jsp, b);
            } else {
                try {
                    subRenderer = (Renderer) Instantiator.getInstanceWithSubElement(renderElement, name.toUpperCase(), b);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    return null;
                }
            }
            addParameters(renderElement, b);

            if (renderer == null) {
                renderer = subRenderer;
            } else {
                if (renderer instanceof ChainedRenderer) {
                    ((ChainedRenderer) renderer).add(subRenderer);
                } else {
                    ChainedRenderer chain = new ChainedRenderer(name.toUpperCase(), b);
                    chain.add(renderer);
                    chain.add(subRenderer);
                    renderer = chain;
                }
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
        if (!"".equals(jsp)) {
            processor = new JspProcessor(jsp, b);
        } else {
            try {
                processor = (Processor) Instantiator.getInstanceWithSubElement(processorElement, name.toUpperCase(), b);
            } catch (Exception e) {
                log.error(e);
                return null;
            }
        }
        addParameters(processorElement, b);

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

    public String getBundle() {
        return bundle;
    }

    public Collection<Setting<?>> getSettings() {
        return settings.values();
    }

    public Setting<?> getSetting(String name) {
        return settings.get(name);
    }

    public Collection<Component> getDependencies() {
        return Collections.unmodifiableCollection(dependencies);
    }

    public Collection<VirtualComponent> getUnsatisfiedDependencies() {
        return Collections.unmodifiableCollection(unsatisfied);
    }

    public Map<String, Action> getActions() {
        return ActionRepository.getInstance().get(getName());
    }

    public void resolve(VirtualComponent unsat, Component comp) {
        unsatisfied.remove(unsat);
        dependencies.add(comp);
    }
}
