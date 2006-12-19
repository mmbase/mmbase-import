<%--
  Figure out the following variables:
  - current username (into variable $username)
  - current user id (into variable $user)
  - current server name (into varabiel $servername)
  - if 'provider' is not set: 
    - current provider (into variable $provider)
      - if there is more than 1 provider, look if there is an 'url' related to
         one of the providers with the $servername name.
  - if 'education' is not set:
    - current education: if there is only 1 education for the selected provider
    - if there is only one education with an 'url' related to it that matches
      the servername
  - template include path (into variable $includePath)
--%>
<mm:cloudinfo type="user" id="username" write="false" />

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


<jsp:directive.include file="findProvider.jspx" />

<%--
  Step 6: Based on the student and the education, try to find the class.
--%>
<mm:import externid="class" />
<mm:isempty referid="class">
  <mm:isgreaterthan referid="user" value="0">
    <mm:present referid="education">
      <mm:listcontainer path="people,classes,educations" fields="people.number,educations.number,classes.number">
        <mm:constraint field="people.number"     operator="EQUAL" referid="user" />
        <mm:constraint field="educations.number" operator="EQUAL" referid="education" />
          <mm:list>
            <mm:remove referid="class"/>
            <mm:field name="classes.number" id="class" write="false" />
          </mm:list>
        </mm:listcontainer>
      </mm:present>
  </mm:isgreaterthan>
</mm:isempty>

<mm:import externid="workgroup" />


<%--
  Step 7: call the 'validateUser' (which can be overwritten for a specific implementation)
  to make sure that this user may log in. 
--%>
<mm:import escape="trimmer" id="validatemessage">
  <mm:treeinclude page="/shared/validateUser.jsp" objectlist="$includePath" referids="$referids, user" />
</mm:import>

<mm:isnotempty referid="validatemessage">
  <mm:cloud method="delegate"  authenticate="didactor-logout"/>
  <% if (! response.isCommitted()) { %>
  <mm:redirect page="/declined.jsp" referids="validatemessage@message">
    <mm:param name="referrer"><mm:treefile page="/index.jsp" objectlist="$includePath" referids="$referids" /></mm:param>
  </mm:redirect>
  <% } %>

</mm:isnotempty>
