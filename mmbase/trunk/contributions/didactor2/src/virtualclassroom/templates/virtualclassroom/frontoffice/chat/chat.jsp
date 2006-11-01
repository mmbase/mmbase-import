<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
  <%@include file="/shared/setImports.jsp" %>
  <%@include file="userList.jsp" %>  
  <link rel="stylesheet" type="text/css" href="<mm:treefile page="/virtualclassroom/css/base.css" objectlist="$includePath" referids="$referids" />" />  
  <link rel="stylesheet" type="text/css" href="<mm:treefile page="/css/base.css" objectlist="$includePath" referids="$referids" />" />
  <!-- Set applet parameters -->
  <mm:import id="jabchaturl"><%=request.getContextPath()%>/virtualclassroom/frontoffice/chat/jabApplet.jar</mm:import>
  <mm:import id="jabchatcode">nl.stivoro.jabchat.JabberApplet</mm:import>
  <mm:import id="jabchatheight">100%</mm:import>
  <mm:import id="jabchatwidth">100%</mm:import>
  <mm:import id="jabchatalttext"><di:translate key="virtualclassroom.jabchatalttext"/></mm:import>
  <mm:import id="jabchatroomname"><di:translate key="virtualclassroom.classroom"/></mm:import>
  <mm:import id="jabchatbgdclr">0xFFFFFF</mm:import> 
  <mm:import id="finaluserlist"><%=userList.trim()%></mm:import> 
  <mm:import id="finalrolelist"><%=roleList.trim()%></mm:import> 
  <!-- Set applet parameters -->
  <!--[if !IE]>-->
  <object classid="java:<mm:write referid="jabchatcode"/>.class" 
    type="application/x-java-applet"
    archive="<mm:write referid="jabchaturl"/>" 
    height="<mm:write referid="jabchatwidth"/>"
    width="<mm:write referid="jabchatwidth"/>"> 
    <param name=uname value="<mm:write referid="username" />">
    <param name=rooms value="<mm:write referid="jabchatroomname" />">
    <param name=bgdclr value="<mm:write referid="jabchatbgdclr" />">
    <param name=buddies value="<mm:write referid="finaluserlist" />">
    <param name=roles value="<mm:write referid="finalrolelist" />">                
  <!--<![endif]-->
      <object classid="clsid:8AD9C840-044E-11D1-B3E9-00805F499D93" 
        height="<mm:write referid="jabchatwidth"/>"
        width="<mm:write referid="jabchatwidth"/>"> 
        <param name="code" value="<mm:write referid="jabchatcode"/>"/>
        <param name="archive" value="<mm:write referid="jabchaturl"/>"/>
        <param name=uname value="<mm:write referid="username"/>">
        <param name=rooms value="<mm:write referid="jabchatroomname"/>">
        <param name=bgdclr value="<mm:write referid="jabchatbgdclr"/>">
        <param name=buddies value="<mm:write referid="finaluserlist" />">
        <param name=roles value="<mm:write referid="finalrolelist" />">         
      </object> 
  <!--[if !IE]>-->
  </object>
  <!--<![endif]-->
</mm:cloud>
</mm:content>
