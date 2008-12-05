package com.finalist.cmsc.staticdownload;

import java.io.File;

import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;

public class StaticDownload {

   private static final int MAX_OLD_DOWNLOADS = 25;
   private static DownloadThread downloadThread = null;


   /**
    * try to start a download, can only have one download at a moment.
    * 
    * @return true when started, false when already downloading
    */
   public synchronized static boolean startDownload(String url, DownloadSettings settings) {

      if (downloadThread == null || !downloadThread.isDownloading()) {
         downloadThread = new DownloadThread(url, settings);
         downloadThread.start();

         deleteOldDownloads(settings);

         return true;

      }
      else {
         return false;
      }
   }


   private static void deleteOldDownloads(DownloadSettings settings) {
      Cloud cloud = CloudProviderFactory.getCloudProvider().getCloud();
      NodeList downloads = cloud.getNodeManager("staticdownload").getList(null, "number", "DOWN");
      if (downloads.size() > MAX_OLD_DOWNLOADS) {
         for (int count = MAX_OLD_DOWNLOADS; count < downloads.size(); count++) {
            Node node = downloads.getNode(count);
            String fileName = node.getStringValue("filename");
            if (fileName != null && fileName.startsWith(settings.getDownloadUrl())) {
               fileName = fileName.substring(settings.getDownloadUrl().length());
               new File(settings.getStorePath() + fileName).delete();
            }
            node.delete();
         }

      }

   }
}
