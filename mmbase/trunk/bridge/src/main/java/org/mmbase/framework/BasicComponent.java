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

    @Override
    public String getName() {
        return name;
    }
    @Override
    public URI getUri() {
        return uri;
    }
    @Override
    public float getVersion() {
        return version;
    }


    @Override
    public LocalizedString getDescription() {
        return description;
    }

    /**
     * EXPERIMENTAL. The Manifest of the jar in which this component is defined.
     * @since MMBase-1.9.1
     */
    public java.util.jar.Manifest getManifest() {
        if (uri != null) {
            String[] parts = uri.toString().split("!", 2);
            if (parts.length == 2) {
                try {
                    java.net.URL jarUrl = new java.net.URL(parts[0] + "!/");
                    java.net.JarURLConnection jarConnection = (java.net.JarURLConnection)jarUrl.openConnection();
                    return  jarConnection.getManifest();
                } catch (Exception e) {
                    log.warn(e);
                }
            }
        }
        return null;
    }

    @Override
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
                String n = element.getAttribute("component");
                float v = Float.parseFloat(element.getAttribute("version"));
                Component comp = ComponentRepository.repository.getComponent(n);
                if (comp != null && comp.getVersion() >= v) {
                    dependencies.add(comp);
                } else {
                    unsatisfied.add(new VirtualComponent(n, v));

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

        ActionRepository.getInstance().fillFromXml(el, name);

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
                b.putRenderer(Renderer.Type.HEAD, getRenderer("head", Renderer.Type.HEAD, element, b));
                b.putRenderer(Renderer.Type.BODY, getRenderer("body", Renderer.Type.BODY, element, b));
                b.setProcessor(getProcessor(element, b));
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

    private Renderer getRenderer(String elementName, Renderer.Type type, Element block, Block b) {
        NodeList renderElements = block.getElementsByTagName(elementName);
        log.debug("Number of [" + name + "] elements: " + renderElements.getLength());
        if (renderElements.getLength() < 1) return null;
        Renderer renderer = null;
        for (int i = 0; i < renderElements.getLength(); i++) {
            Element renderElement = (Element) renderElements.item(i);
            String jsp = renderElement.getAttribute("jsp");

            Renderer subRenderer;
            if (!"".equals(jsp)) {
                subRenderer = new JspRenderer(type, jsp, b);
            } else {
                try {
                    subRenderer = (Renderer) Instantiator.getInstanceWithSubElement(renderElement, type, b);
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
                    ChainedRenderer chain = new ChainedRenderer(type, b);
                    chain.add(renderer);
                    chain.add(subRenderer);
                    renderer = chain;
                }
            }
        }
        return renderer;

    }

    private Processor getProcessor(Element block, Block b) {
        NodeList processorElements = block.getElementsByTagName("processor");
        if (processorElements.getLength() < 1) return null;
        Element processorElement = (Element) processorElements.item(0);
        String jsp = processorElement.getAttribute("jsp");
        Processor processor;
        if (!"".equals(jsp)) {
            processor = new JspProcessor(jsp, b);
        } else {
            try {
                processor = (Processor) Instantiator.getInstanceWithSubElement(processorElement, b);
            } catch (Exception e) {
                log.error(e);
                return null;
            }
        }
        addParameters(processorElement, b);

        return processor;

    }

    @Override
    public Collection<Block> getBlocks() {
        return Collections.unmodifiableCollection(blocks.values());
    }
    @Override
    public Block getBlock(String name) {
        if (name == null) return getDefaultBlock();
        return blocks.get(name);
    }
    @Override
    public Block getDefaultBlock() {
        return defaultBlock;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String getBundle() {
        return bundle;
    }

    @Override
    public Collection<Setting<?>> getSettings() {
        return settings.values();
    }

    @Override
    public Setting<?> getSetting(String name) {
        return settings.get(name);
    }

    @Override
    public Collection<Component> getDependencies() {
        return Collections.unmodifiableCollection(dependencies);
    }

    @Override
    public Collection<VirtualComponent> getUnsatisfiedDependencies() {
        return Collections.unmodifiableCollection(unsatisfied);
    }

    @Override
    public Map<String, Action> getActions() {
        return ActionRepository.getInstance().get(getName());
    }

    @Override
    public void resolve(VirtualComponent unsat, Component comp) {
        unsatisfied.remove(unsat);
        dependencies.add(comp);
    }
}
