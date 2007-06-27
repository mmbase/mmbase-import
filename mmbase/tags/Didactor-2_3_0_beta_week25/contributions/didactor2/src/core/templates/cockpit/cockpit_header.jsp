<%@page contentType="text/html; charset=UTF-8"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" 
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%><mm:import externid="extraheader" />
<mm:import externid="extrabody" />
<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" authenticate="asis">
<jsp:directive.include file="/shared/setImports.jsp" />
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <mm:write referid="extraheader" escape="none" />    
    <mm:link page="/core/js/sarissa/sarissa.js">
      <script src="${_}"><!-- Help IE --></script>
    </mm:link>    
    <mm:link page="/core/js/utils.js">
      <script src="${_}"><!-- Help IE --></script>
    </mm:link>    
    <link rel="stylesheet" type="text/css" href="${mm:treefile('/css/base.css', pageContext,includePath)}" />
  </head>
  <body class="componentbody" <mm:write referid="extrabody" escape="none" />>

   <div class="">
     <mm:import externid="reset" />
      <mm:treeinclude page="/cockpit/applicationbar.jsp" objectlist="$includePath"
                      referids="$referids,reset?"/>
      <mm:treeinclude page="/cockpit/providerbar.jsp" objectlist="$includePath" referids="$referids"
                      />
      <mm:treeinclude page="/cockpit/educationbar.jsp" objectlist="$includePath" referids="$referids" />
    </div>

</mm:cloud>
</mm:content>
