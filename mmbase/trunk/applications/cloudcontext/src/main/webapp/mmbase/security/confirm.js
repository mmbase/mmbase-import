<mm:write referid="prompt" jspvar="promptt" vartype="string">
var element =  document.getElementById('object<mm:field name="number" />');
element.className = '<mm:even>even</mm:even> highlight'; 
if (confirm('<%=getPrompt(m, promptt)%>')) { 
    return true; 
} else { 
    element.className = '<mm:even>even</mm:even>';
    return false; 
}
</mm:write>
