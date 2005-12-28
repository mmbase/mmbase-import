<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
  <%@include file="/shared/setImports.jsp" %>
  <mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
    <mm:param name="extraheader">
      <title><di:translate key="virtualclassroom.virtualclassroom"/></title>
    </mm:param>
  </mm:treeinclude>
  <link rel="stylesheet" type="text/css" href="<mm:treefile page="/virtualclassroom/css/base.css" objectlist="$includePath" referids="$referids" />" />
  <div class="rows">
    <div class="navigationbar">
      <div class="titlebar">
        <di:translate key="virtualclassroom.virtualclassroom"/>
      </div>
    </div>
    <div class="contentBody">
      <div class="topRow" >
        <div class="leftHalfRow">
          <mm:treeinclude page="/virtualclassroom/frontoffice/media/index.jsp" objectlist="$includePath" referids="$referids"/>      
        </div>
        <div class="rightHalfRow">
           <mm:treeinclude page="/virtualclassroom/frontoffice/chat/chat.jsp" objectlist="$includePath" referids="$referids"/>
        </div>
      </div>
      <div class="bottomRow">
        <mm:import externid="typeof"/>
        <mm:present referid="typeof">
          <mm:import externid="destination" />
          <mm:notpresent referid="destination">
            <mm:treeinclude page="/virtualclassroom/frontoffice/workspace/index.jsp" objectlist="$includePath" referids="$referids"/>
          </mm:notpresent>
          <mm:present referid="destination">
            <mm:treeinclude page="/virtualclassroom/frontoffice/workspace/workspace.jsp" objectlist="$includePath" referids="$referids,destination">
              <mm:param name="destination"><mm:write referid="destination"/></mm:param> 
            </mm:treeinclude>
          </mm:present>
        </mm:present>
        <mm:notpresent referid="typeof">
          <mm:import externid="destination" />
          <mm:notpresent referid="destination">
            <mm:treeinclude page="/virtualclassroom/frontoffice/workspace/index.jsp?typeof=1" objectlist="$includePath" referids="$referids"/>
          </mm:notpresent>
          <mm:present referid="destination">
            <mm:treeinclude page="/virtualclassroom/frontoffice/workspace/workspace.jsp" objectlist="$includePath" referids="$referids,destination">
              <mm:param name="destination"><mm:write referid="destination"/></mm:param> 
            </mm:treeinclude>
          </mm:present>
        </mm:notpresent>
      </div>
    </div>
  </div>
  <mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</mm:cloud>
</mm:content>