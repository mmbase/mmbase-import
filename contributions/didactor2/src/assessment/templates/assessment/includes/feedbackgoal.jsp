<tr>
  <td colspan="2">
    <div class="grayBar" style="width:100%;">
      <img src="<mm:treefile page="/assessment/gfx/minus.gif" objectlist="$includePath" 
           referids="$referids"/>" border="0" title="<di:translate key="assessment.show_goals" />" 
           alt="<di:translate key="assessment.show_goals" />" 
           onClick="toggle('goals');" id="toggle_imagegoals"/>
      <di:translate key="assessment.goals" />
    </div>
  </td>
</tr>
<tr id="toggle_divgoals">
  <td colspan="2">
    <mm:node number="<%= ownerId %>">
      <mm:relatednodes type="goals" path="posrel,goals" orderby="posrel.pos">
        <mm:first><table style="FONT-SIZE : 1.0em"></mm:first>
          <mm:field name="number" jspvar="goal_number" vartype="String" write="false">
            <tr>
              <td>
                <img src="<mm:treefile page="/assessment/gfx/plus.gif" objectlist="$includePath" 
                     referids="$referids"/>" border="0" title="<di:translate key="assessment.show_goal" />"
                     alt="<di:translate key="assessment.show_goal" />" 
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
  </td>
</tr>
