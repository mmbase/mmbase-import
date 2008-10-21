<%@ include file="page_base.jsp"
%>
<mm:write referid="style" />

<title><%= m.getString("config.config") %></title>
</head>
<mm:content language="$config.lang" country="$config.country" expires="0">
<body class="basic">

  <mm:context referid="config" id="config">
    <mm:write cookie="mmjspeditors_style"         referid="style_sheet" />
    <mm:write cookie="mmjspeditors_liststyle"     referid="liststyle"   />
    <mm:write cookie="mmjspeditors_language"      referid="lang"         />
    <mm:write cookie="mmjspeditors_country"       referid="country"         />
    <mm:write cookie="mmjspeditors_session"       referid="session"      />
    <mm:write cookie="mmjspeditors_indexoffset"   referid="indexoffset"      />
    <mm:write cookie="mmjspeditors_page_size"     referid="page_size"      />
    <mm:write cookie="mmjspeditors_xmlmode"       referid="xmlmode"      />
  </mm:context>
  <form name="config">
    <table class="edit" summary="editor configuration" width="93%"  cellspacing="1" cellpadding="3" border="0">
      <tr><th colspan="2"><%= m.getString("config.config") %></th></tr>
      <tr>
        <td><%= m.getString("config.pagesize")%></td>
        <td>
          <mm:import id="mmjspeditors_page_size_list" vartype="list">5,10,20,30,40,100</mm:import>
          <select name="mmjspeditors_page_size">
            <mm:stringlist referid="mmjspeditors_page_size_list">
               <option value="<mm:write/>" <mm:compare referid2="config.page_size">selected="selected"</mm:compare>><mm:write/></option>
            </mm:stringlist>
          </select>
          <mm:import id="mmjspeditors_indexoffset_list" vartype="list">0,1</mm:import>
          <select name="mmjspeditors_indexoffset">
            <mm:stringlist referid="mmjspeditors_indexoffset_list">
              <option value="<mm:write/>" <mm:compare referid2="config.indexoffset">selected="selected"</mm:compare>><mm:write/></option>
            </mm:stringlist>
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
        <td><%= m.getString("config.session")%></td>
        <td><input type="text" size="30" name="mmjspeditors_session" value="<mm:write referid="config.session" />" /></td>
      </tr>
      <tr>
        <td>
          <mm:write referid="config.lang">
            <mm:compare value="en" inverse="true">language/</mm:compare>
          </mm:write>
          <%= m.getString("config.language") %></td>
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
      <tr>
        <td><%= m.getString("config.country") %></td>
        <td>
          <input type="text" size="30" name="mmjspeditors_country" value="<mm:write referid="config.country" />" />
          <select name="countries" onChange="document.forms['config'].elements['mmjspeditors_country'].value = document.forms['config'].elements['countries'].value;">
            <mm:import id="countries" vartype="list">NL,BE,US,GB,CA,ES,DE,FR,CN,JP</mm:import>
            <mm:aliaslist referid="countries">
              <option value="<mm:write />" <mm:compare referid2="config.country"><mm:import id="foundcountry" />selected="selected"</mm:compare>><mm:locale language="$config.lang" country="$_" jspvar="loc"><%= loc.getDisplayCountry(loc)%></mm:locale></option>
            </mm:aliaslist>
            <mm:notpresent referid="foundcountry">
              <option value="<mm:write referid="config.country" />" selected="selected"><mm:locale language="$config.lang" country="$config.country" jspvar="loc"><%= loc.getDisplayCountry(loc)%></mm:locale></option>
            </mm:notpresent>
          </select>
        </td>
      </tr>
      <tr>
        <td><%= m.getString("config.xmlmode") %></td>
        <td>
          <select name="mmjspeditors_xmlmode">
            <mm:import id="xmlmodes" vartype="list">wiki,xml,prettyxml,kupu</mm:import>
            <mm:aliaslist referid="xmlmodes">
              <option value="<mm:write />" <mm:compare referid2="config.xmlmode">selected="selected"</mm:compare>><mm:write /></option>
            </mm:aliaslist>
          </select>
        </td>
      </tr>
      <input type="hidden" name="configsubmitted" value="yes" />
      <tr><td colspan="2"><input type="submit"  name="config" value="config" /></td></tr>
    </table>
  </form>
  <mm:cloud method="asis" jspvar="cloud">
    <%@ include file="foot.jsp"  %>
  </mm:cloud>
</mm:content>
