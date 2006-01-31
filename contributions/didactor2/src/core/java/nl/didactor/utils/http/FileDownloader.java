package nl.didactor.utils.http;

import java.net.*;
import java.io.InputStream;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import nl.didactor.utils.http.exceptions.*;

/**
 * File downloader
 * It downloads files with automatic restart if connection brokes.
 *
 */


public class FileDownloader
{
   private static Logger log = Logging.getLoggerInstance(FileDownloader.class.getName());

   public static String getTextFile(String sURL, int iNumberOfCounts) throws Exception
   {
      log.info("Downloading file: " + sURL);
      URL urlExternalSchemaPath = new URL(sURL);
      URLConnection connection;
      InputStream is;
      int iPageSize = urlExternalSchemaPath.openConnection().getContentLength();
      byte[] arbPage = new byte[iPageSize];
      int iOffset = 0;
      boolean bDownloadOK = false;

      int iAttemptCounter = 0;
      while (!bDownloadOK)
      {
         bDownloadOK = true;
         for (int f = 0; f < arbPage.length; f++)
         {
            if (arbPage[f] == 0)
            {
               bDownloadOK = false;
               iOffset = f;
               break;
            }
         }

         if((!bDownloadOK) && (iAttemptCounter == iNumberOfCounts))
         {//We tried too many times, Exception!
            throw new LimitOfAttemptsExceed();
         }

         if(!bDownloadOK)
         {
            connection = urlExternalSchemaPath.openConnection();
            connection.setRequestProperty("Range", "bytes=" + iOffset + "-");
            connection.connect();
            is = connection.getInputStream();
            byte[] arbytesTemp = new byte[iPageSize - iOffset];
            is.read(arbytesTemp, 0, iPageSize - iOffset);

            for(int f =0; f < arbytesTemp.length; f++)
            {
               arbPage[iOffset + f] = arbytesTemp[f];
            }
         }
         //System.out.println("offset " + iOffset);
         iAttemptCounter++;
         log.info("Downloading file (attempt " + iAttemptCounter + "): offset=" + iOffset);
      }
      log.info("Downloading file: success!");

      return new String(arbPage);
   }
}