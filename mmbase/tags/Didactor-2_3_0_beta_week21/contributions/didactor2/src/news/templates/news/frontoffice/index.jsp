<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud jspvar="cloud">
  <%@include file="/shared/setImports.jsp" %>
  <mm:import id="presenttime"><mm:time time="now"/></mm:import>
  <script type="text/javascript">
		  function openNewsContent(number, parentnumber ) {
		    if ( number > 0 ) {
		      frames['content'].location.href='<mm:treefile page="/news/frontoffice/show_content.jsp" objectlist="$includePath" referids="$referids" escapeamps="false"/>'+'&node='+number+'&parentnode='+parentnumber;
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
        <mm:field id="parentnumber" name="number" write="false"/>
        <mm:relatednodes>
     <%-- <mm:relatednodes type="simplecontents" constraints="${presenttime} BETWEEN simplecontents.online_date AND simplecontents.offline_date"> --%>
          <a href="javascript:openNewsContent('<mm:field name="number"/>','<mm:write referid="parentnumber"/>' );" style="padding-left: 0px"><b><mm:field name="title"/></b></a><br/>         
        </mm:relatednodes>
      </mm:relatednodescontainer> 
    </mm:notpresent>
  </mm:listnodes> 
</mm:cloud>
</mm:content>
						    
