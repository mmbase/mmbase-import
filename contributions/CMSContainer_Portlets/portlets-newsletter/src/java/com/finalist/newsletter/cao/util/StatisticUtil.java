package com.finalist.newsletter.cao.util;

import java.util.ArrayList;
import java.util.List;

import org.mmbase.bridge.NodeList;

import com.finalist.newsletter.domain.StatisticResult;

public class StatisticUtil {

   public List<StatisticResult> convertFromNodeList(NodeList nodelist) {

      List<StatisticResult> list = new ArrayList<StatisticResult>();
      for (int i = 0; i < nodelist.size(); i++) {
         StatisticResult result = new StatisticResult();
         result.setUserId(nodelist.getNode(i).getIntValue("userid"));
         result.setNewsletterId(nodelist.getNode(i).getIntValue("newsletter"));
         result.setBounches(nodelist.getNode(i).getIntValue("bounches"));
         result.setPost(nodelist.getNode(i).getIntValue("post"));
         result.setRemoved(nodelist.getNode(i).getIntValue("removed"));
         result.setSubscribe(nodelist.getNode(i).getIntValue("subscribe"));
         result.setUnsubscribe(nodelist.getNode(i).getIntValue("unsubscribe"));
         result.setLogdate(nodelist.getNode(i).getDateValue("logdate"));
         list.add(result);
      }
      return list;
   }
}
