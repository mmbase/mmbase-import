<%@ include file="page_base.jsp"
%><mm:write referid="style" />
<mm:import id="configsubmitted" externid="config" from="parameters" />

<mm:present referid="configsubmitted">
 <mm:remove referid="config" /> <!-- remove configuration, and recreate -->
 <mm:context id="config">
    <mm:import externid="page_size" />
    <mm:import externid="hide_search" />
    <mm:import externid="style_sheet" />
    <mm:import externid="method" />
    <mm:import externid="session" />
    <mm:import externid="lang" />
    <mm:write  referid="lang"        cookie="mmjspeditors_language" />
    <mm:write  referid="style_sheet" cookie="mmjspeditors_style" />
    <mm:write  referid="method" cookie="mmjspeditors_method" />
    <mm:write  referid="session" cookie="mmjspeditors_session" />
    <mm:log>Writing lang cookie <mm:write referid="lang" /></mm:log>
 </mm:context>
 <mm:write referid="config" session="mmeditors_config" />
 <% response.sendRedirect(response.encodeRedirectURL("config.jsp"));%>
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
             <option value="false" <mm:compare referid="config.hide_search" value="false">selected="selected"</mm:compare>><%= m.getString("false") %></option>
             <option value="true"  <mm:compare referid="config.hide_search" value="true">selected="selected"</mm:compare>><%= m.getString("true") %></option>
             </select></td>
     </tr>
     <tr><td><%= m.getString("config.stylesheet") %></td>
         <td><select name="style_sheet">
             <option value="base.css" <mm:compare referid="config.style_sheet" value="base.css">selected="selected"</mm:compare>>basic</option>
             <option value="mmbase.css" <mm:compare referid="config.style_sheet" value="mmbase.css">selected="selected"</mm:compare>>default</option>
             <option value="classic.css" <mm:compare referid="config.style_sheet" value="classic.css">selected="selected"</mm:compare>>classic</option>
             <option value="red.css" <mm:compare referid="config.style_sheet" value="red.css">selected="selected"</mm:compare>>red</option>
             <option value="blue.css" <mm:compare referid="config.style_sheet" value="blue.css">selected="selected"</mm:compare>>blue</option>
             <option value="purple.css" <mm:compare referid="config.style_sheet" value="purple.css">selected="selected"</mm:compare>>purple</option>
             <option value="yellow.css" <mm:compare referid="config.style_sheet" value="yellow.css">selected="selected"</mm:compare>>yellow</option>
            </select></td>
     </tr>
     <tr><td><%= m.getString("config.method") %></td>
         <td><select name="method">
             <option value="http" <mm:compare referid="config.method" value="http">selected="selected"</mm:compare>>http</option>
             <option value="loginpage" <mm:compare referid="config.method" value="loginpage">selected="selected"</mm:compare>>loginpage</option>
            </select></td>
     </tr>
     <tr><td><%= m.getString("config.session")%></td>  
         <td><input type="text" size="30" name="session" value="<mm:write referid="config.session" />" /></td>
     </tr>
     <tr><td><%= m.getString("config.language") %></td>  
         <td><input type="text" size="30" name="lang" value="<mm:write referid="config.lang" />" />
              <select name="languages" onChange="document.forms['config'].elements['lang'].value = document.forms['config'].elements['languages'].value;">
           <mm:import id="langs" vartype="list">en,nl,it,es,fr,eo,fy,de,zh,ja</mm:import>
           <mm:aliaslist referid="langs">
             <option value="<mm:write />" <mm:compare referid2="config.lang"><mm:import id="found" />selected="selected"</mm:compare>><mm:locale language="$_" jspvar="loc"><%= loc.getDisplayLanguage(loc)%></mm:locale></option>
           </mm:aliaslist>
           <mm:notpresent referid="found">
             <option value="<mm:write referid="config.lang" />" selected="selected"><mm:locale language="$config.lang" jspvar="loc"><%= loc.getDisplayLanguage(loc)%></mm:locale></option>
           </mm:notpresent>
         </select>
         </td>

     </tr>
     <tr><td colspan="2"><input type="submit"  name="config" value="config" /></td></tr>
   </table>
   </form>
<mm:locale language="$config.lang">
<mm:cloud method="$config.method" loginpage="login.jsp" sessionname="$config.session" jspvar="cloud">
<%@ include file="foot.jsp"  %>
</mm:cloud>
</mm:locale>
