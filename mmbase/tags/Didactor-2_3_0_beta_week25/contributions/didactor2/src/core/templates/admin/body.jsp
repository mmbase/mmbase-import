<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%-- expires is 0; show always new content --%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<mm:import externid="action"/>
<mm:import externid="back"/>
<mm:import externid="referer" jspvar="referer"><%= request.getHeader("referer") %></mm:import>

<mm:present referid="back">
  <% response.sendRedirect(referer); %>
</mm:present>

<mm:present referid="action">
  <mm:import id="actiontext"><di:translate key="core.save" /></mm:import>
  <mm:compare referid="action" referid2="actiontext">
    <mm:node referid="user">
      <mm:fieldlist field="initials,firstname,lastname,username,email,address,zipcode,city,telephone,dayofbirth">
        <mm:fieldinfo type="useinput" />
      </mm:fieldlist>
      <mm:treeinclude page="/admin/handle_settings.jsp" objectlist="$includePath" referids="$referids" />
    </mm:node>
  </mm:compare>
</mm:present>

    <%-- Show the form --%>
    <form name="setting" class="formInput" method="post" action="<mm:treefile page="/admin/index.jsp" objectlist="$includePath" referids="$referids"/>">
      <input type="hidden" name="referer" value="<mm:write referid="referer"/>"/>
      <mm:node referid="user">
        <table class="font">
        <mm:fieldlist fields="initials,firstname,lastname,username,email,address,zipcode,city,telephone,dayofbirth,lastactivity">
          <tr>
          <mm:import id="fieldname"><mm:fieldinfo type="name"/></mm:import>
          <mm:compare referid="fieldname" value="lastactivity" inverse="true">
            <mm:locale language="$language">
              <td><mm:fieldinfo type="guiname"/>:</td>
            </mm:locale>
          </mm:compare>
          <mm:compare referid="fieldname" valueset="initials,firstname,lastname,username,lastactivity">
            <mm:compare referid="fieldname" value="lastactivity" inverse="true">
              <td><mm:fieldinfo type="value" options="date" /></td>
            </mm:compare>
            <mm:compare referid="fieldname" value="lastactivity">
              <div style="display: none"><mm:fieldinfo type="input" /></div>
            </mm:compare>
          </mm:compare>
          <mm:compare referid="fieldname" valueset="initials,firstname,lastname,username,lastactivity" inverse="true">
            <td><mm:fieldinfo type="input" options="date"/></td>
          </mm:compare>
          </tr>
        </mm:fieldlist>

        <mm:treeinclude page="/admin/render_settings.jsp" objectlist="$includePath" referids="$referids" />
      </table>
      </mm:node>
      <br />
      <input class="formbutton" type="submit" name="action" value="<di:translate key="core.save" />"/>
      <input class="formbutton" type="submit" name="back" value="<di:translate key="core.back" />"/>
    </form>
</mm:cloud>
</mm:content>
