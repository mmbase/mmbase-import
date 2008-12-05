<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud jspvar="cloud">
  <%@include file="/shared/setImports.jsp" %>
  <script type="text/javascript">
	  function openHelpContent( number ) {
	    if ( number > 0 ) {
	      frames['content'].location.href='<mm:treefile page="/cmshelp/frontoffice/show_help.jsp" objectlist="$includePath" referids="$referids" />'+'&amp;node='+number;
	    }
	  }
  </script>
  <mm:node number="$provider" notfound="skipbody">
    <mm:relatednodescontainer path="settingrel,components">
      <mm:constraint field="components.name" value="cmshelp"/>
      <mm:relatednodes>
        <mm:import id="showcmshelp" />
      </mm:relatednodes>
    </mm:relatednodescontainer>
  </mm:node>
  <mm:present referid="showcmshelp">
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
    	    <div class="menuSeparator"> | </div>
            <div class="menuItem">
    	      <a class="menubar" href="javascript:openHelpContent('<mm:field name="number"/>');" style="padding-left: 0px"><mm:field name="name"/></a>
    	    </div>
    	  </mm:notpresent>
    	</mm:relatednodes>
    </mm:node>
  </mm:present>
</mm:cloud>
</mm:content>


