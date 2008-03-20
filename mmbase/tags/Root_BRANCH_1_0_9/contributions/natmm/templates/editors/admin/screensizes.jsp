<%@include file="/taglibs.jsp" %>
<mm:cloud name="mmbase" method="http" rank="basic user" jspvar="cloud">
<html>
<head>
   <title></title>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
</head>
<body style="overflow:auto;">
  <h3>Overzicht van gebruikte schermresoluties</h3>
  <table class="formcontent" style="width:auto;">
  <tr><td><b>IP</b></td><td><b>Breedte</b></td><td><b>Hoogte</b></td></tr>
  <%
      TreeMap screenSizes = (TreeMap) application.getAttribute("visitorscreens");
      if(screenSizes!=null){
          TreeMap screens  = (TreeMap) screenSizes.clone();
          while(screens.size()>0) {
              String thisUser = (String) screens.firstKey();
              String thisScreen = (String) screens.get(thisUser);
              screens.remove(thisUser);
              %><tr><td><%= thisUser %></td><%= thisScreen %></tr><%
          }
      } else {
              %><tr><td>-</td><td>-</td><td>-</td></tr><%
      }
  %></table><br>
</body>
</html>
</mm:cloud>
