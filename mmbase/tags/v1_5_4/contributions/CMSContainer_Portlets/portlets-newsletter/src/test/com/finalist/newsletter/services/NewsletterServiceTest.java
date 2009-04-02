package com.finalist.newsletter.services;

import com.finalist.newsletter.NewsletterTest;
import org.dbunit.DatabaseUnitException;
import org.dbunit.operation.DatabaseOperation;

import java.io.IOException;
import java.sql.SQLException;

public class NewsletterServiceTest extends NewsletterTest {


   public void setUp() throws SQLException, DatabaseUnitException, IOException {
      super.setUp();
      dbtemp.execute("newsletter_service_ds.xml", DatabaseOperation.INSERT);
   }

   protected void tearDown() throws Exception {
      dbtemp.execute("newsletter_service_ds.xml", DatabaseOperation.DELETE);
   }

   public void testProcessBouncesOfPublication() throws SQLException, DatabaseUnitException, IOException {

   }
}
