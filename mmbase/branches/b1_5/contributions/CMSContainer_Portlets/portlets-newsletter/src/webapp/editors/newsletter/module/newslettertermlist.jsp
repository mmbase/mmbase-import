<%@page language="java" contentType="text/html;charset=utf-8"
%><%@include file="globals.jsp"
%><%@page import="java.util.Iterator,com.finalist.cmsc.mmbase.PropertiesUtil"
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="newsletter.term.title">
<c:url var="actionUrl" value="/editors/newsletter/module/NewsletterTermAction.do"/>
<c:url var="addUrl" value="/editors/newsletter/module/NewsletterTermAction.do?method=addInit"/>
<c:url var="termUrl" value="/editors/newsletter/NewsletterTermsAddAction.do"/>
<script src="../../repository/search.js" type="text/javascript"></script>
<script src="../../repository/content.js" type="text/javascript"></script>
<script src="../../../js/prototype.js" type="text/javascript"></script>
<script src="../newsletter.js" type="text/javascript"></script>
<script type="text/javascript">
function update(number) {
   if($("name_"+number).value == ""){
      alert('<fmt:message key="newsletter.term.mandatory" />');
      return;
   }
   var myAjax = new Ajax.Request(
    '${actionUrl}',
    {   parameters:"method=modify&id="+number+"&name="+$("name_"+number).value,
    onComplete: postUpdate
    }
  );
}

function deleteInfo(number,offset,resultLength) {
   if(confirm('<fmt:message key="newsletter.term.delete.confirm" />')) {
      if(resultLength == "1") {
         offset = eval(offset -1);
      }
      if(offset < 0) {
         offset = 0;
      }
      document.forms[0].offset.value = offset;
      document.forms[0].method.value = "delete";
      $("id").value = number;
      document.forms[0].submit();
   }            
}

function postUpdate(originalRequest) {
  var responseTxt =  originalRequest.responseText;
  if(responseTxt ==  "term.modify.success") {
    alert('<fmt:message key="newsletter.term.update.success" />');
  }
  else if(responseTxt ==  "term.exist") {
    alert('<fmt:message key="newsletter.term.exist" />');
  }
}

</script>
</cmscedit:head>
<body>
<mm:cloud jspvar="cloud" rank="basic user" loginpage="../../login.jsp">
<mm:import externid="newsletterId"/>
<mm:import externid="action">search</mm:import><%-- either: search of select --%>

<div class="tabs">
<div class="tab_active">
<div class="body">
   <div>
      <a href="NewsletterTermAction.do?method=list&init=true"><fmt:message key="newsletter.term.title" /></a>
   </div>
</div>
</div>
</div>
<div class="editor" style="height:500px">
<div class="body">
<html:form action="/editors/newsletter/module/NewsletterTermAction" method="post">
   <html:hidden property="method" value="list"/>
   <input type="hidden" name="id" id="id" value=""/>
   <input type="hidden" name="deleteRequest" value=""/>
   <html:hidden property="offset"/>
   <input type="hidden" name="newsletter" value="${newsletterId}"/>
   <mm:notpresent referid="newsletterId">
      <mm:hasrank minvalue="administrator">
   <ul class="shortcuts">
     <li class="new" style="text-decoration: none;">
            <a href="${addUrl}"><fmt:message key="newsletter.term.add" />
            </a>
     </li>
   </ul>
     </mm:hasrank>
   </mm:notpresent>
   <table border="0" style="padding-left:10px;">
      <tr>
         <td style="width: 110px"><fmt:message key="newsletter.term.name" /></td>
         <td><html:text size="20" property="name"/></td>
      </tr>
      <tr>
      <td></td>
      <td>
         <input type="submit" name="submitButton" onclick="setOffset(0);" value="<fmt:message key="newsletter.term.search" />"/>   
     </td>
   </tr>
   </table>
</html:form>
</div>
<div class="ruler_green"><div><fmt:message key="newsletter.term.search.result" /></div></div>
<div class="body">
<mm:notpresent referid="newsletterId" >
<form action="${actionUrl}" name="termForm" method="post">
<input type="hidden" name="method" value="delete"/>
<input type="hidden" name="deleteRequest" value=""/>
</mm:notpresent>
<mm:present referid="newsletterId">
<form action="${termUrl}" name="termForm" method="post">
<input type="hidden" name="newsletterId" value="${newsletterId}"/>
</mm:present>
<mm:import jspvar="resultCount" vartype="Integer">${resultCount}</mm:import>
<mm:import externid="offset" jspvar="offset" vartype="Integer">${offset}</mm:import>
<c:if test="${resultCount > 0}">
<%@include file="../../repository/searchpages.jsp" %>
<mm:notpresent referid="newsletterId">
<c:if test="${fn:length(resultList) >1}">
<input type="button" class="button" value="<fmt:message key="newsletter.term.action.delete" />" onclick="massDelete('<fmt:message key="newsletter.term.delete.confirm" />')"/>
</c:if>
</mm:notpresent>
<mm:present referid="newsletterId">
<input type="submit" class="button" value="<fmt:message key="newsletter.term.action.link" />"/>
</mm:present>
   <table>
      <tr class="listheader">
         <th> <input type="checkbox"  name="selectall"  onclick="selectAll(this.checked, 'termForm', 'chk_');" value="on"/> </th>
         <th><fmt:message key="newsletter.term.name.upper" /></th>
         <mm:notpresent referid="newsletterId">
         <th><fmt:message key="newsletter.term.action.upper" /></th>
         </mm:notpresent>
      </tr>
      <tbody class="hover">
          <c:set var="useSwapStyle">true</c:set>
         <mm:listnodes referid="resultList" jspvar="node">
               <tr <c:if test="${useSwapStyle}">class="swap"</c:if>>
                  <td style="white-space:nowrap;">
                   <input type="checkbox" name="chk_<mm:field name="number" />" id="chk_<mm:field name="number" />" value="<mm:field name="number"/>">
                    <mm:notpresent referid="newsletterId">
                     <mm:hasrank minvalue="administrator">
                         <a href="javascript:deleteInfo('<mm:field name="number"/>','${offset}',${fn:length(resultList)})">
                                  <img src="../../gfx/icons/delete.png" title="<fmt:message key="newsletter.term.action.delete" />"/></a>
                     </mm:hasrank>  
                    </mm:notpresent> 
                   </td>
                   <td >
                    <mm:notpresent referid="newsletterId">
                   <input type="text" name="key" id="name_<mm:field name="number"/>"  value="<mm:field name="name"/>"/>
                  </mm:notpresent> 
                   <mm:present referid="newsletterId">
                     <mm:field name="name"/>
                   </mm:present> 
                   </td>
                    <mm:notpresent referid="newsletterId">
                      <td>
                     <mm:hasrank minvalue="administrator">
                      <a href="javascript:update('<mm:field name="number"/>')"><fmt:message key="newsletter.term.action.save" /></a>
                       </mm:hasrank>     
                      </td>
                    </mm:notpresent> 
               </tr>
           <c:set var="useSwapStyle">${!useSwapStyle}</c:set>
         </mm:listnodes>
      </tbody>
   </table>
<mm:notpresent referid="newsletterId">
<c:if test="${fn:length(resultList) >1}">
<input type="button" class="button" value="<fmt:message key="newsletter.term.action.delete" />" onclick="massDelete('<fmt:message key="newsletter.term.delete.confirm" />')"/>
</c:if>	
</mm:notpresent>
<mm:present referid="newsletterId">
<input type="submit" class="button" value="<fmt:message key="newsletter.term.action.link" />" onclick="massDelete()"/>
</mm:present>
</c:if>
</form>
</div>
<c:if test="${resultCount == 0}">
<fmt:message key="newsletter.term.noresult" />
</c:if>
<c:if test="${isAddSuccess != null}">
<fmt:message key="newsletter.term.add.success" />
</c:if>
<c:if test="${resultCount > 0}">
<%@include file="../../repository/searchpages.jsp" %>
</c:if>
</mm:cloud>
</body>
</html:html>
</mm:content>