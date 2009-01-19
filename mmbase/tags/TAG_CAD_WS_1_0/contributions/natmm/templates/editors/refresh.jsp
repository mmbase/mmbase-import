<%@include file="/taglibs.jsp" %>
<mm:cloud method="http" jspvar="cloud" rank="basic user">
<html>
<head>
  <link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
  <link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
  <script>
    // *** refresh every X minutes , avoid session timeout ***
    function resubmit() {
      document.forms[0].submit();
    }
  </script>
</head>
  <%
  String action = request.getParameter("action");
  HashMap loggedInAccount = (HashMap) application.getAttribute("logged_in_account");
  if(loggedInAccount==null || "reset".equals(action)) {
    loggedInAccount = new HashMap();
    application.setAttribute("logged_in_account", loggedInAccount);
  }
  HashMap loggedInTime = (HashMap) application.getAttribute("logged_in_time");
  if(loggedInTime==null || "reset".equals(action)) {
    loggedInTime = new HashMap();
    application.setAttribute("logged_in_time", loggedInTime);
  }
  if("showall".equals(action)||"reset".equals(action)) {
    %>
    <body style="overflow:auto;">
    <table class="formcontent" style="width:auto;" border="1">
    <tr><th>session</th><th>#minutes logged in</th></tr>
    <%
    for(Iterator it=loggedInTime.keySet().iterator();it.hasNext();) {
      String sessionId = (String) it.next();
      %>
      <tr><td><%= loggedInAccount.get(sessionId) %></td><td><%= loggedInTime.get(sessionId) %></td></tr>
      <%
    }
    %>
    </table>
    </body>
    <%
  } else {
    
    String sessionId = session.getId();
    
    Integer numberOfMinutes = (Integer) loggedInTime.get(sessionId);
    if(numberOfMinutes==null) { numberOfMinutes = new Integer(0); }
    loggedInTime.put(sessionId,new Integer(numberOfMinutes.intValue()+1));
    
    String accountId = request.getParameter("account");
    if(accountId!=null) {
      loggedInAccount.put(sessionId,accountId + " / " + ServerUtil.getDateTimeString());
    }
    %>
    <body onload="javascript:setTimeout('resubmit()',60*1000);" style="background-color:#00FF00;">
      <form name="dummy" method="post" target=""></form>
      <!-- number of minutes logged in <%= numberOfMinutes %> -->
    </body>
    <%
  }
  %>
</mm:cloud>
