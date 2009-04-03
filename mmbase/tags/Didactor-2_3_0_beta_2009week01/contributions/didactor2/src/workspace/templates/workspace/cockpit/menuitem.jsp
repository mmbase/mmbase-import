<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<mm:cloud jspvar="cloud" method="delegate">
<%@include file="/shared/setImports.jsp" %>
<mm:import externid="type" />
<mm:import externid="scope">none</mm:import>
<%-- 'my documents and shared documents' is only valid in the 'education' scope --%>
<mm:compare referid="scope" value="education">
    <div class="menuSeparator"> </div>
    <div class="menuItem" id="menuWorkspace">
      <a href="<mm:treefile page="/workspace/index.jsp?typeof=1" objectlist="$includePath" referids="$referids" />" class="menubar"><di:translate key="workspace.mydocumentsmenuitem" /></a>
    </div>
    <div class="menuSeparator"> </div>
    <div class="menuItem" id="menuWorkspace">
      <a href="<mm:treefile page="/workspace/index.jsp?typeof=2" objectlist="$includePath" referids="$referids" />" class="menubar"><di:translate key="workspace.shareddocumentsmenuitem" /></a>
    </div>
   <mm:node number="$user" notfound="skip">
        <mm:relatednodes type="workgroups" max="1">
        <div class="menuSeparator"> </div>
        <div class="menuItem" id="menuWorkspace">
          <a href="<mm:treefile page="/workspace/index.jsp?typeof=3" objectlist="$includePath" referids="$referids" />" class="menubar"><di:translate key="workspace.workgroupdocumentsmenuitem" /></a>
        </div>
        </mm:relatednodes>
    </mm:node>
</mm:compare>

</mm:cloud>

