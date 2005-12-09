package nl.didactor.utils.debug;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import nl.didactor.utils.debug.LogController;

public class InitLogController implements ServletContextListener
{
   public void contextInitialized(ServletContextEvent e)
   {
      ServletContext sc = e.getServletContext();
      LogController.setContext(sc);
   }


   public void contextDestroyed(ServletContextEvent e)
   {
      ServletContext sc = e.getServletContext();
   }
}
