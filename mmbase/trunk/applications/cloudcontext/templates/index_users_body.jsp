<mm:import id="url">index_users.jsp</mm:import>

<mm:import externid="orderby"    from="parameters,session">username</mm:import>
<mm:import externid="directions" from="parameters,session">UP</mm:import>

<mm:write session="orderby"    referid="orderby" />
<mm:write session="directions" referid="directions" />

  <mm:import externid="search" />
  <mm:import id="nodetype"><%=org.mmbase.security.implementation.cloudcontext.Authenticate.getInstance().getUserProvider().getUserBuilder().getTableName()%></mm:import>
  <mm:import id="fields" externid="user_fields"><mm:write value="${mm:managerProperty(nodetype, 'security_editor_fields')}" write="true"><mm:isempty>username,defaultcontext,status,owner</mm:isempty></mm:write></mm:import>

  <%@include file="you.div.jsp" %>
  <mm:import id="current">users</mm:import>
  <%@include file="navigate.div.jsp" %>

  <p class="action">
    <mm:maycreate type="mmbaseusers">
      <a href="<mm:url referids="parameters,$parameters"><mm:param name="url">create_user.jsp</mm:param></mm:url>"><img src="<mm:url page="${location}images/mmbase-new.gif" />" alt="+" title="create user"  /></a>
    </mm:maycreate>
    <mm:maycreate type="mmbaseusers" inverse="true">
      <%=getPrompt(m, "notallowedtocreateusers")%>
    </mm:maycreate>
  </p>

  <%@include file="search.form.jsp" %>

  <table summary="Users">

    <mm:listnodescontainer type="$nodetype">

      <mm:import externid="offset">0</mm:import>

      <%@include file="search.jsp" %>

      <mm:offset value="$offset" />
      <mm:maxnumber value="10" />

      <tr>
        <th>
          <mm:present referid="extrauserlink">
            <mm:include  page="$extrauserlink" notfound="skip" />
          </mm:present>
        </th>
        <mm:fieldlist nodetype="$nodetype"  fields="$fields">
          <th>
            <a title="order" href='<mm:url referids="search,parameters,$parameters" ><mm:param name="orderby"><mm:fieldinfo type="name" /></mm:param>
              <mm:fieldinfo type="name">
                <mm:compare referid2="orderby">
                  <mm:write referid="directions">
                    <mm:compare value="UP">
                      <mm:param name="directions">DOWN</mm:param>
                    </mm:compare>
                    <mm:compare value="DOWN">
                      <mm:param name="directions">UP</mm:param>
                    </mm:compare>
                  </mm:write>
                </mm:compare>
              </mm:fieldinfo>
              <mm:fieldlist  nodetype="mmbaseusers" fields="$fields"><mm:fieldinfo type="reusesearchinput" /></mm:fieldlist>
            </mm:url>' ><mm:fieldinfo type="guiname" /></a>
          </th>
        </mm:fieldlist>
        <th><%=getPrompt(m,"rank")%></th>
        <th><a title="order" href='<mm:url referids="search,parameters,$parameters"><mm:param name="orderby">number</mm:param>
          <mm:compare referid="orderby" value="number">
            <mm:write referid="directions">
              <mm:compare value="UP">
                <mm:param name="directions">DOWN</mm:param>
              </mm:compare>
              <mm:compare value="DOWN">
                <mm:param name="directions">UP</mm:param>
              </mm:compare>
            </mm:write>
          </mm:compare>
        </mm:url>'>
        *</a>
        <%@include file="pager.jsp" %></th>
      </tr>
      <mm:sortorder field="$orderby" direction="$directions" />
      <mm:listnodes id="user">
      <tr id="object<mm:field name="number"/>" <mm:even>class="even"</mm:even> >
        <td>
          <mm:present referid="extrauserlink">
            <mm:include referids="user" page="$extrauserlink" notfound="skip"/>
          </mm:present>
        </td>
        <mm:fieldlist fields="$fields">
          <td><mm:fieldinfo type="guivalue" /></td>
        </mm:fieldlist>
        <td>
          <mm:function name="rank" />
        </td>
        <td class="commands">
          <mm:maywrite>
            <a onclick="document.getElementById('object<mm:field name="number" />').className = 'active'; "
            href="<mm:url referids="user,parameters,$parameters"><mm:param name="url">edit_user.jsp</mm:param></mm:url>"><img src="<mm:url page="${location}images/mmbase-edit.gif" />" alt="<%=getPrompt(m,"update")%>" title="<%=getPrompt(m,"update")%>" /></a>
          </mm:maywrite>
          <mm:function name="rank" >
            <mm:compare value='<%="" + org.mmbase.security.Rank.ADMIN.getInt()%>' inverse="true">
              <mm:maydelete>
                <mm:import id="prompt">reallydeleteusers</mm:import>
                <a onclick="<%@include file="confirm.js" %>" href="<mm:url referids="user@deleteuser,parameters,$parameters"><mm:param name="url">delete_user.jsp</mm:param></mm:url>"><img src="<mm:url page="${location}images/mmbase-delete.gif" />" alt="<%=getPrompt(m,"delete")%>" title="<%=getPrompt(m,"delete")%>" /></a>
              </mm:maydelete>
            </mm:compare>
          </mm:function>
        </td>
      </tr>
      </mm:listnodes>
    </mm:listnodescontainer>
  </table>
