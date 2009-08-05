package nl.eo.batchupload.preview;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.awt.image.renderable.ParameterBlock;
import java.util.Observable;
import java.util.Vector;

import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.swing.JPanel;
import javax.swing.table.AbstractTableModel;
 
/**
 * This class represents a Thumbnail, a scaled-down version of a larger image. Thumbnails are
 * generated in a seperate thread, so that generating them is not blocking the application.
 *
 * To be sure that not too many thumbnails are generated at the same time (thread bombs), 
 * a lock is used to ensure that a maximum of 2 threads are generating thumbnails at the
 * same time.
 */
public class ImageThumbnail extends JPanel implements PreviewInterface {
	Image currentimg;
	protected int width;
	protected int height;
	protected String currentfile = "";
	protected RenderedOp renderedop;
	protected int renderstatus = 0;
	protected Vector renderingops;
	protected AbstractTableModel table;
    protected ImageRenderThread renderThread;

	/**
	 * Public constructor
	 * @param width The width of the thumbnail
	 * @param height The height of the thumbnail
	 */
	public ImageThumbnail (int width, int height, ImageRenderThread rt) {
		this.width = width;
		this.height = height;		
        this.renderThread = rt;
	}

    public void clearImage() {
        renderstatus = 0;
        currentfile = "";
    }

	/**
	 * Set the image for this thumbnail to a given parameter. This image will not be scaled,
	 * width and height of the thumbnail will be changed to those of the given image.
	 * @param newimage The new image object that is the source of this thumbnail
	 * @param filename The filename of the image
	 */
	public void setImage(Image newimage, String filename) {
		currentimg = newimage;
		currentfile = filename;
		renderstatus = 2;
		if (table != null)
			table.fireTableDataChanged();
	}

	/**
	 * Load an image from file.
	 * This method will spawn a new thread that will use the current Thumbnail object as
	 * method to invoke 'run()' on. 
	 * @param filename The name of the image file to load.
	 */
	public void loadImage(String filename) {
		if (filename != null && !currentfile.equals(filename)) {
			currentfile = filename;
			renderstatus = 1;
			renderThread.add(this);
			if (table != null)
				table.fireTableDataChanged();
		}
	}

	/**
	 * Load an image from a RenderedOp
     * @param renderings a Vector containing the redering options
	 */
	public void setRenderingOps(Vector renderings) {
		this.renderingops = renderings;
		renderstatus = 1;
		renderThread.add(0, this);
		if (table != null)
			table.fireTableDataChanged();
	}

	public void setParentTable(AbstractTableModel newtable) {
		this.table = newtable;
	}
	
	/**
	 * Paint the thumbnail.
	 * This method is triggered by the Swing toolkit the moment the thumbnail needs to be
	 * repainted. It will stretch the image to the size of the panel it has to be painted on.
	 * @param arg0 The graphics object to paint the thumbnail on.
	 */	
	public void paint(Graphics arg0) {
		int width = this.getWidth();
		int height = this.getHeight();
		if (renderstatus == 2) {
			Image i = currentimg.getScaledInstance(width,height, Image.SCALE_FAST);
			if (!isEnabled()) {
				i = i.getScaledInstance(10,10, Image.SCALE_FAST);
			}
			arg0.drawImage(i, 0, 0, width, height, null);

		} else if (renderstatus == 1) {
			arg0.drawString("Generating",1,20);
			arg0.drawString("Thumbnail",1,30);
			arg0.drawString("...",1,40);
		}
	};

	public Dimension getThumbnailSize() {
		return new Dimension(width, height);	
	}	
}