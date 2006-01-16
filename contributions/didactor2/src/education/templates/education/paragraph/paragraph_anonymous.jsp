<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>
<mm:cloud jspvar="cloud">
<mm:import externid="node_id"/>
<mm:import externid="path_segment"/>
<mm:node referid="node_id">

   <!-- image -->
   <%@include file="relatedimage.jsp"%>

   <mm:related path="posrel,paragraphs" orderby="posrel.pos" directions="UP">
      <mm:first inverse="true">
         <br><% //This is a padding for the next paragraph %>
      </mm:first>
      <mm:node element="paragraphs">
         <table border="0" cellspacing="0" cellpadding="0">
            <tr>
               <td style="padding-bottom:10px;">
                  <mm:field name="showtitle">
                     <mm:compare value="1">
                        <mm:field name="title" jspvar="paragraphs_title" vartype="String" write="false">
                           <h4><%= paragraphs_title %></h4>
                        </mm:field>
                     </mm:compare>
                  </mm:field>

                  <!-- <image> -->
                  <%@include file="relatedimage.jsp"%>
                  <!-- </image> -->

                  <table border="0" cellpadding="0" cellspacing="0">
                     <tr>
                        <td>
                           <mm:field name="body"><mm:isnotempty><mm:write /></mm:isnotempty></mm:field>
                        </td>
                     </tr>
                  </table>
               </td>
            </tr>
         </table>

         <style type="text/css">
            .urls{color:0000FF;
                }
         </style>

         <mm:import id="there_is_additional_information" reset="true">false</mm:import>
         <mm:related path="posrel,attachments">
            <mm:import id="there_is_additional_information" reset="true">true</mm:import>
         </mm:related>
         <mm:related path="posrel,urls">
            <mm:import id="there_is_additional_information" reset="true">true</mm:import>
         </mm:related>

         <mm:compare referid="there_is_additional_information" value="true">
            <table border="0" cellpadding="0" cellspacing="0" style="font-size:11px; color:#9C9C9C"><tr><th align="left">Meer informatie</th>

               <mm:related path="posrel,urls" orderby="posrel.pos">
                  <tr>
                     <td>
                        <%@include file="relatedurl.jsp" %>
                     </td>
                  </tr>
               </mm:related>

               <mm:related path="posrel,attachments" orderby="posrel.pos">
                  <tr>
                     <td><img src="<mm:write referid="path_segment"/>gfx/attachment.gif"/> download:
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
