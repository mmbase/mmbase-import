/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is LeoCMS.
 *
 * The Initial Developer of the Original Code is
 * 'De Gemeente Leeuwarden' (The dutch municipality Leeuwarden).
 *
 * See license.txt in the root of the LeoCMS directory for the full license.
 */
package nl.leocms.util;

import javax.activation.DataSource;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.ByteArrayInputStream;

public class ByteArrayDataSource implements DataSource {
   byte[] bytes;
   String contentType;
   String name;

   public ByteArrayDataSource(byte[] bytes, String contentType, String name) {
      this.bytes = bytes;
      if (contentType == null) {
         this.contentType = "application/octet-stream";
      }
      else {
         this.contentType = contentType;
      }
      this.name = name;
   }

   public String getContentType() {
      return contentType;
   }

   public InputStream getInputStream() {
      // remove the final CR/LF
      return new ByteArrayInputStream(bytes, 0, bytes.length - 2);
   }

   public String getName() {
      return name;
   }

   public OutputStream getOutputStream() throws IOException {
      throw new FileNotFoundException();
   }
}
