package ebunders.mmbase.shorthand;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;


/**
 *  Use the TraX interface to perform a transformation in the simplest manner possible
 *  (3 statements).
 */
public class Transform {
	public static void main(String[] args)throws Exception{  
      
      String stylesheet = args[0];
      String inputXml = args[1];
      String outputXml = args[2];
      boolean validate = new Boolean(args[3]);
      
      TransformerFactory tfactory = TransformerFactory.newInstance();
      if(tfactory.getFeature(SAXSource.FEATURE))
      {
        SAXParserFactory pfactory= SAXParserFactory.newInstance();
        pfactory.setNamespaceAware(true);
        
        if(validate){
            System.out.println("====validating is on. this takes a while (don't know why)=====");
            pfactory.setValidating(true);
        }
        
        XMLReader reader = pfactory.newSAXParser().getXMLReader();

        if(validate){
            reader.setFeature("http://apache.org/xml/features/validation/schema",true);
        }
    
        Handler handler = new Handler();
        reader.setErrorHandler( handler);
    
        Transformer t = tfactory.newTransformer(new StreamSource(stylesheet));
        
        SAXSource source = new SAXSource(reader,new InputSource(inputXml));
        
        try{
          t.transform(source, new StreamResult(outputXml));
        }catch (TransformerException te){
          System.out.println("Not a SAXParseException warning or error: " + te.getMessage());
        }
                                    
        System.out.println("=====Done=====");
      }
      else{
          System.out.println("tfactory does not support SAX features!");
      }
      
      
      
      
      
//	TransformerFactory tFactory = TransformerFactory.newInstance();
//    
//	Transformer transformer = tFactory.newTransformer(new StreamSource(stylesheet));
//	transformer.transform(new StreamSource(inputXml), new StreamResult(new FileOutputStream(outputXml)));
//	System.out.println("transformation done");	

  }

    static class Handler extends DefaultHandler{
      public void warning (SAXParseException spe)throws SAXException{
        System.out.println("SAXParseException warning: " + spe.getMessage());
      }    
    
      public void error (SAXParseException spe)throws SAXException{
        System.out.println("SAXParseException error: " + spe.getMessage());
      }     
    }
}
