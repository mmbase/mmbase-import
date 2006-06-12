package com.finalist.cmsc.richtext.builders;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import net.sf.mmapps.commons.util.XmlUtil;

import org.mmbase.applications.wordfilter.WordHtmlCleaner;
import org.mmbase.bridge.*;
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
public class HtmlBuilder extends MMObjectBuilder {

    /** MMbase logging system */
    private static Logger log = Logging.getLoggerInstance(HtmlBuilder.class.getName());

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

        String tmp = getInitParameter("htmlFields");
        log.debug("htmlFields are: " + tmp);
        if (tmp != null) {
            StringTokenizer tokenizer = new StringTokenizer(tmp, ", ");
            while (tokenizer.hasMoreTokens()) {
                String field = tokenizer.nextToken();
                log.debug("html field: " + field.trim());
                htmlFields.add(field);
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
        List<String> relList = new ArrayList<String>();
        // Resolve images
        List fields = getFields(NodeManager.ORDER_EDIT);
        Iterator iFields = fields.iterator();
        while (iFields.hasNext()) {
            Field field = (Field) iFields.next();

            if (field != null) {
                String fieldName = field.getName();
                log.debug("Got field " + fieldName + " : persistent = " + (!field.isVirtual())
                        + ", stringtype = " + (field.getType() == Field.TYPE_STRING)
                        + ", isHtmlField = " + htmlFields.contains(fieldName));

                if (!field.isVirtual() && field.getType() == Field.TYPE_STRING
                        && htmlFields.contains(fieldName)) {
                    log.debug("Evaluating " + fieldName);

                    cleanFieldRichText(node, field);
                    
                    // Persistent string field.
                    String fieldValue = (String) node.getValues().get(fieldName);

                    try {
                        Document retDoc = XmlUtil.toDocument(RichText.RICHTEXT_ROOT_OPEN
                                + fieldValue + RichText.RICHTEXT_ROOT_CLOSE);

                        log.debug("handling links for: " + fieldName);
                        resolveLinks(retDoc, relList, node);

                        log.debug("Resolving images");
                        // collect <IMG> tags
                        NodeList nl = retDoc.getElementsByTagName(RichText.IMG_TAGNAME);
                        resolveImages(nl, relList, node);
                        // collect <img> tags (lowercase);
                        nl = retDoc.getElementsByTagName(RichText.IMG_TAGNAME.toLowerCase());
                        resolveImages(nl, relList, node);

                        String out = XmlUtil.serializeDocument(retDoc, false, false, true, true);
                        log.debug("before fixing anchors = " + out);
                        out = WordHtmlCleaner.fixEmptyAnchors(out);
                        out = out.replaceAll("<.?richtext.?>", "");
                        log.debug("final richtext tekst = " + out);
                        // make changes
                        node.setValue(fieldName, out);

                    }
                    catch (Exception e) {
                        log.error("An error occured while resolving inline images!");
                        log.error(Logging.stackTrace(e));
                    }
                }
            }
        }

        boolean committed = super.commit(node);

        if (committed) {
            // remove outdated inlinerel
            Enumeration idrels = node.getRelations(RichText.INLINEREL_NM);
            while (idrels.hasMoreElements()) {
                MMObjectNode rel = (MMObjectNode) idrels.nextElement();
                String referid = rel.getStringValue("referid");
                if ((rel.getIntValue("snumber") == node.getNumber()) && !relList.contains(referid)) {
                    inlinerelBuilder.removeNode(rel);
                    log.debug("removed unused inlinerel: " + referid);
                }
            }

            // remove outdated imageinlinerel
            Enumeration imagerels = node.getRelations(RichText.IMAGEINLINEREL_NM);
            while (imagerels.hasMoreElements()) {
                MMObjectNode rel = (MMObjectNode) imagerels.nextElement();
                String referid = rel.getStringValue("referid");
                if (!relList.contains(referid)) {
                    imagerelBuilder.removeNode(rel);
                    log.debug("removed unused imageinlinerel: " + referid);
                }
            }
        }

        return committed;
    }

    /**
     * Cleans a field if it contains html junk.
     * 
     * @param node
     *            Node of the field to clean
     * @param field
     *            Definition of field to clean
     */
    private void cleanFieldRichText(MMObjectNode node, Field field) {
        String fieldName = field.getName();
        // Persistent string field.
        String originalValue = (String) node.getValues().get(fieldName);

        if (originalValue != null && !"".equals(originalValue.trim())) {

            // Edited value: clean.
            log.debug("before cleaning: " + originalValue);
            String newValue = WordHtmlCleaner.cleanHtml(originalValue);
            log.debug("after cleaning: " + newValue);
            node.setValue(fieldName, newValue);

            if (log.isDebugEnabled() && !originalValue.equals(newValue)) {
                log.debug("Replaced " + fieldName + " value \"" + originalValue
                        + "\"\n \t by \n\"" + newValue + "\"");
            }
        }
        else {
            // if string is null or empty, (re)set it's value to empty string
            node.setValue(fieldName, "");
        }
    }

    /**
     * resolve links in the richtextfield en make inlinerel of it. Add the id to the anchortag so
     * that the link can be resolved in the frontend en point to the correct article.
     */
    private void resolveLinks(Document doc, List<String> rellist, MMObjectNode mmObj) {
        // collect <A> tags
        NodeList nl = doc.getElementsByTagName(RichText.LINK_TAGNAME);
        log.debug("number of links: " + nl.getLength());

        for (int i = 0, len = nl.getLength(); i < len; i++) {
            Element link = (Element) nl.item(i);

            // handle relations to other objects
            if (link.hasAttribute(RichText.DESTINATION_ATTR)
                    && link.hasAttribute(RichText.RELATIONID_ATTR)) {
                // get relation id of the link
                String strIdrel = link.getAttribute(RichText.RELATIONID_ATTR);
                try {
                    boolean inlineRelExists = existInlineRel(strIdrel);
                    if (inlineRelExists) {
                        // record existing relation
                        rellist.add(strIdrel);
                        log.debug("recorded inlinerel: " + strIdrel);
                    }
                    else {
                        // check if destination still exists
                        int objId = Integer.parseInt(link.getAttribute(RichText.DESTINATION_ATTR));

                        if (getNode(objId) != null) {
                            // create inlinerel to related object
                            String referid = createInlineRel(mmObj, objId);
                            // update richtext
                            link.setAttribute(RichText.RELATIONID_ATTR, referid);

                            // add to existing relations list
                            rellist.add(referid);

                            log.debug("added inlinerel: " + referid);
                        }
                        else {
                            // destination does also not exist!
                            log
                                    .warn("In de linktag van een richtext field staat een id van een relatie die niet bestaat in het systeem !!!!");
                            log
                                    .warn("Tevens in de linktag van een richtext field staat een id van een bestemming  die ook niet bestaat in het systeem !!!!");

                            link.setAttribute("href", "#");
                            link.setAttribute("title",
                                    "Link, element of bestemming is verwijderd !!!");
                        }
                    }
                }
                catch (SearchQueryException e) {
                    log.debug("Search for referid " + strIdrel + " failed - " + e.getMessage(), e);
                }
            }
            else {
                if (link.hasAttribute(RichText.DESTINATION_ATTR)
                        && !link.hasAttribute(RichText.RELATIONID_ATTR)) {
                    // create inlinerel to related object
                    int objId = Integer.parseInt(link.getAttribute(RichText.DESTINATION_ATTR));

                    String referid = createInlineRel(mmObj, objId);
                    link.setAttribute(RichText.RELATIONID_ATTR, referid);

                    // add to existing relations list
                    rellist.add(referid);

                    log.debug("added inlinerel: " + referid);
                }
                else {
                    if (link.hasAttribute(RichText.RELATIONID_ATTR)) {
                        // record existing relation
                        String referid = link.getAttribute(RichText.RELATIONID_ATTR);
                        rellist.add(referid);

                        log.debug("recorded inlinerel: " + referid);
                    }
                }
            }
        }
    }

    private void resolveImages(NodeList nl, List<String> rellist, MMObjectNode mmObj) {
        log.debug("number of images: " + nl.getLength());

        for (int i = 0, len = nl.getLength(); i < len; i++) {
            Element image = (Element) nl.item(i);

            if (image.hasAttribute(RichText.DESTINATION_ATTR)
                    && image.hasAttribute(RichText.RELATIONID_ATTR)) {
                // get relation id of the image
                String strIdrel = image.getAttribute(RichText.RELATIONID_ATTR);
                try {
                    boolean inlineRelExists = existImageInlineRel(strIdrel);
                    if (inlineRelExists) {
                        // record existing relation
                        rellist.add(strIdrel);
                        log.debug("recorded inlinerel: " + strIdrel);
                    }
                    else {
                        // check if destination still exists
                        int objId = Integer.parseInt(image.getAttribute(RichText.DESTINATION_ATTR));

                        if (getNode(objId) != null) {
                            // update richtext
                            String size = image.getAttribute(RichText.SIZE_ATTR);
                            String legend = image.getAttribute(RichText.LEGEND);

                            String referid = createImageIdRel(mmObj, objId, size, legend);
                            image.setAttribute(RichText.RELATIONID_ATTR, referid);

                            // add to existing relations list
                            rellist.add(referid);

                            log.debug("added imgidrel: " + referid);
                        }
                        else {
                            // destination does also not exist!
                            log.error("Relation was not found! This image will be skipped.");
                            log.error("Destination of image does also not exist.");
                            // image.removeAttribute("src");
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
                }
                catch (SearchQueryException e) {
                    log.debug("Search for referid " + strIdrel + " failed - " + e.getMessage(), e);
                }
            }
            else {
                // handle relations to other objects
                if (image.hasAttribute(RichText.DESTINATION_ATTR)
                        && !image.hasAttribute(RichText.RELATIONID_ATTR)) {
                    // create inlinerel to related object
                    int objId = Integer.parseInt(image.getAttribute(RichText.DESTINATION_ATTR));

                    String size = image.getAttribute(RichText.SIZE_ATTR);
                    String legend = image.getAttribute(RichText.LEGEND);

                    String referid = createImageIdRel(mmObj, objId, size, legend);
                    image.setAttribute(RichText.RELATIONID_ATTR, referid);

                    // add to existing relations list
                    rellist.add(referid);

                    log.debug("added imgidrel: " + referid);
                }
                else {
                    if (!image.hasAttribute(RichText.RELATIONID_ATTR)) {
                        log.debug("Importing external image.");
                        String src = image.getAttribute("src");
                        try {
                            String alt = image.getAttribute("alt");
                            String owner = mmObj.getStringValue("owner");
                            MMObjectNode imageNode = createImage(owner, src, alt);

                            String referid = createImageIdRel(mmObj, imageNode.getNumber(), null,
                                    null);

                            image.setAttribute(RichText.RELATIONID_ATTR, referid);
                            image.setAttribute("src", "../media/wysiwyg/createImage.gif");

                            // add to existing relations list
                            rellist.add(referid);

                            log.debug("imported image " + imageNode.getNumber()
                                    + " and added imgidrel: " + referid);
                        }
                        catch (IOException ioe) {
                            log.error("There was a problem while retrieving the image from " + src);
                            log.error(ioe);
                        }
                    }
                    else {
                        // record existing relation
                        String referid = image.getAttribute(RichText.RELATIONID_ATTR);
                        rellist.add(referid);

                        log.debug("recorded imgidrel: " + referid);
                    }
                }
            }
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

    private boolean existImageInlineRel(String strIdrel) throws SearchQueryException {
        return existRelation(strIdrel, imagerelBuilder, "referid");
    }

    private boolean existInlineRel(String strIdrel) throws SearchQueryException {
        return existRelation(strIdrel, inlinerelBuilder, "referid");
    }

    private boolean existRelation(String strIdrel, MMObjectBuilder builder, String idField)
            throws SearchQueryException {
        // id field is unique in the system so it is enough to query on that.

        // get all relations which are related to the article containing the link
        NodeSearchQuery query = new NodeSearchQuery(builder);
        StepField referidStepField = query.getField(builder.getField(idField));
        BasicFieldValueConstraint cReferid = new BasicFieldValueConstraint(referidStepField,
                strIdrel);
        cReferid.setOperator(FieldCompareConstraint.EQUAL);
        query.setConstraint(cReferid);

        int countIdrels = builder.count(query);
        // if relations are not found
        boolean inlineRelExists = countIdrels > 0;
        return inlineRelExists;
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

        return idrel.getStringValue("referid");
    }

    /**
     * Creates a relation for an inline image
     */
    private String createImageIdRel(MMObjectNode mmObj, int dnumber, String size, String legend) {
        String owner = mmObj.getStringValue("owner");
        MMObjectNode imagerel = imagerelBuilder.getNewNode(owner);
        imagerel.setValue("snumber", mmObj.getNumber());
        imagerel.setValue("dnumber", dnumber);
        imagerel.setValue("rnumber", imagerelNumber);

        imagerel.setValue("size", size);
        imagerel.setValue("legend", legend);
        int insres = imagerel.insert(owner);
        log.debug("insert of imageidrel " + insres + "\r\n" + "   snumber:" + mmObj.getNumber()
                + "\r\n" + "   dnumber:" + dnumber + "\r\n" + "   rnumber:" + imagerelNumber);

        return imagerel.getStringValue("referid");
    }
}