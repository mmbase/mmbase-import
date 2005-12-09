<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<mm:content postprocessor="reducespace">
<mm:cloud jspvar="cloud">
  <%@include file="/shared/setImports.jsp" %>
  <mm:import id="presenttime"><mm:time time="now"/></mm:import>
  <script type="text/javascript">
		  function openNewsContent( number ) {
		    if ( number > 0 ) {
		      frames['content'].location.href='<mm:treefile page="/portalpages/frontoffice/show_content.jsp" objectlist="$includePath" referids="$referids" escapeamps="false"/>'+'&node='+number;
		    }
		  }
  </script>
  <mm:listnodes type="newsnodes">
    <mm:remove referid="notgeneral"/>
    <mm:relatednodes type="educations">
      <mm:import id="notgeneral" reset="true">true</mm:import>
    </mm:relatednodes>
    <mm:relatednodes type="roles">
      <mm:import id="notgeneral" reset="true">true</mm:import>
    </mm:relatednodes>    
    <mm:notpresent referid="notgeneral">
      <mm:relatednodescontainer type="simplecontents">
        <mm:constraint field="simplecontents.online_date" value="${presenttime}" operator="LESS"/>
        <mm:constraint field="simplecontents.offline_date" value="${presenttime}" operator="GREATER"/>
        <mm:relatednodes>
     <%-- <mm:relatednodes type="simplecontents" constraints="${presenttime} BETWEEN simplecontents.online_date AND simplecontents.offline_date"> --%>
          <a href="javascript:openNewsContent('<mm:field name="number"/>');" style="padding-left: 0px"><b><mm:field name="title"/></b></a><br/>         
        </mm:relatednodes>
      </mm:relatednodescontainer> 
    </mm:notpresent>
  </mm:listnodes> 
</mm:cloud>
</mm:content>
						    