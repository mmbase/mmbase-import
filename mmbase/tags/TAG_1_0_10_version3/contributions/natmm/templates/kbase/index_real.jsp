<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"  prefix="mm" %>
<%@page import="nl.leocms.util.tools.*" %>
<%@include file="parts/basics.jsp"%>
<%
  String extraParamsUrl=getParamsFormatted(request,"url",getExtraParams(request));
%>
<mm:cloud jspvar="cloud" method="asis" >
<mm:import externid="node" jspvar="node" vartype="String"><mm:node number="kbase.root"><mm:field name="number"/></mm:node></mm:import>
<mm:import externid="qnode" jspvar="qnode" vartype="String"/>
<mm:import externid="anode" jspvar="anode" vartype="String"/>
<mm:import externid="action"/>
<mm:import externid="type"/>
<mm:import externid="expanded" jspvar="expanded" vartype="String"/>


<mm:import id="realpath"><%=getRealPath(request)%></mm:import>
<table width="100%" height="80%" border="0" cellpadding="0" cellspacing="0" bordercolor="red">
  <%
  String paginaID = (String) session.getAttribute("page");
  if(paginaID!=null) {
    boolean isFirst = true;
    %>
    <mm:node number="<%= paginaID %>">
      <mm:field name="titel_zichtbaar">
         <mm:compare value="0" inverse="true">
            <% isFirst = false; %>
            <tr>
              <td colspan="2" height="50" style="border-bottom:1px solid #797868">
                <h3><mm:field name="titel" /></h3>
         </mm:compare>
      </mm:field>
      <mm:field name="omschrijving" jspvar="text" vartype="String" write="false">
      <% 
        if(text!=null&&!HtmlCleaner.cleanText(text,"<",">","").trim().equals("")) { 
           if(isFirst) {
              isFirst = false;
              %>
              <tr>
                <td colspan="2" height="50" style="border-bottom:1px solid #797868">
              <%
           } %>
           <mm:write /><br/><br/>
           <%
        }
      %>
      </mm:field>
    </mm:node>
    <%
    if(!isFirst) {
      %>
         </td>
      </tr>
      <%
    }
  }
  %>
  <tr>
    <td valign="top" style="width:300px; overflow:auto">
    <%--  ############## begin tree ############# --%>

<script language="javascript">
    <%-- var portalpage="portal=<mm:write referid="portal"/>&page=<mm:write referid="page"/>"; --%>
    var realpath="<mm:write referid="realpath"/>";

    function goThere(path){
      //alert(path);
      document.location=path;
    }

    //navigate: expand all folders, but keeping the focus
 function goExpand(){
    try{
      var path=realpath+"/index.jsp?expanded=all<%=(!extraParamsUrl.equals("")?"&":"")%><%=extraParamsUrl%>&node="+currentFolder.getAttribute('node');
      goThere(path);      
    }catch(er){
      var path=realpath+"/index.jsp<%=(!extraParamsUrl.equals("")?"?":"")%><%=extraParamsUrl%>&expanded=all";
      goThere(path);  
    }
  }
</script>

<div style="padding-bottom:  15px;font-size: 16px;">
  <a href="javascript:goExpand()"><mm:write referid="expandall" /></a> - <a href="index.jsp<%=(!extraParamsUrl.equals("")?"?":"")%><%=extraParamsUrl%>"><mm:write referid="colapseall" /></a>
</div>
<!--mm:formatter xslt="/export/home/mmweb/web/development/kbase/treeview.xslt"-->
<mm:formatter xslt="treeview.xslt">
  <mm:include page="kbasetoxml.jsp"/>
</mm:formatter>

    <%--  ############## einde tree ############# --%>

    </td>
    <td valign="top">
      <table width="100%" cellpadding="0" cellspacine="0">
        <tr>
          <td>

<%--  ############## begin toolbar ############# --%>
<mm:maycreate type="kb_question">
<mm:notpresent referid="action"> <%--only if you are not doing some action--%> 
  <script language="javascript">
  
  //some general stuff
  var possibleQnode="<mm:present referid="qnode">&qnode=<mm:write referid="qnode"/></mm:present>";
  
  
    // edit: add an new folder
    function goNewFolder(){
      var path =realpath+"/index.jsp?action=add&type=category<%=(!extraParamsUrl.equals("")?"&":"")%><%=extraParamsUrl%>&node="+currentFolder.getAttribute('node')+"&expanded="+getExpandedFolders()+possibleQnode;
      goThere(path);
    }
    
  
    // edit: edit the current folder
    function goEditFolder(){
      //als de huidige folder de root is, mag er niet geeidt worden
      if (currentFolder.getAttribute("node")==<mm:node number="kbase.root"><mm:field name="number"/></mm:node>){
        alert("<mm:write referid="rooteditwarning" />");
      }else{
        var path=realpath+"/index.jsp?action=edit<%=(!extraParamsUrl.equals("")?"&":"")%><%=extraParamsUrl%>&type=category&node="+currentFolder.getAttribute('node')+"&expanded="+getExpandedFolders()+possibleQnode;
        goThere(path);
      }
    }  
  
  
    // edit: add aquestion to the current foldder
    function goAddQuestion(){
      var path=realpath+"/index.jsp?action=add&type=question<%=(!extraParamsUrl.equals("")?"&":"")%><%=extraParamsUrl%>&node="+currentFolder.getAttribute('node')+"&expanded="+getExpandedFolders();
        goThere(path);
    }  
    
    //delete: delete the currently selected question
    function goDeleteQuestion(){
      var path=realpath+"/index.jsp?action=delete&type=question<%=(!extraParamsUrl.equals("")?"&":"")%><%=extraParamsUrl%>&node="+currentFolder.getAttribute('node')+"&expanded="+getExpandedFolders()+possibleQnode;
      goThere(path);
    }  
    
    //edit: edit the currently selected question
    function goEditQuestion(){
      var path=realpath+"/index.jsp?action=edit&type=question<%=(!extraParamsUrl.equals("")?"&":"")%><%=extraParamsUrl%>&node="+currentFolder.getAttribute('node')+"&expanded="+getExpandedFolders()+possibleQnode;
      goThere(path);
    }  
    
    // navigate: logout
    function goLogout(){
      var path=realpath+"/parts/logout.jsp?node="+currentFolder.getAttribute('node')+"<%=(!extraParamsUrl.equals("")?"&":"")%><%=extraParamsUrl%>&expanded="+getExpandedFolders();
      goThere(path);
    }    
  </script>
  
  <div class="toolbar" id="toolbar" style="display:none">
  
  <% 
  if(paginaID!=null) {
    %>
    <%@include file="/nmintra/includes/exteditcheck.jsp" %>
    <%
  } %>
  <mm:present referid="isowner">
     <mm:import id="isowner" reset="true">true</mm:import>
  <%-- als er een vraag is geopend kun je die editen --%>
      <mm:present referid="qnode">
          <a href="javascript:goEditQuestion()"><img src="<mm:write referid="realpath" />/img/editquestion.gif" border="0" alt="<mm:write referid="editcurrentquestion" />"/></a>
          <a href="javascript:goDeleteQuestion()" onClick="return doDelete('Weet u zeker dat u deze vraag wilt verwijderen?');"><img src="<mm:write referid="realpath" />/img/remove.gif" alt="<mm:write referid="removeaquestion" />" border="0"/></a>      
      </mm:present>
  <%-- als er een huidige categorie is, kun je die editen--%>
      <a href="javascript:goAddQuestion()"><img src="<mm:write referid="realpath" />/img/createquestion.gif" alt="<mm:write referid="addaquestion" />" border="0"/></a>      
      <a href="javascript:goEditFolder()"><img src="<mm:write referid="realpath" />/img/editfolder.gif" alt="<mm:write referid="editcurrentfolder" />" border="0"/></a>
      <a href="javascript:goNewFolder()"><img src="<mm:write referid="realpath" />/img/createfolder.gif" alt="<mm:write referid="createnewfolderincurrentone" />" border="0"/></a>
  </mm:present>
  
  </div>
</mm:notpresent>
</mm:maycreate>

<mm:notpresent referid="isowner">
   <mm:import id="isowner" reset="true">false</mm:import>
</mm:notpresent>   

<mm:maycreate type="kb_question" inverse="true">
  <script language="javascript">
  //***********************************************
  //  this function generates the link to the login page.
  //***********************************************
  function goLogin(){
    document.location=realpath+"/parts/login.jsp?node="+currentFolder.getAttribute('node')+"<%=(!extraParamsUrl.equals("")?"&":"")%><%=extraParamsUrl%>&expanded="+getExpandedFolders();
  }  
  </script>
  <div class="toolbar" id="toolbar" stype="display:none">
    <a href="javascript:goLogin()">Login</a>
  </div>
</mm:maycreate>
<%--  ############## einde toolbar ############# --%>
        </td>
    </tr>
</table>
<%--  ############## content body ############# --%>

  <%--  *** toon een vraag   ***  --%>
  <mm:notpresent referid="action">
      <mm:present referid="qnode">
      <mm:include referids="isowner" page="parts/showquestion.jsp"/>
    </mm:present>
  </mm:notpresent>

  <mm:present referid="action">
        <%--  ***   edit part   ***  --%>
        <mm:compare referid="action" value="add">
            <mm:include page="parts/addpart.jsp" />
        </mm:compare>
        <%--  ***   add part   ***  --%>
        <mm:compare referid="action" value="edit">
            <mm:include page="parts/editpart.jsp"/>
        </mm:compare>
        <%--  ***   delete part   ***  --%>
        <mm:compare referid="action" value="delete">
            <mm:include page="parts/deletepart.jsp"/>
        </mm:compare>
  </mm:present>
<%--  ############## einde content body ############# --%>
    </td>
  </tr>
</mm:cloud>

