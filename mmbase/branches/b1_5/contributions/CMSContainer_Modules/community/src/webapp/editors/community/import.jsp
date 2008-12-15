<%@include file="globals.jsp"
%><%@ taglib uri="http://jakarta.apache.org/struts/tags-html"	prefix="html"%>
<fmt:setBundle basename="cmsc-community" scope="request" />
<mm:content type="text/html" encoding="UTF-8" expires="0">
<cmscedit:head title="ewsletter.subscription.manage.newsletteroverview"/>
<script language="javascript">
function checkid(chk){
 var i=0;
 var id=0;
 for(i=0;i<chk.length;i++){
    if(chk[i].checked==true){
                            putid=chk[i].id;
                            sid=putid+1;
                            //sid=did+1;
                            CheckedSpan=document.getElementById(sid);
                           // alert(did);
                            CheckedDiv=document.getElementById("msg");
                           // FmtParam=document.getElementById("inmsg");
                            if (getOs()) {
                                         Text=CheckedDiv.innerText;
                                         Text=Text.replace("#",CheckedSpan.innerText);
	                                     if(confirm(Text))
		                                           {document.forms[0].submit();}
                             } else {
                                        Text=CheckedDiv.textContent;
                                         Text=Text.replace("#",CheckedSpan.textContent);
	                                     if(confirm(Text))
		                                           {document.forms[0].submit();}
                             }
     } 
  }
} 
function getOs()
{
   
   if(navigator.userAgent.indexOf("MSIE")>0) {
        return true;
   }
   if(isFirefox=navigator.userAgent.indexOf("Firefox")>0){
        return false;
   }
  
}
</script>
<div class="tabs">
	<div class="tab_active">
		<div class="body">
			<div>
				<a href="#"><fmt:message key="community.data.title" /> </a>
			</div>
		</div>
	</div>
</div>
<div class="editor">
	<div class="body">

		<html:form action="/editors/community/ReferenceImportExportAction"
			enctype="multipart/form-data">
			<tr>
				<td>	<span id="r11"><input name="level" id="r1" type="radio" value="clean"><fmt:message key="community.data.import.option.clean" /></span>
                        <div id="msg" style="display: none;"><fmt:message key="community.data.import.option"><fmt:param>#</fmt:param>
                        </fmt:message>
                     </div>
				</td>
				<br>
				<td>
					    <span id="r21"><input name="level" id="r2" type="radio" value="over"><fmt:message key="community.data.import.option.override" /></span>
				</td>
				<br>
				<td>
					    <span id="r31"><input name="level" id="r3" type="radio" value="add"	checked="checked"><fmt:message key="community.data.import.option.add" /></span>
			   </td>
			</tr>	
			<br>
			<br>
			<br>
			<html:file property="datafile" />
			<input type="hidden" name="action" value="importsubscription" />
			<input type="hidden" name="newsletterId"
				value="${requestScope.newsletterId}" />
			<input type="button" value="Import" id="bn" onclick="checkid(level);" />
		</html:form>
		<div style="margin:4px;color:red;">
			<html:messages id="file" message="false">
				<bean:write name="file" />
				<br>
			</html:messages>
		</div>
		
	</div>

</div>
</div>
</mm:content>

