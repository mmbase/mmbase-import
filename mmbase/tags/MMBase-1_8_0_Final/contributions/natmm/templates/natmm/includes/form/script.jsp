<%-- ********************* create the javascript for posting the values *******************
--%><% if(true) {
%><script>
<%= "<!--" %>
function postIt(searchtype) {
var href = "?p=<%= paginaID %>&pst=";
if(searchtype != 'clear' ) {
<mm:list nodes="<%= paginaID %>" path="pagina,posrel,formulier" orderby="posrel.pos" directions="UP">
   <mm:node element="formulier" jspvar="thisForm">
      <% String thisFormNumber = thisForm.getStringValue("number"); %>
      <mm:related path="posrel,formulierveld" orderby="posrel.pos" directions="UP" >
         <mm:node element="formulierveld" jspvar="thisFormField">
            <%
            String formulierveld_type = thisFormField.getStringValue("type");
            String formulierveld_number = thisFormField.getStringValue("number");
            if(formulierveld_type.equals("6")) { // *** date ***
            %>
               var answer = escape(document.emailform.elements["q<%= thisFormNumber %>_<%= formulierveld_number %>_day"].value);
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
               %><mm:related path="posrel,formulierveldantwoord" orderby="posrel.pos" directions="UP">
                  var answer = document.emailform.q<%= thisFormNumber %>_<%= formulierveld_number %>_<mm:field name="formulierveldantwoord.number" />;
                  if(answer.checked) {
                     href += "|q<%= thisFormNumber %>_<%= formulierveld_number %>_<mm:field name="formulierveldantwoord.number" />=" + answer.value;
                  }
               </mm:related><%
            } else if(formulierveld_type.equals("4")) { // *** radio buttons ***
            %> var answer = document.emailform.q<%= thisFormNumber %>_<%= formulierveld_number %>;
               for (var i=0; i < answer.length; i++){
                  if(answer[i].checked) {
                     var rad_val = answer[i].value;
                     if(rad_val != '') {
                        href += "|q<%= thisFormNumber %>_<%= formulierveld_number %>=" + rad_val;
                     }
                  }
               }
            <% }

            else if(formulierveld_type.equals("1")||formulierveld_type.equals("2")||formulierveld_type.equals("3")) {
            // *** textarea, textline, dropdown ***
            %>
               var answer = escape(document.emailform.elements["q<%= thisFormNumber %>_<%= formulierveld_number %>"].value);
               if(answer != '') {
                  href += "|q<%= thisFormNumber %>_<%= formulierveld_number %>=" + answer;
               }
            <% }
         %></mm:node
      ></mm:related
      ></mm:node
></mm:list>
}
top.location = href;
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
