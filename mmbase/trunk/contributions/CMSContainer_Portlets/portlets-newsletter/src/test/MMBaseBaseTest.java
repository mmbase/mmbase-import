import junit.framework.TestCase;
import org.mmbase.bridge.Cloud;
import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

public class MMBaseBaseTest extends TestCase {

    Cloud cloud;

    public MMBaseBaseTest(String s) {
        super(s);                                               
        CloudProvider provider = CloudProviderFactory.getCloudProvider();
        cloud = provider.getCloud();
    }

    
}
