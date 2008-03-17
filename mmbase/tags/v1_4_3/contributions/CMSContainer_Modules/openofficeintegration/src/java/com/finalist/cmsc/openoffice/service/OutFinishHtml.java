package com.finalist.cmsc.openoffice.service;


import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

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
