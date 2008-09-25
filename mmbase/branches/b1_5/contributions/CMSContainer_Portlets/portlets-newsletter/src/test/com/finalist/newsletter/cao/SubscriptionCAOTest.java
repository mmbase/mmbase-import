package com.finalist.newsletter.cao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import org.apache.commons.lang.StringUtils;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.RelationManager;

import com.finalist.newsletter.BaseNewsletterTest;
import com.finalist.newsletter.cao.impl.NewsletterSubscriptionCAOImpl;
import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.domain.Subscription;
import com.finalist.newsletter.services.NewsletterSubscriptionServices;
import com.finalist.newsletter.services.impl.NewsletterSubscriptionServicesImpl;

import junit.framework.TestCase;

public class SubscriptionCAOTest extends BaseNewsletterTest {

	NewsletterSubscriptionCAOImpl cao;
	NewsletterSubscriptionServices services;

	public void setUp() throws Exception {
		super.setUp();
		cao = new NewsletterSubscriptionCAOImpl(cloud);
		services = new NewsletterSubscriptionServicesImpl();
	}
	
	public void testGetUserSubscriptionList()
	{
		
		initNewsletters();
		//List<Newsletter> list = cao.getAllNewsletter();
		//Iterator it =  list.iterator();
		//for(int i=0;i<list.size();i++)
		//{
		//	Newsletter newsletter = (Newsletter) it.next();
		//}
		//assertEquals(3,list.size());
		/*Node node = cloud.getNode("778");
		assertEquals("22222",node.getStringValue("title"));
		NodeManager tagManager = cloud.getNodeManager("tag");
		Node tag1 = tagManager.createNode();
		tag1.setStringValue("name", "tagname1");
		tag1.commit();
		Node tag2 = tagManager.createNode();
		tag2.setStringValue("name", "tagname2");
		tag2.commit();
		
		RelationManager insrel = cloud.getRelationManager("newsletter","tag", "tagged");
		node.createRelation(tag1, insrel).commit();
		node.createRelation(tag2, insrel).commit();
		*/
		/*String userName = initSubscriptionRecord();
		List<Newsletter> results = cao.getUserSubscriptionList("username");
		assertEquals(4, results.size());
		assertTrue(StringUtils.isEmpty(results.get(0).getTitle()));
*/
	}

	public void teset() {
		CloudProvider provider = CloudProviderFactory.getCloudProvider();
		Cloud cloud = provider.getCloud();
		NewsletterSubscriptionServices ss = new NewsletterSubscriptionServicesImpl();
		String[] ids = new String[2];
		ids[0]="1111";
		ids[1]="2222";

	}

	/*public List<Node> testQuerySubcriptionByUser(int userId) {
		userId = 1234;
		List<Node> results = cao.querySubcriptionByUser(userId);
		assertEquals(2,results.size());
		return results;
	}*/

	public void testGetNewsletterById() {
		//initSubscriptionRecord();
		//NewsletterSubscriptionServices ss = new NewsletterSubscriptionServicesImpl();
		//Newsletter newsletter = ss.addRecordInfo(initAllowNewsletter(), initRecordList());
		//assertEquals("normal", newsletter.getStatus());
		
		/*int number = initNewsletters();

		Newsletter letter = cao.getNewsletterById(number);
		assertNotNull(letter);
		assertEquals("testtitle", letter.getTitle());
		assertNotNull(letter.getTags());
		assertEquals(2, letter.getTags().size());
		assertEquals("tagname1", letter.getTags().get(0).getName());*/

	}
	
	/*public void testGetAllNewsletter(){
		List<Newsletter> list = cao.getAllNewsletter();
		assertEquals(11,list.size());
	}*/

	private int[] initSubscriptionRecord(){
		NodeManager recordManager = cloud.getNodeManager("subscriptionrecord");
		Node node = recordManager.createNode();
		node.setIntValue("subscriber", 1234);
		node.setStringValue("status", "PAUSED");
		node.setStringValue("format", "HTML");
		node.commit();		
		
		
		NodeManager tagManager = cloud.getNodeManager("tag");
		Node tag1 = tagManager.createNode();
		tag1.setStringValue("name", "tagname1111");
		tag1.commit();
		Node tag2 = tagManager.createNode();
		tag2.setStringValue("name", "tagname2222");
		tag2.commit();
		Node tag3 = tagManager.createNode();
		tag3.setStringValue("name", "tagname3333");
		tag3.commit();
		
		
		RelationManager insrel = cloud.getRelationManager("subscriptionrecord","tag", "tagged");
		node.createRelation(tag1, insrel).commit();
		node.createRelation(tag2, insrel).commit();
		node.createRelation(tag3, insrel).commit();
		

	  NodeManager newsletterManager = cloud.getNodeManager("newsletter");
		Node newsletter1 = newsletterManager.createNode();
		newsletter1.setStringValue("title", "titletest");
		newsletter1.commit();
		
		RelationManager insrel2 = cloud.getRelationManager("subscriptionrecord","newsletter", "newslettered");
		node.createRelation(newsletter1, insrel2).commit();
		
		
		

		RelationManager insreltag = cloud.getRelationManager("newsletter","tag", "tagged");
		insreltag.createRelation(newsletter1, tag1).commit();
		insreltag.createRelation(newsletter1, tag2).commit();
		
		int[] returnValues = new int[5];
		returnValues[0] = newsletter1.getNumber();
		returnValues[1] = node.getNumber();
		return returnValues;
	}
	
	private int initNewsletters() {
		NodeManager letterManager = cloud.getNodeManager("newsletter");
		Node node = letterManager.createNode();
		node.setStringValue("title", "food");
		node.commit();

		NodeManager tagManager = cloud.getNodeManager("tag");

		Node tag1 = tagManager.createNode();
		tag1.setStringValue("name", "meet");
		tag1.commit();
		Node tag2 = tagManager.createNode();
		tag2.setStringValue("name", "bread");
		tag2.commit();
		

		RelationManager insrel = cloud.getRelationManager("newsletter","tag", "tagged");
		
		node.createRelation(tag1, insrel).commit();
		node.createRelation(tag2, insrel).commit();

		return node.getNumber();
	}
	private Newsletter initAllowNewsletter() {
		Newsletter newsletter = new Newsletter();
		newsletter.setTitle("1");
		return newsletter;
	}
	private List<Newsletter> initRecordList() {
		Newsletter newsletter = new Newsletter();
		List<Newsletter> list = new ArrayList<Newsletter>();
		for(int i=0;i<2;i++)
		{
			newsletter.setTitle(""+i);
			newsletter.setStatus("pause");
			list.add(newsletter);			
		}
		return list;		
	}

	public void testGetSubscription(){
		int[] keys = initSubscriptionRecord();
		int newsletterId = keys[0];
		Subscription subscription = cao.getSubscription(newsletterId, 1234);
		assertEquals(keys[1], subscription.getId());
	}
}
