package nl.eo.batchupload.upload;

import java.awt.image.renderable.ParameterBlock;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Vector;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.TransposeDescriptor;

import nl.eo.batchupload.preview.ImageRenderThread;
import nl.eo.batchupload.preview.ImageThumbnail;
import nl.eo.batchupload.utils.Base64;


/**
 * This class is a wrapper around an image. Besides the image, it contains some additional
 * information, including: 
 * <ul>
 *   <li> Username who is manipulating / uploading the image 
 *   <li> Title of the picture
 *   <li> Description of the picture
 *   <li> Filename of the picture
 * </li>
 * Besides information specified above, there is also a thumbnail version of the image
 * present (in the @see(Thumbnail) class). The image itself is not stored in this class,
 * only a reference to it by filename. This is to save memory.
 * 
 * Several operations are possible on UploadImage. These will be recorded and applied
 * the moment that the real image is requested.
 * @author Johannes Verelst
 */
public class UploadImage {

	private String user;
	private String filename;
	private String title;
	private String description;
    private String filepath;
	private ImageThumbnail thumb;
	private int relatedObject = -1;
    
	private Vector renderings = new Vector();

	private boolean enabled = true;

	/**
	 * Public constructor. Create a new image by specifying a path and a filename. This
	 * constructor will generate a thumbnail and put random values in the Description
	 * and Title fields.
	 * @param user The user who owns the image
	 * @param path The path to the image
	 * @param filename The image filename
	 */
	public UploadImage(String user, String path, String filename, ImageRenderThread rt) {
		this.user = user;
		this.filename = filename;
		this.filepath = path + File.separator + filename;
		this.thumb = new ImageThumbnail(100, 100, rt);

		thumb.loadImage(filepath);
	}
	
	/**
	 * Public 'get' method for the user field
	 * @return the user that owns the image
	 */
	public String getUser() {
		return user;
	}
	
	/**
	 * Public 'get' method for the filename
	 * @return the filename of the image
	 */
	public String getFilename() {
		return filename;
	}
	
	/**
	 * Public 'get' method for the thumbnail
	 * @return a thumbnail for this image
	 */
	public ImageThumbnail getThumbnail() {
		return thumb;
	}
	
    public void setRelateTo(int relatedObject) {
        this.relatedObject = relatedObject;
    }
    
    public int getRelateTo() {
        return relatedObject;
    }
    
	/**
	 * Return a base64 encoded version of this image. This method will do the following:
	 * <ul>
	 *  <li> Open the file, read the image
	 *  <li> Apply all rendering operations
	 *  <li> Re-encode the image to jpeg format
	 *  <li> Base64 encode the image into string
	 * </ul>
	 * @return Base64 encoded string
	 */
	public String getBase64EncodedBinary() {
		return Base64.encode(getBytes());
	}
	
    public byte[] getBytes() {
        ParameterBlock pb = new ParameterBlock();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        RenderedOp im = JAI.create("encode", getImage(), buffer, "jpeg");
        im.dispose();
        byte bytes[] = buffer.toByteArray();
        return bytes;    
    }
    
	public PlanarImage getImage() {
		PlanarImage img;
		
		ParameterBlock pb =  (new ParameterBlock()).add(filepath);
		RenderedOp imgsrc = new RenderedOp("fileload", pb, null);
		
		if (renderings != null && renderings.size() > 0) {
			RenderedOp myOp = (RenderedOp)renderings.get(0);
			ParameterBlock p = myOp.getParameterBlock();
			p.addSource(imgsrc);
			myOp.setParameterBlock(p);
			imgsrc = (RenderedOp)renderings.get(renderings.size() - 1);
		}
		img = imgsrc.getRendering();

		return img; 
	}
	
	/**
	 * Public 'get' method for the title of the image
	 * @return the title of the image
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Public 'get' method for the description of the image
	 * @return the description of the image
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Public 'set' method for the title of the image
	 * @param newtitle the new title for the image
	 */
	public void setTitle(String newtitle) {
		title = newtitle;
	}
	
	/**
	 * Public 'set' method for the description of the image
	 * @param newdescription the new description for the image
	 */
	public void setDescription(String newdescription) {
		description = newdescription;
	}

	/**
	 * Set whether or not this image is enabled: if it may be uploaded / manipulated
	 */
	public void setEnabled(boolean b) {
		enabled = b;
		thumb.setEnabled(b);
	}
	
	/**
	 * Return whether or not this image may be uploaded / manipulated
	 */
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Image manipulation routines
	 */
	
	/**
	 * Rotate the image with a specified number of degrees (0 to 360), clockwise
	 * @param degrees the number of degrees to rotate the image
	 **/
	public void doRotate(double degrees) {
		RenderedOp rotate;
		ParameterBlock p = (new ParameterBlock());
		
		if (degrees % 90.0 == 0) {
			TransposeDescriptor d = new TransposeDescriptor();
			int degs = 0;
			if (degrees == 90.0)
				degs = d.ROTATE_90;
			else if (degrees == 180.0)
				degs = d.ROTATE_180;
			else if (degrees == 270.0) 
				degs = d.ROTATE_270;
				
			p.add(degs);
		
			if (renderings.size() > 0) {
				RenderedOp previous = (RenderedOp)renderings.get(renderings.size() - 1);
				p.addSource(previous);
			}

			rotate = new RenderedOp("transpose", p, null);		
			renderings.add(rotate);
		} else {
			// degrees is no multiple of 90.0, so we must use the 'rotate' operator
		}
		
		thumb.setRenderingOps(renderings);
	}
	
}
