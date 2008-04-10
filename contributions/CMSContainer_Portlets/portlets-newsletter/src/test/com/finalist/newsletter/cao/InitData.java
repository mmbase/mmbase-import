package com.finalist.newsletter.cao;

import com.finalist.newsletter.BaseNewsletterTest;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InitData extends BaseNewsletterTest {
   public void testInit() {

      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

      try {
         int r1id = insertNewsletterRecords("title1");
         int r2id = insertNewsletterRecords("title2");

         insertNewsletterdailylogRecords(1, 1, 1, 1, 1, r1id, format
               .parse("2008-1-1"));
         insertNewsletterdailylogRecords(1, 1, 1, 1, 1, r1id, format
               .parse("2008-1-2"));
         insertNewsletterdailylogRecords(1, 1, 1, 1, 1, r2id, format
               .parse("2008-1-3"));
         insertNewsletterdailylogRecords(1, 1, 1, 1, 1, r2id, format
               .parse("2008-1-4"));
         insertNewsletterdailylogRecords(1, 1, 1, 1, 1, 1, format
               .parse("2008-1-3"));
         insertNewsletterdailylogRecords(1, 1, 1, 1, 1, 1, format
               .parse("2008-1-4"));
         insertNewsletterdailylogRecords(1, 1, 1, 1, 1, 2, format
               .parse("2008-1-3"));
         insertNewsletterdailylogRecords(1, 1, 1, 1, 1, 2, format
               .parse("2008-1-4"));
      } catch (ParseException e) {
         e.printStackTrace();
      }

   }

   private void insertNewsletterdailylogRecords(int post, int subscribe, int unsubscribe, int remove, int bounches, int newsletter, Date logDate) {

      NodeManager manager = cloud.getNodeManager("newsletterdailylog");
      Node node = manager.createNode();

      node.setIntValue("post", post);
      node.setIntValue("subscribe", subscribe);
      node.setIntValue("bounches", bounches);
      node.setIntValue("unsubscribe", unsubscribe);
      node.setIntValue("removed", remove);
      node.setDateValue("logdate", logDate);
      node.setIntValue("newsletter", newsletter);

      node.commit();
   }

   private int insertNewsletterRecords(String title) {
      NodeManager manager = cloud.getNodeManager("newsletter");
      Node node = manager.createNode();
      node.setStringValue("title", title);
      node.commit();
      return node.getNumber();
   }
}
