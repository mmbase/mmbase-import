<%@page language="java" contentType="text/html;charset=utf-8"
%><%@include file="globals.jsp" 
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="deepcopy.title">
<script language="javascript">

   function opennew() {
      window.open("searchsite.jsp","searchsite","height=400, width=400");
     // openPopupWindow("searchsite", 500, 500, "searchsite.jsp");
   }
   function nextStep() {
      if(document.forms[0]["destination"].value == "" ){
         alert('<fmt:message key="deepcopy.destinationchannel.empty" />')
         return;
      }
      document.forms[0].submit();
   }
</script>
   <style type="text/css">
   p { 
      margin-left:10px;
   }
   .contents{
      margin-left:10px;
   }
  input {
         margin-left:20px;
  }
   </style>
</cmscedit:head>
<mm:cloud jspvar="cloud" rank="basic user" loginpage="../login.jsp">
 <mm:import externid="number" from="parameters" />
<body>
<cmscedit:sideblock title="deepcopy.title" titleClass="side_block_green">
 <mm:present referid="number">
   <form action="channelinput.jsp" >
      <input type="hidden" name="parent" value="<mm:write referid="number" />"/>
      <div >
         <p><fmt:message key="deepcopy.destination.site.title" />
         <input type="input" name="sitetitle" id="sitetitle" width="12px" length="12" size="12"><font color="red"></font> 
         <input type="hidden" name="destination" id="destination">
         <a href="#" onclick="javascript:opennew()">
         <fmt:message key="deepcopy.search" /></a><br/>
         <br/><br/>
         <input type="button" onclick="nextStep()" style="margin-left:10px;height:auto" value="<fmt:message key="deepcopy.button.next" />"> 
      </div>
   </form>
</mm:present>
</cmscedit:sideblock>
</body>
</mm:cloud>
</html:html>
</mm:content>