<mm:context id="temp">
  <mm:node number="$user" notfound="skip">
    <mm:field id="username" name="username" write="false" />

    <!-- check whether a poster object was related to this user already -->
    <mm:relatednodescontainer path="posters,forums" element="posters">
      <mm:constraint field="forums.number" value="${forumid}" />
      <mm:relatednodes>
        <mm:node id="haveposter" />
      </mm:relatednodes>
    </mm:relatednodescontainer>

    <!-- check with mmbob too -->

    <mm:functioncontainer set="mmbob" name="createPoster">
      <mm:param name="password">blank</mm:param>
      <mm:param name="confirmpassword">blank</mm:param>
      <mm:param name="firstname"><mm:field name="firstname" write="true"><mm:isempty>Anonymous</mm:isempty></mm:field></mm:param>
      <mm:param name="lastname"><mm:field name="suffix" /> <mm:field name="lastname" /></mm:param>
      <mm:param name="email"><mm:field name="email" write="true"><mm:isempty><mm:field name="username" />@<di:getsetting component="email" setting="emaildomain" write="true"><mm:compare regexp=".*\..*" inverse="true">.org</mm:compare></di:getsetting></mm:isempty></mm:field></mm:param>
      <mm:param name="location"><mm:field name="city" /></mm:param>
      <mm:param name="gender">male</mm:param><!-- ? no females in didactor? -->
      <mm:function referids="forumid,username@account">
        <mm:compare inverse="true" regexp="ok|inuse">
          Error: <mm:write />
          <mm:log>Error <mm:write /></mm:log>
        </mm:compare>
      </mm:function>
    </mm:functioncontainer>

    <!--
         now, find the poster object according to mmbob. It must exist now, because createPoster was
         just called
    -->
    <mm:node number="${forumid}">
      <mm:relatednodescontainer type="posters">
        <mm:constraint field="account" value="${username}" />
        <mm:relatednodes>
          <mm:node id="mmbobposter" />
        </mm:relatednodes>
      </mm:relatednodescontainer>
    </mm:node>

    <%--
    <mm:log>found poster: ${haveposter.number} mmbob thinks: ${mmbobposter.number}</mm:log>
    --%>
    <!-- create reletion with user node if not yet existed -->
    <c:if test="${empty haveposter || haveposter.number ne mmbobposter.number}">
      <mm:createrelation role="related" source="user" destination="mmbobposter" />
    </c:if>
  </mm:node>
</mm:context>

<mm:import id="posterid">${temp.mmbobposter.number}</mm:import>
<mm:remove referid="temp" />


<mm:import externid="lang" />
<mm:node number="$forumid">
  <mm:present referid="lang" inverse="true">
    <mm:remove referid="lang" />
    <mm:import id="lang"><mm:field name="language" /></mm:import>
  </mm:present>
</mm:node>
