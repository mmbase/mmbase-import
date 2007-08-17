<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<%@page import="java.util.*" %>
<%@page import="java.text.SimpleDateFormat" %>



<mm:cloud method="delegate" jspvar="cloud">

<%@include file="/shared/setImports.jsp"%>


<%
   SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
%>


<mm:import id="wizardjsp"><mm:treefile write="true" page="/editwizards/jsp/wizard.jsp" objectlist="$includePath" />?referrer=/education/wizards/ok.jsp&language=en</mm:import>

<html>
<head>
<title>File manager</title>
   <link rel="stylesheet" type="text/css" href='<mm:treefile page="/css/base.css" objectlist="$includePath" referids="$referids" />' />
   <link rel="stylesheet" type="text/css" href="<mm:treefile page="/editwizards/style/layout/list.css" objectlist="$includePath" referids="$referids" />" />
   <link rel="stylesheet" type="text/css" href="<mm:treefile page="/editwizards/style/color/list.css" objectlist="$includePath" referids="$referids" />" />
</head>
<body>


<table class="head">
   <tr class="headsubtitle">
      <td>
         <div><di:translate key="assessment.email_notification_title" /></div>
      </td>
   </tr>
</table>


<style>

   .cell
   {
      border-left:0px;
      border-right:0px;
   }

   .first_cell
   {
      border-right:0px;
   }

   .last_cell
   {
      border-left:0px;
   }
</style>


<table class="body">
   <tr class="listcanvas">
      <td>
         <table class="listcontent" border="1" cellspacing="0" cellpadding="3" style="border:0px; padding-left:0px">
            <tr class="listheader">
               <th class="first_cell" style="width:0px">&nbsp;</th>
               <th class="cell" style="width:20%"><di:translate key="assessment.email_notification_subject" /></th>
               <th class="cell" style="width:20%"><di:translate key="assessment.email_notification_to" /></th>
               <th class="cell" style="width:40%"><di:translate key="assessment.email_notification_body" /></th>
               <th class="last_cell" style="width:20%"><di:translate key="assessment.email_notification_senddate" /></th>
            </tr>

         <mm:listnodes type="email_notifications" orderby="email_notifications.subject">
            <tr class="listcontent">
               <td class="first_cell" style="border-top:0px">
                  <a onClick="return window.confirm('<di:translate key="assessment.email_notification_are_you_sure" />')"  href="delete_notification.jsp?notification_to_delete=<mm:field name="number"/>"><img src="../gfx/remove_email_notification.gif"/></a>
               </td>
               <td class="cell" style="border-top:0px">
                  <a href='<mm:write referid="wizardjsp"/>&wizard=config/email_notification/email_notifications&objectnumber=<mm:field name="number"/>' style="color:#000000; text-decoration:underline">
                     <mm:field name="subject"/>
                  </a>
                  &nbsp;
               </td>
               <td class="cell" style="border-top:0px">
                  <mm:relatednodes type="classes">
                     <nobr>
                        <di:translate key="assessment.email_notification_class" />
                        <mm:field name="name"/>
                     </nobr>
                  </mm:relatednodes>
                  <mm:relatednodes type="people">
                     <nobr>
                        <di:translate key="assessment.email_notification_person" />
                        <mm:field name="firstname"/>
                        <mm:field name="initials"/>
                        <mm:field name="suffix"/>
                        <mm:field name="lastname"/>
                     </nobr>
                  </mm:relatednodes>
                  &nbsp;
               </td>
               <td class="cell" style="border-top:0px">
                  <mm:field name="body"/>
                  &nbsp;
               </td>
               <td class="last_cell" style="border-top:0px">
                  <%
                     String sDate = null;
                  %>
                  <mm:import id="trigger" reset="true"><mm:field name="trigger_type"/></mm:import>


                  <mm:compare referid="trigger" value="0">
                     <mm:field name="senddate" jspvar="date" vartype="Date">
                        <%= sDate = df.format(date) %>
                     </mm:field>
                  </mm:compare>
                  <mm:compare referid="trigger" value="1">
                     <mm:field name="trigger_setting1" jspvar="date" vartype="Integer">
                        <di:translate key="assessment.email_notification_weeks_time" arg0="<%= "" + date.intValue() %>"/>
                     </mm:field>
                  </mm:compare>
                  <mm:compare referid="trigger" value="-1">
                     <di:translate key="assessment.email_notification_status_already_send" />
                  </mm:compare>
                  <mm:compare referid="trigger" value="-2">
                     <di:translate key="assessment.email_notification_status_disabled" />
                  </mm:compare>


               </td>
            </tr>
         </mm:listnodes>
      </td>
   </tr>
</table>

<br/>
<a href='<mm:write referid="wizardjsp"/>&wizard=config/email_notification/email_notifications&objectnumber=new'><img src="../gfx/new_email_notification.gif"/></a>

</body>


</mm:cloud>
