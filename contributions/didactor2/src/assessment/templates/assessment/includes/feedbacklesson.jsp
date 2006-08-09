        <mm:listnodes type="problemtypes" orderby="pos">
          <mm:field name="key" jspvar="problem_type" vartype="String">
            <%= problem_type %><br/><br/>
            <mm:related path="related,problems,posrel,learnblocks"
                constraints="<%= "learnblocks.number=" + lessonId %>" fields="problems.number" distinct="true">
              <mm:field name="problems.name" jspvar="dummy" vartype="String" write="false">
                <%= ( "".equals(dummy) ? "&nbsp;" : dummy )%>
              </mm:field><br/>
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
                  %><img src="<mm:treefile page="<%= "/assessment/gfx/icon_rating_" + rating + ".gif"%>" objectlist="$includePath" 
                       referids="$referids"/>" border="0" title="<%=problemWeights[rating] %>" alt="<%=problemWeights[rating] %>" /><%
                }
              %>
              </mm:field><br/>
            </mm:related>
          </mm:field><br/><br/>
        </mm:listnodes>