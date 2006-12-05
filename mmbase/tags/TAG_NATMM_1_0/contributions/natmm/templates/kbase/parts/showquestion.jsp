<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%@include file="basics.jsp"%>
<mm:cloud jspvar="wolk" method="asis" >
<mm:import id="realpath"><%=getRealPath(request)%></mm:import>
<mm:import externid="qnode"/>
<mm:import externid="isowner">false</mm:import>
<script language="javascript">
  //some general stuff
  var possibleQnode="<mm:present referid="qnode">&qnode=<mm:write referid="qnode"/></mm:present>";
  var realpath="<mm:write referid="realpath"/>";
  <mm:present referid="portal">
    var portalpage="portal=<mm:write referid="portal"/>&page=<mm:write referid="page"/>";
  </mm:present>
  <mm:notpresent referid="portal">var portalpage=""</mm:notpresent>

  //function goThere(path){
  //  document.location=path;
  //}
  //***********************************************
  // desc   :   this function generates the link to the editpage with all the right params for adding an aswer
  //***********************************************
  function goNewAnswer(){
    path=realpath+"/index.jsp?action=add&type=answer&"+extraParamsUrl+"&node="+currentFolder.getAttribute('node')+"&expanded="+getExpandedFolders()+possibleQnode;
    goThere(path);
  }

  //***********************************************
  // desc   :   this function generates the link to the editpage with all the right params for editing an answer
  //***********************************************  
  function goEditAnswer(whichAnswer){
    path=realpath+"/index.jsp?action=edit&type=answer&"+extraParamsUrl+"&node="+currentFolder.getAttribute('node')+"&anode="+whichAnswer+"&expanded="+getExpandedFolders()+possibleQnode;
    goThere(path);
  }
  
  //***********************************************
  // desc   :   this function generates the link to the editpage with all the right params for deleting an answer
  //***********************************************  
  function goDeleteAnswer(whichAnswer){
    path=realpath+"/index.jsp?action=delete&type=answer&"+extraParamsUrl+"&node="+currentFolder.getAttribute('node')+"&anode="+whichAnswer+"&expanded="+getExpandedFolders();
    goThere(path);
  }
   var cancelClick = false;
   function doDelete(prompt) {
      var conf;
      if (prompt && prompt!="") {
      	conf = confirm(prompt);
      } else conf=true;
      cancelClick=true;
      return conf;
   }
</script>
<div style="overflow:auto">
<mm:node number="$qnode">
  <h3><mm:field name="question"/></h3>
  <mm:field name="description"><mm:isnotempty><p><mm:write /></p></mm:isnotempty></mm:field>
  <%-- hh <table class="list" cellpadding="0" cellspacing="0" width="90%" style="margin-top:10px;">
    <tr>
      <td><mm:write referid="questionsissubmittedby" /> <a href="mailto:<mm:field name="email"/>"><mm:field name="name"/></a> op <mm:field name="date"><mm:time format="<%= timeFormat %>"/></mm:field></td>
    </tr>
  </table> --%>
  
  <h5><mm:write referid="answerstothisquestion" />:</h5>
  <mm:relatednodes type="kb_answer" orderby="number" directions="up">
    <div style="max-height:600px;overflow:auto;margin-top:10px;">
      <mm:maycreate type="kb_answer">
      <mm:compare referid="isowner" value="true">
      <a href="javascript:goEditAnswer(<mm:field name="number"/>)">
        <img src="<mm:write referid="realpath"/>/img/smallpen.gif" border="0"  title="bewerk antwoord" />
      </a>
      <a href="javascript:goDeleteAnswer(<mm:field name="number"/>)" onClick="return doDelete('Weet u zeker dat u deze antwoord wilt verwijderen?');">
        <img src="<mm:write referid="realpath"/>/img/remove.gif" border="0" title="verwijder antwoord" />
      </a>
      </mm:compare>
      
      </mm:maycreate>
      <div style="width: 80%">
        <mm:field name="answer" jspvar="answer" vartype="String"><%=formatCodeBody(answer)%></mm:field>
      </div>
    </div>
    <%-- hh <table class="list" cellpadding="0" cellspacing="0" width="90%" style="margin-top:10px;">
    <tr>
      <td><mm:write referid="answerissubmittedby" /> <a href="mailto:<mm:field name="email"/>"><mm:field name="name"/></a> op <mm:field name="date"><mm:time format="<%= timeFormat %>"/></mm:field></td>
    </tr>
  </table>  --%>  
  </mm:relatednodes>
</mm:node>
  <mm:maycreate type="kb_answer">
    <mm:compare referid="isowner" value="true">
    <a href="javascript:goNewAnswer()"><h5><img src="<mm:write referid="realpath"/>/img/create.gif" border="0"/><mm:write referid="addananswertothisquestion" /></h5></a>
    </mm:compare>
  </mm:maycreate>
  </div>
</mm:cloud>
