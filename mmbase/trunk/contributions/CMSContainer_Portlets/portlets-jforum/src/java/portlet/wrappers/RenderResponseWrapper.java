package portlet.wrappers;

import javax.servlet.*;
import javax.portlet.*;

/**
 * Class to wrap a ServletResponse so it can be used as a RenderResponse
 */
public class RenderResponseWrapper extends PortletResponseWrapper implements RenderResponse {

   public static java.lang.String EXPIRATION_CACHE = RenderResponse.EXPIRATION_CACHE;

   public RenderResponseWrapper(ServletResponse response) {
      super(response);
   }

   public PortletURL createActionURL() {
      System.out.println("RenderResponseWrapper: cannot createActionURL from a ServletResponse!");
      return null;
   }

   public PortletURL createRenderURL() {
      System.out.println("RenderResponseWrapper: cannot createRenderURL from a ServletResponse!");
      return null;
   }

   public void flushBuffer() throws java.io.IOException {
      response.flushBuffer();
   }

   public int getBufferSize() {
      return response.getBufferSize();
   }

   public java.lang.String getCharacterEncoding() {
      return response.getCharacterEncoding();
   }

   public java.lang.String getContentType() {
      System.out.println("RenderResponseWrapper: cannot getContentType from a ServletResponse!");
      return null;
   }

   public java.util.Locale getLocale() {
      return response.getLocale();
   }

   public java.lang.String getNamespace() {
      System.out.println("RenderResponseWrapper: cannot getNamespace from a ServletResponse!");
      return null;
   }

   public java.io.OutputStream getPortletOutputStream() throws java.io.IOException {
      return response.getOutputStream();
   }

   public java.io.PrintWriter getWriter() throws java.io.IOException {
      return response.getWriter();
   }

   public boolean isCommitted() {
      return response.isCommitted();
   }

   public void reset() {
      response.reset();
   }

   public void resetBuffer() {
      response.resetBuffer();
   }

   public void setBufferSize(int size) {
      response.setBufferSize(size);
   }

   public void setContentType(java.lang.String type) {
      response.setContentType(type);
   }

   public void setTitle(java.lang.String title) {
      System.out.println("RenderResponseWrapper: cannot setTitle from a ServletResponse!");
   }

}