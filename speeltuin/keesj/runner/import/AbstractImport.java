
import org.mmbase.bridge.*;
import java.util.*;
/**
 * @author keesj
 * @version $Id: AbstractImport.java,v 1.2 2004-07-08 11:49:21 keesj Exp $
 */
public abstract class AbstractImport{
    public Cloud newCloud ;
    public Cloud oldCloud ;
    public SyncNodes syncNode;
    public static String IMPORT_SOURCE_NAME="oldmmbase";
    public static String oldCloudRMI = "rmi://127.0.0.1:1111/old";
    public static String newCloudRMI = "rmi://127.0.0.1:1111/new";

    public AbstractImport(){
            HashMap user = new HashMap();
                user.put("username","admin");
                user.put("password","passwd");
                oldCloud = ContextProvider.getCloudContext(oldCloudRMI).getCloud("mmbase");
                newCloud = ContextProvider.getCloudContext(newCloudRMI).getCloud("mmbase","name/password",user);

	syncNode = new SyncNodes(newCloud);
    }

    public abstract void doImport();
}
