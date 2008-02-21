<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di"
%>
<!--
     TODO: This JSP is hard to understand.
     I think it lists flashpages, with related flashpages and other objects, and tries
     to make a table of it, in a bit convoluted way.

-->
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
         <mm:relatednodes type="flashpages">
       <mm:relatednodes type="attachments">
    <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,47,0"  id="flashpage">
                  <param name="movie" value="<mm:attachment/>">
                  <param name="quality" value="high">
                  <embed src="<mm:attachment/>" quality="high" pluginspage="http://www.macromedia.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveFlash" type="application/x-shockwave-flash" width="<%= width %>" height="<%= height %>" name="flashpage" swLiveConnect="true">
                  </embed>
              </object>
      </mm:relatednodes>
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
	<mm:relatednodes type="attachments" role="related">
		<h3><mm:field name="title"/></h3>
     	<p>
      	<i><mm:field name="description" escape="inline"/></i><br>
      	<a href="<mm:attachment/>"><img src="<mm:treefile page="/education/gfx/attachment.gif" objectlist="$includePath" />" border="0" title="Download <mm:field name="title"/>" alt="Download <mm:field name="title"/>"></a>
    	</p>
    </mm:relatednodes>
  	<div class="audiotapes">
    <mm:relatednodes type="audiotapes" role="posrel" orderby="posrel.pos">
        <h3><mm:field name="title"/></h3>
      	<p>
          <i><mm:field name="subtitle"/></i>
      	</p>
        <i><mm:field name="intro" escape="p"/></i>
        <p>
          <mm:field name="body" escape="inline"/><br>
          <a href="<mm:field name="url" />"><img src="<mm:treefile page="/education/gfx/audio.gif" objectlist="$includePath" />" border="0" title="Beluister <mm:field name="title" />" alt="Beluister <mm:field name="title" />"></a></b>
      	</p>
      </mm:relatednodes>
  	</div>

   <div class="videotapes">
    <mm:relatednodes type="videotapes" role="posrel" orderby="posrel.pos">
      <p>
        <h3><mm:field name="title"/></h3>
        <i><mm:field name="subtitle"/></i>
      </p>
      <i><mm:field name="intro" escape="p"/></i>
      <p>
        <mm:field name="body" escape="inline"/><br>
        <a href="<mm:field name="url" />"><img src="<mm:treefile page="/education/gfx/video.gif" objectlist="$includePath" />" border="0" title="Bekijk <mm:field name="title" />" alt="Bekijk <mm:field name="title" />"></a>
      </p>
    </mm:relatednodes>
   </div>

    <div class="urls">
      <mm:relatednodes type="urls" role="posrel" orderby="posrel.pos">
        <mm:field name="showtitle">
          <mm:compare value="1">
            <h3><mm:field name="name"/></h3>
          </mm:compare>
        </mm:field>
        <p>
          <i><mm:field name="description" escape="inline"/></i><br/>
          <a href="<mm:field name="url"/>" target="_blank"><mm:field name="url"/></a>
        </p>
      </mm:relatednodes>
    </div>

    <div class="images">
      <mm:relatednodes type="images">
        <mm:field name="showtitle">
          <mm:compare value="1">
            <h3><mm:field name="title"/></h3>
          </mm:compare>
        </mm:field>
        <img src="<mm:image />" width="200" border="0" /><br/>
        <!-- showing the original image. Is that really ok? -->
        <mm:field name="description" escape="none"/>
      </mm:relatednodes>
    </div>
  </mm:relatednodes>
