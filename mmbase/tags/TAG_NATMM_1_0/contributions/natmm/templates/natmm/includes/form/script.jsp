<%-- ********************* create the javascript for posting the values *******************
--%><% if(true) {
%><script>
<%= "<!--" %>
function postIt(searchtype) {
var href = "?p=<%= paginaID %>&pst=";
var nums = '';
if(searchtype != 'clear' ) {
<mm:list nodes="<%= paginaID %>" path="pagina,posrel,formulier" orderby="posrel.pos" directions="UP">
   <mm:node element="formulier" jspvar="thisForm">
      <% String thisFormNumber = thisForm.getStringValue("number"); %>
      <mm:related path="posrel,formulierveld" orderby="posrel.pos" directions="UP" >
         <mm:node element="formulierveld" jspvar="thisFormField">
            <%
            String formulierveld_type = thisFormField.getStringValue("type");
            String formulierveld_number = thisFormField.getStringValue("number");
				String formulierveld_else = thisFormField.getStringValue("label_eng");
            if(formulierveld_type.equals("6")) { // *** date ***
            %> var answer = escape(document.emailform.elements["q<%= thisFormNumber %>_<%= formulierveld_number %>_day"].value);
               if(answer != '') {
                  href += "|q<%= thisFormNumber %>_<%= formulierveld_number %>_day=" + answer;
               }
               var answer = escape(document.emailform.elements["q<%= thisFormNumber %>_<%= formulierveld_number %>_month"].value);
               if(answer != '') {
                  href += "|q<%= thisFormNumber %>_<%= formulierveld_number %>_month=" + answer;
               }
               var answer = escape(document.emailform.elements["q<%= thisFormNumber %>_<%= formulierveld_number %>_year"].value);
               if(answer != '') {
                  href += "|q<%= thisFormNumber %>_<%= formulierveld_number %>_year=" + answer;
            }
            <%
            } else if(formulierveld_type.equals("5")) { // *** check boxes ***
               %> var else_answer = '';<% 
					if (formulierveld_else.equals("1")){
						 %>else_answer = escape(document.emailform.elements["q<%= thisFormNumber %>_<%= formulierveld_number %>_else"].value);<% 
					} %>
						if (else_answer != ''){
							href += "|q<%= thisFormNumber %>_<%= formulierveld_number %>_else=" + else_answer;
						}
						<mm:related path="posrel,formulierveldantwoord" orderby="posrel.pos" directions="UP">
                  var answer = document.emailform.q<%= thisFormNumber %>_<%= formulierveld_number %>_<mm:field name="formulierveldantwoord.number" />;
                  if(answer.checked) {
  	                  href += "|q<%= thisFormNumber %>_<%= formulierveld_number %>_<mm:field name="formulierveldantwoord.number" />=" + answer.value;
							if (nums != '') { nums += ','; }
							nums += '<mm:field name="formulierveldantwoord.number" />';
						}
               </mm:related><%
            } else if(formulierveld_type.equals("4")) { // *** radio buttons ***
            %> var answer = document.emailform.q<%= thisFormNumber %>_<%= formulierveld_number %>;
					var flag = false;
               for (var i=0; i < answer.length; i++){
                  if(answer[i].checked) {
                     var rad_val = answer[i].value;
                     if(rad_val != '') {
                        href += "|q<%= thisFormNumber %>_<%= formulierveld_number %>=" + rad_val;
								flag = true;
								<mm:related path="posrel,formulierveldantwoord" orderby="posrel.pos" directions="UP">
									if (rad_val == '<mm:field name="formulierveldantwoord.waarde" />') {
										if (nums != '') { nums += ','; }
										nums += '<mm:field name="formulierveldantwoord.number" />';
									}
								</mm:related>
                     }
                  }
               }
					var else_answer = '';<% 
   				if (formulierveld_else.equals("1")) {
						%>else_answer = escape(document.emailform.elements["q<%= thisFormNumber %>_<%= formulierveld_number %>_else"].value);<% 
					} %>
					if (else_answer != '') {
						if (flag){
							href += ", " + else_answer;
						} else {
							href += "|q<%= thisFormNumber %>_<%= formulierveld_number %>=" + else_answer;
						}
					}
            <% }

            else if(formulierveld_type.equals("1")||formulierveld_type.equals("2")||formulierveld_type.equals("3")) {
            // *** textarea, textline, dropdown ***
             %>var answer = escape(document.emailform.elements["q<%= thisFormNumber %>_<%= formulierveld_number %>"].value);
					var else_answer = '';<% 
   				if (formulierveld_else.equals("1")) {
						%>else_answer = escape(document.emailform.elements["q<%= thisFormNumber %>_<%= formulierveld_number %>_else"].value);<% 
					} %>
					if ((else_answer != '') || (answer != '')){
						href += "|q<%= thisFormNumber %>_<%= formulierveld_number %>=";
						if (answer != ''){
							href += answer;
							if (else_answer != ''){
								href += ", " + else_answer;
							}
						} else {
							href += else_answer;
						}
					}
            <% if (formulierveld_type.equals("3")||formulierveld_type.equals("4")||formulierveld_type.equals("5")) { %>
						<mm:related path="posrel,formulierveldantwoord" orderby="posrel.pos" directions="UP">
							if (answer == '<mm:field name="formulierveldantwoord.waarde" />') {
								if (nums != '') { nums += ','; }
								nums += '<mm:field name="formulierveldantwoord.number" />';
							}
						</mm:related>
				<%	}
				}
         %></mm:node
      ></mm:related
      ></mm:node
></mm:list>
}
top.location = href+"&nums="+nums;
return false;
}
function handleEnter (field, event) {
	var keyCode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;
	if (keyCode == 13) {
		var i;
		for (i = 0; i < field.form.elements.length; i++)
			if (field == field.form.elements[i])
				break;
		i = (i + 1) % field.form.elements.length;
		field.form.elements[i].focus();
		return false;
	} 
	else
	return true;
}
<%= "//-->" %>
</script><%
} %>
