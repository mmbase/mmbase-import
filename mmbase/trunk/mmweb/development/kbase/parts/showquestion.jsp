<script language="javascript">
//some general stuff
var possibleQnode="<mm:present referid="qnode">&qnode=<mm:write referid="qnode"/></mm:present>";

//***********************************************
// desc   :   this function generates the link to the editpage with all the right params for adding an aswer
//***********************************************
  function goNewAnswer(){
    document.location="edit.jsp?action=add&type=answer&node="+currentFolder.getAttribute('node')+"&expanded="+getExpandedFolders()+possibleQnode;
  }

//***********************************************
// desc   :   this function generates the link to the editpage with all the right params for editing an answer
//***********************************************  
  function goEditAnswer(whichAnswer){
    document.location="edit.jsp?action=edit&type=answer&node="+currentFolder.getAttribute('node')+"&anode="+whichAnswer+"&expanded="+getExpandedFolders()+possibleQnode;
  }
</script>
<div style="overflow:auto">
<mm:node number="$qnode">
  <h3><mm:field name="question"/></h3>
  <p><mm:field name="description"/></p>
  <table class="list" cellpadding="0" cellspacing="0" width="90%" style="margin-top:10px;">
    <tr>
      <td>this question was submitted by: <a href="mailto:<mm:field name="email"/>"><mm:field name="name"/></a> on <mm:field name="date"><mm:time format="dd-MM-yyyy"/></mm:field></td>
    </tr>
  </table>
  
  <h5>answers to this quetion:</h5>
  <mm:relatednodes type="kb_answer" orderby="number" directions="up">
    <div style="max-height:600px;overflow:auto;margin-top:10px;">
      <a href="javascript:goEditAnswer(<mm:field name="number"/>)">
        <img src="img/smallpen.gif" border="0"/>
      </a>
      <mm:field name="answer" jspvar="answer" vartype="String"><%=formatCodeBody(answer)%></mm:field>
    </div>
    <table class="list" cellpadding="0" cellspacing="0" width="90%" style="margin-top:10px;">
    <tr>
      <td>this answer was submitted by: <a href="mailto:<mm:field name="email"/>"><mm:field name="name"/></a> on <mm:field name="date"><mm:time format="dd-MM-yyyy"/></mm:field></td>
    </tr>
  </table>    
  </mm:relatednodes>
</mm:node>
  <a href="javascript:goNewAnswer()"><h5><img src="img/create.gif" border="0"/>add an answer to this question</h5></a>
  </div>
