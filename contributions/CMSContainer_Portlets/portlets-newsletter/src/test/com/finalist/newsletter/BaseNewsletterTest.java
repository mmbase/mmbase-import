package com.finalist.newsletter;

import junit.framework.TestCase;
import org.mmbase.module.core.MMBaseContext;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.tools.MMAdmin;
import org.mmbase.module.tools.ApplicationInstaller;
import org.mmbase.module.Module;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.Node;
import org.mmbase.storage.search.Step;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import java.util.List;

public abstract class BaseNewsletterTest extends TestCase {

   protected Cloud cloud;
   protected DBUnitTemplate dbtemp;
   protected ApplicationContext context;

   protected void setUp() throws Exception {
      context = new ClassPathXmlApplicationContext("spring-newsletter.xml");
      dbtemp = (DBUnitTemplate) context.getBean("dbUnitTemplate");

      MMBaseContext.init();
      MMBase mmb = MMBase.getMMBase();

      MMAdmin mmadmin = (MMAdmin) Module.getModule("mmadmin", true);
      ApplicationInstaller installer = new ApplicationInstaller(mmb, mmadmin);
      installer.installApplications();

      CloudProvider provider = CloudProviderFactory.getCloudProvider();
      cloud = provider.getCloud();
   }

   protected void clearAllNode(String nodeType) {
      NodeQuery query = cloud.createNodeQuery();
      Step theStep = query.addStep(cloud.getNodeManager(nodeType));
      query.setNodeStep(theStep);
      List<Node> list = query.getList();
      for (Node node : list) {
         node.deleteRelations();
         node.delete();
      }
   }
}
