<%@ include file="page_base.jsp"
%>
<mm:write referid="style" />

<title><%= m.getString("config.config") %></title>
</head>
<body class="basic">

  <mm:context referid="config" id="config">
    <mm:write cookie="mmjspeditors_style"     referid="style_sheet" />
    <mm:write cookie="mmjspeditors_liststyle" referid="liststyle"   />
    <mm:write cookie="mmjspeditors_language"  referid="lang"         />
    <mm:write cookie="mmjspeditors_method"    referid="method"       />
    <mm:write cookie="mmjspeditors_session"   referid="session"      />
    <mm:write cookie="mmjspeditors_indexoffset"   referid="indexoffset"      />
    <mm:write cookie="mmjspeditors_page_size"   referid="page_size"      />
  </mm:context>  
  <form name="config">
    <table class="edit" summary="editor configuration" width="93%"  cellspacing="1" cellpadding="3" border="0">
      <tr><th colspan="2"><%= m.getString("config.config") %></th></tr>
      <tr>
        <td><%= m.getString("config.pagesize")%></td>  
        <td>
            <select name="mmjspeditors_page_size">
              <option value="5" <mm:compare referid="config.page_size" value="5">selected="selected"</mm:compare>>5</option>
              <option value="10" <mm:compare referid="config.page_size" value="10">selected="selected"</mm:compare>>10</option>
              <option value="20" <mm:compare referid="config.page_size" value="20">selected="selected"</mm:compare>>20</option>
              <option value="30" <mm:compare referid="config.page_size" value="30">selected="selected"</mm:compare>>30</option>
              <option value="40" <mm:compare referid="config.page_size" value="40">selected="selected"</mm:compare>>40</option>
              <option value="100" <mm:compare referid="config.page_size" value="100">selected="selected"</mm:compare>>100</option>
          </select>
            <select name="mmjspeditors_indexoffset">
              <option value="0" <mm:compare referid="config.indexoffset" value="0">selected="selected"</mm:compare>>0</option>
              <option value="1" <mm:compare referid="config.indexoffset" value="1">selected="selected"</mm:compare>>1</option>
          </select>
        </td>
      </tr>
      <tr>
        <td><%= m.getString("config.stylesheet") %></td>
        <td>
          <select name="mmjspeditors_style">
            <option value="base.css" <mm:compare referid="config.style_sheet" value="base.css">selected="selected"</mm:compare>>basic</option>
            <option value="mmbase.css" <mm:compare referid="config.style_sheet" value="mmbase.css">selected="selected"</mm:compare>>default</option>
            <option value="mmbase-1.6.css" <mm:compare referid="config.style_sheet" value="mmbase-1.6.css">selected="selected"</mm:compare>>mmbase 1.6</option>
            <option value="classic.css" <mm:compare referid="config.style_sheet" value="classic.css">selected="selected"</mm:compare>>classic</option>
            <option value="red.css" <mm:compare referid="config.style_sheet" value="red.css">selected="selected"</mm:compare>>red</option>
            <option value="blue.css" <mm:compare referid="config.style_sheet" value="blue.css">selected="selected"</mm:compare>>blue</option>
            <option value="purple.css" <mm:compare referid="config.style_sheet" value="purple.css">selected="selected"</mm:compare>>purple</option>
            <option value="yellow.css" <mm:compare referid="config.style_sheet" value="yellow.css">selected="selected"</mm:compare>>yellow</option>
            <option value="my_editors.css.jsp" <mm:compare referid="config.style_sheet" value="my_editors.css.jsp">selected="selected"</mm:compare>>My Editors</option>
          </select>
        </td>
      </tr>
      <tr>
        <td><%=m.getString("config.liststyle")%></td>
        <td>
          <select name="mmjspeditors_liststyle">
            <option value="short" <mm:compare referid="config.liststyle" value="short">selected="selected"</mm:compare>><%=m.getString("search_node.showshortlist")%></option>
            <option value="long" <mm:compare referid="config.liststyle" value="long">selected="selected"</mm:compare>><%=m.getString("search_node.showall")%></option>
          </select>
        </td>
      </tr>
      <tr>
        <td><%= m.getString("config.method") %></td>
        <td>
          <select name="mmjspeditors_method">
            <option value="http" <mm:compare referid="config.method" value="http">selected="selected"</mm:compare>>http</option>
            <option value="loginpage" <mm:compare referid="config.method" value="loginpage">selected="selected"</mm:compare>>loginpage</option>
            <option value="delegate" <mm:compare referid="config.method" value="delegate">selected="selected"</mm:compare>>delegate</option>
          </select>
        </td>
      </tr>
      <tr>
        <td><%= m.getString("config.session")%></td>  
        <td><input type="text" size="30" name="mmjspeditors_session" value="<mm:write referid="config.session" />" /></td>
      </tr>
      <tr>
        <td><%= m.getString("config.language") %></td>  
        <td>
          <input type="text" size="30" name="mmjspeditors_language" value="<mm:write referid="config.lang" />" />
          <select name="languages" onChange="document.forms['config'].elements['mmjspeditors_language'].value = document.forms['config'].elements['languages'].value;">
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
      <input type="hidden" name="configsubmitted" value="yes" />
      <tr><td colspan="2"><input type="submit"  name="config" value="config" /></td></tr>
    </table>
  </form>
  
  <mm:locale language="$config.lang">
    <mm:cloud method="$config.method" loginpage="login.jsp" sessionname="$config.session" jspvar="cloud">
      <%@ include file="foot.jsp"  %>      
    </mm:cloud>
  </mm:locale>
