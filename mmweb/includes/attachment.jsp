<mm:related path="posrel,attachments" orderby="posrel.pos">
<mm:node element="attachments">
<mm:first><%-- create table with header row --%>
   <table class="relationcontainer" <mm:present referid="twocolumns">style="width:100%;"</mm:present>>
        <tr>
                 <th <mm:present referid="twocolumns">colspan="2"</mm:present>>Attachments</th>
            </tr>
</mm:first>
<mm:present referid="twocolumns"><mm:odd><tr><mm:import id="oddattachment" /></mm:odd></mm:present>
<mm:notpresent referid="twocolumns"><tr></mm:notpresent>
	<td <mm:present referid="twocolumns">style="width:50%;"</mm:present>>
	<%-- inner table with attachement --%>
	<table cellpadding="0" cellspacing="0" class="layout" width="100%">
		<tr><td><mm:field name="title" jspvar="attachments_filename" vartype="String" write="false"
                            ><a href="<mm:attachment />"><%
                            if(attachments_filename.indexOf(".pdf")>-1){
                                %><img src="/media/pdf.gif" alt="" border="0"><%
                            } else if(attachments_filename.indexOf(".doc")>-1){ 
                                %><img src="/media/word.gif" alt="" border="0"><%
                            } else if(attachments_filename.indexOf(".xls")>-1){ 
                                %><img src="/media/xls.gif" alt="" border="0"><%
                            } else if(attachments_filename.indexOf(".ppt")>-1){
                                %><img src="/media/ppt.gif" alt="" border="0"><%
                            } else {
                                %> download <% 
                            } %></a></mm:field><br><mm:field name="description" />
            </td>
        </tr>
        <mm:notpresent referid="noattachmenttitle">
            <tr>
                <td>
                    <a href="<mm:attachment />"><mm:field name="title" /></a>
                </td>
            </tr>
        </mm:notpresent>
    </table><%-- end inner table with attachment --%>
</td>
<mm:present referid="twocolumns"><mm:even></tr><mm:remove referid="oddattachment" /></mm:even></mm:present>
<mm:notpresent referid="twocolumns"></tr></mm:notpresent>
<mm:last>
	<mm:present referid="oddattachment">
		<td></td></tr>
	</mm:present>
	</table>
</mm:last>
    </mm:node>
</mm:related>
