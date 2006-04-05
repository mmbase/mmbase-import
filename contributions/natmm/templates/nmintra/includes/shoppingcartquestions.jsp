<mm:related path="posrel,formulierveld" orderby="posrel.pos" directions="UP"><% 
   String questions_type = ""; 
   %><mm:field name="formulierveld.type" jspvar="dummy" vartype="String" write="false"
       ><% questions_type = dummy; 
   %></mm:field><%
   boolean isRequired = false; 
   %><mm:field name="formulierveld.verplicht" jspvar="dummy" vartype="String" write="false"
       ><% isRequired = dummy.equals("1"); 
   %></mm:field><%
   String questions_number = ""; 
   %><mm:field name="formulierveld.number" jspvar="dummy" vartype="String" write="false"
       ><% questions_number= dummy; 
   %></mm:field
   ><mm:first><table cellspacing="0" cellpadding="0" border="0" style="width:auto;"></mm:first
   ><tr><td style="padding-bottom:10px;">
   <mm:field name="formulierveld.label" /><%
   if(numberOrdered>1) { %> (item nummer <%= i+1 %>)<% } 
   if(isRequired) { %>&nbsp;(*)<% } %><br><%

   // *** radio buttons or checkboxes
   // *** todo: insert something more intelligent for HtmlCleaner.filterEntities(answer).replaceAll("'","rsquo;").replaceAll("&rsquo;","`") ***
   if(questions_type.equals("4")||questions_type.equals("5")) { 
       %><mm:list nodes="<%= questions_number %>" path="formulierveld,posrel,formulierveldantwoord"
               orderby="posrel.pos" directions="UP"
           ><mm:field name="formulierveldantwoord.waarde" jspvar="answer" vartype="String" write="false"><%
           if(questions_type.equals("4")) { 
               %><input type="radio" name="q_<%= thisPool %>_<%= questions_number %>_<%= i 
                   %>" value="<%= HtmlCleaner.filterEntities(answer).replaceAll("'","rsquo;").replaceAll("&rsquo;","`") %>" ><%
           } else if(questions_type.equals("5")) {
               %><input type="checkbox" name="q_<%= thisPool %>_<%= questions_number %>_<%= i %>_<mm:field name="answer.number" 
                   />" value="<%= HtmlCleaner.filterEntities(answer).replaceAll("'","&rsquo;").replaceAll("&rsquo;","`") %>" ><%
           } 
           %><%= answer 
           %></mm:field
       ></mm:list><%
   }

   // *** dropdown
   if(questions_type.equals("3")) { 
       %><select name="q_<%= thisPool %>_<%= questions_number %>_<%= i %>">
           <option>...</option>
           <mm:list nodes="<%= questions_number %>" path="formulierveld,posrel,formulierveldantwoord"
                   orderby="posrel.pos" directions="UP"
                   ><mm:field name="formulierveldantwoord.waarde" jspvar="answer" vartype="String" write="false">
				<option value="<%= HtmlCleaner.filterEntities(answer).replaceAll("'","&rsquo;").replaceAll("&rsquo;","`") %>"><%= answer %>
			  </option>
		        </mm:field
		></mm:list
       ></select><%
   } 

   // *** textarea and textline
   if(questions_type.equals("1")||questions_type.equals("2")) {
       if(questions_type.equals("1")) { 
           %><textarea rows="4" name="q_<%= thisPool %>_<%= questions_number %>_<%= i %>" wrap="physical"></textarea><%
       } else {
           %><input type="text" name="q_<%= thisPool %>_<%= questions_number %>_<%= i %>"><%
       }
       %><%
   }

   // *** date
   if(questions_type.equals("6")) { // *** create input fields for day, month and year
       %><table cellspacing="0" cellpadding="0" border="0"><tr>
           <td style="width:64px;">
               Dag<br>
               <input type="text" name="q_<%= thisPool %>_<%= questions_number %>_<%= i %>_day" style="width:54px;"><br>
           </td><td style="width:128px;">
               Maand<br>
               <select name="q_<%= thisPool %>_<%= questions_number %>_<%= i %>_month" style="width:118px;">
                   <option>...</option><%
                   for(int m=0; m<12; m++) { %><option value="<%= months_lcase[m] %>"><%= months_lcase[m] %></option><% } 
               %></select><br>
           </td><td>
               Jaar<br>
               <input type="text" name="q_<%= thisPool %>_<%= questions_number %>_<%= i %>_year" style="width:54px;"><br>
           </td>
       </tr></table><%
   } 

   %></td></tr>
   <mm:last></table></mm:last>
</mm:related>