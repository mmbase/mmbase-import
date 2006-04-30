<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<!--
This file can be used to upload a zip with images, which will be unpacked and stored in MMBase.
You can add the following parameters to the request:
source=...  : the source node to which the images will be attached
role=....   : the relation by which the images will be attached to the source node
referer=... : the page to which the user is redirected after uploading is done
-->
<% 
String referrer =  request.getHeader("referer"); // html-specs are wrong
if(referrer==null) { referrer = ""; }
%>
<script>
   var clickedButton = '';
   function showMessage(obj,contentString){
      string = 'theTarget = document.getElementById("message");';
      eval(string);
      if(theTarget != null){
         theTarget.innerHTML = contentString;
      }
      clickedButton=obj;
      document.EvenementForm.command.value = clickedButton.value;
   }
</script>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud name="mmbase" method="http" rank="basic user" jspvar="cloud">
  <mm:import externid="source"/>
  <mm:import externid="role"/>
  <mm:import externid="referrer"><%= referrer %></mm:import>
  <div style="color:red;font-weight:bold;" id="message"></div>
  <form method="post" enctype="multipart/form-data" action="image_upload2.jsp" onsubmit="clickedButton.disabled=true;">
    <input type="hidden" name="source" value="<mm:write referid="source"/>"/>
    <input type="hidden" name="role" value="<mm:write referid="role"/>"/>
    <mm:present referid="referrer">
      <input type="hidden" name="referrer" value="<mm:write referid="referrer"/>"/>
    </mm:present>
    <input type="file" name="filename" />
    <input type="submit" value="upload" onclick="javascript:showMessage(this,'De zip-file met afbeeldingen wordt geimporteerd.<br>Een moment geduld a.u.b.');" />
  </form>
</mm:cloud>
</mm:content>
