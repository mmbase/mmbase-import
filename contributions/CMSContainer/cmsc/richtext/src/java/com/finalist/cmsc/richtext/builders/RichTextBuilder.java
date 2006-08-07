package com.finalist.cmsc.richtext.builders;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import net.sf.mmapps.commons.util.StringUtil;


import org.mmbase.applications.wordfilter.WordHtmlCleaner;
import org.mmbase.bridge.*;
import org.mmbase.core.CoreField;
import org.mmbase.datatypes.DataType;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.BasicFieldValueConstraint;
import org.mmbase.storage.search.implementation.NodeSearchQuery;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.finalist.cmsc.richtext.RichText;

/**
 * @author Nico Klasens (Finalist IT Group)
 * @author Hillebrand Gelderblom
 * @author Cees Roele
 */
public class RichTextBuilder extends MMObjectBuilder {

    /** MMbase logging system */
    private static Logger log = Logging.getLoggerInstance(RichTextBuilder.class.getName());

    /** list of html text fields to clean */
    private List<String> htmlFields = new ArrayList<String>();

    private MMObjectBuilder inlinerelBuilder = null;
    private int inlinerelNumber = -1;

    private MMObjectBuilder imagerelBuilder = null;
    private int imagerelNumber = -1;

    /**
     * @see org.mmbase.module.core.MMObjectBuilder#init()
     */
    public boolean init() {
        if (!super.init()) { return false; }

        Collection fields = getFields();
        for (Iterator iter = fields.iterator(); iter.hasNext();) {
            CoreField field = (CoreField) iter.next();
            DataType dataType = field.getDataType();
            while(StringUtil.isEmpty(dataType.getName())) {
                dataType = dataType.getOrigin();
            }
            if (RichText.RICHTEXT_TYPE.equals(dataType.getName())) {
                 String fieldname = field.getName();
                 log.debug("richtext field: " + fieldname.trim());
                 htmlFields.add(fieldname);
             }
        }
        return true;
    }

    private void initInlineBuilders() {
        if (inlinerelBuilder == null) {
            inlinerelBuilder = mmb.getMMObject(RichText.INLINEREL_NM);
            if (inlinerelBuilder == null) { throw new RuntimeException("Builder '"
                    + RichText.INLINEREL_NM + "' does not exist."); }
            inlinerelNumber = mmb.getRelDef().getNumberByName(RichText.INLINEREL_NM);
        }

        if (imagerelBuilder == null) {
            imagerelBuilder = mmb.getMMObject(RichText.IMAGEINLINEREL_NM);
            if (imagerelBuilder == null) { throw new RuntimeException("Builder '"
                    + RichText.IMAGEINLINEREL_NM + "' does not exist."); }
            imagerelNumber = mmb.getRelDef().getNumberByName(RichText.IMAGEINLINEREL_NM);
        }
    }
    
    public boolean commit(MMObjectNode node) {
        initInlineBuilders();

        log.debug("committing node " + node);
        List<String> idsList = new ArrayList<String>();
        // Resolve images
        List fields = getFields(NodeManager.ORDER_EDIT);

        boolean htmlFieldChanged = false;
        
        Iterator checkFields = fields.iterator();
        while (checkFields.hasNext()) {
            Field field = (Field) checkFields.next();
            if (field != null) {
                String fieldName = field.getName();
                if (htmlFields.contains(fieldName) && node.getChanged().contains(fieldName)) {
                    htmlFieldChanged = true;
                }
            }
        }
        
        if (htmlFieldChanged) {
            Iterator iFields = fields.iterator();
            while (iFields.hasNext()) {
                Field field = (Field) iFields.next();
    
                if (field != null) {
                    String fieldName = field.getName();
                    if(log.isDebugEnabled()) {
                        log.debug("Got field " + fieldName + " : persistent = " + (!field.isVirtual())
                            + ", stringtype = " + (field.getType() == Field.TYPE_STRING)
                            + ", isHtmlField = " + htmlFields.contains(fieldName));
                    }
                    
                    if (htmlFields.contains(fieldName)) {
                        log.debug("Evaluating " + fieldName);
                        if (node.getChanged().contains(fieldName)) {
                            // Persistent string field.
                            String fieldValue = (String) node.getValues().get(fieldName);
                            if (!StringUtil.isEmpty(fieldValue)) {
                                try {
                                    if (RichText.hasRichtextItems(fieldValue)) {
                                        Document doc = RichText.getRichTextDocument(fieldValue);

                                        resolveLinks(doc, idsList, node);
                                        resolveImages(doc, idsList, node);

                                        String out = RichText.getRichTextString(doc);
                                        out = WordHtmlCleaner.fixEmptyAnchors(out);
                                        log.debug("final richtext text = " + out);

                                        node.setValue(fieldName, out);
                                    }
                                }
                                catch (Exception e) {
                                    log.error("An error occured while resolving inline resources!", e);
                                }
                            }
                        }
                        else {
                            String fieldValue = (String) node.getValues().get(fieldName);
                            if (!StringUtil.isEmpty(fieldValue) && RichText.hasRichtextItems(fieldValue)) {
                                Document doc = RichText.getRichTextDocument(fieldValue);
                                fillIdFromLinks(doc, idsList);
                                fillIdFromImages(doc, idsList);
                            }
                        }
                    }
                }
            }
        }

        boolean committed = super.commit(node);
        
        if (committed) {
            if (htmlFieldChanged) {
                // remove outdated inlinerel
                Enumeration idrels = node.getRelations(RichText.INLINEREL_NM);
                while (idrels.hasMoreElements()) {
                    MMObjectNode rel = (MMObjectNode) idrels.nextElement();
                    String referid = rel.getStringValue(RichText.REFERID_FIELD);
                    if ((rel.getIntValue("snumber") == node.getNumber()) && !idsList.contains(referid)) {
                        inlinerelBuilder.removeNode(rel);
                        log.debug("removed unused inlinerel: " + referid);
                    }
                }

                // remove outdated imageinlinerel
                Enumeration imagerels = node.getRelations(RichText.IMAGEINLINEREL_NM);
                while (imagerels.hasMoreElements()) {
                    MMObjectNode rel = (MMObjectNode) imagerels.nextElement();
                    String referid = rel.getStringValue(RichText.REFERID_FIELD);
                    if (!idsList.contains(referid)) {
                        imagerelBuilder.removeNode(rel);
                        log.debug("removed unused imageinlinerel: " + referid);
                    }
                }
            }
        }

        return committed;
    }

    private void fillIdFromImages(Document doc, List<String> idsList) {
        fillIds(doc, idsList, RichText.IMG_TAGNAME);
    }

    private void fillIdFromLinks(Document doc, List<String> idsList) {
        fillIds(doc, idsList, RichText.LINK_TAGNAME);
    }

    private void fillIds(Document doc, List<String> idsList, String tagname) {
        NodeList nl = doc.getElementsByTagName(tagname);
        for (int i = 0, len = nl.getLength(); i < len; i++) {
            Element link = (Element) nl.item(i);
            if (link.hasAttribute(RichText.RELATIONID_ATTR)) {
                String id = link.getAttribute(RichText.RELATIONID_ATTR);
                idsList.add(id);
            }
        }
    }

    /**
     * resolve links in the richtextfield en make inlinerel of it. Add the id to the anchortag so
     * that the link can be resolved in the frontend en point to the correct article.
     */
    private void resolveLinks(Document doc, List<String> idsList, MMObjectNode mmObj) {
        if (doc == null) {
            return;
        }
        // collect <A> tags
        NodeList nl = doc.getElementsByTagName(RichText.LINK_TAGNAME);
        log.debug("number of links: " + nl.getLength());

        for (int i = 0, len = nl.getLength(); i < len; i++) {
            Element link = (Element) nl.item(i);

            // handle relations to other objects
            if (isInlineAttributesComplete(link)) {
                // get id of the link
                String id = link.getAttribute(RichText.RELATIONID_ATTR);
                int destination = Integer.parseInt(link.getAttribute(RichText.DESTINATION_ATTR));

                MMObjectNode inlinerel = getInlineRel(id);
                if (inlinerel != null) {
                    // check if destination is still the same
                    int dnumber = inlinerel.getIntValue("dnumber");
                    if (destination == dnumber) {
                        idsList.add(id);
                    }
                    else {
                        link.removeAttribute(RichText.RELATIONID_ATTR);
                        String referid = createInlineRel(mmObj, destination);
                        // update richtext
                        link.setAttribute(RichText.RELATIONID_ATTR, referid);
                        idsList.add(referid);
                    }
                }
                else {
                    // check if destination still exists
                    if (getNode(destination) != null) {
                        // create inlinerel to related object
                        String referid = createInlineRel(mmObj, destination);
                        // update richtext
                        link.setAttribute(RichText.RELATIONID_ATTR, referid);

                        idsList.add(referid);
                    }
                    else {
                        // destination does also not exist!
                        link.setAttribute("title", "Link, element of bestemming is verwijderd !!!");
                    }
                }
                if (link.hasAttribute(RichText.HREF_ATTR)) {
                    link.removeAttribute(RichText.HREF_ATTR);
                }
            }
            else {
                if (isNewInline(link)) {
                    // create inlinerel to related object
                    int objId = Integer.parseInt(link.getAttribute(RichText.DESTINATION_ATTR));

                    String referid = createInlineRel(mmObj, objId);
                    link.setAttribute(RichText.RELATIONID_ATTR, referid);

                    idsList.add(referid);
                }
                else {
                    if (link.hasAttribute(RichText.RELATIONID_ATTR)) {
                        String id = link.getAttribute(RichText.RELATIONID_ATTR);
                        idsList.add(id);
                    }
                    else {
                        if (link.hasAttribute(RichText.HREF_ATTR)) {
                            String href = link.getAttribute(RichText.HREF_ATTR);
                            String name = link.getAttribute("title");
                            String owner = mmObj.getStringValue("owner");
                            MMObjectNode urlNode = createUrl(owner, href, name);
    
                            String referid = createInlineRel(mmObj, urlNode.getNumber());
    
                            link.setAttribute(RichText.RELATIONID_ATTR, referid);
                            link.setAttribute(RichText.DESTINATION_ATTR, String.valueOf(urlNode.getNumber()));
                            link.removeAttribute(RichText.HREF_ATTR);

                            idsList.add(referid);
                            log.debug("imported url " + urlNode.getNumber()
                                    + " and added idrel: " + referid);
                        }
                    }
                }
            }
        }
    }

    private void resolveImages(Document doc, List<String> idsList, MMObjectNode mmObj) {
        if (doc == null) {
            return;
        }

        NodeList nl = doc.getElementsByTagName(RichText.IMG_TAGNAME);
        log.debug("number of images: " + nl.getLength());
        for (int i = 0, len = nl.getLength(); i < len; i++) {
            Element image = (Element) nl.item(i);

            if (isInlineAttributesComplete(image)) {
                // get id of the image
                String id = image.getAttribute(RichText.RELATIONID_ATTR);
                int destination = Integer.parseInt(image.getAttribute(RichText.DESTINATION_ATTR));

                MMObjectNode imagerel = getImageInlineRel(id);
                if (imagerel != null) {
                    String width = null;
                    if (image.hasAttribute(RichText.WIDTH_ATTR)) {
                        width = image.getAttribute(RichText.WIDTH_ATTR);
                        image.removeAttribute(RichText.WIDTH_ATTR);
                    }
                    String height = null;
                    if (image.hasAttribute(RichText.HEIGHT_ATTR)) {
                        height = image.getAttribute(RichText.HEIGHT_ATTR);
                        image.removeAttribute(RichText.HEIGHT_ATTR);
                    }
                    String legend = null;
                    if (image.hasAttribute(RichText.LEGEND)) {
                        legend = image.getAttribute(RichText.LEGEND);
                        image.removeAttribute(RichText.LEGEND);
                    }

                    // check if destination is still the same
                    int dnumber = imagerel.getIntValue("dnumber");
                    if (destination == dnumber) {
                        setImageIdRelFields(imagerel, height, width, legend);
                        imagerel.commit();
                        idsList.add(id);
                    }
                    else {
                        String referid = createImageIdRel(mmObj, destination, height, width, legend);
                        image.setAttribute(RichText.RELATIONID_ATTR, referid);
                        idsList.add(referid);
                    }
                }
                else {
                    // check if destination still exists
                    if (getNode(destination) != null) {
                        // update richtext
                        String width = null;
                        if (image.hasAttribute(RichText.WIDTH_ATTR)) {
                            width = image.getAttribute(RichText.WIDTH_ATTR);
                            image.removeAttribute(RichText.WIDTH_ATTR);
                        }
                        String height = null;
                        if (image.hasAttribute(RichText.HEIGHT_ATTR)) {
                            height = image.getAttribute(RichText.HEIGHT_ATTR);
                            image.removeAttribute(RichText.HEIGHT_ATTR);
                        }
                        String legend = null;
                        if (image.hasAttribute(RichText.LEGEND)) {
                            legend = image.getAttribute(RichText.LEGEND);
                            image.removeAttribute(RichText.LEGEND);
                        }
                        String referid = createImageIdRel(mmObj, destination, height, width, legend);
                        image.setAttribute(RichText.RELATIONID_ATTR, referid);

                        idsList.add(referid);
                    }
                    else {
                        // destination does also not exist!
                        // image.removeAttribute(RichText.SRC_ATTR);
                        // image.getParentNode().removeChild(image);

                        // Het verwijderen van de image tag bij het ontbreken van het plaatje is
                        // uitgecommentarieerd omdat
                        // dit fout ging bij publiceren. Eerst wordt het artikel gepubliceerd en
                        // vervolgens pas de secundaire
                        // elementen die eraan hangen. Bij het publiceren wordt deze klasse
                        // uitgevoerd voor het opslaan van het
                        // artikel, maar op dat moment is het plaatje nog niet gepubliceerd en
                        // bestaat dus nog niet. De img tag wordt
                        // dan vrolijk verwijderd.
                    }
                }
                if (image.hasAttribute(RichText.SRC_ATTR)) {
                    image.removeAttribute(RichText.SRC_ATTR);
                }
            }
            else {
                // handle relations to other objects
                if (isNewInline(image)) {
                    // create inlinerel to related object
                    int objId = Integer.parseInt(image.getAttribute(RichText.DESTINATION_ATTR));

                    String width = null;
                    if (image.hasAttribute(RichText.WIDTH_ATTR)) {
                        width = image.getAttribute(RichText.WIDTH_ATTR);
                        image.removeAttribute(RichText.WIDTH_ATTR);
                    }
                    String height = null;
                    if (image.hasAttribute(RichText.HEIGHT_ATTR)) {
                        height = image.getAttribute(RichText.HEIGHT_ATTR);
                        image.removeAttribute(RichText.HEIGHT_ATTR);
                    }
                    String legend = null;
                    if (image.hasAttribute(RichText.LEGEND)) {
                        legend = image.getAttribute(RichText.LEGEND);
                        image.removeAttribute(RichText.LEGEND);
                    }
                    String referid = createImageIdRel(mmObj, objId, height, width, legend);
                    image.setAttribute(RichText.RELATIONID_ATTR, referid);

                    idsList.add(referid);
                }
                else {
                    if (!image.hasAttribute(RichText.RELATIONID_ATTR)) {
                        String src = image.getAttribute(RichText.SRC_ATTR);
                        log.warn("Image found which is not linked " + src);
//                        try {
//                            String alt = image.getAttribute("alt");
//                            String owner = mmObj.getStringValue("owner");
//                            MMObjectNode imageNode = createImage(owner, src, alt);
//                            
//                            String width = null;
//                            if (image.hasAttribute(RichText.WIDTH_ATTR)) {
//                                width = image.getAttribute(RichText.WIDTH_ATTR);
//                                image.removeAttribute(RichText.WIDTH_ATTR);
//                            }
//                            String height = null;
//                            if (image.hasAttribute(RichText.HEIGHT_ATTR)) {
//                                height = image.getAttribute(RichText.HEIGHT_ATTR);
//                                image.removeAttribute(RichText.HEIGHT_ATTR);
//                            }
//                            String legend = null;
//                            if (image.hasAttribute(RichText.LEGEND)) {
//                                legend = image.getAttribute(RichText.LEGEND);
//                                image.removeAttribute(RichText.LEGEND);
//                            }                            
//
//                            String referid = createImageIdRel(mmObj, imageNode.getNumber(), height, width, legend);
//
//                            image.setAttribute(RichText.RELATIONID_ATTR, referid);
//                            image.setAttribute(RichText.DESTINATION_ATTR, String.valueOf(imageNode.getNumber()));
//                            image.removeAttribute(RichText.SRC_ATTR);
//
//                            idsList.add(referid);
//
//                            log.debug("imported image " + imageNode.getNumber()
//                                    + " and added imgidrel: " + referid);
//                        }
//                        catch (IOException ioe) {
//                            log.error("There was a problem while retrieving the image from " + src);
//                            log.error(ioe);
//                        }
                    }
                    else {
                        String referid = image.getAttribute(RichText.RELATIONID_ATTR);
                        idsList.add(referid);
                    }
                }
            }
        }
    }
    
    private boolean isNewInline(Element element) {
        return element.hasAttribute(RichText.DESTINATION_ATTR)
                && !element.hasAttribute(RichText.RELATIONID_ATTR);
    }

    private boolean isInlineAttributesComplete(Element element) {
        return element.hasAttribute(RichText.DESTINATION_ATTR)
                && element.hasAttribute(RichText.RELATIONID_ATTR);
    }

    private MMObjectNode getInlineRel(String id) {
        return getRelation(id, inlinerelBuilder, RichText.REFERID_FIELD);
    }
    
    private MMObjectNode getImageInlineRel(String id) {
        return getRelation(id, imagerelBuilder, RichText.REFERID_FIELD);
    }
    
    private MMObjectNode getRelation(String id, MMObjectBuilder builder, String idField) {
        NodeSearchQuery query = getQuery(id, builder, idField);
        try {
            List nodes = builder.getNodes(query);
            for (Iterator iter = nodes.iterator(); iter.hasNext();) {
                MMObjectNode imagerel = (MMObjectNode) iter.next();
                return imagerel;
            }
        } catch (SearchQueryException e) {
            log.error(e);
        }
        return null;
    }
    
    private NodeSearchQuery getQuery(String id, MMObjectBuilder builder, String idField) {
        // get all relations which are related to the node
        NodeSearchQuery query = new NodeSearchQuery(builder);
        StepField referidStepField = query.getField(builder.getField(idField));
        BasicFieldValueConstraint cReferid = new BasicFieldValueConstraint(referidStepField, id);
        cReferid.setOperator(FieldCompareConstraint.EQUAL);
        query.setConstraint(cReferid);
        return query;
    }

    /**
     * Creates a relation for an inline link
     */
    private String createInlineRel(MMObjectNode mmObj, int objId) {
        String owner = mmObj.getStringValue("owner");
        MMObjectNode idrel = inlinerelBuilder.getNewNode(owner);
        idrel.setValue("snumber", mmObj.getNumber());
        idrel.setValue("dnumber", objId);
        idrel.setValue("rnumber", inlinerelNumber);
        idrel.insert(owner);

        return idrel.getStringValue(RichText.REFERID_FIELD);
    }

    /**
     * Creates a relation for an inline image
     */
    private String createImageIdRel(MMObjectNode mmObj, int dnumber, String height, String width, String legend) {
        String owner = mmObj.getStringValue("owner");
        MMObjectNode imagerel = imagerelBuilder.getNewNode(owner);
        imagerel.setValue("snumber", mmObj.getNumber());
        imagerel.setValue("dnumber", dnumber);
        imagerel.setValue("rnumber", imagerelNumber);

        setImageIdRelFields(imagerel, height, width, legend);
        int insres = imagerel.insert(owner);
        log.debug("insert of imageidrel " + insres + "\r\n" + "   snumber:" + mmObj.getNumber()
                + "\r\n" + "   dnumber:" + dnumber + "\r\n" + "   rnumber:" + imagerelNumber);

        return imagerel.getStringValue(RichText.REFERID_FIELD);
    }

    private void setImageIdRelFields(MMObjectNode imagerel, String height, String width, String legend) {
        if (!StringUtil.isEmpty(height)) {
            imagerel.setValue("height", height);
        }
        if (!StringUtil.isEmpty(width)) {
            imagerel.setValue("width", width);
        }
        if (!StringUtil.isEmpty(legend)) {
            imagerel.setValue("legend", legend);
        }
    }

    private MMObjectNode createImage(String owner, String src, String alt)
            throws MalformedURLException {
        URL imageUrl = new URL(src);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            BufferedInputStream is = new BufferedInputStream(imageUrl.openStream());
            int bytesRead = 0;
            byte[] temp = new byte[4096];
            while (-1 != (bytesRead = is.read(temp))) {
                baos.write(temp, 0, bytesRead);
            }
            is.close();
        }
        catch (Throwable t) {
            log.error(Logging.stackTrace(t));
        }
        log.debug("image retrieved. " + src);

        String title = "Imported: " + src.substring(src.lastIndexOf("/") + 1, src.lastIndexOf("."));

        log.debug("Found image with title " + title + " and alt tekst " + alt);

        MMObjectNode imageNode = mmb.getMMObject("images").getNewNode(owner);

        imageNode.setValue("title", title);
        imageNode.setValue("handle", baos.toByteArray());
        imageNode.setValue("description", alt);
        imageNode.insert(owner);
        return imageNode;
    }

    private MMObjectNode createUrl(String owner, String href, String name) {
        MMObjectNode urlNode = mmb.getMMObject("urls").getNewNode(owner);
        if (!StringUtil.isEmpty(name)) {
            urlNode.setValue("name", name);
        }
        else {
            urlNode.setValue("name", href);
        }
        urlNode.setValue("url", href);
        urlNode.insert(owner);
        return urlNode;
    }
}