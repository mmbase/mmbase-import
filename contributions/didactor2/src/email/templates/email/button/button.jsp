<%--
This jsp page will render a button,
include this page, while sendning it the following parameters:
- width: the width in pixels
- height: the height in pixels
- style: the (css) style of the button
- caption: the text on the button
- link: the link of the button (use either this one, or onclick)
- onclick: the javascript onclick of the button (use either this one, or link)
- target: leave empty, or use, '_top' or '_blank', these work just like the HTML magic targets
--%>
<%
  String target = "document.location.href = '";
  String targetEnd = "'";
  if(request.getParameter("target").equals("_top")) {
    target = "top.location.href = '";
  }
  if(request.getParameter("target").equals("_blank")) {
    target = "window.open('";
    targetEnd = "','didactorPopup','width=730,height=500')";
  }

  String onclick = request.getParameter("onclick");
  if(request.getParameter("link") != null && request.getParameter("link").length() > 0 && !request.getParameter("link").equals("null")) {
    onclick = target+request.getParameter("link")+targetEnd;
  }
%>
<table
      <%if(request.getParameter("width") != null) {%> width="<%=request.getParameter("width")%>"<%}%>
      height="<%=request.getParameter("height")%>" cellspacing=0>
	<tr>
    <td align=center valign=middle onclick="<%=onclick%>" class="<%=request.getParameter("style")%>">
      <%=request.getParameter("caption")%>
    </td>
	</tr>
</table>
