<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content expires="0">
<% response.setContentType("text/xml"); %>
<%@page import = "java.lang.Integer" %>
<%@page import = "java.lang.Long" %>
<%@page import = "java.lang.String" %>
<%@page import = "java.util.Random" %>
<%@page import = "java.util.HashMap" %>
<%@page import = "java.util.ArrayList" %>
<%@page import = "java.util.Collection" %>
<%@page import = "java.util.Iterator" %>
<mm:cloud method="logout">
<mm:import externid="username" required="true" jspvar="username"/>
<mm:import externid="password" required="true" jspvar="password"/>
<mm:import externid="templatename" required="true" jspvar="templatename"/>

<mm:cloud username="$username" password="$password" jspvar="cloud" method="pagelogon">
<template>
<% 
  HashMap emailUsers1 = new HashMap();
  ArrayList removeUsers = new ArrayList();
  Long evtypenumber = new Long(0);
  long currenttime = System.currentTimeMillis()/1000;
  long onemonthssec = 30*24*60*60;
  Long firstcondition = new Long(currenttime-onemonthssec);
  Long lastcondition = new Long(currenttime-onemonthssec);
  
  try { 
%>
    <mm:listnodescontainer type="proactivemailbatches">
      <mm:constraint operator="LIKE" field="name" referid="templatename" />
      <mm:listnodes>
        <mm:last>
        <mm:import id="lastsent" jspvar="jsp_lastsent" vartype="Long"><mm:field name="end_time" /></mm:import>
          <% lastcondition = new Long(jsp_lastsent.longValue()-onemonthssec); %>
        </mm:last>
      </mm:listnodes>
    </mm:listnodescontainer>
    <mm:listnodescontainer type="eventtypes">
      <mm:constraint operator="LIKE" field="name" value="firstuserlogin" />
      <mm:listnodes>
        <mm:last>
          <mm:field name="number" jspvar="jsp_evtypenumber" vartype="Long">
            <% evtypenumber = jsp_evtypenumber; %>
          </mm:field>
        </mm:last>
      </mm:listnodes>
    </mm:listnodescontainer>
    <mm:import id="param1"><%=evtypenumber.toString()%></mm:import>
    <mm:import id="param2"><%=firstcondition.toString()%></mm:import>
    <mm:import id="param3"><%=lastcondition.toString()%></mm:import>
    <mm:listnodes type="people">
      <mm:relatedcontainer path="eventdatarel,eventdata">
        <mm:constraint operator="EQUAL" field="eventdatarel.eventid" referid="param1" />
        <mm:constraint operator="LESS" field="eventdata.timestamp" referid="param2" />
        <mm:constraint operator="GREATER" field="eventdata.timestamp" referid="param3" />
        <mm:related>
          <mm:last>
            <mm:field name="eventdata.stringvalue" jspvar="jsp_username">
              <% if (jsp_username != null && "admin".compareTo(jsp_username) == 0 ) { %>
                <mm:import id="adminfound"/>
              <% } %>
            </mm:field>
            <mm:notpresent referid="adminfound">
              <mm:field name="eventdatarel.snumber" jspvar="jsp_userNumber" vartype="Integer">
                <%
                try {
                  emailUsers1.put( jsp_userNumber, jsp_userNumber);
                } catch (Exception e){}
                %>
              </mm:field>
            </mm:notpresent>
          </mm:last>
        </mm:related>
      </mm:relatedcontainer>
    </mm:listnodes>
<% if ( emailUsers1.size() > 0 ) { %>
  <%@include file="users.jsp" %>
  <from><%=jsp_from%></from>
  <subject><%=jsp_subject%></subject>
  <body><%=jsp_body%></body>
  <users>
      <%
      Iterator it3 = emailUsers1.values().iterator();
      while ( it3.hasNext() ) {
        Integer i = (Integer)it3.next();
        if ( emailUsers.containsKey(i) ) {
            Object[] o = (Object[])emailUsers.get(i);
       %>
    <user>
      <firstname><%=(String)o[1]%></firstname>
      <lastname><%=(String)o[2]%></lastname>
      <username><%=(String)o[3]%></username>
      <email><%=(String)o[4]%></email>
      <lastactivity><%=(String)o[5]%></lastactivity>
    </user>
          <%                
        }
      }
      %>
  </users>
<%}%>

<%
    } catch (Exception ex) {
%>
        <error>
          <% ex.toString(); %>
        </error>
<%
    }
%>
</template>            
</mm:cloud>    
</mm:cloud> 
</mm:content>
