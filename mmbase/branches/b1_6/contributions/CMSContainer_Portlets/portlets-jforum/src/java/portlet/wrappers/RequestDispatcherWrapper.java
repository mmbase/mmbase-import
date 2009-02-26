package portlet.wrappers;

import javax.servlet.*;
import javax.portlet.*;

/**
 * Class to wrap a PortletRequestDispatcher so it can be used as a RequestDispatcher
 */
public class RequestDispatcherWrapper implements RequestDispatcher {
   private PortletRequestDispatcher dispatcher;

   public RequestDispatcherWrapper(PortletRequestDispatcher dispatcher) {
      this.dispatcher = dispatcher;
   }

   public void forward(ServletRequest request, ServletResponse response) throws ServletException, java.io.IOException {
      System.out.println("RequestDispatcherWrapper: cannot forward from a PortletRequestDispatcher!");
   }

   public void include(ServletRequest request, ServletResponse response) throws ServletException, java.io.IOException {
      try {
         dispatcher.include(new RenderRequestWrapper(request), new RenderResponseWrapper(response));
      } catch (PortletException e) {
         throw new ServletException(e);
      }
   }
}