<html>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>

<% int nextPos = 0; %>

delete all related and create posrel for metastandard->metadefinition ...<br/>
metastandards:<br/>

<mm:listnodes type="metastandard"  id="metastandard_id">

  <mm:field name="name" />
  <% nextPos = 1; %>

  <mm:relatednodes type="metadefinition" role="related"  id="metadefinition_id" orderby="name">
          <% nextPos +=10 ; %>
      
          <mm:createrelation source="metastandard_id" destination="metadefinition_id" role="posrel" >
             <mm:setfield name="pos"><%=nextPos%></mm:setfield>
          </mm:createrelation>

          <mm:listrelations type="metastandard" role="related" >
             <mm:deletenode />
          </mm:listrelations> 
      
  </mm:relatednodes>
    
</mm:listnodes>

done.






</mm:cloud>
</mm:content>

</html>
