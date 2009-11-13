<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"   prefix="mm"
%><%@include file="import.jsp" %><%@include file="settings.jsp"
%>
<mm:content language="$language" expires="0">
<mm:import externid="user" required="true" />
<mm:cloud loginpage="login.jsp" rank="$rank">

<mm:compare referid="user" value="new">
  <mm:remove referid="user" />
  <mm:import id="wasnew" />
  <mm:createnode id="user" type="mmbaseusers" commitonclose="false" />
</mm:compare>

<mm:import id="current">users</mm:import>
<%@include file="navigate.div.jsp" %>
<%@include file="you.div.jsp" %>
  <mm:form>
    <mm:node referid="user">
      <mm:fieldlist type="edit" fields="owner">
        <mm:fieldinfo type="errors">
          <mm:compare inverse="true" regexp=".*\> \<\/.*">
            <mm:fieldinfo type="guiname" />: <mm:write escape="none" />
          </mm:compare>
        </mm:fieldinfo>
      </mm:fieldlist>
      <mm:valid>
        <mm:import id="valid" />
      </mm:valid>
    </mm:node>
    <mm:commit />
  </mm:form>

  <mm:node id="user" referid="user">

    <mm:context>
  <mm:cloudinfo type="user" write="false" id="clouduser" />

  <%@include file="commitGroupOrUserRights.jsp" %>
    </mm:context>

<mm:present referid="valid">
  <h1><mm:function name="gui" /> (<%=getPrompt(m, "commited")%>)</h1>
</mm:present>
<mm:notpresent referid="valid">
  <h1><mm:function name="gui" /> (<%=getPrompt(m, "notvalid")%>)</h1>
</mm:notpresent>

<%@include file="edit_user.form.jsp" %>
</mm:node>

</mm:cloud>
</mm:content>
