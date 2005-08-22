package nl.didactor.versioning.builders;

import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.Cloud;
import nl.didactor.versioning.VersioningController;
import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

public class EducationsVersioningBuilder extends nl.didactor.builders.SmartpathBuilder
{

  private static Logger log = Logging.getLoggerInstance(EducationsVersioningBuilder.class.getName());
  private Cloud cloud;

  public MMObjectNode preCommit(MMObjectNode node) {
    CloudProvider cloudProvider = CloudProviderFactory.getCloudProvider();
    cloud = cloudProvider.getAdminCloud();
    VersioningController vc = new VersioningController(cloud);
    vc.addLOVersion(cloud.getNode(node.getNumber()));
    super.preCommit(node);
    log.info("education.precommit is called for node " + node.getNumber());
    return node;
  }

  public boolean commit(MMObjectNode node) {
    log.info("education.commit is called for node " + node.getNumber());
    boolean bSuperCommit = super.commit(node);
    return bSuperCommit;
  }
}
