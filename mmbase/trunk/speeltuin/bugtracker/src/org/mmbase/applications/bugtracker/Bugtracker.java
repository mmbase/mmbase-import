import org.mmbase.bridge.*;
public class MMCITest {
    public static void main(String argv[]) throws Exception{
        CloudContext  cloudContext = ContextProvider.getCloudContext("rmi://24.132.250.86:1111/remotecontext");
        Cloud cloud = cloudContext.getCloud("mmbase");
	NodeManager nodeManager = cloud.getNodeManager("people");

    }
}
