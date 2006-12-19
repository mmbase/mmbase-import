<mm:import externid="_handle_name" from="multipart"/>
<mm:import externid="_handle_type" from="multipart"/>
<mm:import externid="_handle_size" from="multipart"/>
<mm:import externid="_title" from="multipart"/> 

<mm:compare referid="_handle_size" value="0" inverse="true">
   <mm:createnode type="attachments" id="newitem">
      <mm:setfield name="title"><mm:write referid="_title"/></mm:setfield>
      <mm:setfield name="description"></mm:setfield>
      <mm:setfield name="filename"><mm:write referid="_handle_name"/></mm:setfield>
      <mm:setfield name="mimetype"><mm:write referid="_handle_type"/></mm:setfield>
      <mm:setfield name="size"><mm:write referid="_handle_size"/></mm:setfield>
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
</mm:compare>
