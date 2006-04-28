<mm:write referid="prompt" jspvar="prompt" vartype="string">
var element =  document.getElementById('object<mm:field name="number" />');
element.className = '<mm:even>even</mm:even> highlight'; 
if (confirm('<%=getPrompt(m, prompt)%>')) { 
    return true; 
} else { 
    element.className = '<mm:even>even</mm:even>';
    return false; 
}
</mm:write>
