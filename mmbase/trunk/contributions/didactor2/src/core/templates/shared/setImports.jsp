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

<%-- get the $servername --%>
<mm:import id="servername"><%=pageContext.getRequest().getServerName() %></mm:import>

<mm:import externid="provider" />
<mm:isempty referid="provider">
  <mm:remove referid="provider"/>
</mm:isempty>

<%-- 
  Step 1. If there is only one provider, it is easy to figure it out and set it
--%>
<mm:notpresent referid="provider">
  <%-- calculate the provider --%>
  <mm:listnodescontainer type="providers">
    <mm:size id="provider_size" write="false" />
    <mm:compare referid="provider_size" value="1">
      <mm:listnodes>
        <mm:node id="provider" />
      </mm:listnodes>
    </mm:compare>

    <%-- 
      Step 2. More than 1 provider, but only 1 that has a related 'url' object
      for the current servername
    --%>
    <mm:compare referid="provider_size" value="1" inverse="true">
      <mm:listcontainer path="providers,urls" fields="urls.url,providers.number">
        <mm:constraint operator="equal" field="urls.url" referid="servername" />
        <mm:list>
          <mm:field id="provider" name="providers.number" write="false" />
        </mm:list>
      </mm:listcontainer>
    </mm:compare>
    <mm:remove referid="provider_size" />
  </mm:listnodescontainer>
</mm:notpresent>


<mm:import externid="education" />
<mm:isempty referid="education">
  <mm:remove referid="education" />
</mm:isempty>

<%-- 
  Step 3: Multiple providers, so also multiple educations. Maybe we can find both by 
  finding an URL that is related to an education which has the current hostname.
--%>
<mm:notpresent referid="provider">
  <mm:notpresent referid="education">
    <mm:listcontainer path="providers,educations,related,urls" fields="urls.url,providers.number,educations.number">
      <mm:constraint operator="equal" field="urls.url" referid="servername" />
      <mm:size id="nr_educations" write="false" />
      <mm:compare referid="nr_educations" value="1">
        <mm:list>
          <mm:field id="provider" name="providers.number" write="false" />
          <mm:field id="education" name="educations.number" write="false" />
        </mm:list>
      </mm:compare>
      <mm:remove referid="nr_educations" />
    </mm:listcontainer>
  </mm:notpresent>
</mm:notpresent>

<%--
  Step 4: found a provider, if there is only one education then we can find it
--%>
<mm:notpresent referid="education">
  <%-- if there is only 1 education for this provider, then we can figure it out --%>  
  <mm:present referid="provider">
  <mm:node number="$provider" notfound="skipbody">
    <mm:relatednodescontainer type="educations">
      <mm:size id="educations_size" write="false" />
      <mm:compare referid="educations_size" value="1">
        <mm:relatednodes>
          <mm:field id="education" write="false" name="number" />
        </mm:relatednodes>
      </mm:compare>
      <mm:remove referid="educations_size" />
    </mm:relatednodescontainer>
  </mm:node>
  </mm:present>
</mm:notpresent>

<%--
  Step 5: found a provider, if there is only one education with a matching URL then we can find it
--%>
<mm:present referid="provider">
  <mm:notpresent referid="education">
    <mm:listcontainer path="providers,educations,urls" fields="urls.url,providers.number,educations.number">
      <mm:constraint operator="equal" field="urls.url" referid="servername" />
      <mm:constraint operator="equal" field="providers.number" referid="provider" />
      <mm:size id="nr_educations" write="false" />
      <mm:compare referid="nr_educations" value="1">
        <mm:list>
          <mm:field id="education" name="educations.number" write="false" />
        </mm:list>
      </mm:compare>
      <mm:remove referid="nr_educations" />
    </mm:listcontainer>
  </mm:notpresent>
</mm:present>

<%--
  Step 6: Based on the student and the education, try to find the class.
--%>
<mm:import externid="class" />
<mm:isempty referid="class">
  <mm:isgreaterthan referid="user" value="0">
    <mm:present referid="education">
      <mm:listcontainer path="people,classes,educations" fields="people.number,educations.number,classes.number">
        <mm:constraint field="people.number" operator="EQUAL" referid="user" />
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

<mm:present referid="education">
  <mm:import id="includePath"><mm:write referid="provider" />,<mm:write referid="education" /></mm:import>
</mm:present>
<mm:notpresent referid="education">
  <mm:import id="includePath"><mm:write referid="provider" /></mm:import>
</mm:notpresent>
<mm:import id="referids">provider?,education?,class?,workgroup?</mm:import>
<%@ include file="globalLang.jsp" %>

<%--
  Step 7: call the 'validateUser' (which can be overwritten for a specific implementation)
  to make sure that this user may log in. 
--%>
<mm:import escape="trimmer" id="validatemessage">
  <mm:treeinclude page="/shared/validateUser.jsp" objectlist="$includePath" referids="$referids">
    <mm:param name="user"><mm:write referid="user" /></mm:param>
  </mm:treeinclude>
</mm:import>

<mm:isnotempty referid="validatemessage">
  <mm:cloud method="delegate" jspvar="cloud" authenticate="didactor-logout"/>
  <mm:redirect page="/declined.jsp">
    <mm:param name="referrer"><mm:treefile page="/index.jsp" objectlist="$includePath" referids="$referids" /></mm:param>
    <mm:param name="message"><mm:write referid="validatemessage" /></mm:param>
  </mm:redirect>
</mm:isnotempty>
