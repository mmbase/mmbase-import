<%@ include file="page_base.jsp"
%><mm:write referid="style" />
<mm:import id="configsubmitted" externid="config" from="parameters" />
<mm:present referid="configsubmitted">
 <mm:remove referid="config" /> <!-- remove configuration, and recreate -->
 <mm:context id="config">
    <mm:import externid="page_size" />
    <mm:import externid="hide_search" />
    <mm:import externid="style_sheet" />
    <mm:import externid="lang" />
    <mm:write  referid="lang" cookie="mmjspeditors_language" />
 </mm:context>
 <mm:write referid="config" session="mmeditors_config" />
</mm:present>
<title><%= m.getString("config.config") %></title>
</head>
<body class="basic">
   <form name="config" method="post">
   <table class="edit" summary="editor configuration" width="93%"  cellspacing="1" cellpadding="3" border="0">
     <tr><th colspan="2"><%= m.getString("config.config") %></th></tr>
     <tr><td><%= m.getString("config.pagesize")%></td>  
         <td><input type="text" size="30" name="page_size" value="<mm:write referid="config.page_size" />" /></td>
     </tr>
     <tr><td><%= m.getString("config.hidesearch") %></td>
         <td><select name="hide_search">
             <option <mm:compare referid="config.hide_search" value="false">selected="selected"</mm:compare>><%= m.getString("false") %></option>
             <option <mm:compare referid="config.hide_search" value="true">selected="selected"</mm:compare>><%= m.getString("true") %></option>
             </select></td>
     </tr>
     <tr><td><%= m.getString("config.stylesheet") %></td>
         <td><input type="text" size="30" name="style_sheet" value="<mm:write referid="config.style_sheet" />" /></td>
     </tr>
     <tr><td><%= m.getString("config.language") %></td>  
         <td><input type="text" size="30" name="lang" value="<mm:write referid="config.lang" />" /></td>
     </tr>
     <tr><td colspan="2"><input type="submit"  name="config" value="config" /></td></tr>
   </table>
   </form>
<mm:locale language="$config.lang">
<mm:cloud method="http" sessionname="${SESSION}" jspvar="cloud">
<%@ include file="foot.jsp"  %>
</mm:cloud>
</mm:locale>
