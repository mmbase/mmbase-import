package nl.didactor.utils.debug;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import nl.didactor.utils.debug.LogController;

/**
 * @javadoc
 * @version $Id: InitLogController.java,v 1.2 2007-04-24 16:09:01 michiel Exp $
 */
public class InitLogController implements ServletContextListener {
   public void contextInitialized(ServletContextEvent e) {
      ServletContext sc = e.getServletContext();
      LogController.setContext(sc);
   }


   public void contextDestroyed(ServletContextEvent e) {
      ServletContext sc = e.getServletContext();
   }
}
