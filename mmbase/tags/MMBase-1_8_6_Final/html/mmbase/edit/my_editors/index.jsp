<%@ include file="inc/top.jsp" %>
<mm:content type="text/html" escaper="none" expires="0">
<mm:cloud jspvar="cloud" method="loginpage" loginpage="login.jsp" rank="$rank">
<mm:import externid="ntype" />
<mm:import externid="pagetitle">Home</mm:import>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="nl">
<head>
  <%@ include file="inc/head.jsp" %>
<%-- script type="text/javascript">
var req;

function loadXMLDoc(url) {
    // branch
    if (window.XMLHttpRequest) {        // Mozilla, Safari?
        req = new XMLHttpRequest();
        req.open("GET", url, true);
        req.setRequestHeader('content-type', 'text/xml');
        req.onreadystatechange = processReqChange;
        req.send("");
    } else if (window.ActiveXObject) {  // IE/Windows
        req = new ActiveXObject("Microsoft.XMLHTTP");
        if (req) {
            req.onreadystatechange = processReqChange;
            req.open("GET", url, true);
            req.send();
        }
    }
    
}

function processReqChange() {
    // only if req shows 'complete'
    if (req.readyState == 4) {
        // only if 'OK'
        if (req.status == 200) {
            // proces something
            
            
            response = req.responseXML.documentElement;
            method = response.getElementsByTagName('method')[0].firstChild.data;
            result = response.getElementsByTagName('result')[0].firstChild.data;
            //countRelations('', result);
            eval(method + '(\'\', result)');
        } else {
            alert("There was a problem retrieving the xml data:\n" + req.statusText);
        }
    }
}

function countRelations(input, response) {
    if (response != '') {       // Response mode
        alert("The response is: " + response)
        message = document.getElementById('relcount');
        message.classname = 'error';
        message.innerHTML = "we hebben een waarde:" + response;
    } else {                    // Input mode
        url = "http://127.0.0.1:8080<mm:url page="/my_editors/xmlgetsome.jsp" />?nr=" + input;
        loadXMLDoc(url);
        message = document.getElementById('relcount');
        message.classname = 'hidden';
    }
}

</script --%>
</head>
<body>
<div id="frame">
<%@ include file="inc/pageheader.jsp" %>
<div id="sidebar">
  <div class="padsidebar">
    <table border="0" cellspacing="0" cellpadding="3" id="nodetypes">
    <caption>List of <strong><a href="config.jsp"><mm:write referid="list" /></a></strong> node types</caption>
    <thead>
    <tr>
      <th scope="col" class="right">sort by <strong><a href="javascript:;" onclick="sortT('nodetypes', 0);">name</a></strong></th>
      <th scope="col"><strong><a href="javascript:;" onclick="sortT('nodetypes', 1);">guiname</a></strong></th>
      <th>&nbsp;</th>
    </tr>
    </thead>
    <tbody>
      
      <%-- all nodetypes --%>
      <mm:import vartype="List" jspvar="typelist" id="typelist" />
      <mm:listnodescontainer type="typedef">
        <mm:sortorder field="name" />
        <mm:listnodes jspvar="n">
          <mm:import id="name" jspvar="name" reset="true"><mm:field name="name" /></mm:import>
          <mm:import id="dnumber" reset="true">no</mm:import>
          <mm:fieldlist type="create">
            <mm:fieldinfo type="name" id="fldname" write="false" />
            <mm:compare referid="fldname" value="dnumber"><%-- test for fieldname dnumber --%>
              <mm:import id="dnumber" reset="true">yes</mm:import>
            </mm:compare>
          </mm:fieldlist>
          <%-- import typedefs --%>
          <% if (cloud.hasNodeManager(name)) { // check if it is active %>
			<mm:compare referid="list" value="all" inverse="true"><%-- import only without dnumber --%>
			  <mm:compare referid="dnumber" value="no"><% typelist.add(n); %></mm:compare>
			</mm:compare>
			<mm:compare referid="list" value="all"><% typelist.add(n); %></mm:compare>
          <% } %>
        </mm:listnodes>
      </mm:listnodescontainer>

      <mm:listnodes referid="typelist">
        <mm:import id="name" reset="true"><mm:field name="name" /></mm:import>
        <tr <mm:odd>class="odd"</mm:odd><mm:even>class="even"</mm:even>>
          <td class="right"><mm:write referid="name" /></td>
          <td>
            <a href="index.jsp?ntype=<mm:write referid="name" />" title="List <mm:nodeinfo nodetype="$name" type="guitype" /> nodes"><mm:nodeinfo nodetype="$name" type="guitype" /></a>
          </td>
          <td>
            <mm:maycreate type="$name"><a href="new_object.jsp?ntype=<mm:write referid="name" />" title="Create a new <mm:nodeinfo nodetype="$name" type="guitype" /> node"><img src="img/mmbase-new.png" alt="new node" width="21" height="20" /></a></mm:maycreate>
          </td>
        </tr>
      </mm:listnodes>

    </tbody>
    </table>
  </div><!-- / .padsidebar -->
</div><!-- / #sidebar -->
<div id="content">
<mm:present referid="ntype">
  <div class="padcontent">
  <!-- ### search and results ### -->
  <mm:compare referid="searchbox" value="after" inverse="true"><%@ include file="inc/searchbox.jsp" %></mm:compare>
  <%@ include file="inc/searchresults.jsp" %>
  <mm:compare referid="searchbox" value="after"><%@ include file="inc/searchbox.jsp" %></mm:compare>
<%--  
<div id="relcount" class="error">relcount is here</div>  

<ul>
<mm:listnodes type="text">  
 <li>
  <a href="#" onclick="countRelations(<mm:field name="number" />,'');return false;">+</a> <mm:field name="title" />
 </li>
</mm:listnodes>  
</ul>
--%>
  </div><!-- / .padder -->
  <div class="padfoot">&nbsp;</div>
</mm:present>
</div><!-- / #content -->
<%@ include file="inc/footer.jsp" %>
</div><!-- / #frame -->
</body>
</html>
</mm:cloud></mm:content>
