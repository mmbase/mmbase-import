<mm:maycreate type="kb_question">
<script language="javascript">
//some general stuff
var possibleQnode="<mm:present referid="qnode">&qnode=<mm:write referid="qnode"/></mm:present>";

//***********************************************
// desc   :   this function generates the link to the add category page, and goos there
//***********************************************
  function goNewFolder(){
    document.location="edit.jsp?action=add&type=category&node="+currentFolder.getAttribute('node')+"&expanded="+getExpandedFolders()+possibleQnode;
  }
  
//***********************************************
//  this function generates the link to the edit category page, and goos there
//***********************************************
  function goEditFolder(){
    //als de huidige folder de root is, mag er niet geeidt worden
    if (currentFolder.getAttribute("node")==<mm:node number="kbase.root"><mm:field name="number"/></mm:node>){
      alert("you can't (and shouldn't wont to) edit the root folder");
    }else{
      document.location="edit.jsp?action=edit&type=category&node="+currentFolder.getAttribute('node')+"&expanded="+getExpandedFolders()+possibleQnode;
      //alert("edit.jsp?action=edit&type=category&node="+
      //      currentFolder.getAttribute('node')+
      //      "&expanded="+
      //      getExpandedFolders()+possibleQnode);
    }
    

  }  

//***********************************************
//  this function generates the link to the add question page, and goos there
//***********************************************
  function goAddQuestion(){
    document.location="edit.jsp?action=add&type=question&node="+currentFolder.getAttribute('node')+"&expanded="+getExpandedFolders();
  }  

//***********************************************
//  this function generates the link to the edit question page, and goos there
//***********************************************
  function goEditQuestion(){
    document.location="edit.jsp?action=edit&type=question&node="+currentFolder.getAttribute('node')+"&expanded="+getExpandedFolders()+possibleQnode;
  }  
</script>

<div class="toolbar" id="toolbar" style="display:none">
	<%-- als er een vraag is geopend kun je die editen --%>
			<mm:present referid="qnode">
				<a href="javascript:goEditQuestion()"><img src="img/editquestion.gif" border="0" alt="edit current question"/></a>
			</mm:present>
		<%-- als er een huidige categorie is, kun je die editen--%>

          <a href="javascript:goAddQuestion()"><img src="img/createquestion.gif" alt="add a question" border="0"/></a>		
          <a href="javascript:goEditFolder()"><img src="img/editfolder.gif" alt="edit current folder" border="0"/></a>
          <a href="javascript:goNewFolder()"><img src="img/createfolder.gif" alt="create new folder in current one"border="0"/></a>
	  <a href="parts/logout.jsp">Logout</a>

</div>
</mm:maycreate>
<mm:maycreate type="kb_question" inverse="true">
  <script language="javascript">
  //***********************************************
  //  this function generates the link to the login page.
  //***********************************************
  function goLogin(){
    document.location="parts/login.jsp?node="+currentFolder.getAttribute('node')+"&expanded="+getExpandedFolders();
  }  
  </script>
  <div class="toolbar" id="toolbar" stype="display:none">
  	<a href="javascript:goLogin()">Login</a>
  </div>
</mm:maycreate>
