<%@ page language="java" contentType="text/html; charset=utf-8" 
%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" 
%><mm:import id="rank"><%= org.mmbase.util.xml.UtilReader.get("editors.xml").getProperties().getProperty("rank", "basic user")%></mm:import>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<mm:cloud method="loginpage" loginpage="../login.jsp" rank="$rank">
<mm:import externid="ntype" />
<mm:import externid="nfield" />
<mm:import externid="search" />
<mm:import externid="changeto" />
<mm:import externid="action" />

<mm:import externid="max">25</mm:import>
<mm:import externid="offset">0</mm:import>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <title>Change field values of nodes</title>
  <link rel="stylesheet" href="styles.css" type="text/css" />
<script type="text/javascript" language="javascript">
/* <![CDATA[ */
/* Check all checkboxes */
function markAll() {
	var flag = true;
	for(var i=0; i < document.theform.IDs.length; i++) { document.theform.IDs[i].checked = true; }
}

/* Toggle visibility */
function toggle(targetId){
  if (document.getElementById){
  		target = document.getElementById(targetId);
  			if (target.style.display == "none"){
  				target.style.display = "";
  			} else {
  				target.style.display = "none";
  			}
  	}
}
/* ]]> */
</script>
</head>
<body>
<div id="top">
  [<a href="../index.jsp">back to my_editors</a>] 
  [<a href="<mm:url />">start again</a>] 
  [<a href="<mm:url referids="ntype?" />">reload</a>] 
</div>

<h1>Change field values</h1>
<p>
  This tools allows you to search for a field value in nodes of a certain type and
  change those values. First you need to choose the type of node you wish to change,
  second choose a field to change and next the nodes with that value and change them.
</p>
<p>
  <strong>Warning!</strong><br />
  Please not that when searching for 'text' you will find 
  every node with the string 'text' in the field you selected. When you change it to
  f.e. 'tekst', every instance of 'this text' will change in 'tekst'!
</p>

<mm:notpresent referid="ntype">
  <h2>Choose a node type</h2>
  <form action="<mm:url />" method="post">
    <fieldset>
      <select id="ntype" name="ntype">
        <mm:listnodescontainer type="typedef">
          <mm:sortorder field="name" direction="UP" />
          <mm:listnodes>
            <mm:import id="nmname" reset="true"><mm:field name="name" /></mm:import>
            <option label="<mm:field name="name" />" value="<mm:field name="name" />"
              <mm:compare referid="ntype" value="$nmname">selected="selected"</mm:compare>
            ><mm:field name="name" /></option>
          </mm:listnodes>
        </mm:listnodescontainer>
      </select>
      <input name="action" id="action" type="submit" value="OK" />
    </fieldset>
  </form>
</mm:notpresent>

<mm:present referid="ntype">
  <h2>Choose field of '<mm:write referid="ntype" />' ${nfield}</h2>
  <form action="<mm:url />" method="post">
    <fieldset>
      <label>Select field</label>
      <select id="nfield" name="nfield">
        <mm:fieldlist type="edit" nodetype="$ntype">
          <mm:import id="nmfield" reset="true"><mm:fieldinfo type="name" /></mm:import>
          <option label="<mm:fieldinfo type="name" />" value="<mm:fieldinfo type="name" />"
            <mm:compare referid="nfield" value="$nmfield">selected="selected"</mm:compare>><mm:fieldinfo type="name" /></option>
        </mm:fieldlist>
      </select>
      
      <c:if test="${!empty nfield}">
        <label>Search field</label>
        <mm:fieldlist fields="$nfield" nodetype="$ntype">
          <mm:fieldinfo type="searchinput" />
        </mm:fieldlist>
        <input name="search" type="hidden" value="search" />
        
        <c:if test="${!empty search}">
          <mm:listnodescontainer type="$ntype" id="result">
            <mm:fieldlist nodetype="$ntype" type="search">
              <mm:fieldinfo type="usesearchinput" /><%-- 'usesearchinput' can add constraints to the surrounding container --%>
            </mm:fieldlist>             
            <label>Found <mm:size id="size" /> nodes, change to</label>
            <input name="changeto" type="text" value="${changeto}" size="80" />

            <mm:size id="total" write="false" />
            <mm:maxnumber value="$max" />
            <mm:offset value="$offset" />
            <mm:sortorder field="number" direction="DOWN" />

            <mm:listnodes>
              <mm:first>
                <table border="0" cellspacing="0" cellpadding="3" style="margin-top:10px;">
                <caption>Total of ${total} ${ntype} nodes</caption>
                <tr>
                  <th>#</th>
                  <mm:fieldlist type="list" nodetype="$ntype" fields="$nfield">
                    <th><mm:fieldinfo type="guiname" /></th>
                  </mm:fieldlist>
                </tr>
              </mm:first>
              <tr>
                <c:if test="${!empty changeto}">
                  <mm:setfield name="$nfield">${changeto}</mm:setfield>
                  <c:set var="changed" value="${changed + 1}" />
                </c:if>
                
                <td><mm:index offset="${offset + 1}" /></td>
                <mm:fieldlist type="list" nodetype="$ntype" fields="$nfield">
                  <td><mm:fieldinfo type="guivalue" /></td>
                </mm:fieldlist>
              </tr>
              <mm:last>
                </table>
              </mm:last>              
            </mm:listnodes>

            <%-- browse pages --%>
            <div id="browse">
              <mm:url id="baseurl" referids="max,ntype,nfield,search,changeto" write="false">
                <mm:fieldlist nodetype="$ntype" type="search">
                  <mm:fieldinfo type="reusesearchinput" />
                </mm:fieldlist>
              </mm:url>
              <div class="browseleft">
                <c:if test="${offset != 0}">
                  &laquo; <a href="<mm:url referid="baseurl">
                    <mm:param name="offset">${(offset - max)}</mm:param>
                  </mm:url>">Previous</a>
                </c:if>
              </div>
              <div class="browseright">
                <c:if test="${(total - offset) > max}">
                  <a href="<mm:url referid="baseurl">
                    <mm:param name="offset">${(max + offset)}</mm:param>
                  </mm:url>">Next</a> &raquo;
                </c:if>
              </div>
            </div>
            
          </mm:listnodescontainer>
        </c:if>
      </c:if>
      
      <c:if test="${changed gt 0}">
        <p class="msg">${changed} nodes changed</p>
      </c:if>

      <p>
        <input name="action" id="action" type="submit" value="OK" />
        <input type="hidden" name="ntype" value="${ntype}" />
      </p>
      
    </fieldset>
  </form>
</mm:present><%-- /ntype --%>


</body>
</html>
</mm:cloud>
