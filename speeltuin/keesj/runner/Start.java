import org.mmbase.util.logging.Logging;
import org.mmbase.module.tools.MMAdmin;
import org.mmbase.util.ResourceLoader;
import org.mmbase.module.core.MMBase;
import java.io.*;

public class Start{
	public static void main(String[] argv) throws Exception{
		Logging.configure(ResourceLoader.getConfigurationRoot().getChildResourceLoader("log"), "log.xml");
		org.mmbase.module.core.MMBaseContext.init();
		MMBase mmbase = MMBase.getMMBase();
	        while (! mmbase.hasStarted()) {
       		     Thread.sleep(1000);
        	}
		System.err.println("started");
	}
}
