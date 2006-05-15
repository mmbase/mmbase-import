<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.didactor.nl/reports-taglib_1.0" prefix="rep" %>

<mm:cloud method="delegate" jspvar="cloud" >

<%-- get the $username --%>
<mm:import id="username" jspvar="username"><%=cloud.getUser().getIdentifier()%></mm:import>
<%-- get the $user --%>
<mm:listnodescontainer type="people">
  <mm:constraint operator="equal" field="username" referid="username" />
  <mm:listnodes>
    <mm:first>
        <mm:node>
            <mm:field id="user" name="number" write="false" />
        </mm:node>
    </mm:first>
  </mm:listnodes>
  <mm:notpresent referid="user">
    <mm:import id="user">0</mm:import>
  </mm:notpresent>
</mm:listnodescontainer>
<mm:present referid="user">
   <mm:node number="$user" notfound="skip">
      <mm:setfield name="islogged">0</mm:setfield>
<%
      Object loginTimeObj = session.getAttribute( username + "-login-time" );
      if( loginTimeObj != null )
      {
        long loginTime = ((Long)loginTimeObj).longValue();
        long duration = System.currentTimeMillis() - loginTime;
%>
        <rep:event eventtype="<%= nl.didactor.reports.data.EventType.LOGOUT + "" %>" eventvalue="<%= duration + "" %>" note="logout" />
<%
        session.removeAttribute( username + "-login-time" );
        session.removeAttribute( "session_listener" );
      }

      String educationId = request.getParameter("education") + "-" + username + "-" + session.getId();
      Object startReadingEducation = session.getAttribute( educationId );
      if( startReadingEducation != null )
      {
        long startReading = ((Long)startReadingEducation).longValue();
        long duration2 = System.currentTimeMillis() - startReading;
        String edId = educationId.substring( 0, educationId.indexOf( "-" ) );
%>
        <rep:event eventtype="<%= nl.didactor.reports.data.EventType.READING_EDUCATION + "" %>" educationId="<%= edId %>" eventvalue="<%= duration2 + "" %>" note="read education" />
<%
      }
      session.removeAttribute( "educationId" );	
      session.removeAttribute( educationId );	
%>      
   </mm:node>
</mm:present>
</mm:cloud>

<mm:cloud method="delegate" jspvar="cloud" authenticate="didactor-logout">

<%=cloud.getUser().getIdentifier()%>

</mm:cloud>

<mm:redirect page="/" />
