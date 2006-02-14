<%@page session="true" language="java" contentType="text/html; charset=UTF-8" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<mm:content postprocessor="reducespace">
<mm:cloud jspvar="cloud" method="delegate" authenticate="asis">
  <%@include file="/shared/setImports.jsp" %>
  <mm:treeinclude page="/cockpit/cockpit_intro_header.jsp" objectlist="$includePath" referids="$referids">
   <mm:param name="extraheader">
     <style>.columns {height: 100%;}</style>
     <link rel="stylesheet" type="text/css" href='<mm:treefile page="/register/css/register.css" objectlist="$includePath" referids="$referids" />' />
   </mm:param>
  </mm:treeinclude>

  <mm:import externid="firstname" jspvar="firstname" />
  <mm:import externid="lastname" jspvar="lastname" />
  <mm:import externid="address" />
  <mm:import externid="zipcode" />
  <mm:import externid="city" />
  <mm:import externid="email" jspvar="email"/>
  <mm:import externid="country" />
  <mm:import externid="formsubmit">false</mm:import>

  <mm:import id="error" />

  <mm:compare referid="formsubmit" value="true">
    <mm:isempty referid="firstname">
      <mm:import id="error" reset="true"><mm:write referid="error" escape="none"/><li>Verplicht veld 'Voornaam' is niet gevuld!</li></mm:import>
    </mm:isempty>
    <mm:isempty referid="lastname">
      <mm:import id="error" reset="true"><mm:write referid="error" escape="none"/><li>Verplicht veld 'Achternaam' is niet gevuld!</li></mm:import>
    </mm:isempty>
    <mm:isempty referid="address">
      <mm:import id="error" reset="true"><mm:write referid="error" escape="none"/><li>Verplicht veld 'Adres' is niet gevuld!</li></mm:import>
    </mm:isempty>
    <mm:isempty referid="zipcode">
      <mm:import id="error" reset="true"><mm:write referid="error" escape="none"/><li>Verplicht veld 'Postcode' is niet gevuld!</li></mm:import>
    </mm:isempty>
    <mm:isempty referid="city">
      <mm:import id="error" reset="true"><mm:write referid="error" escape="none"/><li>Verplicht veld 'Plaats' is niet gevuld!</li></mm:import>
    </mm:isempty>
    <mm:isempty referid="email">
      <mm:import id="error" reset="true"><mm:write referid="error" escape="none"/><li>Verplicht veld 'Email adres' is niet gevuld!</li></mm:import>
    </mm:isempty>
    <mm:isempty referid="country">
      <mm:import id="error" reset="true"><mm:write referid="error" escape="none"/><li>Verplicht veld 'Land' is niet gevuld!</li></mm:import>
    </mm:isempty>
    <mm:isnotempty referid="email">
      <% if (email != null && !email.matches("(.*)@(.*)\\.(.*)")) { %>
        <mm:import id="error" reset="true"><mm:write referid="error" escape="none"/><li>Emailadres is niet in de goede vorm!</li></mm:import>
      <% } %>
    </mm:isnotempty>

    <mm:isnotempty referid="error">
      <mm:import id="formsubmit" reset="true">false</mm:import>
    </mm:isnotempty>
  </mm:compare>

  <mm:compare referid="formsubmit" value="true">
      <%
        // Generate a 8-character base username, consisting of the first
   // character of the firstname, and the entire lastname. Strip out
   // all non-letter characters, and append a number if the account
   // already exists.

        String uname = firstname.substring(0, 1) + lastname;
   uname = uname.replaceAll(" ", "").toLowerCase().replaceAll("[^a-z]", "");
   if (uname.length() > 8) {
     uname = uname.substring(0, 8);
   }
   boolean founduser = false;
   String constraint = "";
   for (int i=-1; i<100 && !founduser; i++) {
     constraint = uname;
     if (i >= 0) {
       constraint += i;
     }
     %>
       <mm:listnodescontainer type="people">
         <mm:constraint field="username" operator="EQUAL" value="<%=constraint%>" />
         <mm:size write="false" id="peoplecount" />
         <mm:compare referid="peoplecount" value="0">
            <%
               founduser = true;
            %>
         </mm:compare>
         <mm:remove referid="peoplecount" />
       </mm:listnodescontainer>
          <%
   }

        // Generate a random 6-digit password
        char[] dict = new char[] {
     'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
     'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
     'y', 'z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<6; i++) {
          sb.append(dict[(int)(Math.random() * dict.length)]);
        }
        String password = sb.toString();
      %>

      <%-- Now create the person, and relate him to the education --%>
      <mm:createnode type="people" id="person">
         <mm:setfield name="username"><%=constraint%></mm:setfield>
         <mm:setfield name="password"><%=password%></mm:setfield>
         <mm:setfield name="firstname"><mm:write referid="firstname" /></mm:setfield>
         <mm:setfield name="lastname"><mm:write referid="lastname" /></mm:setfield>
         <mm:setfield name="address"><mm:write referid="address" /></mm:setfield>
         <mm:setfield name="zipcode"><mm:write referid="zipcode" /></mm:setfield>
         <mm:setfield name="city"><mm:write referid="city" /></mm:setfield>
         <mm:setfield name="country"><mm:write referid="country" /></mm:setfield>
         <mm:setfield name="email"><mm:write referid="email" /></mm:setfield>
      </mm:createnode>


      <mm:import id="edu" externid="education" />

      <mm:present referid="edu">
         <mm:remove referid="edu" />
         <mm:node number="$education" id="edu" />
         <mm:createrelation role="classrel" source="edu" destination="person" />
         <mm:remove referid="edu" />
      </mm:present>

      <mm:remove referid="person" />


      <mm:treeinclude page="/register/register_done.jsp" objectlist="$includePath" referids="$referids">
        <mm:param name="uname"><%=constraint%></mm:param>
        <mm:param name="password"><%=password%></mm:param>
     </mm:treeinclude>
  </mm:compare>

  <mm:compare referid="formsubmit" value="true" inverse="true">
    <mm:treeinclude page="/register/register_form.jsp" objectlist="$includePath" referids="$referids">
      <mm:param name="error"><mm:write referid="error" escape="none" /></mm:param>
    </mm:treeinclude>
  </mm:compare>
</mm:cloud>
</mm:content>
