package com.finalist.newsletter.services;

import com.finalist.newsletter.cao.NewsletterSubscriptionCAO;
import com.finalist.newsletter.cao.impl.NewsletterSubscriptionCAOImpl;
import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.services.impl.NewsletterSubscriptionServicesImpl;
import com.sevenirene.archetype.testingplatform.impl.logic.mock.MockController;
import junit.framework.TestCase;

public class NewsLetterSubscriptionServiceTest extends TestCase {
	
	MockController mockControl;
	NewsletterSubscriptionCAO caoMock;
	NewsletterSubscriptionServicesImpl serviceImpl;
	
	public void setUp(){
		mockControl = new MockController();
	    caoMock = (NewsletterSubscriptionCAO) mockControl.getMock(NewsletterSubscriptionCAOImpl.class);
	    serviceImpl = new NewsletterSubscriptionServicesImpl();
	}
	
	
	
	public void testGetAllowedNewsletterList() {
		
		mockControl.expect(new NewsletterSubscriptionCAOImpl() {             // Expect the AccountManager.deductFunds() method to be called
			public Newsletter getNewsletterById(int id){
				assertEquals(1111, id);
               return new Newsletter();
            }
        });
		mockControl.expect(new NewsletterSubscriptionCAOImpl() {             // Expect the AccountManager.deductFunds() method to be called
			public Newsletter getNewsletterById(int id){
				assertEquals(2222, id);
               return new Newsletter();
            }
        });
		mockControl.expect(new NewsletterSubscriptionCAOImpl() {             // Expect the AccountManager.deductFunds() method to be called
			public Newsletter getNewsletterById(int id){
				assertEquals(3333, id);
               return new Newsletter();
            }
        });
		
		String[] test=new String[3];

		test[0]="1111";
		test[1]="2222";
		test[2]="3333";
		

		mockControl.verify();
	}
	
	public void testAddRecordInfo(){
		
	}

}
