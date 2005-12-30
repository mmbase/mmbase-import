<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
  <%@include file="/shared/setImports.jsp" %>
  <%@include file="userList.jsp" %>  
  <link rel="stylesheet" type="text/css" href="<mm:treefile page="/virtualclassroom/css/base.css" objectlist="$includePath" referids="$referids" />" />  
  <!-- Set applet parameters -->
  <mm:import id="jabchaturl"><%=request.getContextPath()%>/virtualclassroom/frontoffice/chat/jabApplet.jar</mm:import>
  <mm:import id="jabchatcode">nl.stivoro.jabchat.JabberApplet</mm:import>
  <mm:import id="jabchatheight">376</mm:import>
  <mm:import id="jabchatwidth">590</mm:import>
  <mm:import id="jabchatalttext"><di:translate key="virtualclassroom.jabchatalttext"/></mm:import>
  <mm:import id="jabchatroomname"><di:translate key="virtualclassroom.classroom"/></mm:import>
  <mm:import id="jabchatbgdclr">0xFFFFFF</mm:import> 
  <!-- Set applet parameters -->
  <APPLET
    CODE="<mm:write referid="jabchatcode" />"
    CODEBASE="<%=request.getServerName()%>"
    ARCHIVE="<mm:write referid="jabchaturl" />"
    WIDTH="<mm:write referid="jabchatwidth" />"
    HEIGHT="<mm:write referid="jabchatheight" />"
    ALT="<mm:write referid="jabchatalttext" />">
    <param name=uname value="<mm:write referid="username" />">
    <param name=rooms value="<mm:write referid="jabchatroomname" />">
    <param name=bgdclr value="<mm:write referid="jabchatbgdclr" />">
    <param name=buddies value="<%=userList.trim()%>">
    <param name=roles value="<%=roleList.trim()%>">
  </APPLET>
</mm:cloud>
</mm:content>