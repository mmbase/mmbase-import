<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<c:url var="context" value="/"/>
<head>
<cmscedit:head title="dataconversion.title">
<script language="javascript">
   function check() {
      var node = document.getElementById("node");
      if(node.value == "") {
         alert('<fmt:message key="dataconversion.nodeid.empty" />');
         return ;
      }
      document.forms["dataconversion"].submit();
   }
</script>
</cmscedit:head>
<body>
<mm:cloud jspvar="cloud" >
      <div class="tabs">
         <div class="tab_active">
            <div class="body">
               <div>
                  <a href="#" ><fmt:message key="dataconversion.title" /></a>
               </div>
            </div>
         </div>
      </div>      
      <div class="editor" style="height:500px">
      <div class="body">
         <form name="dataconversion" action="${context}editors/dataconversion/Converse.do"  method="post">          	
                <table border="0" >
                   <tr>
                      <td><fmt:message key="dataconversion.driver" /></td><td><input type="text" id="driver"  name="driver" size="50" value="org.postgresql.Driver"/></td>
                   </tr>
                   <tr>
                      <td><fmt:message key="dataconversion.url" /></td><td><input type="text" id="url"  name="url" size="50" value="jdbc:postgresql://192.168.1.230:5432/roa"/></td>
                   </tr>
                   <tr>
                      <td><fmt:message key="dataconversion.user" /></td><td><input type="text" id="user"  name="user" size="50" value="root"/></td>
                   </tr>
                   <tr>
                      <td><fmt:message key="dataconversion.password" /></td><td><input type="password" id="password"  name="password" size="50"/></td>
                   </tr>
                   <tr>
                    <tr>
                      <td><fmt:message key="dataconversion.nodeid" /></td><td><input type="text" id="node"  name="node" size="50"/></td>
                   </tr>
                      <td><input type="button" value="<fmt:message key="dataconversion.submit" />" onclick="check()"/></td>
                   </tr>
                </table>
    	    </form>
      </div>
 </div>
</mm:cloud>
</body>
</html:html>
</mm:content>