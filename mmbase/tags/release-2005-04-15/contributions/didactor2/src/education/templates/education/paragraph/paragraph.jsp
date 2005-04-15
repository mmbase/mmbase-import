   <table border="0" cellspacing="0" cellpadding="0">
      <tr>
         <td style="padding-bottom:10px;">
            <mm:field name="showtitle">
               <mm:compare value="1">
                  <mm:field name="name" jspvar="sTitle" vartype="String" write="false">
                     <h1><%= sTitle %></h1>
                  </mm:field>
               </mm:compare>
            </mm:field>

            <!-- image -->
            <%@include file="/education/paragraph/relatedimage.jsp"%>

            <mm:related path="posrel,paragraphs" orderby="posrel.pos" directions="UP">
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

                           <!-- image -->
                           <%@include file="/education/paragraph/relatedimage.jsp"%>

                           <mm:relatednodes type="attachments">
                              <mm:field name="mimetype" write="false">
                                 <a href="<mm:attachment/>"><mm:field name="filename"/></a>
                              </mm:field>
                           </mm:relatednodes>

                           <mm:field name="body"><mm:isnotempty><mm:write /></mm:isnotempty></mm:field>
                        </td>
                     </tr>
                  </table>
               </mm:node>
            </mm:related>

         </td>
      </tr>
   </table>
