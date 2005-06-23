<%--
   This is the list of users with role student, as used in the big (main) cockpit.
   It lists all users from the classes of which the current user is a member.
--%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
  <%@include file="/shared/setImports.jsp" %>
<%@page import ="java.util.*" %>
<%  //creates a HashTable (unique list) to put in all the user. 
  Hashtable htUserList = new Hashtable();
%>
  <mm:node referid="user">
    <mm:relatednodes type="classes">
      <%-- get associated education, assign to edu --%>
      <mm:remove referid="edu"/>
      <mm:import id="edu">0</mm:import>
      <mm:relatednodes type="educations">
        <mm:remove referid="edu"/>
        <mm:field id="edu" name="number" write="false"/>
      </mm:relatednodes>
       
      <mm:relatednodes type="people">
        <mm:import id="personno"><mm:field name="number"/></mm:import>
        <mm:compare referid="user" referid2="personno" inverse="true">
        <%-- check if the current person has the role student --%>
        <di:hasrole referid="personno" role="student" education="edu">
          <mm:remove referid="itemno"/>
          <mm:import id="itemno" jspvar="itemno"><mm:field name="number" /></mm:import>
          <%
            // number has been saved and can be put in the HashTable
            htUserList.put(itemno, itemno);
          %>
        </di:hasrole>
        </mm:compare>
        <mm:remove referid="personno"/>
      </mm:relatednodes>
    </mm:relatednodes>
  </mm:node>
  <%
    //implements the Enumeration in the userlist, so iteration through the list can be started
    Enumeration eUserList = htUserList.elements();
    while (eUserList.hasMoreElements() )
    {
      String nodeNo = eUserList.nextElement().toString();// get current object
    %>
    <mm:node number="<%=nodeNo%>">
      <a href="<mm:treefile page="/portfolio/index.jsp" objectlist="$includePath" referids="$referids">
         <mm:param name="contact"><mm:field name="number"/></mm:param>
       </mm:treefile>" class="users">
      <%-- Online/offline status is retrieved using the nl.didactor.builders.PeopleBuilder class  --%>
            <mm:field name="isonline" id="isonline" write="false" />
      <mm:compare referid="isonline" value="0">
        <img src="<mm:treefile write="true" page="/gfx/icon_offline.gif" objectlist="$includePath" />" width="6" height="12" border="0" alt="offline" />
      </mm:compare>
      <mm:compare referid="isonline" value="1">
        <img src="<mm:treefile write="true" page="/gfx/icon_online.gif" objectlist="$includePath" />" width="6" height="12" border="0" alt="online" />
      </mm:compare>
      <mm:remove referid="isonline" />
      <mm:field name="firstname" /> <mm:field name="lastname" /></a><br />
    </mm:node>
    <%
    }
  %>
</mm:cloud>
</mm:content>
