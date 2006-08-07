package nl.leocms.util;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class DateUtil{
  private static final Logger log = Logging.getLoggerInstance(DateUtil.class);

  public String getObjectNumber(Cloud cloud, Date date) {
  // returns the object number of the node that is created on date
  int today_days = (int)(date.getTime() / (60 * 60 * 24)) / 1000;
  NodeList nl = cloud.getNodeManager("daymarks").getList("daymarks.daycount = "
      + today_days,"number", "DOWN");
  if (nl.size()>0){
    return nl.getNode(0).getStringValue("mark");
  } else {
    return "-1";
  }

  }

}
