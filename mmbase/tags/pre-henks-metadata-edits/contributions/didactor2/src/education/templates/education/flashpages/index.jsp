<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">

<mm:import externid="learnobject" required="true"/>

<!-- TODO show the flash animation -->

<%@include file="/shared/setImports.jsp" %>

<%-- remember this page --%>
<mm:treeinclude page="/education/storebookmarks.jsp" objectlist="$includePath" referids="$referids">
    <mm:param name="learnobject"><mm:write referid="learnobject"/></mm:param>
    <mm:param name="learnobjecttype">flashpages</mm:param>
</mm:treeinclude>


<mm:node number="$learnobject">
   <mm:relatednodes type="attachments">
<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,47,0" width="760" height="440" id="flashpage">
              <param name="movie" value="<mm:attachment/>">
              <param name="quality" value="high">
              <embed src="<mm:attachment/>" quality="high" pluginspage="http://www.macromedia.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveFlash" type="application/x-shockwave-flash" width="770" height="440" name="flashpage" swLiveConnect="true">
              </embed> 			  
          </object>
  </mm:relatednodes>
</mm:node>

</mm:cloud>
</mm:content>
 
