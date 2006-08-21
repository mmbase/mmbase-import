<mm:listnodes type="problemtypes" orderby="pos">
  <mm:field name="number" jspvar="problemtypeId" vartype="String" write="false">
    <mm:field name="key" jspvar="problem_type" vartype="String" write="false">
      <tr>
        <td colspan="2">
          <div class="grayBar" style="width:100%;">
            <mm:relatednodes type="problems" max="1">
              <img src="<mm:treefile page="/assessment/gfx/minus.gif" objectlist="$includePath" 
                referids="$referids"/>" border="0" title="<di:translate key="assessment.show_problems" />"
                alt="<di:translate key="assessment.show_problems" />"
                onClick="toggle(<%= problemtypeId %>);" id="toggle_image<%= problemtypeId %>"/>
            </mm:relatednodes>
            <di:translate key="<%= "assessment." + problem_type %>" />
          </div>
        </td>
      </tr>
      <tr id="toggle_div<%= problemtypeId %>">
        <td colspan="2">
          <mm:related path="related,problems,posrel,people" constraints="<%= "people.number=" + ownerId %>"
              fields="problems.number" distinct="true">
            <mm:field name="problems.number" jspvar="problemId" vartype="String" write="false">
              <img src="<mm:treefile page="/assessment/gfx/plus.gif" objectlist="$includePath" 
                   referids="$referids"/>" border="0" title="<di:translate key="assessment.show_problem" />"
                   alt="<di:translate key="assessment.show_problem" />" 
                   onClick="toggle(<%= problemId %>);" id="toggle_image<%= problemId %>"/>
              <mm:field name="problems.name" jspvar="dummy" vartype="String" write="false">
                <b><%= ( "".equals(dummy) ? "&nbsp;" : dummy ) %></b>
              </mm:field><br/>
              <div id="toggle_div<%=problemId %>" style="padding-left:15px; display:none">
                <% int rating = -1; // not rated %>
                <mm:list nodes="<%= problemId %>" path="problems,posrel,learnblocks" 
                    constraints="<%= "learnblocks.number=" + lessonId %>" fields="posrel.pos">
                  <mm:field name="posrel.pos" jspvar="problem_weight" vartype="Integer" write="false">
                  <%
                    try {
                      rating = problem_weight.intValue();
                    }
                    catch (Exception e) {
                    }
                  %>
                  </mm:field>
                </mm:list>
                <di:translate key="assessment.how_much_trouble" />
                <%
                  if (rating < 0) {
                    %><di:translate key="assessment.not_filled_in" /><%
                  } else {
                    %><%=problemWeights[rating]%><%
                  }
                %>
              </div>
            </mm:field>
          </mm:related>
        </td>
      </tr>
    </mm:field>
  </mm:field>
</mm:listnodes>
