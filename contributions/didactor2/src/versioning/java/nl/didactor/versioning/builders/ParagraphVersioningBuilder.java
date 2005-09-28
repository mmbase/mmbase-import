package nl.didactor.versioning.builders;

import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.Cloud;
import nl.didactor.versioning.VersioningController;
import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

public class ParagraphVersioningBuilder extends MMObjectBuilder
{

  private static Logger log = Logging.getLoggerInstance(ParagraphVersioningBuilder.class.getName());
  private Cloud cloud;

  public MMObjectNode preCommit(MMObjectNode node) {
    CloudProvider cloudProvider = CloudProviderFactory.getCloudProvider();
    cloud = cloudProvider.getAdminCloud();
    Node originalNode = cloud.getNode(node.getNumber());
    NodeList learnobjects = learnobjects = originalNode.getRelatedNodes("learnobjects");
    for(int i=0;i<learnobjects.size();i++) {
      VersioningController.addLOVersion(learnobjects.getNode(i));
      log.info("paragraph.precommit : lo : " + learnobjects.getNode(i).getNumber());
    }
    NodeList educations = originalNode.getRelatedNodes("educations");
    for(int i=0;i<educations.size();i++) {
      VersioningController.addLOVersion(educations.getNode(i));
      log.info("paragraph.precommit : education : " + educations.getNode(i).getNumber());
    }
    super.preCommit(node);
    log.info("paragraph.precommit is called for node " + node.getNumber());
    return node;
  }

  public boolean commit(MMObjectNode node) {
    log.info("paragraph.commit is called for node " + node.getNumber());
    boolean bSuperCommit = super.commit(node);
    return bSuperCommit;
  }
}
