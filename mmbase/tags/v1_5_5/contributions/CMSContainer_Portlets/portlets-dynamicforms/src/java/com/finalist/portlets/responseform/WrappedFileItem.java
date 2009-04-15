package com.finalist.portlets.responseform;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

import org.apache.commons.fileupload.FileItem;

public class WrappedFileItem implements DataSource {

   private FileItem fileItem;


   public WrappedFileItem(FileItem fileItem) {
      this.fileItem = fileItem;
   }


   public String getContentType() {
      return fileItem.getContentType();
   }


   public InputStream getInputStream() throws IOException {
      return fileItem.getInputStream();
   }


   /**
    * this is nessessary because of the different behavier of the brousers. IE
    * returns the full pathname as filename, whereas firefox returns the
    * filename only, as needed.
    */
   public String getName() {
      File file = new File(fileItem.getName());
      return file.getName();
   }


   public OutputStream getOutputStream() throws IOException {
      return fileItem.getOutputStream();
   }
}
