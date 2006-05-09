<%! public void putProductGroup(TreeMap product_groups, String thisProductGroup, String thisShop_item)
   {  TreeSet shopItems = (TreeSet) product_groups.get(thisProductGroup);
      if(shopItems==null) {
         shopItems = new TreeSet();
      }
      shopItems.add(thisShop_item);
      product_groups.put(thisProductGroup, shopItems); 
   }
   public void putEmails(TreeMap emails, String thisEmails, String thisShop_item)
   {  thisEmails += ";";
      int semicolon = thisEmails.indexOf(";"); 
      while(semicolon>-1) {
         String emailAddress = thisEmails.substring(0,semicolon).trim();
         TreeSet shopItems = (TreeSet) emails.get(emailAddress);
         if(shopItems==null) {
            shopItems = new TreeSet();
         }
         shopItems.add(thisShop_item);
         emails.put(emailAddress, shopItems);
         
         thisEmails = thisEmails.substring(semicolon+1);
         semicolon = thisEmails.indexOf(";");
      }
   }
%><%
String responseText = "";
String thisPool = null;   
%><mm:node number="<%= thisShop_item %>"
	><mm:relatednodes type="pagina" max="1" jspvar="thisPage"
	   ><mm:field name="number" jspvar="thisProductGroup" vartype="String" write="false"><%
		if(thisProductGroup!=null&&!thisProductGroup.equals("")) {
			putProductGroup(product_groups,thisProductGroup,thisShop_item);
		} else { 
			%><mm:field name="title" jspvar="thisPageTitle" vartype="String" write="false"><%
			if(thisPageTitle!=null&&!thisPageTitle.equals("")) {
	               putProductGroup(product_groups,thisPageTitle,thisShop_item);
			} 
			%></mm:field><%
		} 
	%></mm:field
	></mm:relatednodes
	><mm:field name="quotetitle" jspvar="thisEmails" vartype="String" write="false"
		><mm:isnotempty><% putEmails(emails, thisEmails, thisShop_item); %></mm:isnotempty
      ><mm:isempty
         ><mm:remove referid="emailfound"
         /><mm:related path="posrel,medewerkers" max="1"
   		     ><mm:field name="medewerkers.email" jspvar="owner_email" vartype="String" write="false"><%
                 responseText += "Er is geen email-adres gevonden voor het onderstaande product. " 
                     + "De bestelling wordt daarom verstuurd naar de eigenaar van het product: "
                     + owner_email + "<br><br>";
                 putEmails(emails, owner_email, thisShop_item);
      		  %></mm:field
   	        ><mm:import id="emailfound" 
   	   /></mm:related
   	   ><mm:notpresent referid="emailfound"><%
              responseText += "Er is geen email-adres gevonden voor het onderstaande product. " 
                  + "De bestelling wordt daarom verstuurd naar het standaard email adres van FZ: "
                  + defaultFZAddress + "<br><br>";
              putEmails(emails, defaultFZAddress, thisShop_item);
         %></mm:notpresent
      ></mm:isempty
   ></mm:field
	><mm:field name="title" jspvar="dummy" vartype="String" write="false"><%
	   responseText += "<b>" + numberOfItems + " x " + dummy + "</b><br>\n";
	%></mm:field
	><mm:related path="posrel,pools" orderby="posrel.pos" directions="UP" searchdir="destination"
		><mm:node element="pools"
			><mm:field name="number" jspvar="dummy" vartype="String" write="false"><%
			   thisPool = dummy; 
			%></mm:field
			><mm:field name="name" jspvar="pools_name" vartype="String" write="false"><%
				responseText += "<br>" + pools_name + "\n"; 
			%></mm:field><%
			
			int numberOrdered = 1;
			%><mm:field name="view"
			   ><mm:compare value="shop_repeat"><%
			      numberOrdered = Integer.parseInt(numberOfItems);
			   %></mm:compare
			></mm:field><%
   	   for(int i =0; i< numberOrdered; i++) { 
      	   %><mm:related path="posrel,formulierveld" orderby="posrel.pos" directions="UP"
   				><mm:first><% responseText += "<ol>\n<li>"; %></mm:first
   				><mm:first inverse="true"><% responseText += "</li>\n<li>"; %></mm:first><%
   				
   				String questions_title = ""; 
   				%><mm:field name="formulierveld.label" jspvar="dummy" vartype="String" write="false"
   						><%  questions_title = dummy;
   				%></mm:field><%
   				if(numberOrdered>1) { questions_title += " (item nummer " + (i+1) + ")"; }
   				
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
   				%></mm:field><%
   
   				responseText += questions_title + " : "; 
   
   				if(questions_type.equals("6")) { // *** date ***
   
   					responseText += "(Dag,Maand,Jaar) ";
   					String answerValue = getResponseVal("q_" + thisPool + "_" + questions_number + "_" + i + "_day",postingStr);
   					if(answerValue.equals("")) {
   						responseText += noAnswer;
   						if(isRequired) {
   							isValidAnswer = false;
   							warningMessage += "&#149; Dag in " + questions_title + "<br>";
   						}
   					} else {
   						responseText += answerValue;
   					}
   					answerValue = getResponseVal("q_" + thisPool + "_" + questions_number + "_" + i + "_month",postingStr);
   					if(answerValue.equals("")) {
   						responseText += ", " + noAnswer;
   						if(isRequired) {
   							isValidAnswer = false;
   							warningMessage += "&#149; Maand in " + questions_title + "<br>";
   						}
   					} else {
   						responseText += ", " + answerValue;
   					}
   					answerValue = getResponseVal("q_" + thisPool + "_" + questions_number + "_" + i + "_year",postingStr);
   					if(answerValue.equals("")) {
   						responseText +=  ", " + noAnswer;
   						if(isRequired) {
   							isValidAnswer = false;
   							warningMessage += "&#149; Jaar in " + questions_title + "<br>";
   						}
   					} else {
   						responseText +=  ", " + answerValue;
   					}
   
   				} else if(questions_type.equals("5")) { // *** check boxes ***
   					boolean hasSelected = false; 
   					%><mm:list nodes="<%= questions_number %>" path="formulierveld,posrel,formulierveldantwoord" orderby="posrel.pos" directions="UP"
      					><mm:field name="formulierveldantwoord.number" jspvar="answer_number" vartype="String" write="false"><%
   					   String answerValue = getResponseVal("q_" + thisPool + "_" + questions_number + "_" + i + "_" + answer_number,postingStr);
   						if(!answerValue.equals("")) { 
   							hasSelected = true;
   							responseText += "<br>&#149; " + answerValue;
   						}
   						%></mm:field
   					></mm:list><%
   					if(!hasSelected) {
   						responseText += noAnswer;
   						if(isRequired) {
   							isValidAnswer = false;
   							warningMessage += "&#149; " + questions_title + "<br>";
   						}
   					} 
   
   				} else { // *** textarea, textline, dropdown, radio buttons ***
   					String answerValue = getResponseVal("q_" + thisPool + "_" + questions_number + "_" + i,postingStr);
   					if(answerValue.equals("")) {
   						responseText += noAnswer;
   						if(isRequired) {
   							isValidAnswer = false;
   							warningMessage += "&#149; " + questions_title + "<br>";
   						}
   					}
   					responseText += answerValue;
   					// *** check whether this question provides the client email address ***
					   // *** the object cloud has to contain a question with alias client_email ***
   					%><mm:list nodes="client_email" path="formulierveld" constraints="<%= "formulierveld.number=" + questions_number %>"><%
   					      clientEmail = answerValue;
   					%></mm:list><%
   					
   					// *** check whether this question provides the client email address ***
					   // *** the object cloud has to contain a question with alias client_department ***
   					%><mm:list nodes="client_department" path="formulierveld" constraints="<%= "formulierveld.number=" + questions_number %>"><%
   					      clientDept = answerValue;
   					%></mm:list><%
   				} 
   				%><mm:last><% responseText += "</li>\n</ol>\n"; %></mm:last
   			></mm:related><%
		   }
		   
		%></mm:node
></mm:related><% 
responses.put(thisShop_item, responseText);
%></mm:node>
