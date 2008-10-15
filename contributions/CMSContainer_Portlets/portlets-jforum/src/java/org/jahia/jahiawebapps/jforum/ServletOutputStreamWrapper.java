package org.jahia.jahiawebapps.jforum;

import javax.servlet.ServletOutputStream;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * Outputstream Wrapper
 *
 * @author Tlili Khaled
 */
public class ServletOutputStreamWrapper extends ServletOutputStream {
   private ArrayList byteList = new ArrayList();
   private static Logger logger = Logger.getLogger(ServletOutputStreamWrapper.class);


   /**
    * Gets the AsByteArray attribute of the ServletOutputStreamWrapper object
    *
    * @return The AsByteArray value
    */
   public byte[] getAsByteArray() {
      byte[] bb = new byte[byteList.size()];
      for (int i = 0; i < byteList.size(); i++) {
         Byte b = (Byte) byteList.get(i);
         bb[i] = b.byteValue();
      }
      return bb;
   }


   /**
    * Writes the specified byte to this output stream.
    *
    * @param b the <code>byte</code>.
    * @throws java.io.IOException Description of Exception
    * @throws IOException         if an I/O error occurs. In particular, an
    *                             <code>IOException</code> may be thrown if the output stream has been
    *                             closed.
    * @todo Implement this java.io.OutputStream method
    */
   public void write(int b) throws java.io.IOException {
      Byte bb = new Byte(String.valueOf(b));
      if (bb == null) {
         throw new java.io.IOException("Can't convert string to byte");
      }
      byteList.add(bb);

   }


   /**
    * Writes the specified byte array to this output stream.
    *
    * @param bytes Description of Parameter
    * @throws java.io.IOException Description of Exception
    */
   public void write(byte[] bytes) throws java.io.IOException {
      for (int i = 0; i < bytes.length; i++) {
         Byte bb = new Byte(bytes[i]);
         if (bb == null) {
            throw new java.io.IOException("Can't convert string to byte");
         }
         byteList.add(bb);
      }

   }


   /**
    * Writes the specified byte array to this output stream.
    *
    * @param bytes  Description of Parameter
    * @param off    Description of Parameter
    * @param length Description of Parameter
    * @throws java.io.IOException Description of Exception
    */
   public void write(byte[] bytes, int off, int length) throws java.io.IOException {
      for (int i = off; i < (off + length); i++) {
         Byte bb = new Byte(bytes[i]);
         if (bb == null) {
            throw new java.io.IOException("Can't convert string to byte");
         }
         byteList.add(bb);
      }

   }


   /**
    * flush
    *
    * @throws java.io.IOException Description of Exception
    */
   public void flush() throws java.io.IOException {
      logger.debug("Call flush method");
		super.flush();
	}

}
