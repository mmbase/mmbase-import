package nl.leocms.connectors.UISconnector.input.customers.process;

import java.io.*;
import java.net.*;


import org.mmbase.util.logging.*;
import nl.leocms.connectors.UISconnector.input.customers.model.CustomerInformation;

public class Reciever
{
   String sURL;
   private static final Logger log = Logging.getLoggerInstance(Reciever.class);

   public static Object recieve(String sURL) throws Exception
   {

      BufferedInputStream in;
      try
      {
         URL url = new URL(sURL);
         URLConnection connection = url.openConnection();

         in = new BufferedInputStream(connection.getInputStream());

         try
         {
            CustomerInformation customerInformation = nl.leocms.connectors.UISconnector.input.customers.process.JaxBUnmarshaller.Unmarshaller(in);

            try
            {
               nl.leocms.connectors.UISconnector.input.customers.process.Updater.update(customerInformation);
               return customerInformation;
            }
            catch (Exception e)
            {
               String sException = "Can't update the db for document from URL=" + sURL + "\n the exception is=" + errorReport(e);
               log.error(sException);
               return sException;
            }
         }
         catch (Exception e)
         {
            String sException = "Can't unmarshall document" + sURL + "\n the exception is=" + errorReport(e);
            log.error(sException);
            return sException;
         }

      }
      catch (Exception e)
      {
         String sException = "Can't get an XML document from URL=" + sURL + " for current user information updating\n The exception is=" + errorReport(e);
         log.error(sException);
         return sException;
      }
   }

   private static String errorReport(Exception e){
      StringWriter writer = new StringWriter();
      PrintWriter print = new PrintWriter(writer);
      e.printStackTrace(print);
      return writer.toString();
   }
}
