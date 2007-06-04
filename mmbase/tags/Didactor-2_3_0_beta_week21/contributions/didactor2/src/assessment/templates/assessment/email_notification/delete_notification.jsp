<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<%@page import="java.util.*" %>
<%@page import="java.text.SimpleDateFormat" %>


<mm:cloud method="delegate" jspvar="cloud">

<%@include file="/shared/setImports.jsp"%>

<html>
<head>
<title>File manager</title>
   <link rel="stylesheet" type="text/css" href='<mm:treefile page="/css/base.css" objectlist="$includePath" referids="$referids" />' />
   <link rel="stylesheet" type="text/css" href="<mm:treefile page="/editwizards/style/layout/list.css" objectlist="$includePath" referids="$referids" />" />
   <link rel="stylesheet" type="text/css" href="<mm:treefile page="/editwizards/style/color/list.css" objectlist="$includePath" referids="$referids" />" />
</head>
<body>


   <mm:import externid="notification_to_delete"/>

   <mm:node number="$notification_to_delete" notfound="skip">
      <mm:deletenode deleterelations="true"/>
   </mm:node>

   <di:translate key="assessment.email_notification_deleted" />

   <script>
      history.back();
   </script>

</body>

</mm:cloud>
