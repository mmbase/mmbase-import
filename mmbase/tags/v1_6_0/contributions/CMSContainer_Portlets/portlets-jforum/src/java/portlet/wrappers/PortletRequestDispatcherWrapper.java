package portlet.wrappers;

import javax.servlet.*;
import javax.portlet.*;

/**
 * Class to wrap a RequestDispatcher so it can be used as a PortletRequestDispatcher
 */
public class PortletRequestDispatcherWrapper implements PortletRequestDispatcher {
   private RequestDispatcher dispatcher;

   public PortletRequestDispatcherWrapper(RequestDispatcher dispatcher) {
      this.dispatcher = dispatcher;
   }

   public void include(RenderRequest request, RenderResponse response) throws PortletException, java.io.IOException {
      try {
         dispatcher.include(new ServletRequestWrapper(request), new ServletResponseWrapper(response));
      } catch (ServletException e) {
         throw new PortletException(e);
      }
   }
}