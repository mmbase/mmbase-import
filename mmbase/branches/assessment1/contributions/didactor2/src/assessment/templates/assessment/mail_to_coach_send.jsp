<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:cloud method="delegate" jspvar="cloud">

<%@include file="/shared/setImports.jsp" %>

<mm:remove referid="provider"/>
<mm:remove referid="education"/>
<mm:remove referid="class"/>

<mm:import externid="provider" />
<mm:import externid="education" />
<mm:import externid="class" />
<mm:import externid="message" />
<mm:import externid="list_of_coaches" />


<head>
   <link rel="stylesheet" type="text/css" href="<mm:treefile page="/css/base.css" objectlist="$includePath" referids="$referids" />" />
   <link rel="stylesheet" type="text/css" href="css/assessment.css" />
</head>
<mm:node number="$user">
   <mm:import id="from"><mm:field name="email"/></mm:import>
</mm:node>
<mm:import id="subject"><di:translate key="assessment.mail_to_coach_letter___subject" /></mm:import>
<mm:import id="body"><mm:write referid="message" /></mm:import>



<mm:list nodes="$list_of_coaches" path="people">
   <mm:import id="to" reset="true"><mm:field name="people.email"/></mm:import>
   <mm:field name="people.firstname"/> <mm:field name="people.lastname"/> &lt;<mm:write referid="to"/>&gt;
   <br/>
   <%@ include file="includes/sendmail.jsp" %>
</mm:list>

<di:translate key="assessment.mail_to_coach_sent___done_message" />

</mm:cloud>
