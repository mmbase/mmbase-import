<%@page session="true" language="java" contentType="text/html; charset=UTF-8"
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" 
%>
<mm:cloud  authenticate="asis">
  <jsp:directive.include file="/shared/setImports.jsp" />
  <mm:content postprocessor="reducespace" language="$language" expires="0">
  <mm:treeinclude page="/cockpit/cockpit_intro_header.jsp" objectlist="$includePath" referids="$referids">
   <mm:param name="extraheader">
     <style>.columns {height: 100%;}</style>
     <link rel="stylesheet" type="text/css" href="${mm:treefile('/register/css/register.css', pageContext,  includePath)}"  />
   </mm:param>
  </mm:treeinclude>

  <mm:import externid="firstname" jspvar="firstname" />
  <mm:import externid="lastname" jspvar="lastname" />
  <mm:import externid="suffix" />
  <mm:import externid="address" />
  <mm:import externid="zipcode" />
  <mm:import externid="city" />
  <mm:import externid="email" jspvar="email"/>
  <mm:import externid="country" />
  <mm:import externid="remarks" />
  <mm:import externid="formsubmit">false</mm:import>

  <mm:import id="error" />


  <mm:compare referid="formsubmit" value="true">
    <mm:fieldlist nodetype="people" fields="firstname,lastname,address,zipcode,city,email,country">
      <mm:fieldinfo type="name" id="name">
        <mm:isempty referid="${_}">
          <mm:import id="error" reset="true" escape="trimmer">
            <mm:write referid="error" escape="none"/>	    
            <mm:fieldinfo type="guiname">
              <li title="${name}"><di:translate key="register.mandatory_missing" arg0="${_}" /></li>
            </mm:fieldinfo>
          </mm:import>
        </mm:isempty>
      </mm:fieldinfo>
    </mm:fieldlist>
    <mm:isnotempty referid="email">
      <% if (email != null && !email.matches("(.*)@(.*)\\.(.*)")) { %>
        <mm:import id="error" reset="true"><mm:write referid="error" escape="none"/><li><di:translate key="register.email_not_well_formed" /></li></mm:import>
      <% } %>
    </mm:isnotempty>

    <mm:isnotempty referid="error">
      <mm:import id="formsubmit" reset="true">false</mm:import>
    </mm:isnotempty>
  </mm:compare>

  <mm:compare referid="formsubmit" value="true">
    <mm:import id="template">CCCCCC</mm:import>
    <%-- Now create the person, and relate him to the education --%>
    <mm:createnode type="people" id="person">
      <mm:setfield name="password"><mm:function id="password" set="utils" name="generatePassword" referids="template" /></mm:setfield>
      <mm:setfield name="firstname"><mm:write referid="firstname" /></mm:setfield>
      <mm:setfield name="lastname"><mm:write referid="lastname" /></mm:setfield>
      <mm:setfield name="username"><mm:function name="generateUserName" /></mm:setfield>
      <mm:setfield name="suffix"><mm:write referid="suffix" /></mm:setfield>
      <mm:setfield name="address"><mm:write referid="address" /></mm:setfield>
      <mm:setfield name="zipcode"><mm:write referid="zipcode" /></mm:setfield>
      <mm:setfield name="city"><mm:write referid="city" /></mm:setfield>
      <mm:setfield name="country"><mm:write referid="country" /></mm:setfield>
      <mm:setfield name="email"><mm:write referid="email" /></mm:setfield>
      <mm:setfield name="remarks"><mm:write referid="remarks" /></mm:setfield>
    </mm:createnode>
    
    <mm:createrelation role="related" source="education" destination="person" />
    <mm:log>Created person ${person}, for education ${education}</mm:log>
    
    
    <mm:treeinclude page="/register/register_done.jsp" objectlist="$includePath" referids="$referids">
      <mm:param name="uname">${person.username}</mm:param>
      <mm:param name="password">${password}</mm:param>
    </mm:treeinclude>
  </mm:compare>
  
  <mm:compare referid="formsubmit" value="true" inverse="true">
    <mm:treeinclude page="/register/register_form.jsp" objectlist="$includePath" referids="$referids">
      <mm:param name="error"><mm:write referid="error" escape="none" /></mm:param>
    </mm:treeinclude>
  </mm:compare>
</mm:content>
</mm:cloud>

