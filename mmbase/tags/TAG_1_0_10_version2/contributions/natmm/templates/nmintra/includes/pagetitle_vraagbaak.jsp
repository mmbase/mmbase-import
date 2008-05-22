<table border="0" cellpadding="0" cellspacing="0">
   <tr>
      <%-- <td><img src="media/rdcorner.gif" style="filter:alpha(opacity=75)"></td> --%>
      <td class="transperant" style="width:100%;"><img src="media/spacer.gif" width="1" height="6"><br>
      
         <mm:node number="<%= paginaID %>">
           <mm:field name="titel_zichtbaar">
              <mm:compare value="0" inverse="true">
               <div align="right">      
                  <span class="pageheader"><span class="dark"><mm:node number="<%= paginaID %>"><mm:field name="titel"/></mm:node
                  ><mm:present referid="extratext"><mm:write referid="extratext"/></mm:present
                  ></span></span>
               </div>
              </mm:compare>
           </mm:field>     
         </mm:node>                  

      </td>
      <td class="transperant"><img src="media/spacer.gif" width="10" height="28"></td>
   </tr>
</table>