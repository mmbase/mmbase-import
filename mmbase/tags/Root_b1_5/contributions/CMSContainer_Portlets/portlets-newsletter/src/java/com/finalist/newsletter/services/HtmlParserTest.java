package com.finalist.newsletter.services;

import junit.framework.TestCase;

import org.htmlparser.Parser;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.HtmlPage;

public class HtmlParserTest extends TestCase {

   public void test() {
      String a = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
            "\n" +
            "\n" +
            "   \n" +
            "   <html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\" xml:lang=\"en\">\n" +
            "\n" +
            "   \n" +
            "      <head>\n" +
            "         <title>test</title>\n" +
            "         \n" +
            "\n" +
            "\n" +
            "      </head>\n" +
            "      <body>\n" +
            "      \n" +
            "      \n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "     testa\n" +
            "     \n" +
            "     -----------\n" +
            "\n" +
            "     testa\n" +
            "     \n" +
            "     -----------\n" +
            "\n" +
            "     tesat\n" +
            "     \n" +
            "     -----------\n" +
            "\n" +
            "      \n" +
            "      </body>\n" +
            "   \n" +
            "   </html>";
      Parser myParser;
        myParser = Parser.createParser(a,"utf-8");

        HtmlPage visitor = new HtmlPage(myParser);

      try {
         myParser.visitAllNodesWith(visitor);
      } catch (ParserException e) {
         e.printStackTrace();
      }

      //String textInPage = visitor.getTitle();
      //NodeList nodelist ;
      //nodelist = visitor.getBody();
   }

}
