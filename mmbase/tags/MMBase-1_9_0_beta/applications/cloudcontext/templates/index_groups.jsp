<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"   prefix="mm"
%><%@page language="java" contentType="text/html; charset=UTF-8"
%><%@include file="import.jsp" %>
<%@include file="settings.jsp" %>
<mm:content language="$language"  postprocessor="reducespace">
<mm:import id="url">index_groups.jsp</mm:import>

<mm:import id="orderby"    externid="orderby_gr"    from="parameters,session">name</mm:import>
<mm:import id="directions" externid="directions_gr" from="parameters,session">UP</mm:import>

<mm:write session="orderby_gr"    referid="orderby" />
<mm:write session="directions_gr" referid="directions" />

<mm:import externid="offset">0</mm:import>
<mm:cloud loginpage="login.jsp" rank="$rank">
<mm:import externid="group" vartype="list" />
<mm:import id="nodetype">mmbasegroups</mm:import>
<mm:import id="fields" externid="group_fields">name,description,owner</mm:import>

<mm:import externid="search" />
<mm:import id="current">groups</mm:import>
<%@include file="you.div.jsp" %>
<%@include file="navigate.div.jsp" %>


<p class="action">
  <mm:maycreate type="mmbasegroups">
    <a href="<mm:url referids="parameters,$parameters"><mm:param name="url">create_group.jsp</mm:param></mm:url>"><img src="<mm:url page="${location}images/mmbase-new.gif" />" alt="+" tooltip="create group"  /></a>
  </mm:maycreate>
  <mm:maycreate type="mmbasegroups" inverse="true">
    <%=getPrompt(m, "notallowedtocreategroups")%>
  </mm:maycreate>
</p>

<mm:notpresent referid="group">
  <%@include file="search.form.jsp" %>

  <table summary="Groups">
    <mm:listnodescontainer id="cc" type="$nodetype">
      <%@include file="search.jsp" %>

      <tr>
        <mm:fieldlist nodetype="$nodetype"  fields="$fields">
          <th>
            <a title="order" href='<mm:url referids="url,search,parameters,$parameters" ><mm:param name="orderby_gr"><mm:fieldinfo type="name" /></mm:param>
              <mm:fieldinfo type="name">
                <mm:compare referid2="orderby">
                  <mm:write referid="directions">
                    <mm:compare value="UP">
                      <mm:param name="directions_gr">DOWN</mm:param>
                    </mm:compare>
                    <mm:compare value="DOWN">
                      <mm:param name="directions_gr">UP</mm:param>
                    </mm:compare>
                  </mm:write>
                </mm:compare>
              </mm:fieldinfo>
              <mm:fieldlist  nodetype="$nodetype" fields="$fields"><mm:fieldinfo type="reusesearchinput" /></mm:fieldlist>
            </mm:url>' ><mm:fieldinfo type="guiname" /></a>
          </th>
        </mm:fieldlist>
        <th>
          <a title="order" href='<mm:url referids="url,search,parameters,$parameters"><mm:param name="orderby_gr">number</mm:param>
            <mm:compare referid="orderby" value="number">
              <mm:write referid="directions">
                <mm:compare value="UP">
                  <mm:param name="directions_gr">DOWN</mm:param>
                </mm:compare>
                <mm:compare value="DOWN">
                  <mm:param name="directions_gr">UP</mm:param>
                </mm:compare>
              </mm:write>
            </mm:compare>
          </mm:url>'>
          *</a>
        <th />
      </tr>
      <mm:sortorder field="$orderby" direction="$directions" />

      <mm:listnodes id="currentgroup">
        <tr id="object<mm:field name="number" />" <mm:even>class="even"</mm:even> >
        <mm:fieldlist fields="$fields">
          <td><mm:fieldinfo type="guivalue" /></td>
        </mm:fieldlist>
        <td class="commands">
          <a onclick="document.getElementById('object<mm:field name="number" />').className = 'active'; "
             href="<mm:url referids="currentgroup@group,parameters,$parameters,url" />"><img src="<mm:url page="${location}images/mmbase-edit.gif" />" alt="<%=getPrompt(m,"update")%>" title="<%=getPrompt(m,"update")%>" /></a>
          <mm:maydelete>
            <mm:relatednodescontainer role="contains" searchdirs="destination">
              <mm:size>
                <mm:compare value="0">
                  <mm:import id="prompt">reallydeletegroups</mm:import>
                  <a onclick="<%@include file="confirm.js" %>"
                  href="<mm:url referids="currentgroup@group,parameters,$parameters"><mm:param name="url">delete_group.jsp</mm:param></mm:url>"><img src="<mm:url page="${location}images/mmbase-delete.gif" />" alt="<%=getPrompt(m,"delete")%>" title="<%=getPrompt(m,"delete")%>" /></a>
                </mm:compare>
              </mm:size>
            </mm:relatednodescontainer>
          </mm:maydelete>
        </td>
      </tr>
    </mm:listnodes>

  </mm:listnodescontainer>
</table>
</mm:notpresent>
<mm:present referid="group">
  <mm:stringlist referid="group">
    <mm:node id="currentgroup"  number="$_">
      <%@include file="group.div.jsp" %>
    </mm:node>
  </mm:stringlist>
</mm:present>

</mm:cloud>
</mm:content>
