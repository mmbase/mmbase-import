<%
// ********************* create the javascript for posting the values *******************
// pre-condition: variables allShop_items, thisForm must be defined.

if(true) { 
%><script type="text/javascript">
var needToConfirm = true;
window.onbeforeunload = confirmExit;
function confirmExit()
{
   if (needToConfirm) {
      return 'De reeds ingevoerde gegevens worden dan niet opgeslagen.\nAls u nog niets heeft ingevoerd klik dan op "Ok"';
   }
}

function changeIt(url) {
	var href = "&pst=";
<mm:list nodes="<%= allShop_items %>" path="items,posrel,formulier" orderby="formulier.number" directions="UP" 
	searchdir="destination" distinct="yes" fields="formulier.number"
	><mm:node element="formulier"
		><mm:field name="number" jspvar="dummy" vartype="String" write="false"
			><% thisForm = dummy; 
		%></mm:field
		
		><%@include file="nordered.jsp" %><%
		for(int i =0; i< numberOrdered; i++) {
		%><mm:related path="posrel,formulierveld"
			orderby="posrel.pos" directions="UP"
			><mm:node element="formulierveld" jspvar="thisQuestion"><%

			   String questions_number = thisQuestion.getStringValue("number");
				String questions_type = thisQuestion.getStringValue("type");

				if(questions_type.equals("6")) { // *** date ***
				%>	var answer = escape(document.emailform.elements["q_<%= thisForm %>_<%= questions_number %>_<%= i %>_day"].value);
					if(answer != '') {
						href += "|q_<%= thisForm %>_<%= questions_number %>_<%= i %>_day=" + answer;
					}
					var answer = escape(document.emailform.elements["q_<%= thisForm %>_<%= questions_number %>_<%= i %>_month"].value);
					if(answer != '') {
						href += "|q_<%= thisForm %>_<%= questions_number %>_<%= i %>_month=" + answer;
					}
					var answer = escape(document.emailform.elements["q_<%= thisForm %>_<%= questions_number %>_<%= i %>_year"].value);
					if(answer != '') {
						href += "|q_<%= thisForm %>_<%= questions_number %>_<%= i %>_year=" + answer;
					}
				<% 
				} else if(questions_type.equals("5")) { // *** check boxes ***
					%><mm:related path="posrel,formulierveldantwoord" orderby="posrel.pos" directions="UP">
						var answer = document.emailform.q_<%= thisForm %>_<%= questions_number %>_<%= i %>_<mm:field name="formulierveldantwoord.number" />;
						if(answer.checked) {
							href += "|q_<%= thisForm %>_<%= questions_number %>_<%= i %>_<mm:field name="formulierveldantwoord.number" />=" + answer.value;
						}
					</mm:related><% 
				} else if(questions_type.equals("4")) { // *** radio buttons ***
				%>	var answer = document.emailform.q_<%= thisForm %>_<%= questions_number %>_<%= i %>;
					for (var i=0; i < answer.length; i++){
						if (answer[i].checked) {
							var rad_val = answer[i].value;
							if(rad_val != '') {
								href += "|q_<%= thisForm %>_<%= questions_number %>_<%= i %>=" + rad_val;
							}
						}
					}
				<% }

				else if(questions_type.equals("1")||questions_type.equals("2")||questions_type.equals("3")) { 
				// *** textarea, textline, dropdown ***
				%>
					var answer = escape(document.emailform.elements["q_<%= thisForm %>_<%= questions_number %>_<%= i %>"].value);
					if(answer != '') {
						href += "|q_<%= thisForm %>_<%= questions_number %>_<%= i %>=" + answer;
					}
				<% } 
			%></mm:node
		></mm:related><% 
		
		} %></mm:node
></mm:list>
	document.location = url + href + "|";
	return false; 
}
</script><%
} %>

