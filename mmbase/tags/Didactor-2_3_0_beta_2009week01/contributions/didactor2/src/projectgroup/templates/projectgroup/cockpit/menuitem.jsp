<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<mm:cloud jspvar="cloud" method="delegate">
<%@include file="/shared/setImports.jsp" %>
<mm:import externid="type" />
<mm:import externid="scope">none</mm:import>
    <mm:node referid="education">
        <mm:relatednodes type="components" max="1" constraints="components.name='projectgroup'">
         <div class="menuSeparator"> </div>
        <div class="menuItem" id="menuWorkspace">
          <a href="<mm:treefile page="/projectgroup/index.jsp" objectlist="$includePath" referids="$referids" />" class="menubar"><di:translate key="workspace.projectgroups" /></a>
        </div>
            
        </mm:relatednodes>
    </mm:node>

</mm:cloud>

