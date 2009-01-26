package nl.leocms.connectors.UISconnector.output.orders.test.post;

import java.net.*;
import java.io.*;

public class Sender
{
   public Sender()
   {
   }



   public static void main(String[] args)
   {
      String sXML = "<test><tag1>235423525?</tag1><tag2>34345345345</tag2></test>";
      String sEncodedXML = null;
      try{
         sEncodedXML = URLEncoder.encode(sXML, "windows-1252");
         System.out.println(sEncodedXML);
      }
      catch(Exception e){
         System.out.println(e);
      }


      try{
         URL url = new URL("http://localhost:8080/WebModule/post_test.jsp");
         HttpURLConnection connection = (HttpURLConnection) url.openConnection();
         connection.setDoOutput(true);
         connection.setRequestMethod("POST");
         connection.connect();

         PrintWriter out = new PrintWriter(connection.getOutputStream());
         out.print("xml=" + sEncodedXML + "&test=test");
         out.flush();
         out.close();


         BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

         String inputLine;

         while ((inputLine = in.readLine()) != null)
                System.out.println(inputLine);

         in.close();
      }
      catch(Exception e){
         System.out.println(e);
      }
   }



}
