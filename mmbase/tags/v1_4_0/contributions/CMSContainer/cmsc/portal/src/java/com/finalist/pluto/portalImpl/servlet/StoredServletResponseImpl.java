/*
 * Copyright 2003,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/* 

 */

package com.finalist.pluto.portalImpl.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.pluto.util.PrintWriterServletOutputStream;

public class StoredServletResponseImpl extends ServletResponseImpl {

   private boolean usingWriter;
   private boolean usingStream;

   private ServletOutputStream wrappedStream;
   private PrintWriter writer;


   public StoredServletResponseImpl(HttpServletResponse response, PrintWriter _writer) {
      super(response);
      writer = _writer;
   }


   public void setResponse(HttpServletResponse response) {
      super.setResponse(response);
   }


   public ServletOutputStream getOutputStream() throws IllegalStateException, IOException {
      if (usingWriter) {
         throw new IllegalStateException("getOutputStream can't be used after getWriter was invoked");
      }

      if (wrappedStream == null) {
         wrappedStream = new PrintWriterServletOutputStream(writer, getResponse().getCharacterEncoding());
      }

      usingStream = true;

      return wrappedStream;
   }


   public PrintWriter getWriter() throws IOException {
      if (usingStream) {
         throw new IllegalStateException("getWriter can't be used after getOutputStream was invoked");
      }

      usingWriter = true;

      return writer;
   }


   public void setBufferSize(int size) {
      // ignore
   }


   public int getBufferSize() {
      return 0;
   }


   public void flushBuffer() throws IOException {
      writer.flush();
   }


   public boolean isCommitted() {
      return false;
   }


   public void reset() {
      // ignore right now
   }

}
