<% 
   String currentLesson = "-1";
   String lastClosedLesson = "-1";
   int iLesson = 0; // index of current lesson
%>
<mm:node number="$assessment_education" notfound="skip">
  <mm:relatednodes type="learnblocks" path="posrel,learnblocks" orderby="posrel.pos">
    <% boolean hasFeedback = false; %>
    <mm:relatedcontainer path="classrel,people">
      <mm:constraint field="people.number" value="$user"/>
      <mm:related>
        <mm:node element="classrel">
          <mm:relatednodes type="popfeedback">
            <% hasFeedback = true; %>
          </mm:relatednodes>
        </mm:node>
      </mm:related>
    </mm:relatedcontainer>
    <mm:field name="number" jspvar="dummy" vartype="String" write="false">
      <% 
         if(!hasFeedback && "-1".equals(currentLesson)) { currentLesson = dummy; } 
         if(hasFeedback) { lastClosedLesson = dummy; }
         if("-1".equals(currentLesson)) { iLesson++; }
      %>
    </mm:field>
  </mm:relatednodes>
</mm:node>
