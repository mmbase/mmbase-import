<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@include file="header.jsp" %><html xmlns="http://www.w3.org/1999/xhtml">
  <mm:cloud name="mmbase" method="http" jspvar="cloud" rank="$rank">
  <%  Stack states = (Stack)session.getValue("mmeditors_states");
      if (states==null) {
          states=new Stack();
          session.putValue("mmeditors_states",states);
      }
      // remove all transactions
      while (!states.empty()) {
        Properties state= (Properties)states.pop();
        String transactionID = state.getProperty("transaction");
        if (transactionID!=null) try {
          Transaction trans=cloud.getTransaction(transactionID);
          trans.cancel();
        } catch (Exception e) { %><%=e%> <%}
      }
%><head>
    <title>Editors</title>
    <link rel="stylesheet" href="css/mmeditors.css" type="text/css" />
  </head>
  <frameset rows="23,*" border="0" frameborder="0" framespacing="0">
    <frame src="<mm:url page="editnav.jsp" />" name="navigatorarea" scrolling="no" marginheight="0" marginwidth="0" />
    <frameset cols="220,*" framespacing="0" frameborder="0">
      <frame src="<mm:url page="editlist.jsp" />" name="selectarea" marginheight="0" marginwidth="0" />
      <frame src="<mm:url page="help.html" />" name="workarea" scrolling="auto" marginheight="0" marginwidth="0" />
    </frameset>
  </frameset>
  <noframes>
    <body>
      <p>This page is designed to be viewed by a browser which supports Netscape's Frames extension. This text will be shown by browsers which do not support the Frames extension.</p>
    </body>
  </noframes>
  </mm:cloud>
</html>


