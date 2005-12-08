<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
  <mm:node referid="provider">
    <mm:countrelations type="flashpages" write="false">
        <mm:islessthan value="1">
          <p>
            <h1><di:translate key="core.welcome" /></h1>
          </p>
          <br />
          <p>
            <h3><di:translate key="core.welcomemessage" /></h3>
          </p>
         </mm:islessthan>
    </mm:countrelations>
    <mm:relatednodes type="flashpages" jspvar="flash">
      <mm:field name="showtitle">
        <mm:compare value="1">
          <h1><mm:field name="name"/></h1>
        </mm:compare>
      </mm:field>

    <% 
        int layout = flash.getIntValue("layout");
        int width = 520;
        int height= 440;
        if (layout >= 0) {
            if (layout >= 2) {
                width = 260;
                height = 220;
                %><table class="Font"><tr><td valign="top"><%
            }
            if (layout == 2 || layout == 0) {
                %><mm:field name="text" escape="none"/><%
                if (layout == 2) {
                    %></td><td valign="top"><% 
                }
            }
        }
        %>
       <mm:relatednodes type="attachments">
    <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,47,0"  id="flashpage">
                  <param name="movie" value="<mm:attachment/>">
                  <param name="quality" value="high">
                  <embed src="<mm:attachment/>" quality="high" pluginspage="http://www.macromedia.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveFlash" type="application/x-shockwave-flash" width="<%= width %>" height="<%= height %>" name="flashpage" swLiveConnect="true">
                  </embed> 			  
              </object>
      </mm:relatednodes>
        <%
            if (layout >= 2) {
                if (layout == 2) {
                    %></td><%
                }
                if (layout == 3) {
                    %></td><td valign="top"><mm:field name="text" escape="none"/></td><%
                }
                %></tr></table><%
            }
            else if (layout == 1) {
                %><mm:field name="text" escape="none"/><%
            }
        %>
    </mm:relatednodes>
  </mm:node>
</mm:cloud>
</mm:content>

