<mm:listnodes type="problemtypes" orderby="pos">
  <mm:field name="number" jspvar="problemtypeId" vartype="String" write="false">
    <mm:field name="key" jspvar="problem_type" vartype="String" write="false">
      <tr>
        <td colspan="2">
          <div class="grayBar" style="width:100%;">
            <img src="<mm:treefile page="/assessment/gfx/minus.gif" objectlist="$includePath" 
                 referids="$referids"/>" border="0" title="show problems" alt="show problems"
                 onClick="toggle(<%= problemtypeId %>);" id="toggle_image<%= problemtypeId %>"/>
            <%= problem_type %>
          </div>
        </td>
      </tr>
      <tr id="toggle_div<%= problemtypeId %>">
        <td colspan="2">
          <mm:related path="related,problems,posrel,people" constraints="<%= "people.number=" + ownerId %>"
              fields="problems.number" distinct="true">
            <mm:field name="problems.number" jspvar="problemId" vartype="String" write="false">
              <mm:list nodes="<%= problemId %>" path="problems,posrel,learnblocks" 
                  constraints="<%= "learnblocks.number=" + lessonId %>" fields="posrel.pos">
                <img src="<mm:treefile page="/assessment/gfx/plus.gif" objectlist="$includePath" 
                     referids="$referids"/>" border="0" title="show problem" alt="show problem" 
                     onClick="toggle(<%= problemId %>);" id="toggle_image<%= problemId %>"/>
                <mm:field name="problems.name" jspvar="dummy" vartype="String" write="false">
                  <b><%= ( "".equals(dummy) ? "&nbsp;" : dummy ) %></b>
                </mm:field><br/>
                <div id="toggle_div<%=problemId %>" style="padding-left:15px; display:none">
                  <%= problem_type %><br/>
                  <% int rating = -1; // not rated %>
                  <mm:field name="posrel.pos" jspvar="problem_weight" vartype="Integer" write="false">
                  <%
                    try {
                      rating = problem_weight.intValue();
                    }
                    catch (Exception e) {
                    }
                    if (rating < 0) {
                      %>&nbsp;<%
                    } else {
                      %>
                      How much trouble does it cause you? <%=problemWeights[rating]%><%
                    }
                  %>
                  </mm:field>
                </div>
              </mm:list>
            </mm:field>
          </mm:related>
        </td>
      </tr>
    </mm:field>
  </mm:field>
</mm:listnodes>
