
package com.finalist.cmsc.upload.service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class ChangeHtml {
	Parserfactory pf = new Parserfactory();
	public void change(String xmlUrl,HashMap hs) throws IOException, TransformerFactoryConfigurationError, TransformerException
	{
		 FileReader fr = new FileReader(xmlUrl);
		 InputSource  source = new InputSource(fr); 
		 DocumentBuilderFactory domfac=DocumentBuilderFactory.newInstance();
		 try { 
			  DocumentBuilder dombuilder=domfac.newDocumentBuilder();
			  Document doc=dombuilder.parse(source);
			  Element root=doc.getDocumentElement();
			  NodeList books=root.getChildNodes();
			  for(int i=0;i<books.getLength();i++)
			  {
				  Node childnode = books.item(i);
				  getChildNode(doc,childnode,hs);
			  }
			  	FileOutputStream fos = new FileOutputStream(xmlUrl+".html");
			    OutputStreamWriter outwriter = new OutputStreamWriter(fos);
			    Source sourcea = new DOMSource(doc);			    
			    Result result = new StreamResult(outwriter);
			    Transformer xformer = TransformerFactory.newInstance()
			    .newTransformer();
			    xformer.setOutputProperty(OutputKeys.ENCODING, "gb2312");
			    xformer.transform(sourcea, result);
			    outwriter.close();
			    fos.close();           
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
	}
	public void getChildNode(Document doc,Node node,HashMap hs)
	{
		pf.process(doc,node, hs);
		if(node.hasChildNodes())
		{
			NodeList childNodes = node.getChildNodes();
			 for(int i=0;i<childNodes.getLength();i++)
			  {
				  Node childnode = childNodes.item(i);
				  getChildNode(doc,childnode,hs);
			  }
		}
	}
	
}
