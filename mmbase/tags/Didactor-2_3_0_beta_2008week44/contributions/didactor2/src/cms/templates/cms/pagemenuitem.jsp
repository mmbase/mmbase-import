<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%-- expires is set so renaming a folder does not show the old name --%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud jspvar="cloud" method="delegate">
<%@include file="/shared/setImports.jsp" %>
<mm:import externid="page" required="true"/>
<mm:import externid="menupage" required="true"/>
<mm:node referid="menupage">
  <div class="lblevel1">
    <mm:compare referid="menupage" value="$page"><b></mm:compare>
    <a href="<mm:treefile page="/cms/index.jsp" objectlist="$includePath" referids="$referids">
               <mm:param name="page"><mm:write referid="menupage"/></mm:param>
             </mm:treefile>"><mm:field name="name" write="true"/></a>
    <mm:relatednodes role="posrel" type="pages" orderby="posrel.pos" searchdir="destination">
      <mm:treeinclude page="/cms/pagemenuitem.jsp" objectlist="$includePath" referids="page,$referids">
        <mm:param name="menupage"><mm:field name="number"/></mm:param>
      </mm:treeinclude>
    </mm:relatednodes>
    <mm:compare referid="menupage" value="$page"></b></mm:compare>
  </div>
</mm:node>
</mm:cloud>
</mm:content>
