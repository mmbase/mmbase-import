<!-- this piece of code exports the metastandards which are present in the cloud to an executable piece of jsp -->
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>

&lt;%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %&gt;<br/>
&lt;%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %&gt;<br/>
&lt;mm:content postprocessor="reducespace" expires="0"&gt;<br/>
&lt;mm:cloud method="delegate" jspvar="cloud"&gt;<br/>
&lt;%@include file="/shared/setImports.jsp" %&gt;<br/>

<%
String [] metadef_fields = { "name", "description", "type", "required", "maxvalues", "minvalues", "handler" };
%>
<mm:listnodes type="metastandard">
   <mm:remove referid="contains_metadefs" />
   <mm:related path="posrel,metadefinition" orderby="posrel.pos" directions="UP">
      <mm:import id="contains_metadefs" />
   </mm:related>
   <mm:present referid="contains_metadefs">
   <!-- print metastandard -->
   &lt;mm:createnode type="metastandard" id="n<mm:field name="number" />" &gt;<br/>
      &lt;mm:setfield name="name"&gt;<mm:field name="name"/>&lt;/mm:setfield&gt;<br/>
      &lt;mm:setfield name="isused"&gt;<mm:field name="isused"/>&lt;/mm:setfield&gt;<br/>
   &lt;/mm:createnode&gt;<br/>
   &lt;%<br/>
   if(true) {<br/>   
   <%
   for(int i=0; i<metadef_fields.length; i++) {
      %>
      String [] metadef_<%= metadef_fields[i] %> = { 
      <mm:related path="posrel,metadefinition" orderby="posrel.pos" directions="UP"
         > "<mm:field name="<%= "metadefinition." +  metadef_fields[i] %>" />"<mm:last inverse="true">, </mm:last></mm:related>
      };<br/>
      <%
   }
   %>
      for(int i=0; i&lt;metadef_name.length; i++) {<br/>
         %&gt;<br/>
         <!-- print metadefinition -->
         &lt;mm:createnode type="metadefinition" id="&lt;%= "n<mm:field name="number" />_" + i %&gt;" &gt;<br/>
         <%
         for(int i=0; i<metadef_fields.length; i++) {
            %>         
            &lt;mm:setfield name="<%= metadef_fields[i] %>"&gt;&lt;%=  metadef_<%= metadef_fields[i] %>[i] %&gt;&lt;/mm:setfield&gt;<br/>
            <%
         }
         %>
         &lt;/mm:createnode&gt;<br/>
         &lt;mm:createrelation source="n<mm:field name="number" />" destination="&lt;%= "n<mm:field name="number" />_" + i %&gt;" role="posrel" &gt;<br/>
            &lt;mm:setfield name="pos"&gt;&lt;%= i %&gt;&lt;/mm:setfield&gt;<br/>
         &lt;/mm:createrelation&gt;<br/>
         &lt;%<br/>
      }<br/>
   }<br/>
   %&gt;<br/>
   </mm:present>
</mm:listnodes>
<mm:listnodes type="metadefinition">
   <mm:remove referid="contains_metavocs" />
   <mm:related path="related,metavocabulary" orderby="metavocabulary.value" directions="UP">
      <mm:import id="contains_metavocs" />
   </mm:related>
   <mm:present referid="contains_metavocs">
   <!-- find metadefinition -->
   &lt;mm:listnodescontainer type="metadefinition" &gt;<br/>
      &lt;mm:constraint field="name" value="<mm:field name="name"/>" /&gt;<br/>
      &lt;mm:listnodes&gt;<br/>
         &lt;mm:node id="n<mm:field name="number"/>" &gt;<br/>
            &lt;%<br/>
            if(true) {<br/>
               String [] metavalues = { 
               <mm:related path="related,metavocabulary" orderby="metavocabulary.value" directions="UP"
                  > "<mm:field name="metavocabulary.value" />"<mm:last inverse="true">, </mm:last></mm:related>
               };<br/>
               for(int i=0; i&lt;metavalues.length; i++) {<br/>
                  %&gt;<br/>
                  <!-- print metavocabulary -->
                  &lt;mm:createnode type="metavocabulary" id="&lt;%= "n<mm:field name="number" />_" + i %&gt;" &gt;<br/>
                     &lt;mm:setfield name="value"&gt;&lt;%=  metavalues[i] %&gt;&lt;/mm:setfield&gt;<br/>
                  &lt;/mm:createnode&gt;<br/>
                  &lt;mm:createrelation source="n<mm:field name="number" />" destination="&lt;%= "n<mm:field name="number" />_" + i %&gt;" role="related" /&gt;<br/>
               &lt;%<br/>
               }<br/>
            }<br/>
            %&gt;<br/>
         &lt;/mm:node&gt;<br/>
      &lt;/mm:listnodes&gt;<br/>
   &lt;/mm:listnodescontainer&gt;<br/>
   </mm:present>
</mm:listnodes>
<mm:listnodes type="metavocabulary">
   <mm:remove referid="contains_metavocs" />
   <mm:related path="posrel,metavocabulary" orderby="posrel.pos" directions="UP" searchdir="destination">
      <mm:import id="contains_metavocs" />
   </mm:related>
   <mm:present referid="contains_metavocs">
   <!-- find metavocabulary -->
   &lt;mm:listnodescontainer type="metavocabulary" &gt;<br/>
      &lt;mm:constraint field="value" value="<mm:field name="value"/>" /&gt;<br/>
      &lt;mm:listnodes&gt;<br/>
         &lt;mm:node id="n<mm:field name="number"/>" &gt;<br/>
            &lt;%<br/>
            if(true) {<br/>
               String [] metavalues = { 
               <mm:related path="posrel,metavocabulary" orderby="posrel.pos" directions="UP" searchdir="destination"
                  > "<mm:field name="metavocabulary.value" />"<mm:last inverse="true">, </mm:last></mm:related>
               };<br/>
               for(int i=0; i&lt;metavalues.length; i++) {<br/>
                  %&gt;<br/>
                  <!-- print metavocabulary -->
                  &lt;mm:createnode type="metavocabulary" id="&lt;%= "n<mm:field name="number" />_" + i %&gt;" &gt;<br/>
                     &lt;mm:setfield name="value"&gt&lt;%=  metavalues[i] %&gt;&lt;/mm:setfield&gt;<br/>
                  &lt;/mm:createnode&gt;<br/>
                  &lt;mm:createrelation source="n<mm:field name="number" />" destination="&lt;%= "n<mm:field name="number" />_" + i %&gt;" role="posrel" &gt;<br/>
                     &lt;mm:setfield name="pos"&gt;&lt;%= i+1 %&gt;&lt;/mm:setfield&gt;<br/>
                  &lt;/mm:createrelation&gt;<br/>
               &lt;%<br/>
               }<br/>
            }<br/>
            %&gt;<br/>
         &lt;/mm:node&gt;<br/>
      &lt;/mm:listnodes&gt;<br/>
   &lt;/mm:listnodescontainer&gt;<br/>
   <mm:remove referid="isfirstmetavoc" />
   </mm:present>
</mm:listnodes>


&lt;/mm:cloud&gt;<br/>
&lt;/mm:content&gt;<br/>

</mm:cloud>
</mm:content>
Done.

