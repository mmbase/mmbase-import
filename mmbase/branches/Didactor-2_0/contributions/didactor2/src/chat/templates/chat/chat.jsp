<%--
  This template shows the chat
--%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">

<%@include file="/shared/setImports.jsp" %>

<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title>Chat</title>
  </mm:param>
</mm:treeinclude>

<div class="rows">

<div class="navigationbar">
  <div class="titlebar">
    <img src="<mm:treefile write="true" page="/gfx/icon_chat.gif" objectlist="$includePath" />" width="25" height="13" border="0" title="chat" alt="chat" /> Chat
  </div>
</div>

<div class="folders">
  <div class="folderHeader">
    &nbsp;
  </div>
  <div class="folderBody">
    &nbsp;
  </div>
</div>

<div class="mainContent">
  <div class="contentHeader">
    &nbsp;
  </div>

  <!-- hierin wordt de werkelijke chat-sessie opgezet -->
  <div class="contentBodywit">
      <mm:remove referid="chaturl"/>
      <mm:import id="chaturl">flashchat.swf?port=${chat.internal.port}&host=<%= request.getServerName() %>&user=<mm:write referid="username"/>&enterchannel=<mm:write referid="class"/>&sessionkey=123&class=<mm:write referid="class"/>&provider=<mm:write referid="provider"/>&education=<mm:write referid="education"/></mm:import>

      <map name="chat">
      </map>

<OBJECT classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
                codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,47,0"
                ID="chat_eo" WIDTH="730" HEIGHT="480" ALIGN="">

<PARAM NAME="movie" VALUE="<mm:write referid="chaturl"/>">
<PARAM "NAME"=quality VALUE="high"> <PARAM NAME="bgcolor" VALUE="#FFFFFF">
<EMBED src="<mm:write referid="chaturl"/>" quality="high" bgcolor="#FFFFFF"
swLiveConnect="FALSE" WIDTH="730" HEIGHT="480" NAME="chat_eo" ALIGN=""
TYPE="application/x-shockwave-flash" PLUGINSPAGE="http://www.macromedia.com/go/getflashplayer">
</EMBED>
</OBJECT>
      <p/>
      &nbsp;&nbsp;Om gebruik te kunnen maken van de chat, heb je de <a href="http://www.macromedia.com/shockwave/download/download.cgi?P1_Prod_Version=ShockwaveFlash&P5_Language=Dutch&Lang=Dutch" target="_blank">nieuwste Flash Player</a> nodig, minimaal versie 6.

  </div>
</div>
</div>

<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />

</mm:cloud>
</mm:content>
