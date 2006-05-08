<% 
// show navigation to other pages if there are more than 10 articles
if(listSize>10) { 
    %><table cellpadding="0" cellspacing="0" border="0" align="center">
        <tr>
            <td><img src="media/spacer.gif" width="10" height="1"></td>
            <td><img src="media/spacer.gif" width="1" height="1"></td>
            <td><div><%
                if(thisOffset>0) { 
                    %><a target="_top" href="<%= sTemplateUrl  %><%= extTemplateQueryString  %>&offset=<%= thisOffset-1 
                            %>"><<</a>&nbsp;&nbsp;<%
                } for(int i=0; i < ((listSize-1)/10 + 1); i++) { 
			           if((i>0)&&(i%20==0)) { %> <% } 
                    if(i==thisOffset) {
                        %><%= i+1 %>&nbsp;&nbsp;<%
                    } else { 
                        %><a target="_top" href="<%= sTemplateUrl  %><%=  extTemplateQueryString  %>&offset=<%= i 
                            %>"><%= i+1 %></a>&nbsp;&nbsp;<%
                    } 
                }
                if(thisOffset+1<((listSize-1)/10 + 1)) { 
                    %><a target="_top" href="<%= sTemplateUrl  %><%= extTemplateQueryString %>&offset=<%= thisOffset+1 
                            %>">>></a><%
                } 
            %></div>
            </td>
        </tr>
        <tr>
            <td><img src="media/spacer.gif" width="1" height="10"></td>
            <td><img src="media/spacer.gif" width="1" height="10"></td>
            <td><img src="media/spacer.gif" width="1" height="10"></td>
        </tr>
    </table><%
}
%>