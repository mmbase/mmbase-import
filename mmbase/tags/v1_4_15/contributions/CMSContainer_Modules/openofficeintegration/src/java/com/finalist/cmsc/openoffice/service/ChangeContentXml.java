package com.finalist.cmsc.openoffice.service;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class ChangeContentXml {

	Log log = LogFactory.getLog(ChangeContentXml.class);
    Properties styles = new Properties();
    InputSource inputSource;
    HashMap hs = new HashMap();

    public ChangeContentXml() {

    	InputStream in = new BufferedInputStream(this.getClass().getResourceAsStream("style.properties"));

        try {
            styles.load(in);
        } catch (IOException e) {
            log.error("Error when load style properties", e);
        }
    }

    public HashMap getStyleMap(HashMap contentStyle) {
        HashMap hs = new HashMap();
        String newKey = null;
        String vaule = null;
        String newValue = null;
        Set contentKey = contentStyle.keySet();
        Iterator it = contentKey.iterator();
        while (it.hasNext()) {
        	newKey = it.next().toString();
            vaule = contentStyle.get(newKey).toString();
            //bold
            if (vaule.contains("font-weight=\"bold\"")) {
                newValue = "1";
            } else {
                newValue = "0";
            }
            //italic
            if (vaule.contains("font-style=\"italic\"")) {
                newValue = newValue + "1";
            } else {
                newValue = newValue + "0";
            }
            //underline
            if (vaule.contains("text-underline-style=\"solid\"")) {
                newValue = newValue + "1";
            } else {
                newValue = newValue + "0";
            }
            //throughline
            if (vaule.contains("text-line-through-style=\"solid\"")) {
                newValue = newValue + "1";
            } else {
                newValue = newValue + "0";
            }
            //superscript
            if (vaule.contains("text-position=\"super")) {
                newValue = newValue + "1";
            } else if (vaule.contains("text-position=\"sub")) {
                newValue = newValue + "2";
            } else {
                newValue = newValue + "0";
            }
            //list
            if (vaule.contains("bullet-char")) {
                newValue = "ul";
            }
            if (vaule.contains("num-format")) {
                newValue = "ol";
            }
            hs.put(newKey, newValue);
        }
        return hs;
    }

    public HashMap getContentStyle(String ContentUrl) {
        InputSource inputSource;
        inputSource = getXml(ContentUrl);
        hs = getStyleAttributes(inputSource);
        return hs;
    }

    public String getPsoperties(String key, String filePath) throws IOException {
        Properties props = new Properties();
        String Property = null;
        InputStream in = new BufferedInputStream(new FileInputStream(filePath));
        props.load(in);
        if (props.getProperty(key) != null){
            Property = props.getProperty(key);
        }
        return Property;
    }

    public HashMap getStyleAttributes(InputSource styleSource) {
        HashMap contentFirstHashMap = new HashMap();
        DocumentBuilderFactory domfac = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder dombuilder = domfac.newDocumentBuilder();
            Document doc = dombuilder.parse(styleSource);
            Element root = doc.getDocumentElement();
            NodeList books = root.getChildNodes();
            for (int i = 0; i < books.getLength(); i++) {
                Node book = books.item(i);
                if ("office:automatic-styles".equals(book.getNodeName())) {
                    NodeList nexts = book.getChildNodes();
                    for (int j = 0; j < nexts.getLength(); j++) {
                        Node next = nexts.item(j);
                        if ("style:style".equals(next.getNodeName())) {
                            String styleKey = next.getAttributes().getNamedItem("style:name").getNodeValue();
                            NodeList allAttributes = next.getChildNodes();
                            StringBuffer contentAttributes = new StringBuffer();
                            for (int x = 0; x < allAttributes.getLength(); x++) {
                                Node contentAttribute = allAttributes.item(x);
                                int length = contentAttribute.getAttributes().getLength();
                                for (int y = 0; y < length; y++) {
                                    String everyAttribute = contentAttribute.getAttributes().item(y).toString();
                                    StringTokenizer tokens = new StringTokenizer(everyAttribute, "=");
                                    String key = null;
                                    key = tokens.nextElement().toString();
                                    StringTokenizer newTokens = new StringTokenizer(key, ":");
                                    newTokens.nextElement();
                                    String needKey = newTokens.nextElement().toString();
                                    String newAttribute = styles.getProperty(needKey);
                                    if (newAttribute != null) {
                                        StringTokenizer inputTokens = new StringTokenizer(everyAttribute, ":");
                                        inputTokens.nextElement();
                                        contentAttributes.append(inputTokens.nextElement() + ";");
                                    }
                                }
                            }
                            contentFirstHashMap.put(styleKey, contentAttributes.toString());
                        }
                        if ("text:list-style".equals(next.getNodeName())) {
                            String listStyleKey = next.getAttributes().getNamedItem("style:name").getNodeValue();
                            NodeList allListAttributes = next.getChildNodes();
                            String listAttribute = "";
                            for (int x = 0; x < allListAttributes.getLength(); x++) {
                                Node contentListAttribute = allListAttributes.item(x);
                                int listLength = contentListAttribute.getAttributes().getLength();
                                for (int y = 0; y < listLength; y++) {
                                    String everyAttribute = contentListAttribute.getAttributes().item(y).toString();
                                    StringTokenizer tokens = new StringTokenizer(everyAttribute, "=");
                                    String key = null;
                                    key = tokens.nextElement().toString();
                                    StringTokenizer newTokens = new StringTokenizer(key, ":");
                                    newTokens.nextElement();
                                    String needKey = newTokens.nextElement().toString();
                                    String newAttribute = styles.getProperty(needKey);
                                    if (newAttribute != null) {
                                        listAttribute = newAttribute;
                                    }
                                }
                            }
                            contentFirstHashMap.put(listStyleKey, listAttribute);
                        }
                    }
                }
            }
        }
        catch (SAXException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return contentFirstHashMap;
    }

    public InputSource getXml(String url) {
        try {
            JarInputStream jarStream = new JarInputStream(new FileInputStream(url), false);
            JarEntry jarEntry;
            while ((jarEntry = jarStream.getNextJarEntry()) != null &&
                    !("content.xml".equals(jarEntry.getName())))
                ;
            inputSource = new InputSource(jarStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputSource;
    }
}
