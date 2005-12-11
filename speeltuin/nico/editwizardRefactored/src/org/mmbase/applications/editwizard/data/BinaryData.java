/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard.data;

/**
 * this class is used to cache image in file system but not in memory. 
 * we could minimize the memory usage. Actually, the binary data will 
 * not be accessed frequently. 
 * 
 * @todo javadoc
 * 
 * @author caicai
 * @created 2005-9-30
 * @version $Id: BinaryData.java,v 1.2 2005-12-11 11:51:04 nklasens Exp $
 */
public class BinaryData{
    
    private int version = 0;
    
    private String originalFilePath = null;
    
    private int length = 0;
    
    private byte[] data = null;
    
    private String contentType = null;
    
    /**
     * set the binary data into this class to cache.
     * @param data
     */
    public void setData(byte[] data) {
        version++;
        if (data == null) {
            this.length=0;
        }
        else {
            this.data = data;
            this.length = this.data.length;
        }
    }
    
    /**
     * get the binary data cached by this class
     * @return
     */
    public byte[] getData() {
        if (this.length==0) {
            return new byte[0];
        }
        return data;
    }
    
    /**
     * get original file name, it is the path in the client in which upload the file.
     * @return Returns the originalFilePath.
     */
    public String getOriginalFilePath() {
        return originalFilePath;
    }

    
    /**
     * set original file name, it is the path in the client in which upload the file.
     * @param originalFilePath The originalFilePath to set.
     */
    public void setOriginalFilePath(String originalFilePath) {
        this.originalFilePath = originalFilePath;
    }
    
    /**
     * get original file name. the return value only contains the file name, 
     * the directory path is not include in return value.
     * @return
     */
    public String getOriginalFileName() {
        if (originalFilePath==null) {
            return null;
        }
        // the path passed is in the cleint system's format,
        // so test all known path separator chars ('/', '\' and "::" )
        // and pick the one which would create the smallest filename
        // Using Math is rather ugly but at least it is shorter and performs better
        // than Stringtokenizer, regexp, or sorting collections
        int last = Math.max(Math.max(
                originalFilePath.lastIndexOf(':'), // old mac path (::)
                originalFilePath.lastIndexOf('/')),  // unix path
                originalFilePath.lastIndexOf('\\')); // windows path
        if (last > -1) {
            return originalFilePath.substring(last+1);
        }
        return originalFilePath;
    }

    
    /**
     * get the length of the binarydata.
     * use this method to get length will avoid unnecessary file read.
     * @return Returns the length.
     */
    public int getLength() {
        return length;
    }

    
    /**
     * @return Returns the contentType.
     */
    public String getContentType() {
        return contentType;
    }

    
    /**
     * @param contentType The contentType to set.
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    
    /**
     * @return Returns the version.
     */
    public String getVersion() {
        return ""+version;
    }

}
