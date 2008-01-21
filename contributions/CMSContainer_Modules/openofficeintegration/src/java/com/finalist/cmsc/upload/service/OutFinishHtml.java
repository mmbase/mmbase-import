package com.finalist.cmsc.upload.service;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class OutFinishHtml {

    private String odtLocation;
    private String middleFileLocation;

    ChangeContentXml ccx;
    ChangeHtml ch = new ChangeHtml();

    public OutFinishHtml(String odtLocation, String middleFileLocation) {
        this.odtLocation = odtLocation;
        this.middleFileLocation = middleFileLocation;
        ccx = new ChangeContentXml();
    }

    public void getFirstHtml() throws IOException, SAXException, TransformerException {
        FileOutputStream htmlStream = new FileOutputStream(this.middleFileLocation);


        StreamSource streamSource = new StreamSource(this.getClass().getResourceAsStream("transform.xsl"));
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer(streamSource);

        InputSource inputSource = ccx.getXml(this.odtLocation);
        XMLReader reader = XMLReaderFactory.createXMLReader();
        reader.setEntityResolver(new ResolveDTD());

        SAXSource saxSource = new SAXSource(reader, inputSource);
        saxSource.setSystemId(this.odtLocation);

        transformer.transform(saxSource, new StreamResult(htmlStream));
        htmlStream.close();
    }

}
