<td class="listItem">
  <mm:field name="problems.name" jspvar="dummy" vartype="String" write="false">
    <%= ( "".equals(dummy) ? "&nbsp;" : dummy.replaceAll("\\n", "<br/>\n") ) %>
  </mm:field>
</td>
<% count = 0; %>
<mm:node number="$assessment_education" notfound="skip">
  <mm:relatednodes type="learnblocks" path="posrel,learnblocks" orderby="posrel.pos"  max="<%= "" + lessonsNum %>">
<%
    int rating = -1; // not rated
    String problemConstraint = "problems.number=" + problem_number;
%>
    <mm:related path="posrel,problems" constraints="<%= problemConstraint %>">
      <mm:field name="posrel.pos" jspvar="problem_weight" vartype="Integer" write="false">
<%
        try {
          rating = problem_weight.intValue();
        }
        catch (Exception e) {
        }
%>
      </mm:field>
    </mm:related>
    <td class="listItem" style="padding:0 0 0 0;text-align:center;" <%= styles.get(count) %>>
<%
      if (rating < 0) {
        %>&nbsp;<%
      } else {
        %><img src="<mm:treefile page="<%= "/assessment/gfx/icon_rating_" + rating + ".gif"%>" objectlist="$includePath" 
               referids="$referids"/>" border="0" title="<%=problemWeights[rating] %>" alt="<%=problemWeights[rating] %>" /><%
      }
      count++;
%>
    </td>
  </mm:relatednodes>
</mm:node>
