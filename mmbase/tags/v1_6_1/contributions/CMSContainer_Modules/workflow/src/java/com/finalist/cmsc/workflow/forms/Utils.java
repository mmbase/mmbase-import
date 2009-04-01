package com.finalist.cmsc.workflow.forms;

import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

public class Utils {

   public static String onClickandStyle(PageContext pageContext, String column) {
      String status = (String) pageContext.findAttribute("status");
      Boolean lastValue = (Boolean) pageContext.findAttribute("lastvalue");
      String orderby = (String) pageContext.findAttribute("orderby");
      String workflowNodetype = (String) pageContext.findAttribute("workflowNodetype");
      if (StringUtils.isEmpty(workflowNodetype)) {
         workflowNodetype = "";
      }
      String template = "onclick=\"selectTab('%s','%s','%s','%s')\" %s";

      if ("undefined".equals(orderby)) {
         return String.format(template, status, workflowNodetype,"lastmodifieddate", "true", "class=\"sortup\"").trim();
      } else if (column.equals(orderby)) {
         return String.format(template, status, workflowNodetype,column, lastValue, lastValue ? "class=\"sortup\"" : "class=\"sortdown\"");
      } else {
         return String.format(template, status, workflowNodetype,column, "false", "").trim();
      }
   }

   public static String tabClass(PageContext pageContext, String status) {
      if (status.equals(pageContext.findAttribute("status"))) {
         return "tab_active";
      } else {
         return "tab";
      }
   }
}
