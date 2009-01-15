<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"

          xmlns:di="http://www.didactor.nl/ditaglib_1.0"
          >

  <mm:import externid="mode">educations</mm:import>
  <mm:import externid="e">${education}</mm:import>

  <di:html
      type="application/xhtml+xml"
      styleClass="editwizards ${mode}"
      component="education.wizards"
      title_key="education.editwizards"
      css="/education/wizards/style.css"
      rank="editor" expires="0">

    <mm:link page="/core/js/jquery-treeview/lib/jquery.cookie.js">
      <script src="${_}" type="text/javascript"><jsp:text> </jsp:text></script>
    </mm:link>

    <mm:link page="/core/js/jquery-treeview/jquery.treeview.pack.js">
      <script type="text/javascript" src="${_}"><jsp:text> </jsp:text></script>
    </mm:link>


    <mm:treefile
        write="false"
        objectlist="${includePath}"
        page="/education/wizards/wizard.js">
      <script type="text/javascript" src="${_}"><jsp:text> </jsp:text></script>
    </mm:treefile>

    <div class="rows" id="rows">

      <mm:treefile
          write="false"
          objectlist="${includePath}"
          page="/education/wizards/modes/${mode}.jsp"
          referids="education_topmenu_course?">
        <a href="${_}" id="mode_url" style="display: none;">Mode url</a>
      </mm:treefile>

      <di:include debug="html" page="/education/wizards/navigation.jspx" />

      <div class="mainContent">
        <table class="layout">
          <tr>
            <td id="left_menu">
              <di:include  page="/education/wizards/code.jsp"  />
            </td>
            <td class="content">
              <mm:treefile id="ok" page="/education/wizards/ok.jsp"
                           objectlist="$includePath" referids="$referids" write="false">
                <iframe id="text" name="text" width="99%" height="100%" marginwidth="0" marginheight="0" border="1" src="${ok}"><jsp:text> </jsp:text></iframe>
              </mm:treefile>
            </td>
          </tr>
        </table>
      </div>
    </div>
  </di:html>
</jsp:root>
