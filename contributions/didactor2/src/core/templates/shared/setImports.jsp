<%--
  Figure out the following variables:
  - current username (into variable $username)
  - current user id (into variable $user)
  - current server name (into varabiel $servername)
  - current provider (into variable $provider)
    - if there is more than 1 provider, look if there is an 'url' related to
       one of the providers with the $servername name.
  - current education (if set, into variable $education)
  - template include path (into variable $includePath)
  TODO:
   - if provider/education/class is set: do not calculate again!
--%>

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

<%-- get the $servername --%>
<mm:import id="servername"><%=pageContext.getRequest().getServerName() %></mm:import>

<%-- get the $provider --%>
<mm:listnodescontainer type="providers">
  <mm:size id="provider_size" write="false" />
  <mm:compare referid="provider_size" value="1">
    <mm:listnodes>
      <mm:first>
        <mm:node>
          <mm:field id="provider" name="number" write="false" />
        </mm:node>
      </mm:first>
    </mm:listnodes>
  </mm:compare>
  <mm:compare referid="provider_size" value="1" inverse="true">
    <mm:listcontainer path="providers,urls" fields="urls.url,providers.number">
      <mm:constraint operator="equal" field="urls.url" referid="servername" />
      <mm:list>
        <mm:field id="provider" name="providers.number" write="false" />
      </mm:list>
    </mm:listcontainer>
  </mm:compare>

  <mm:notpresent referid="provider">
    <%-- see if we get a provider id from the request --%>
    <mm:import externid="provider"/>
    <%-- if no provider can be found, we will set it to 'providers', so that
     a leafinclude or treeinclude might still go well --%>
     <mm:notpresent referid="provider">
        <mm:import id="provider">providers</mm:import>
    </mm:notpresent>
  </mm:notpresent>

  <mm:import id="includePath"><mm:write referid="provider" /></mm:import>
  <mm:remove referid="provider_size" />
  <mm:import id="referids">provider?,education?,class?,workgroup?</mm:import>
  <mm:import externid="education" />
  <mm:import externid="class" />
  <mm:import externid="workgroup"/>
</mm:listnodescontainer>



<%// Global language code %>
<%
{
   String sDefaultLanguage = "nl";
   String[] sSupportedLanguages = {"en", "nl"};
   String sLangCode = request.getLocale().getLanguage();

   boolean bLangCodeIsCorrect = false;
   for(int f = 0; f < sSupportedLanguages.length; f ++)
   {
      if(sSupportedLanguages[f].equals(sLangCode))
      {
         bLangCodeIsCorrect = true;
         break;
      }
   }
   if(!bLangCodeIsCorrect)
   {
      sLangCode = sDefaultLanguage;
   }

   %>
      <mm:import id="lang_code"><%= sLangCode %></mm:import>
   <%
}
%>
