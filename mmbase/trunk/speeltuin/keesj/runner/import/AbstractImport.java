
import org.mmbase.bridge.*;
import java.util.*;
/**
 * @author keesj
 * @version $Id: AbstractImport.java,v 1.1.1.1 2004-07-08 10:32:15 keesj Exp $
 */
public abstract class AbstractImport{
    public Cloud newCloud ;
    public Cloud oldCloud ;
    public SyncNodes syncNode;
    public static String IMPORT_SOURCE_NAME="oldmmbase";
    public static String oldCloudRMI = "rmi://127.0.0.1:1120/old";
    public static String newCloudRMI = "rmi://127.0.0.1:1120/new";

    public AbstractImport(){
            HashMap user = new HashMap();
                user.put("username","admin");
                user.put("password","temp$$42");
                //user.put("password","nietgebruikenaub");
                oldCloud = ContextProvider.getCloudContext("rmi://127.0.0.1:1120/old").getCloud("mmbase");
                newCloud = ContextProvider.getCloudContext("rmi://127.0.0.1:1120/new").getCloud("mmbase","name/password",user);

	syncNode = new SyncNodes(newCloud);
    }

    public abstract void doImport();
}
