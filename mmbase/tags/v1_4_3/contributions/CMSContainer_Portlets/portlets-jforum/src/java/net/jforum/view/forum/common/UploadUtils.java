/*
 * Copyright (c) Rafael Steil
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, 
 * with or without modification, are permitted provided 
 * that the following conditions are met:
 * 
 * 1) Redistributions of source code must retain the above 
 * copyright notice, this list of conditions and the 
 * following  disclaimer.
 * 2)  Redistributions in binary form must reproduce the 
 * above copyright notice, this list of conditions and 
 * the following disclaimer in the documentation and/or 
 * other materials provided with the distribution.
 * 3) Neither the name of "Rafael Steil" nor 
 * the names of its contributors may be used to endorse 
 * or promote products derived from this software without 
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT 
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 * THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 * 
 * Created on Jan 18, 2005 4:06:08 PM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.view.forum.common;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;

import net.jforum.util.legacy.commons.fileupload.FileItem;

/**
 * @author Rafael Steil
 * @version $Id: UploadUtils.java,v 1.1 2008-01-17 08:04:51 mguo Exp $
 */
public class UploadUtils
{
	private FileItem item;
	private String extension = "";
	
	public UploadUtils(FileItem item)
	{
		this.item = item;
	}
	
	public String getExtension()
	{
		if (this.extension == null || this.extension.equals("")) {
			this.extension = this.item.getName().substring(this.item.getName().lastIndexOf('.') + 1);
		}
		
		return this.extension;
	}
	
	public void saveUploadedFile(String filename) throws Exception
	{
		BufferedInputStream inputStream = null;
		FileOutputStream outputStream = null;
		
		try {
			inputStream = new BufferedInputStream(this.item.getInputStream());
			outputStream = new FileOutputStream(filename);
			
			int c = 0;
			byte[] b = new byte[4096];
			while ((c = inputStream.read(b)) != -1) {
				outputStream.write(b, 0, c);
			}
		}
		finally {
			if (outputStream != null) {
				outputStream.flush();
				outputStream.close();
			}
			
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}
}
