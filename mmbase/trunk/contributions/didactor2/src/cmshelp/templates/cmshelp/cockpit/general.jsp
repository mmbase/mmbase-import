<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<mm:content postprocessor="reducespace">
<mm:cloud jspvar="cloud">
  <%@include file="/shared/setImports.jsp" %>
  <div class="menuSeparator"> | </div>
  <div class="menuItem">
  <script type="text/javascript">
	  function openHelpContent( number ) {
	    if ( number > 0 ) {
	      frames['content'].location.href='<mm:treefile page="/cmshelp/frontoffice/show_help.jsp" objectlist="$includePath" referids="$referids" escapeamps="false"/>'+'&node='+number;
	    }
	  }
  </script>
  <mm:listnodes type="helpcontainers">    
    <mm:first>  
      <mm:field id="helpcontainer" name="number" write="false" />
    </mm:first>   
  </mm:listnodes> 
  <mm:node number="$helpcontainer" notfound="skipbody">
	<mm:relatednodes type="helpnodes">
	  <mm:remove referid="notgeneral"/>
	  <mm:relatednodes type="educations">
	    <mm:import id="notgeneral" reset="true">true</mm:import>
	  </mm:relatednodes>
	  <mm:relatednodes type="roles">
	    <mm:import id="notgeneral" reset="true">true</mm:import>
	  </mm:relatednodes>    
	  <mm:notpresent referid="notgeneral">
	    <a class="menubar" href="javascript:openHelpContent('<mm:field name="number"/>');" style="padding-left: 0px"><mm:field name="name"/></a>
	  </mm:notpresent>
	</mm:relatednodes> 
  </mm:node>
  </div> 
</mm:cloud>
</mm:content>

						    