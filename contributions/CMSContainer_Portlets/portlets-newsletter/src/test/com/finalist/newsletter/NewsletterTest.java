package com.finalist.newsletter;

import junit.framework.TestCase;
import org.mmbase.bridge.Cloud;
import org.dbunit.DatabaseUnitException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import net.sf.mmapps.modules.cloudprovider.CloudProvider;
import net.sf.mmapps.modules.cloudprovider.CloudProviderFactory;

import java.util.Collections;
import java.sql.SQLException;
import java.io.IOException;


public class NewsletterTest extends TestCase {

   protected DBUnitTemplate dbtemp;
   protected ApplicationContext context;

   public void setUp() throws DatabaseUnitException, SQLException, IOException {
      //得到Spring容器，TestConfiguration是自己写的在测试开始前初始化Spring容器的类
      context = new ClassPathXmlApplicationContext("spring-newsletter.xml");
      //从容器中得到DBUnitTemplate
      dbtemp = (DBUnitTemplate) context.getBean("dbUnitTemplate");
   }

}
