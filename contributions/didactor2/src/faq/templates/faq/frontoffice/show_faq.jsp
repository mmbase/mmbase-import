<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <jsp:directive.page session="false" />
  <mm:import externid="node" required="true"/>
  <mm:import externid="node2" />
  <mm:node referid="node">
    <mm:url id="faqLink" referids="node" write="false" />

    <h1><mm:field name="name" write="true"/></h1>
    <br/>

    <mm:node number="$node" notfound="skipbody">
      <mm:treeinclude page="/education/paragraph/paragraph.jsp" objectlist="$includePath" referids="$referids">
        <mm:param name="node_id"><mm:write referid="node"/></mm:param>
        <mm:param name="path_segment">../</mm:param>
      </mm:treeinclude>
    </mm:node>
    <table width="100%">
      <mm:relatednodes type="faqitems">
        <tr>
          <td bgcolor="#f8e0c5" style="padding: 1px; padding-left: 5px; cursor: pointer; cursor: hand;"
              onclick="document.location.href='${faqLink}#q${_node}'">
            <table cellspacing="0">
              <tr>
                <td valign="center">
                  <img src="${mm:treelink('/gfx/icon_arrow_tab_closed.gif', includePath)}" />
                </td>
                <td style="padding-left: 7px;" class="plaintext">
                  <mm:field name="question"/>
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </mm:relatednodes>
    </table>
    <mm:relatednodes type="faqitems">
      <mm:import jspvar="itemNumber"><mm:field name="number"/></mm:import>
      <p>
        <a name="q${_node}"></a>
        <table width="100%">
          <tr>
            <td bgcolor="#f8e0c5" style="padding: 1px; padding-left: 5px;">
              <table cellspacing="0">
                <tr>
                  <td>
                    <img src="${mm:treelink('/gfx/icon_arrow_tab_closed.gif', includePath)}" />
                  </td>
                  <td style="padding-left: 7px;"  class="plaintext">
                    <b><mm:field name="question"/></b>
                  </td>
                </tr>
              </table>
            </td>
          </tr>
          <tr>
            <td style="padding: 10px" bgcolor="#f8eee3"  class="plaintext">
              <mm:field name="answer" escape="none"/>
            </td>
          </tr>
        </table>
      </p>
    </mm:relatednodes>
  </mm:node>

  <mm:node number="$node2" notfound="skipbody">

      <p>
        <a name="q${_node}"></a>
        <table width="100%">
          <tr>
            <td bgcolor="#f8e0c5" style="padding: 1px; padding-left: 5px;">
              <table cellspacing="0">
                <tr>
                  <td>
                    <img src="${mm:treelink('/gfx/icon_arrow_tab_closed.gif', includePath)}" />
                  </td>
                  <td style="padding-left: 7px;"  class="plaintext">
                    <b><mm:field name="question"/></b>
                  </td>
                </tr>
              </table>
            </td>
          </tr>
          <tr>
            <td style="padding: 10px" bgcolor="#f8eee3"  class="plaintext">
              <mm:field name="answer" escape="none"/>
            </td>
          </tr>
        </table>
      </p>
  </mm:node>
</jsp:root>
