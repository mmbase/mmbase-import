<%@ include file="settings.jsp" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="cmsc" scope="request" />
<mm:content type="text/html" expires="0" language="en">
<%
    /**
     * Over riding the default upload.jsp from mmbase for layout and language purposes
     *
     * upload.jsp
     *
     * @since    MMBase-1.6
     * @author   Freek Punt
     */

String did = request.getParameter("did");
if (did==null) {
    out.write("No valid parameters for the upload routines. Make sure to supply did field.");
    return;
}

String maxsize = request.getParameter("maxsize");
if (maxsize == null || maxsize.length() == 0) {
	maxsize = String.valueOf(ewconfig.maxupload);
}

%>

<html>
<head>
<title><fmt:message key="upload.title" /></title>
<link rel="stylesheet" type="text/css" href="../../../../editors/css/main.css" />
<script type="text/javascript">
    function upload( filetype ) {
        //validate file type
        var file = document.getElementById("file");
        var i=file.value.lastIndexOf(".");
        var ext=file.value.substring(i);
        var ext1=ext.toLowerCase();
        if(filetype == "image"){//CMSC-1254
         if(ext1!=".gif" && ext1!=".jpg" && ext1!=".jpeg"&&ext1!=".tiff"&&ext1!=".tif"&&ext1!=".bmp"&&ext1!=".svg"&&ext1!=".png")
         {
            alert('<fmt:message key="asset.notimage.warning" />');
                return false;
         }
        }

        var f=document.forms[0];
        f.submit();
        setTimeout('sayWait();',0);
    }

    function sayWait() {
        document.getElementById("form").style.visibility="hidden";
        document.getElementById("busy").style.visibility="visible";
    }

    function closeIt() {
        window.close();
    }

</script>
</head>
<%
    String wizard="";
    Object con=ewconfig.subObjects.peek();
    if (con instanceof Config.SubConfig) {
        wizard=((Config.SubConfig)con).wizard;
    }
%>
<body>

   <div class="side_block" id="form" style="width:290px;">
      <!-- bovenste balkje -->

      <div class="header">
         <div class="title"><fmt:message key="upload.title" /></div>
         <div class="header_end"></div>
      </div>
      <div class="body">
         <p>
            <fmt:message key="upload.intro" />
         </p>
         <p>
            <form action="<mm:url page="processuploads.jsp" />?did=<%=did%>&proceed=true&popupid=<%=popupId%>&sessionkey=<%=ewconfig.sessionKey%>&wizard=<%=wizard%>&maxsize=<%=maxsize%>" enctype="multipart/form-data" method="POST" >
               <input type="file" name="<%=did%>" id="file"></input><br />
               <input type="button" onclick="upload('${param.filetype}');" value="<fmt:message key="upload.button.upload" />"></input><br />
            </form>
         </p>
      </div>
      <!-- einde block -->
      <div class="side_block_end"></div>
   </div>


   <div class="side_block_green" id="busy" style="visibility:hidden;position:absolute;top:0px;width:290px;">
      <!-- bovenste balkje -->

      <div class="header">
         <div class="title"><fmt:message key="upload.title" /></div>
         <div class="header_end"></div>
      </div>
      <div class="body">
         <p>
            <fmt:message key="upload.uploading">
               <fmt:param><a href="#" onclick="closeIt(); return false;"></fmt:param>
               <fmt:param></a></fmt:param>
            </fmt:message>
         </p>
      </div>
      <!-- einde block -->
      <div class="side_block_end"></div>
   </div>

</div>
</body>
</html>
</mm:content>