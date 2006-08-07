Goal<br/>
        <mm:node number="<%= ownerId %>">
          <mm:relatednodes type="goals" path="posrel,goals" orderby="posrel.pos">
            <mm:first><table></mm:first>
              <mm:field name="number" jspvar="goal_number" vartype="String" write="false">
                <tr>
                  <td>
                    <img src="<mm:treefile page="/assessment/gfx/plus.gif" objectlist="$includePath" 
                         referids="$referids"/>" border="0" title="show goal" alt="show goal" 
                         onClick="toggle(<%=goal_number %>);" id="toggle_image<%=goal_number %>"/>
                  </td>
                  <td>
                    <mm:field name="name" jspvar="dummy" vartype="String" write="false">
                      <%= ( "".equals(dummy) ? "&nbsp;" : dummy )%>
                    </mm:field>
                  </td>
                </tr>
                <tr id="toggle_div<%=goal_number %>" style="display:none">
                  <td>&nbsp;</td>
                  <td>
                    <mm:field name="description" jspvar="dummy" vartype="String" write="false">
                      <%= ( "".equals(dummy) ? "&nbsp;" : dummy )%>
                    </mm:field>
                  </td>
                </tr>
              </mm:field>
            <mm:last></table></mm:last>
          </mm:relatednodes>
        </mm:node>
