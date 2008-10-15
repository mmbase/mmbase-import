package org.jahia.portlet.fileupload;

import net.jforum.util.legacy.commons.fileupload.RequestContext;

import javax.portlet.ActionRequest;
import java.io.InputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: jahia
 * Date: 19 mars 2007
 * Time: 13:11:29
 * To change this template use File | Settings | File Templates.
 */
public class PortletRequestContext implements RequestContext {
   private ActionRequest request;

   public PortletRequestContext(ActionRequest request) {
      this.request = request;
   }

   public String getContentType() {
      return request.getContentType();
   }

   public int getContentLength() {
      return request.getContentLength();
   }

   public InputStream getInputStream() throws IOException {
      return request.getPortletInputStream();
   }
}
