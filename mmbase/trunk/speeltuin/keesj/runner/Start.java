import org.mmbase.util.logging.Logging;
import org.mmbase.module.tools.MMAdmin;
import java.io.*;

public class Start{
	public static void main(String[] argv) throws Exception{
                Logging.configure(System.getProperty("mmbase.config") + File.separator + "log" + File.separator + "log.xml");

		org.mmbase.module.core.MMBaseContext.init();
		org.mmbase.module.core.MMBase.getMMBase();
		MMAdmin mmadmin = (MMAdmin) org.mmbase.module.core.MMBase.getModule("mmadmin", true);
	       while (! mmadmin.hasStarted()) {
       		     Thread.sleep(1000);
        	}
		System.err.println("started");
	}
}
