<script language="javascript">
//***********************************************
// desc   :   this function generates the link to index that fully expands the tree
// but maintains the current folder
//***********************************************
  function goExpand(){
    try{
      document.location="index.jsp?expanded=all&node="+currentFolder.getAttribute('node');
    }catch(er){
      document.location="index.jsp?expanded=all";
    }
  }
</script>
<div style="padding-bottom:  15px;font-size: 16px;">
  <a href="javascript:goExpand()">expand all</a> - <a href="index.jsp">colapse all</a>
</div>
<mm:formatter xslt="treeview.xslt" >
  <mm:include page="kbasetoxml.jsp">
    <mm:present referid="expanded"><mm:param name="expanded" value="expanded"/></mm:present>
    <mm:present referid="node"><mm:param name="node" value="node"/></mm:present>          
  </mm:include>
</mm:formatter>
