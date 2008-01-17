package org.jahia.portlet.jforum;

import java.io.*;

import javax.servlet.ServletOutputStream;
import java.util.ArrayList;

/**
 *  Outputstream Wrapper
 *
 *@author    Tlili Khaled
 */
public class ServletOutputStreamWrapper extends ServletOutputStream {
	private ArrayList byteList = new ArrayList();


	/**
	 *  Gets the AsByteArray attribute of the ServletOutputStreamWrapper object
	 *
	 *@return                  The AsByteArray value
	 *@exception  IOException  Description of Exception
	 */
	public byte[] getAsByteArray() throws IOException {
		byte[] bb = new byte[byteList.size()];
		for (int i = 0; i < byteList.size(); i++) {
			Byte b = (Byte) byteList.get(i);
			bb[i] = b.byteValue();
		}
		return bb;
	}


	/**
	 *  Writes the specified byte to this output stream.
	 *
	 *@param  b             the <code>byte</code>.
	 *@throws  IOException  if an I/O error occurs. In particular, an <code>IOException</code>
	 *      may be thrown if the output stream has been closed.
	 *@todo                 Implement this java.io.OutputStream method
	 */
	public void write(int b) throws IOException {
		byteList.add(new Byte(String.valueOf(b)));
	}

}
