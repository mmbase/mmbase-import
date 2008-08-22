<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
  <%@include file="/shared/setImports.jsp" %>
  <mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
    <mm:param name="extraheader">
      <title><di:translate key="virtualclassroom.virtualclassroom"/></title>
      <link rel="stylesheet" type="text/css" href="<mm:treefile page="/virtualclassroom/css/base.css" objectlist="$includePath" referids="$referids" />" />      
      <link rel="stylesheet" type="text/css" href="<mm:treefile page="/css/base.css" objectlist="$includePath" referids="$referids" />" />
    </mm:param>
  </mm:treeinclude>  
  <div class="rows">
    <div class="navigationbar">
      <div class="titlebar">
        <di:translate key="virtualclassroom.virtualclassroom"/>
      </div>
    </div>
    <div class="contentBody">
      <div class="leftHalfRow"><mm:treeinclude page="/virtualclassroom/frontoffice/media/index.jsp" objectlist="$includePath" referids="$referids"/></div>      
      <div class="rightHalfRow"><mm:treeinclude page="/virtualclassroom/frontoffice/chat/chat.jsp" objectlist="$includePath" referids="$referids"/></div>
      <iframe class="bottomRow" frameborder="0" name="workspace" src="<mm:treefile page="/virtualclassroom/frontoffice/workspace/workspace.jsp" objectlist="$includePath" referids="$referids"/>"></iframe>
    </div>
  </div>
  <mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</mm:cloud>
</mm:content>
