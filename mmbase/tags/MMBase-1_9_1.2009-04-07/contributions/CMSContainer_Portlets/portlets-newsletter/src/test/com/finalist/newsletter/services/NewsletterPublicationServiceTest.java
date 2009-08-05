package com.finalist.newsletter.services;

import com.finalist.newsletter.cao.NewsLetterStatisticCAO;
import com.finalist.newsletter.cao.NewsletterPublicationCAO;
import com.finalist.newsletter.cao.NewsletterSubscriptionCAO;
import com.finalist.newsletter.cao.impl.NewsLetterStatisticCAOImpl;
import com.finalist.newsletter.cao.impl.NewsletterPublicationCAOImpl;
import com.finalist.newsletter.cao.impl.NewsletterSubscriptionCAOImpl;
import com.finalist.newsletter.domain.Publication;
import com.finalist.newsletter.domain.Subscription;
import com.finalist.newsletter.domain.Newsletter;
import com.finalist.newsletter.publisher.FakeNewsletterPublisher;
import com.finalist.newsletter.services.impl.NewsletterPublicationServiceImpl;
import com.finalist.newsletter.DBUnitTemplate;
import com.sevenirene.archetype.testingplatform.impl.logic.mock.MockController;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.sql.SQLException;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.ApplicationContext;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.DatabaseUnitException;

public class NewsletterPublicationServiceTest extends TestCase {





}
