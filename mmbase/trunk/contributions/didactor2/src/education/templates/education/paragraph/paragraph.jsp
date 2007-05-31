<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@taglib  prefix="di-t" tagdir="/WEB-INF/tags/di/core" 
%><mm:cloud method="delegate">
<mm:import externid="node_id" required="true"/>
<mm:import externid="path_segment"/>
<mm:node referid="node_id">

   <mm:nodeinfo type="type">
     <%-- to avoid warning in the log, because learnblocks,pos2rel,images does not exist --%>
     <mm:compare value="learnblocks" inverse="true"> 
       <jsp:directive.include file="relatedimage.jsp" />
     </mm:compare>
   </mm:nodeinfo>

   <mm:related path="posrel,paragraphs" orderby="posrel.pos" directions="UP">
     <mm:first inverse="true">
       <br /><% //This is a padding for the next paragraph MM: WTF! %>
     </mm:first>
     <mm:node element="paragraphs">
       <table border="0" cellspacing="0" cellpadding="0">
         <tr>
           <td style="padding-bottom:10px;">
             <mm:field name="showtitle">
               <mm:compare value="1">
                 <h4><mm:field name="title" /></h4>
               </mm:compare>
             </mm:field>
             
             <di-t:pos2relatedimage />
             
             <table border="0" cellpadding="0" cellspacing="0">
               <tr>
                 <td>
                   <mm:field name="body"><mm:isnotempty><mm:write escape="none" /></mm:isnotempty></mm:field>
                 </td>
               </tr>
             </table>
           </td>
         </tr>
       </table>
       
       <!-- sigh, WTF -->
       <style type="text/css">
         .urls{color:0000FF; }
       </style>
       
       <!-- more WTF -->
       <mm:import id="there_is_additional_information" reset="true">false</mm:import>
       <mm:related path="posrel,attachments">
         <mm:import id="there_is_additional_information" reset="true">true</mm:import>
       </mm:related>
       <mm:related path="posrel,urls">
         <mm:import id="there_is_additional_information" reset="true">true</mm:import>
       </mm:related>
       
       <mm:compare referid="there_is_additional_information" value="true">
         <table border="0" cellpadding="0" cellspacing="0" style="font-size:11px; color:#9C9C9C"><tr><th align="left"><di:translate key="education.more_information" /></th>
         
         <mm:related path="posrel,urls" orderby="posrel.pos">
           <tr>
             <td><jsp:directive.include file="relatedurl.jsp" /></td>
           </tr>
         </mm:related>
         
         <mm:related path="posrel,attachments" orderby="posrel.pos">
           <tr>
             <td>
               <img src="${path_segment}gfx/attachment.gif"/> 
               <di:translate key="education.download" />:
               <mm:node element="attachments">
                 <a href="<mm:attachment />" target="_blank" class="urls"><mm:field name="filename" /></a><mm:last inverse="true"><span class="urls">, </span></mm:last>
               </mm:node>
             </td>
           </tr>
         </mm:related>
       </table>
     </mm:compare>
   </mm:node>
 </mm:related>
</mm:node>
</mm:cloud>
