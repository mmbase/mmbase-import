package nl.eo.batchupload.utils;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Small class that implements a FilenameFilter to return files with extensions that are
 * accepted as images
 */
public class ImageFileFilter implements FilenameFilter {
	private String[] extensions = {"jpg", "jpeg", "gif", "png", "bmp"};

	/**
	 * Return true if the file 'name' in directory 'dir' has a valid image extension
	 * @param dir The directory where the file is located
	 * @param name The name of the file
	 * @return boolean indicating if the file is an image file
	 */
    public boolean accept(File dir, String name) {
    	for (int i=0; i < extensions.length; i++)
    		if (name.toLowerCase().endsWith("." + extensions[i].toLowerCase()))
    			return true;
        return false;
    }
}
