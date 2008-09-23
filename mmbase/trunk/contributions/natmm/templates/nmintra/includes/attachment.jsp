<mm:related path="posrel,attachments" orderby="posrel.pos,attachments.title" directions="UP,UP" searchdir="destination">
    <mm:node element="attachments">
    <table>
    <tr><td style="width:60px;">
    <mm:field name="filename" jspvar="attachments_filename" vartype="String" write="false">
        <a href="<mm:attachment />" target="_blank">
            <% if(attachments_filename.indexOf(".pdf")>-1){ 
               %><img src="media/pdf.gif" alt="<mm:field name="title" />" border="0"><%
            } else if(attachments_filename.indexOf(".doc")>-1){ 
               %><img src="media/word.gif" alt="<mm:field name="title" />" border="0"><%
            } else if(attachments_filename.indexOf(".xls")>-1){
               %><img src="media/xls.gif" alt="<mm:field name="title" />" border="0"><%
            } else if(attachments_filename.indexOf(".ppt")>-1){ 
               %><img src="media/ppt.gif" alt="<mm:field name="title" />" border="0"><%
            } else { 
               %><img src="media/txt.gif" alt="<mm:field name="title" />" border="0"><%
            } %>
        </a>
    </mm:field>
    </td><td><a href="<mm:attachment />" target="_blank"><mm:notpresent referid="noattachmenttitle"><mm:field name="title" /></a></mm:notpresent
    ></td></tr>
    </table>
    </mm:node>
</mm:related>
