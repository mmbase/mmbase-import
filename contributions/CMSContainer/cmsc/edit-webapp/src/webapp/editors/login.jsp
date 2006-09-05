<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp" %>
<mm:import from="request" externid="referrer">.</mm:import>
<mm:import externid="reason">please</mm:import>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<head>
   <title><fmt:message key="login.title" /></title>
   <link rel="stylesheet" type="text/css" href="<cmsc:staticurl page='/editors/css/main.css'/>" />
   <style type="text/css">
      body {
         behavior: url(./css/hover.htc);
         margin: 100px;
         text-align: center;
      }
      div.side_block, div.side_block table {
         position: relative;
         margin: 0px auto;
      }
   </style>
<script type="text/javascript">
function setFocusOnFirstInput() {
    var form = document.forms[0];
    for (var i=0; i < form.elements.length; i++) {
        var elem = form.elements[i];
        // find first editable field
        var hidden = elem.getAttribute("type"); //.toLowerCase();
        if (hidden != "hidden") {
            elem.focus();
            break;
        }
    }
}
</script>
   
</head>	
<body onload="setFocusOnFirstInput()">
   <script type="text/javascript">
   <!--
   if (window!= top)
   top.location.href=location.href
   // -->
   </script>
   <div class="side_block">
      <div class="header">
         <div class="title"><fmt:message key="login.title" /></div>
         <div class="header_end"></div>
      </div>
      &nbsp;
<!-- restricted url: <mm:write referid="referrer" />  -->
      <form method="post" action="<mm:url page='/editors/' />" target="_top">
         <input type="hidden" name="command" value="login" />
         <input type="hidden" name="cloud" value="mmbase" /><!-- also default -->
         <input type="hidden" name="authenticate" value="name/password" />

         <table>
            <tr class="inputrow">
               <td><fmt:message key="login.name" /></td>
               <td><input type="text" name="username"/></td>
            </tr>
            <tr class="inputrow">
               <td><fmt:message key="login.password" /></td>
               <td><input type="password" name="password"/></td>
            </tr>
            <tr>
               <td class="version"><fmt:message key="login.version" /> <cmsc:version/></td>
               <td id="Submit" align="right"><input type="submit" value="<fmt:message key="login.submit" />" /></td>
            </tr>
            <mm:write referid="reason">
               <mm:compare value="failed">
                  <tr class="inputrow">
                     <td colspan="2"><fmt:message key="login.error" /></td>
                  </tr>
               </mm:compare>
            </mm:write>
         </table>
      </form>
      <div class="side_block_end"></div>
   </div>
</body>
</html:html>
</mm:content>