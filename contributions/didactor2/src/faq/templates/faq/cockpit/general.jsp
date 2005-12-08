<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<mm:content postprocessor="reducespace">
<mm:cloud jspvar="cloud">
  <%@include file="/shared/setImports.jsp" %>
  <link rel="stylesheet" type="text/css" href="<mm:treefile page="/portalpages/css/base.css" objectlist="$includePath" referids="$referids" />" /> 
  <script type="text/javascript">
		  function openFaqContent( number ) {
		    if ( number > 0 ) {
		      frames['content'].location.href='<mm:treefile page="/faq/frontoffice/show_faq.jsp" objectlist="$includePath" referids="$referids" escapeamps="false"/>'+'&node='+number;
		    }
		  }
  </script>
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
</mm:cloud>
</mm:content>
