package nl.didactor.component.scorm.packages;

import java.io.RandomAccessFile;
import java.util.Iterator;

import org.mmbase.bridge.*;

/**
 * @javadoc
 */

public class Publisher {
    private final Cloud cloud;
    
    public Publisher(Cloud cloud) {
        this.cloud = cloud;
    }
    
    
    public void savePackage(Node nodeEducation) throws Exception {
        NodeList nlRelatedLearnBlocks = nodeEducation.getRelatedNodes("learnblocks", "posrel", "destination");
        for(Iterator it = nlRelatedLearnBlocks.iterator(); it.hasNext();) {
            Node nodeLearnBlock = (Node) it.next();
            this.saveLearnBlock(nodeLearnBlock);
        }
    }
    
    
    
    
    private void saveLearnBlock(Node nodeLearnBlock) throws Exception {
        NodeList nlRelatedLearnBlocks = nodeLearnBlock.getRelatedNodes("learnblocks", "posrel", "destination");
        for(Iterator it = nlRelatedLearnBlocks.iterator(); it.hasNext();) {
            Node n = (Node) it.next();
            this.saveLearnBlock(n);
        }
        
        
        NodeList nlRelatedHtmlPages = nodeLearnBlock.getRelatedNodes("htmlpages");
        for(Iterator it = nlRelatedHtmlPages.iterator(); it.hasNext();) {
            Node nodeHtmlPage = (Node) it.next();
            RandomAccessFile fileHtmlPage = new RandomAccessFile((String) nodeHtmlPage.getValue("path"), "rw");
            fileHtmlPage.writeBytes((String) nodeHtmlPage.getValue("content"));
            fileHtmlPage.close();
        }
    }
    
}
