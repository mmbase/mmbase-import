<mm:import externid="_handle" from="multipart" />

<mm:compare referid="_handle" value="" inverse="true">
  <c:if test="${! empty _handle.name}">
    <mm:import externid="_title" from="multipart" /> 
    <mm:createnode type="attachments" id="newitem">
      <mm:setfield name="title"><mm:write referid="_title"/></mm:setfield>
      <mm:setfield name="description"></mm:setfield>
      <mm:setfield name="filename">${_handle.name}</mm:setfield>
      <mm:fieldlist fields="handle">
         <mm:fieldinfo type="useinput" />
      </mm:fieldlist>
   </mm:createnode>
   <mm:list nodes="$postingid" path="postings,related,attachments">
      <mm:node element="related"><mm:deletenode/></mm:node>
      <mm:node element="attachments">
         <mm:countrelations>
            <mm:isgreaterthan value="0" inverse="true">
               <mm:deletenode deleterelations="true"/>
            </mm:isgreaterthan>
         </mm:countrelations>
      </mm:node>
   </mm:list>
   <mm:createrelation role="related" source="postingid" destination="newitem"/>
  </c:if>
</mm:compare>
