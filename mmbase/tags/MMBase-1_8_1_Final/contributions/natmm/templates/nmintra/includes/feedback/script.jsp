<%-- ********************* create the javascript for posting the values *******************
--%><% if(true) { 
%><script>
<%= "<!--" %>
function createPosting(el) {
   var href = el.getAttribute("href");
   href += "&pst=";
   var answer = '';
   var element_name = '';
   
   for (i = 0; i < document.formulier.elements.length; i++){
   	element_name = document.formulier.elements[i].name;
   	answer = document.formulier.elements[element_name].value;	
   	if(answer != '') {
      	href += "|" + element_name + "=" + escape(answer);
   	}
   }
   
   document.location =  href; 
return false; 
}
<%= "//-->" %>
</script><%
} %>
