<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">

<mm:import externid="question" required="true"/>
<%@include file="/shared/setImports.jsp" %>
<style type="text/css">

</style>
<mm:node number="$question">

  <h2>
    <mm:field name="title"/>
  </h2>
  <p/>
  <mm:field name="text" escape="none"/>
  <p/>

  <mm:import id="questiontype"><mm:field name="type"/></mm:import>
  <mm:import id="questionlayout"><mm:field name="layout"/></mm:import>

    <div class="images">
      <mm:relatednodes type="images" max="1">
        <h3><mm:field name="title"/></h3>
	<div style="position: relative">
        <img src="<mm:image />" border="0" /><br/>
        <mm:field name="description" escape="none"/>
      </mm:relatednodes>
      <%-- see noscipt tag below --%>
      <script type="text/javascript">
      document.write('<input type="hidden" name="hotspot<mm:write referid="question"/>" value="">');
      var previousHotspot<mm:write referid="question"/> = 0;
      </script>
	    <% int i = 1; int j = 1;%>
	    <mm:relatednodes type="hotspotanswers" orderby="x1,y1">
	      <a href="#"
	      onclick="
	      document.forms[0].elements['hotspot<mm:write referid="question"/>'].value='<mm:field name="number"/>'; 
	      if (previousHotspot<mm:write referid="question"/>) { 
		  previousHotspot<mm:write referid="question"/>.className='hotspot';  
	      } 
	      previousHotspot<mm:write referid="question"/>=this; 
	      this.className='selectedhotspot';
	      return false" 
	      class="hotspot" style="position: absolute; left: <mm:field name="x1" />px; top: <mm:field name="y1" />px; width:<mm:field name="x2" />px; height: <mm:field name="y2" />px;"><%= i++ %></a>
	     </mm:relatednodes>
	     <%-- fallback: if no script enabled, show pulldown menu with answers instead--%>
	    <noscript>
	    <p>
	    <di:translate id="selecthotspot">Kies een gebied</di:translate>: 
	      <select name="hotspot<mm:write referid="question"/>">
	      <mm:relatednodes type="hotspotanswers" orderby="number">
	        <option value="<mm:field name="number"/>"><%= j++ %></option>
	       </mm:relatednodes>
	    </noscript>
	    </p>
          </div>
    

  
	
</mm:node>
</mm:cloud>
</mm:content>
