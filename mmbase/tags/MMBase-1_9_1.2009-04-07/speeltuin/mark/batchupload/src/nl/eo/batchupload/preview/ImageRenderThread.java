package nl.eo.batchupload.preview;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.awt.image.renderable.ParameterBlock;
import java.util.Vector;

import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;

/**
 * @author jverelst
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ImageRenderThread extends Thread {
        Vector unrenderedThumbnails = new Vector();
        boolean mayrender = true;
        
        public void add(ImageThumbnail it) {
            unrenderedThumbnails.add(it);
        }

        public void add(int i, ImageThumbnail it) {
            unrenderedThumbnails.add(i, it);
        }


        public void stopRendering() {
            mayrender = false;
        }
    
        /**
         * Main method of this class. First try to retrieve a lock, then do all the work
         */
        public void run() {
            while (mayrender) {
                if (unrenderedThumbnails.size() > 0) {
    
                    ImageThumbnail t;
                    synchronized (unrenderedThumbnails) {
                        t = (ImageThumbnail)unrenderedThumbnails.get(0);
                        unrenderedThumbnails.remove(0);
                    }
                    
                    PlanarImage img;
                    
                    ParameterBlock pb =  (new ParameterBlock()).add(t.currentfile);
                    RenderedOp imgsrc = new RenderedOp("fileload", pb, null);
                    
                    if (t.renderingops != null && t.renderingops.size() > 0) {
                        RenderedOp myOp = (RenderedOp)t.renderingops.get(0);
                        ParameterBlock p = myOp.getParameterBlock();
                        p.addSource(imgsrc);
                        myOp.setParameterBlock(p);
                        imgsrc = (RenderedOp)t.renderingops.get(t.renderingops.size() - 1);
                    }
                        
                    img = imgsrc.getRendering();
    
                    BufferedImage bi = img.getAsBufferedImage();
                    Image i = bi.getScaledInstance(t.width,  t.height, Image.SCALE_SMOOTH);
    
                    //      Using JAI is about 200ms faster
                    //      Image i = Toolkit.getDefaultToolkit().createImage(currentfile).getScaledInstance(width, height, Image.SCALE_SMOOTH);
                    //      long blaat = System.currentTimeMillis();
                    PixelGrabber pg = new PixelGrabber(i, 0, 0, t.width, t.height, true);
                    try {
                        pg.grabPixels();
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
            
                    BufferedImage nbi = new BufferedImage(t.width, t.height, BufferedImage.TYPE_INT_ARGB);
                    int[] pixels = null;
                    pixels = (int[])pg.getPixels();
                    nbi.setRGB(0, 0, t.width, t.height, pixels,0, t.width);
                    t.setImage(nbi, t.currentfile);
                    t.repaint();
    
                    if (t.renderedop != null) {
                        t.renderedop = null;
                    }
                    t.renderstatus = 2;
    
                } else {
                    try {
                        Thread.currentThread().sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
           }
            unrenderedThumbnails.removeAllElements();
        }
}
