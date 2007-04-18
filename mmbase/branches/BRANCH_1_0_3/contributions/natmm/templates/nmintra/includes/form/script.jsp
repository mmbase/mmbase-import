<%-- ********************* create the javascript for posting the values *******************
--%><% if(true) { 
%><script>
<%= "<!--" %>
function postIt() {
var href = "?p=<%= paginaID %>&pst=";
<mm:list nodes="<%= paginaID %>" path="pagina,posrel,formulier" 
    orderby="posrel.pos" directions="UP"
    ><% String formulier_number = ""; 
    %><mm:field name="formulier.number" jspvar="dummy" vartype="String" write="false"
            ><% formulier_number= dummy; 
    %></mm:field
    ><mm:list nodes="<%= formulier_number %>" path="formulier,posrel,formulierveld"
        orderby="posrel.pos" directions="UP"
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
var answer = document.formulier.q<%= questions_number %>_<%= answer_number %>;
if(answer.checked) {
    href += "|q<%= questions_number %>_<%= answer_number %>=" + escape(answer.value);
}
            </mm:field
            ></mm:list><% 
        } %><%-- 
        
        radio buttons
        --%><% if(questions_type.equals("4")) { %>
var answer = document.formulier.q<%= questions_number %>;
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
var answer = document.formulier.elements["q<%= questions_number %>"].value;
if(answer != '') {
    href += "|q<%= questions_number %>=" + escape(answer);
}
        <% } 
        %></mm:list
    ></mm:list>
document.location =  href; 
return false; 
}
<%= "//-->" %>
</script><%
} %>
