<jsp:root version="2.0"
          xmlns:c="http://java.sun.com/jsp/jstl/core"
          xmlns:fn="http://java.sun.com/jsp/jstl/functions"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0"
          >
  <mm:content
      type="application/xml" postprocessor="none">
    <mm:cloud method="asis">
      <div
          class="content">
        <mm:import externid="object" required="true"/>
        <mm:node referid="object">
          <h1><mm:field name="title" /></h1>
          <mm:field name="abstract" />

          <!-- This stuff with 'impos' is straightforwardy horrible, and has no place here -->
          <mm:field name="impos">
            <mm:compare value="1">
              <mm:field name="body" />
              <table>
                <tr>
                  <td>
                    <mm:relatednodes type="images">
                      <h3><mm:field name="title" /></h3>
                      <mm:image mode="img" width="200" /><br />
                      <mm:field name="description" />
                    </mm:relatednodes>
                  </td>
                </tr>
              </table>
            </mm:compare>
          </mm:field>
          <mm:field name="impos">
            <mm:compare value="0">
              <table>
                <tr>
                  <mm:relatednodes type="images">
                    <h3><mm:field name="title" /></h3>
                    <mm:image mode="img" width="200" /><br />
                    <mm:field name="description" />
                  </mm:relatednodes>
                </tr>
                <tr>
                  <td>
                    <mm:field name="body" />
                  </td>
                </tr>
              </table>
            </mm:compare>
          </mm:field>
          <mm:field name="impos">
            <mm:compare value="2">
              <table>
                <tr>
                  <td>
                    <mm:relatednodes type="images">
                      <table>
                        <tr> <h3><mm:field name="title"/></h3></tr>
                        <tr align="right"> <mm:field name="description" />  </tr>
                        <tr> <td>
                          <mm:image mode="img" width="200" /><br />
                        </td></tr>
                      </table>
                    </mm:relatednodes>
                  </td>
                  <td>
                    <mm:field name="body" />
                  </td>
                </tr>
              </table>
            </mm:compare>
          </mm:field>
        </mm:node>
      </div>
    </mm:cloud>
  </mm:content>
</jsp:root>
