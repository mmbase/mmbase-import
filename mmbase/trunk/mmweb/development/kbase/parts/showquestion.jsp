<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%@include file="basics.jsp"%>
<mm:cloud jspvar="wolk" method="asis" >
<mm:import id="realpath"><%=getRealPath(request)%></mm:import>
<mm:import externid="qnode"/>
<%
  String extraParamsUrl=getParamsFormatted("url",getExtraParams(request));
%>

<script language="javascript">
  //some general stuff
  var possibleQnode="<mm:present referid="qnode">&qnode=<mm:write referid="qnode"/></mm:present>";
  var realpath="<mm:write referid="realpath"/>";
  

  //function goThere(path){
  //  document.location=path;
  //}
  //***********************************************
  // desc   :   this function generates the link to the editpage with all the right params for adding an aswer
  //***********************************************
  function goNewAnswer(){
    path=realpath+"/index.jsp?action=add&type=answer<%=extraParamsUrl != null ? "&" + extraParamsUrl : "" %>&node="+currentFolder.getAttribute('node')+"&expanded="+getExpandedFolders()+possibleQnode;
    goThere(path);
  }

  //***********************************************
  // desc   :   this function generates the link to the editpage with all the right params for editing an answer
  //***********************************************  
  function goEditAnswer(whichAnswer){
    path=realpath+"/index.jsp?action=edit&type=answer<%=extraParamsUrl != null ? "&" + extraParamsUrl : "" %>&node="+currentFolder.getAttribute('node')+"&anode="+whichAnswer+"&expanded="+getExpandedFolders()+possibleQnode;
    goThere(path);
  }
  
</script>
<div style="widht: 65%; padding-right: 20px; "> 
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
    <div style="margin-top:10px;">
      <mm:maycreate type="kb_answer">
      <a href="javascript:goEditAnswer(<mm:field name="number"/>)">
        <img src="<mm:write referid="realpath"/>/img/smallpen.gif" border="0"/>
      </a>
      </mm:maycreate>
      <div style="width: 80%">
        <mm:field name="answer" jspvar="answer" vartype="String"><%=formatCodeBody(answer)%></mm:field>
      </div>
    </div>
    <table class="list" cellpadding="0" cellspacing="0" width="90%" style="margin-top:10px;">
    <tr>
      <td>this answer was submitted by: <a href="mailto:<mm:field name="email"/>"><mm:field name="name"/></a> on <mm:field name="date"><mm:time format="dd-MM-yyyy"/></mm:field></td>
    </tr>
  </table>    
  </mm:relatednodes>
</mm:node>
  <mm:maycreate type="kb_answer">
    <a href="javascript:goNewAnswer()"><h5><img src="<mm:write referid="realpath"/>/img/create.gif" border="0"/>add an answer to this question</h5></a>
  </mm:maycreate>
  </div>
  </mm:cloud>
