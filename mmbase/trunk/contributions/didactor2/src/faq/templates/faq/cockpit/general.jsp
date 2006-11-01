<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud jspvar="cloud">
  <%@include file="/shared/setImports.jsp" %>
  <%-- <link rel="stylesheet" type="text/css" href="<mm:treefile page="/portalpages/css/base.css" objectlist="$includePath" referids="$referids" />" /> --%>
  <div class="menuSeparator">  </div>
  <div class="menuItem">
    <script type="text/javascript">
      <!--
	  function openFaqContent( number ) {
		if ( number > 0 ) {
		  frames['content'].location.href='<mm:treefile page="/faq/frontoffice/show_faq.jsp" objectlist="$includePath" referids="$referids" escapeamps="false"/>'+'&node='+number;
		}
	  }
	  //-->
    </script>
    
  <mm:node number="$provider" notfound="skipbody">
    <mm:relatednodescontainer path="settingrel,components">
      <mm:constraint field="components.name" value="faq"/>
      <mm:relatednodes>
        <mm:import id="showfaq" />
      </mm:relatednodes>
    </mm:relatednodescontainer>
  </mm:node>
    
  <mm:present referid="showfaq">  
    <mm:listnodes type="faqnodes">
      <mm:remove referid="notgeneral"/>
      <mm:relatednodes type="educations">
        <mm:import id="notgeneral" reset="true">true</mm:import>
      </mm:relatednodes>
      <mm:relatednodes type="roles">
        <mm:import id="notgeneral" reset="true">true</mm:import>
      </mm:relatednodes>    
      <mm:notpresent referid="notgeneral"> 
        <a class="menubar" href="javascript:openFaqContent('<mm:field name="number"/>');"><mm:field name="name"/></a>
      </mm:notpresent>
    </mm:listnodes>
  </mm:present>  
  </div>
</mm:cloud>
</mm:content>
