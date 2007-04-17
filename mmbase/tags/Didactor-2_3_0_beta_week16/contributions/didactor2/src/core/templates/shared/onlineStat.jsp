
<mm:field id="classrelNumber" name="classrel.number" jspvar="lastClassRel" write="false"/>
<mm:cloudinfo type="user" id="username2" write="false" />
<mm:import jspvar="username" externid="username2" from="this" />
<mm:node referid="classrelNumber">
  <mm:write referid="oldLastActivity" jspvar="oldLastActivity" vartype="Integer">
    <mm:field name="onlinetime" jspvar="onlinetime" vartype="Integer" write="false">
      <mm:import id="newOnlineTime" jspvar="newOnlineTime" vartype="Integer"><%=onlinetime.intValue()+Math.min(120,(System.currentTimeMillis()/1000-oldLastActivity.intValue()))%></mm:import>
      <mm:log>Setting onlineTime  ${newOnlineTime}</mm:log>

      <%
      Object oldEduObject = session.getAttribute("educationId");
      String oldEducationId = null;
      String education = request.getParameter("education");
      
      String educationId = education != null ? education + "-" + username + "-" + session.getId() : null;
      
      session.setAttribute("educationId", educationId);
      if (oldEduObject != null) {
         oldEducationId = oldEduObject.toString();
      }
      if (educationId !=null && !educationId.equals(oldEducationId))
      {
      %>
      <mm:field name="logincount" jspvar="logincount" vartype="Integer" write="false">
        <mm:log>Increasing logincount of ${username} to <%=logincount + 1%> for session <%=educationId%></mm:log>
      
        <mm:setfield name="logincount"><%=logincount.intValue()+1%></mm:setfield>
      </mm:field>
      <%
      session.setAttribute( educationId, new Long( System.currentTimeMillis() ) );
      Object oldEd = session.getAttribute( oldEducationId );
      if( oldEd != null )
      {
      long startReading = ((Long)oldEd).longValue();
      Long duration = new Long(System.currentTimeMillis() - startReading);
      String edId = oldEducationId.substring( 0, oldEducationId.indexOf( "-" ) );
      %>
      <di:event eventtype="reading_education" educationId="<%= edId %>" eventvalue="<%= duration.toString() %>" note="read education" />
      <%
      session.removeAttribute( oldEducationId );
      }
      }
      %>
    </mm:field>
  </mm:write>
  <mm:setfield name="onlinetime"><mm:write referid="newOnlineTime"/></mm:setfield>
</mm:node>
