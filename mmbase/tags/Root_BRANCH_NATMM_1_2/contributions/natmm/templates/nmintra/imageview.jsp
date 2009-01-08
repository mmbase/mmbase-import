<%@include file="/taglibs.jsp" %><mm:content type="text/html" escaper="none">
<%
String imageId = request.getParameter("image") ; 
if(imageId!=null) {

    %><mm:cloud

    ><mm:list nodes="<%= imageId %>" path="images"
    ><html>
    <head>
        <title><mm:field name="images.title"/></title>
        <link rel="stylesheet" type="text/css" href="../css/website.css">
        <meta http-equiv="imagetoolbar" content="no">
    </head>
    <body bgcolor="#FFF8E8">
        <div align="center">
        <br><br><% String imageTemplate="s(725x525)(>)"; %>
        <a href="javascript:self.close();"><img src=<%@include file="includes/imagessource.jsp" %> alt="sluit dit venster" title="sluit dit venster" border="0"></a>
        <br><br>
        <a href="javascript:self.close();">sluit</a>
        </div>
    </body>
    </html>
    </mm:list
    ></mm:cloud><% 
} 
%>
</mm:content>