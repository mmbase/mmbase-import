<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%-- expires is 0; show always new content --%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
	<%@include file="/shared/setImports.jsp" %>
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><fmt:message key="CONFIGURATION" /></title>
  </mm:param>
</mm:treeinclude>
<mm:import externid="action"/>
<mm:import externid="back"/>
<mm:import externid="referer" jspvar="referer"><%= request.getHeader("referer") %></mm:import>

<mm:present referid="back">
    <% response.sendRedirect(referer); %>
</mm:present>


<mm:present referid="action">
  <mm:import id="actiontext"><fmt:message key="SAVE" /></mm:import>
  <mm:compare referid="action" referid2="actiontext">
    <mm:node referid="user">
      <mm:fieldlist field="initials,firstname,lastname,email,username,address,zipcode,city,telephone">
        <mm:fieldinfo type="useinput" />
      </mm:fieldlist>
      <mm:listrelations role="settingrel">
        <mm:relatednodescontainer type="settings" role="related">
          <mm:constraint field="name" value="mayforward"/>
          <mm:relatednodes>
            <mm:import externid="mayforward"/>
            <%-- Onliner: no problems with spaces in the mayforward value --%>
            <mm:setfield name="value"><mm:compare referid="mayforward" value="on">1</mm:compare><mm:compare referid="mayforward" value="on" inverse="true">0</mm:compare></mm:setfield>
          </mm:relatednodes>
	    </mm:relatednodescontainer>
	  </mm:listrelations>
    </mm:node>
  </mm:compare>
</mm:present>

<div class="rows">
<div class="navigationbar">
  <div class="titlebar">
    <img src="<mm:treefile write="true" page="/gfx/icon_settings.gif" objectlist="$includePath" />" width="25" height="13" border="0" alt="<fmt:message key="CONFIGURATION" />" />
    <fmt:message key="CONFIGURATION" />
  </div>
</div>
<div class="folders">
  <div class="folderHeader">
  </div>
  <div class="folderBody">
  </div>
</div>
<div class="mainContent">
  <div class="contentHeader">
  </div>
  <div class="contentBodywit">
    <%-- Show the form --%>
    <form name="setting" class="formInput" method="post" action="<mm:treefile page="/admin/index.jsp" objectlist="$includePath" referids="$referids"/>">
        <input type="hidden" name="referer" value="<mm:write referid="referer"/>"/>
      <mm:node referid="user">
        <table class="font">
        <mm:fieldlist fields="initials,firstname,lastname,email,username,address,zipcode,city,telephone,lastactivity">
           <tr>
           <mm:import id="fieldname"><mm:fieldinfo type="name"/></mm:import>
             <td><mm:fieldinfo type="guiname"/>:</td>
           <mm:compare referid="fieldname" valueset="initials,firstname,lastname,username,lastactivity">
             <mm:compare referid="fieldname" value="lastactivity" inverse="true">
               <td><mm:fieldinfo type="value"/></td>
             </mm:compare>
           </mm:compare>
           <mm:compare referid="fieldname" valueset="initials,firstname,lastname,username" inverse="true">
             <td><mm:fieldinfo type="input"/></td>
           </mm:compare>
           </tr>
        </mm:fieldlist>
        <%-- Get the mayforward setting for the email-component --%>
        <mm:listrelations role="settingrel">
          <tr>
          <mm:relatednodes type="settings" role="related">
            <mm:remove referid="setting"/>
            <mm:import id="setting"><mm:field name="name"/></mm:import>
            <mm:remove referid="value"/>
            <mm:import id="value"><mm:field name="value"/></mm:import>
            <mm:compare referid="setting" value="mayforward">
              <td/>
              <td>
                <mm:compare referid="value" value="0">
                  <input type="checkbox" name="mayforward"/>
                </mm:compare>
                <mm:compare referid="value" value="1">
				  <input type="checkbox" name="mayforward" checked/>
                </mm:compare>
                <fmt:message key="MAYFORWARDEMAIL" />
              </td>
            </mm:compare>
          </mm:relatednodes>
          </tr>
        </mm:listrelations>
        </table>
      </mm:node>
	  <br />
      <input class="formbutton" type="submit" name="action" value="<fmt:message key="SAVE" />"/>
      <input class="formbutton" type="submit" name="back" value="<fmt:message key="BACK" />"/>
    </form>
  </div>
</div>
</div>
<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</mm:cloud>
</mm:content>
