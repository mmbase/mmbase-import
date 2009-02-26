package portlet.wrappers;

import javax.servlet.*;
import javax.portlet.*;

/**
 * Class to wrap a PortletResponse so it can be used as a ServletResponse
 */
public class ServletResponseWrapper implements ServletResponse {

   protected PortletResponse response;

   public ServletResponseWrapper(PortletResponse response) {
      this.response = response;
   }

   public void flushBuffer() throws java.io.IOException {
      if (response instanceof RenderResponse) {
         ((RenderResponse) response).flushBuffer();
         return;
      }
      System.out.println("ServletResponseWrapper: cannot flushBuffer on a PortletResponse!");
   }

   public int getBufferSize() {
      if (response instanceof RenderResponse) {
         return ((RenderResponse) response).getBufferSize();
      }
      System.out.println("ServletResponseWrapper: cannot getBufferSize on a PortletResponse!");
      return 0;
   }

   public java.lang.String getContentType() {
      if (response instanceof RenderResponse) {
         return ((RenderResponse) response).getContentType();
      }
      System.out.println("ServletResponseWrapper: cannot getContentType on a PortletResponse!");
      return null;
   }

   public java.lang.String getCharacterEncoding() {
      if (response instanceof RenderResponse) {
         return ((RenderResponse) response).getCharacterEncoding();
      }
      System.out.println("ServletResponseWrapper: cannot getCharacterEncoding on a PortletResponse!");
      return null;
   }

   public java.util.Locale getLocale() {
      if (response instanceof RenderResponse) {
         return ((RenderResponse) response).getLocale();
      }
      System.out.println("ServletResponseWrapper: cannot getLocale on a PortletResponse!");
      return null;
   }

   public ServletOutputStream getOutputStream() throws java.io.IOException {
      System.out.println("ServletResponseWrapper: cannot getOutputStream on a PortletResponse!");
      return null;
   }

   public java.io.PrintWriter getWriter() throws java.io.IOException {
      if (response instanceof RenderResponse) {
         return ((RenderResponse) response).getWriter();
      }
      System.out.println("ServletResponseWrapper: cannot getWriter on a PortletResponse!");
      return null;
   }

   public boolean isCommitted() {
      if (response instanceof RenderResponse) {
         return ((RenderResponse) response).isCommitted();
      }
      System.out.println("ServletResponseWrapper: cannot isCommitted on a PortletResponse!");
      return false;
   }

   public void reset() {
      if (response instanceof RenderResponse) {
         ((RenderResponse) response).reset();
         return;
      }
      System.out.println("ServletResponseWrapper: cannot reset on a PortletResponse!");
   }

   public void resetBuffer() {
      if (response instanceof RenderResponse) {
         ((RenderResponse) response).resetBuffer();
         return;
      }
      System.out.println("ServletResponseWrapper: cannot resetBuffer on a PortletResponse!");
   }

   public void setBufferSize(int size) {
      if (response instanceof RenderResponse) {
         ((RenderResponse) response).setBufferSize(size);
         return;
      }
      System.out.println("ServletResponseWrapper: cannot setBufferSize on a PortletResponse!");
   }

   public void setCharacterEncoding(java.lang.String charset) {
      System.out.println("ServletResponseWrapper: cannot setCharacterEncoding on a PortletResponse!");
   }

   public void setContentLength(int len) {
      System.out.println("ServletResponseWrapper: cannot setContentLength on a PortletResponse!");
   }

   public void setContentType(java.lang.String type) {
      if (response instanceof RenderResponse) {
         ((RenderResponse) response).setContentType(type);
         return;
      }
      System.out.println("ServletResponseWrapper: cannot setContentType on a PortletResponse!");
   }

   public void setLocale(java.util.Locale loc) {
      System.out.println("ServletResponseWrapper: cannot setLocale on a PortletResponse!");
   }
}