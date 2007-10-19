<mm:related path="posrel,formulierveld" orderby="posrel.pos" directions="UP">
	<mm:first><table cellspacing="0" cellpadding="0" border="0" style="width:auto;"></mm:first
   ><tr><td style="padding-bottom:10px;">
	<mm:field name="formulierveld.label" />
   <mm:node element="formulierveld" jspvar="thisQuestion"><%
   				
	String questions_number = thisQuestion.getStringValue("number"); 
   String questions_type = thisQuestion.getStringValue("type");
	boolean isRequired = thisQuestion.getStringValue("verplicht").equals("1");
	
   if(numberOrdered>1) { %> (item nummer <%= i+1 %>)<% } 
   if(isRequired) { %>&nbsp;(*)<% } %><br><%

   // *** radio buttons or checkboxes
   // *** todo: insert something more intelligent for HtmlCleaner.filterEntities(answer).replaceAll("'","rsquo;").replaceAll("&rsquo;","`") ***
   if(questions_type.equals("4")||questions_type.equals("5")) { 
       %><mm:related path="posrel,formulierveldantwoord"
               orderby="posrel.pos" directions="UP"
           ><mm:field name="formulierveldantwoord.waarde" jspvar="answer" vartype="String" write="false"><%
           if(questions_type.equals("4")) { 
               %><input type="radio" name="q_<%= thisForm %>_<%= questions_number %>_<%= i 
                   %>" value="<%= HtmlCleaner.filterEntities(answer).replaceAll("'","rsquo;").replaceAll("&rsquo;","`") %>" ><%
           } else if(questions_type.equals("5")) {
               %><input type="checkbox" name="q_<%= thisForm %>_<%= questions_number %>_<%= i %>_<mm:field name="answer.number" 
                   />" value="<%= HtmlCleaner.filterEntities(answer).replaceAll("'","&rsquo;").replaceAll("&rsquo;","`") %>" ><%
           } 
           %><%= answer 
           %></mm:field
       ></mm:related><%
   }

   // *** dropdown
   if(questions_type.equals("3")) { 
       %><select name="q_<%= thisForm %>_<%= questions_number %>_<%= i %>">
           <option>...</option>
           <mm:related path="posrel,formulierveldantwoord"
                   orderby="posrel.pos" directions="UP"
                   ><mm:field name="formulierveldantwoord.waarde" jspvar="answer" vartype="String" write="false">
				<option value="<%= HtmlCleaner.filterEntities(answer).replaceAll("'","&rsquo;").replaceAll("&rsquo;","`") %>"><%= answer %>
			  </option>
		        </mm:field
		></mm:related
      ></select><%
   } 

   // *** textarea and textline
   if(questions_type.equals("1")||questions_type.equals("2")) {
       if(questions_type.equals("1")) { 
           %><textarea rows="4" name="q_<%= thisForm %>_<%= questions_number %>_<%= i %>" wrap="physical"></textarea><%
       } else {
           %><input type="text" name="q_<%= thisForm %>_<%= questions_number %>_<%= i %>"><%
       }
   }

   // *** date
   if(questions_type.equals("6")) { // *** create input fields for day, month and year
       %><table cellspacing="0" cellpadding="0" border="0"><tr>
           <td style="width:64px;">
               Dag<br>
               <input type="text" name="q_<%= thisForm %>_<%= questions_number %>_<%= i %>_day" style="width:54px;"><br>
           </td><td style="width:128px;">
               Maand<br>
               <select name="q_<%= thisForm %>_<%= questions_number %>_<%= i %>_month" style="width:118px;">
                   <option>...</option><%
                   for(int m=0; m<12; m++) { %><option value="<%= months_lcase[m] %>"><%= months_lcase[m] %></option><% } 
               %></select><br>
           </td><td>
               Jaar<br>
               <input type="text" name="q_<%= thisForm %>_<%= questions_number %>_<%= i %>_year" style="width:54px;"><br>
           </td>
       </tr></table><%
   } 

   %>
	</mm:node>
	</td></tr>
   <mm:last></table></mm:last>
</mm:related>