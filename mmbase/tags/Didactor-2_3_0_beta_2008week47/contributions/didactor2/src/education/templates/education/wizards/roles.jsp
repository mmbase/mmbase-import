<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <mm:content postprocessor="none">
    <mm:cloud method="delegate">
      <mm:import id="wizardlang">${language}</mm:import>
      <jsp:directive.include file="mode.include.jsp" />

      <html>
        <head>
          <title>Roles editen</title> <!-- DUTCH -->
        </head>
        <style type="text/css">
          <!-- horrible class names -->
          table.tightborder {
          border-color: #000000;
          border-style: solid;
          border-left-width: 1px;
          border-top-width: 1px;
          border-right-width: 0px;
          border-bottom-width: 0px;
          }
          table.tightborder td {
          border-color: #000000;
          border-style: solid;
          border-left-width: 0px;
          border-top-width: 0px;
          border-right-width: 1px;
          border-bottom-width: 1px;
          }
        </style>
        <body>
          <di:has editcontext="rollen">
            <mm:treefile id="url" page="/education/wizards/roles_cmd.jsp" objectlist="$includePath" referids="$referids" write="false" />
            <form name="roleform" action="${url}" method="post">
              <input type="hidden" name="command" value="-1" />
              <mm:import id="numofroles" jspvar="numOfRoles" vartype="Integer">0</mm:import>
              <table class="tightborder" border="1" cellpadding="0" cellspacing="0">
                <tr align="center">
                  <td>
                    &amp;nbsp;
                  </td>
                  <mm:listnodes type="roles" orderby="name">
                    <td>
                      <di:rotatedtext text="${_node.name}" />
                    </td>
                  </mm:listnodes>
                  <td width="30">&amp;nbsp;</td>
                </tr>
                <tr align="center" valign="middle" height="25">
                  <td>&amp;nbsp;</td>
                  <di:has editcontext="rollen" action="rwd">
                    <mm:listnodes type="roles" orderby="name">
                      <mm:link referid="url" referids="_node@rolenumber">
                        <mm:param name="command">deleterole</mm:param>
                        <td>
                          <a href="${_}"
                             onClick="return confirm('${di:translate('education.areyousuredelrole')}');"
                             target="text">
                            <img src="${mm:treelink('/education/wizards/gfx/minus.gif', includePath)}"
                                 border="0"
                                 title="${di:translate('education.deletethisrole')}"
                                 alt="${di:translate('education.deletethisrole')}"
                                 />
                          </a>
                        </td>
                      </mm:link>
                    </mm:listnodes>
                  </di:has>
                  <di:has editcontext="rollen" action="rwd" inverse="true">
                    <mm:listnodes type="roles" orderby="name">
                      <td>&amp;nbsp;</td>
                    </mm:listnodes>
                  </di:has>
                  <td>
                    <mm:link referid="wizardjsp">
                      <mm:param name="wizard">config/role/roles</mm:param>
                      <mm:param name="objectnumber">new</mm:param>
                      <a href="${_}" target="text">
                        <img src="${mm:treelink('/education/wizards/gfx/plus.gif', includePath)}"
                             border="0"
                             title="${di:translate('education.createnewrole')}"
                             alt="${di:translate('education.createnewrole')}"
                             />
                      </a>
                    </mm:link>
                  </td>
                </tr>
                <mm:listnodes type="editcontexts" orderby="number" id="this_editcontext">
                  <tr>
                    <td><mm:field name="name"/></td>
                    <mm:listnodes type="roles" orderby="name">
                      <mm:related path="posrel,editcontexts" constraints="editcontexts.number='$this_editcontext'">
                        <mm:field id="right" name="posrel.pos" write="false"/>
                      </mm:related>
                      <td>
                        <select name="select_${this_editcontext}_${_node}">
                          <mm:option value="0" compare="${right}" style="background-color:#FF3300">ro</mm:option>
                          <mm:option value="2" compare="${right}" style="background-color:#FFFF00">rw</mm:option>
                          <mm:option value="3" compare="${right}"  style="background-color:#33FF00">rwd</mm:option>
                        </select>
                      </td>
                    </mm:listnodes>
                    <td >&amp;nbsp;</td>
                  </tr>
                </mm:listnodes>
              </table>
            </form>
            <span style="background-color:#33FF00">rwd</span> = <di:translate key="education.abbreviationrwd" /><br/>
            <span style="background-color:#FFFF00">rw&amp;nbsp;</span> = <di:translate key="education.abbreviationrw" /><br/>
            <br/>
            <input type="button" class="formbutton" onClick="roleform.command.value='accept';roleform.submit()"
                   value="${di:translate('education.save')}" />
            <input type="button" class="formbutton" onClick="roleform.reset()"
                   value="${di:translate('education.reset')}" />
          </di:has>
        </body>
      </html>
    </mm:cloud>
  </mm:content>
</jsp:root>
