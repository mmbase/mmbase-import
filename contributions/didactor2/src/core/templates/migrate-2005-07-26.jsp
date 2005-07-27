<html>

<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>

delete all related and create posrel for metastandard->metadefinition ...<br/>
metastandards:<br/>

<mm:listnodes type="metastandard" >

  <mm:field name="name" jspvar="metaStandard" vartype="String">
     <%=metaStandard %> <br/>
  </mm:field>
    
   <mm:node id="metastandard_id" >

     <mm:relatednodes type="metadefinition" role="related">
      
       <mm:node id="metadefinition_id" >
          <mm:createrelation source="metastandard_id" destination="metadefinition_id" role="posrel" >
             <mm:setfield name="pos">1</mm:setfield>
          </mm:createrelation>
          <mm:listrelations type="metastandard" role="related" >
             <mm:deletenode />
          </mm:listrelations> 
      </mm:node>

    </mm:relatednodes>
 
  </mm:node> 
 
</mm:listnodes>

done.






</mm:cloud>
</mm:content>

</html>
