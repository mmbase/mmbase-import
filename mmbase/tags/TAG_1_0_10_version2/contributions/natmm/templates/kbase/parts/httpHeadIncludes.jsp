
<link type="text/css" href="<%=localPath%>/css/treeview.css" rel="stylesheet"/>
<link type="text/css" href="<%=localPath%>/css/mmbase.css" rel="stylesheet"/>
<style type="text/css">
  .small, .big{
    background:     white;
    border:         1px solid grey;
    width:          98%;
    font-family: "Lucida Grande", Verdana, Arial, sans-serif;
    font-size: 10px;
  }
</style>

<script language="javascript">
  //deze variabel kan overal gebruikt worden om de portal en page velden in url's te plakken
  var portalpage="page=<mm:write referid="page"/>&portal=<mm:write referid="portal"/>";
  alert(portalpage);
</script>

<script type="text/javascript" language="javascript" src="<%=localPath%>/js/treeview.js"></script>
<script type="text/javascript" language="javascript" src="<%=localPath%>/js/callbacks.js"></script>
