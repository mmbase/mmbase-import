<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><mm:content postprocessor="reducespace" expires="0">
<mm:cloud rank="editor">
  <jsp:directive.include file="/shared/setImports.jsp" />
  <mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
    <mm:param name="extraheader">
      <title><di:translate key="education.editwizards" /></title>
      <style type="text/css">
        a {
        font-size: 11px;
        }
        .menu_font{
        font-size: 11px;
      }
      .folderBodyTree {
         width: 20%;
         height: 90%;
         overflow: scroll;
      }
      .contentBody {

        top: 0em;
        left: 0em;
        right: 0em;
        bottom: 0em;
        height: 100%;
        padding: 0em;
      }
      .componentBody {
      }
    </style>
    </mm:param>
    <mm:param name="extrabody">
      onLoad="loadTree();" onUnload="storeTree();"
    </mm:param>

  </mm:treeinclude>


  <table cellpadding="0" cellspacing="0" border="0" width="100%" height="100%">
   <tr class="navigationbar">
      <td colspan="2" class="titlebar">
        <img src="${mm:treefile('/gfx/icon_agenda.gif', pageContext, includePath)}"
             title="${di:translate('education.editwizards')}" alt="${di:translate('education.editwizards')}" />
        <span class="menu_font">Editwizards:</span>
        <mm:treeinclude page="/education/wizards/tree_top_menu.jsp" objectlist="$includePath" />
      </td>
   </tr>
   <tr>
      <td style="width:20%">
         <div id="left_menu" style="overflow:auto; width:100%; height:100%" >
           <mm:treeinclude debug="html" page="/education/wizards/code.jsp" objectlist="$includePath" />
         </div>
      </td>

      <td width="100%">
        <mm:treefile id="ok" page="/education/wizards/ok.jsp" objectlist="$includePath" referids="$referids" write="false" />
        <iframe id="text" name="text" width="99%" height="100%" marginwidth="0" marginheight="0" border="1" src="${ok}"></iframe>
      </td>
   </tr>
</table>

<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids"/>
</mm:cloud>
</mm:content>
