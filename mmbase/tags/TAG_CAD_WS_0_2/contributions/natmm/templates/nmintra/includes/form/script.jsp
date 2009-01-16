<%@ page isELIgnored="false" %>
<mm:import externid="formnumber" jspvar="formnum" from="parameters">0</mm:import>
<%-- ********************* create the javascript for posting the values ******************* --%>
<script type="text/javascript">
   var currentForm = null; 
   var forms = new Array();

function showForm(formulier) {
   if (formulier && formulier.value != "") {
      
      if (currentForm) {
   	   currentForm.style.display = "none";
      }
      else {
         var form = document.getElementById(forms[0]);
         form.style.display = "none";
      }
   
      var form = document.getElementById("form" + formulier.value);
      if (form) { //show current form
         form.style.display = "block";
         currentForm = form;
      }
   }
}

function postIt(formnumber) {
   var href = "?p=<%= paginaID %>&pst=";
<mm:list nodes="<%= paginaID %>" path="pagina,posrel,formulier" orderby="posrel.pos" directions="UP" searchdir="destination"
    ><% String formulier_number = ""; 
    %><mm:field name="formulier.number" jspvar="dummy" vartype="String" write="false"
            ><% formulier_number = dummy;
    %></mm:field
    > 
    <mm:list nodes="<%= formulier_number %>" path="formulier,posrel,formulierveld" orderby="posrel.pos" directions="UP" searchdir="destination"
        ><% String questions_type = ""; 
        %><mm:field name="formulierveld.type" jspvar="dummy" vartype="String" write="false"
            ><% questions_type = dummy; 
        %></mm:field
        ><% String questions_number = ""; 
        %><mm:field name="formulierveld.number" jspvar="dummy" vartype="String" write="false"
            ><% questions_number= dummy; 
        %></mm:field
        ><%-- 
        
        check boxes 
        --%><% if(questions_type.equals("5")) { 
            %><mm:list nodes="<%= questions_number %>" path="formulierveld,posrel,formulierveldantwoord" orderby="posrel.pos" directions="UP"
            ><mm:field name="formulierveldantwoord.number" jspvar="answer_number" vartype="String" write="false">
          var answer = document.getElementById("form<%=formulier_number%>").q<%= questions_number %>_<%= answer_number %>;
if(answer.checked) {
    href += "|q<%= questions_number %>_<%= answer_number %>=" + escape(answer.value);
}
            </mm:field
            ></mm:list><% 
        } %><%-- 
        
        radio buttons
        --%><% if(questions_type.equals("4")) { %>
var answer = document.getElementById("form<%=formulier_number%>").q<%= questions_number %>;
for (var i=0; i < answer.length; i++){
    if (answer[i].checked) {
        var rad_val = answer[i].value;
        if(rad_val != '') {
            href += "|q<%= questions_number %>=" + escape(rad_val);
        }
    }
}
        <% } %><%-- 
        
        textarea, textline, dropdown
        --%><% if(questions_type.equals("1")||questions_type.equals("2")||questions_type.equals("3")) { %>
   var answer = document.getElementById("form<%=formulier_number%>").elements["q<%= questions_number %>"].value;
   href += "|q<%= questions_number %>=" + escape(answer);
        <% } 
        %></mm:list>
    </mm:list>
   document.location = href + "&formnumber=" + formnumber; 
   return false; 
}
</script>