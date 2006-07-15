<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"   prefix="mm"
%><%@page language="java" contentType="text/html; charset=UTF-8" errorPage="error.jsp"
%><%@include file="import.jsp" %><%@include file="settings.jsp" %>
<mm:content postprocessor="reducespace" language="$language">

<mm:import id="url">index_contexts.jsp</mm:import>

<mm:import externid="offset">0</mm:import>
<mm:cloud loginpage="login.jsp"  rank="$rank">
<mm:import externid="context" vartype="list" />
<mm:import externid="search" />
<mm:import id="nodetype">mmbasecontexts</mm:import>
<mm:import id="fields" externid="context_fields">name,description,owner</mm:import>
<body>
<%@include file="you.div.jsp" %>
<mm:import id="current">contexts</mm:import>
<%@include file="navigate.div.jsp" %>

<p class="action">
  <mm:maycreate type="mmbasecontexts">
    <a href="<mm:url referids="parameters,$parameters"><mm:param name="url">create_context.jsp</mm:param></mm:url>"><img src="<mm:url page="${location}images/mmbase-new.gif" />" alt="+" tooltip="create context"  /></a>
  </mm:maycreate>
  <mm:maycreate type="mmbasecontexts" inverse="true">
      <%=getPrompt(m, "notallowedtocreatecontexts")%>
  </mm:maycreate>
</p>
<mm:notpresent referid="context">
  <%@include file="search.form.jsp" %>
  <table summary="Contexts">
    <mm:listnodescontainer id="cc" type="$nodetype">
      <%@include file="search.jsp" %>
      <tr>
        <mm:fieldlist nodetype="$nodetype"  fields="$fields">
          <th><mm:fieldinfo type="guiname" /></th>
        </mm:fieldlist>
        <th />
      </tr>
      <mm:listnodes id="currentcontext">
        <tr id="object<mm:field name="number" />" <mm:even>class="even"</mm:even> >
          <mm:fieldlist fields="$fields">
            <td><mm:fieldinfo type="guivalue" /></td>
          </mm:fieldlist>
          <td class="commands">
            <a onclick="document.getElementById('object<mm:field name="number" />').className = 'active'; " 
               href="<mm:url referids="parameters,$parameters,currentcontext@context,url" />"><img src="<mm:url page="${location}images/mmbase-edit.gif" />" alt="<%=getPrompt(m,"update")%>" title="<%=getPrompt(m,"update")%>" /></a>
            <mm:maydelete>
              <mm:field id="curcontext"  name="name" write="false" />
              <mm:listnodescontainer type="object">
                <mm:constraint field="owner" value="$curcontext" />
                <mm:size>
                  <mm:compare value="0">
                    <mm:import id="prompt">reallydeletecontexts</mm:import>
                    <a onclick="<%@include file="confirm.js" %>"
                       href="<mm:url referids="currentcontext@context,parameters,$parameters"><mm:param name="url">delete_context.jsp</mm:param></mm:url>"><img src="<mm:url page="${location}images/mmbase-delete.gif" />" alt="<%=getPrompt(m,"delete")%>" title="<%=getPrompt(m,"delete")%>" /></a>
                  </mm:compare>
                </mm:size>
              </mm:listnodescontainer>
            </mm:maydelete>
          </td>
        </tr>
      </mm:listnodes>

    </mm:listnodescontainer>
    </table>
   </mm:notpresent>
   <mm:present referid="context">
     <mm:stringlist referid="context">
      <mm:node id="currentcontext" number="$_">
          <%@include file="context.div.jsp" %>
      </mm:node>
     </mm:stringlist>
   </mm:present>

</mm:cloud>
</mm:content>

