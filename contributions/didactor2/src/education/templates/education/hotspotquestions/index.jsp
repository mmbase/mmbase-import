<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">

<mm:import externid="question" required="true"/>
<mm:import externid="madetest">-1</mm:import>
<%@include file="/shared/setImports.jsp" %>
<style type="text/css">

</style>
<mm:node number="$question">
  <mm:import id="givenanswer">-1</mm:import>
  <mm:relatedcontainer path="givenanswers,madetests">
    <mm:constraint field="madetests.number" value="$madetest"/>
    <mm:related>
      <mm:node element="givenanswers">
        <mm:import id="givenanswer" reset="true"><mm:field name="text"/></mm:import>
      </mm:node>
    </mm:related>
  </mm:relatedcontainer>
  <mm:import id="choosenhotspot" reset="true">-1</mm:import>
  <mm:compare referid="givenanswer" value="-1" inverse="true">
    <% int of = 1; %>
    <mm:relatednodes type="hotspotanswers" orderby="number">
      <mm:compare referid="givenanswer" value="<%=""+of%>">
        <mm:import id="choosenhotspot" reset="true"><mm:field name="number"/></mm:import>
      </mm:compare>
      <% of++; %>
    </mm:relatednodes>
  </mm:compare>
  <mm:field name="showtitle">
    <mm:compare value="1">
      <h2><mm:field name="title"/></h2>
    </mm:compare>
  </mm:field>
  <p/>
  <mm:field name="text" escape="none"/>
  <p/>

  <mm:import id="questionlayout"><mm:field name="layout"/></mm:import>

    <div class="images">
      <mm:relatednodes type="images" max="1">
        <mm:field name="showtitle">
          <mm:compare value="1">
            <h3><mm:field name="title"/></h3>
          </mm:compare>
        </mm:field>

	<div style="position: relative">
        <img src="<mm:image />" border="0" /><br/>
        <mm:field name="description" escape="none"/>
      </mm:relatednodes>
      <%-- see noscipt tag below --%>
      <script type="text/javascript">
      document.write('<input type="hidden" name="hotspot<mm:write referid="question"/>" value="<mm:write referid="choosenhotspot"/>">');
      var previousHotspot<mm:write referid="question"/> = 0;
      </script>
	    <% int i = 1; int j = 1;%>
	    <mm:relatednodes type="hotspotanswers" orderby="x1,y1">
          <mm:import id="hotspotclass" reset="true">hotspot</mm:import>
          <mm:field name="number">
            <mm:compare referid2="choosenhotspot">
              <mm:import id="hotspotclass" reset="true">selectedhotspot</mm:import>
            </mm:compare>
          </mm:field>
	      <a href="#" id="<mm:field name="number"/>"
	      onclick="
	      document.forms[0].elements['hotspot<mm:write referid="question"/>'].value='<mm:field name="number"/>'; 
		  previousHotspot<mm:write referid="question"/>.className='hotspot';  
	      if (previousHotspot<mm:write referid="question"/>) { 
		  previousHotspot<mm:write referid="question"/>.className='hotspot';  
	      } 
	      <mm:compare referid="choosenhotspot" value="-1" inverse="true">
            <mm:write referid="choosenhotspot"/>.className='hotspot'; 
          </mm:compare>
	      previousHotspot<mm:write referid="question"/>=this; 
	      this.className='selectedhotspot';
	      return false" 
	      class="<mm:write referid="hotspotclass"/>" style="position: absolute; left: <mm:field name="x1" />px; top: <mm:field name="y1" />px; width:<mm:field name="x2" />px; height: <mm:field name="y2" />px;"><%= i++ %></a>
	     </mm:relatednodes>
	     <%-- fallback: if no script enabled, show pulldown menu with answers instead--%>
	    <noscript>
	    <p>
	    <di:translate key="education.selecthotspot" />: 
	      <select name="hotspot<mm:write referid="question"/>">
	      <mm:relatednodes type="hotspotanswers" orderby="number">
            <mm:field name="number">
              <option value="<mm:write/>" <mm:compare referid2="choosenhotspot">selected</mm:compare>><%= j++ %></option>
            </mm:field>  
	      </mm:relatednodes>
	    </noscript>
	    </p>
          </div>
	
</mm:node>
</mm:cloud>
</mm:content>
