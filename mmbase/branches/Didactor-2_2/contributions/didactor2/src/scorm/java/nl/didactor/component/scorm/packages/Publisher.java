package nl.didactor.component.scorm.packages;

import java.io.RandomAccessFile;
import java.util.Iterator;

import org.mmbase.bridge.*;




public class Publisher
{
   private Cloud cloud;

   public Publisher(Cloud cloud)
   {
      this.cloud = cloud;
   }




   public void savePackage(Node nodeEducation) throws Exception
   {
      NodeList nlRelatedLearnBlocks = nodeEducation.getRelatedNodes("learnblocks", "posrel", "destination");
      for(Iterator it = nlRelatedLearnBlocks.iterator(); it.hasNext();)
      {
         Node nodeLeranBlock = (Node) it.next();
         this.saveLearnBlock(nodeLeranBlock);
      }
   }




   private void saveLearnBlock(Node nodeLearnBlock) throws Exception
   {
//      System.out.println("learnblock=" + nodeLearnBlock.getNumber());
      NodeList nlRelatedLearnBlocks = nodeLearnBlock.getRelatedNodes("learnblocks", "posrel", "destination");
      for(Iterator it = nlRelatedLearnBlocks.iterator(); it.hasNext();)
      {
         Node nodeLeranBlock = (Node) it.next();
         this.saveLearnBlock(nodeLeranBlock);
      }


      NodeList nlRelatedHtmlPages = nodeLearnBlock.getRelatedNodes("htmlpages");
      for(Iterator it = nlRelatedHtmlPages.iterator(); it.hasNext();)
      {
         Node nodeHtmlPage = (Node) it.next();
//         System.out.println("htmlpage=" + nodeHtmlPage.getNumber());
         RandomAccessFile fileHtmlPage = new RandomAccessFile((String) nodeHtmlPage.getValue("path"), "rw");
         fileHtmlPage.writeBytes((String) nodeHtmlPage.getValue("content"));
         fileHtmlPage.close();
      }
   }

}