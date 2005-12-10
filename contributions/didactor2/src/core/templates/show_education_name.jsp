<%= arrstrEducation[1] %>
<%
   if(arrstrEducation[2] != null)
   {
      %>
         <mm:node number="<%= arrstrEducation[2] %>" jspvar="nodeClass">
            (<%
               String sClassName = (String) nodeClass.getValue("name");
               if(sClassName.length() > 7)
               {
                  out.print(sClassName.substring(0, 7));
                  %>...<%
               }
               else
               {
                  out.print(sClassName);
               }
            %>)
         </mm:node>
      <%
   }
%>
