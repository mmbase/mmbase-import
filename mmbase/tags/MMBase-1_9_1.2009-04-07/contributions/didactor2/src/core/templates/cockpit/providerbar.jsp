<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <jsp:directive.page session="false" />
  <!--
      Show all components that are related to the current provider.
      There are several 'default' components that will be shown in the
      standard layout: as hyperlinks from left to right. All other
      components that are directly related to the provider object will
      be placed in a dropdown box.

  -->
  <mm:cloud method="asis">

    <div class="providerMenubar">
      <mm:hasrank minvalue="didactor user">
        <mm:node referid="provider">
          <mm:functioncontainer>
            <mm:param name="bar">provider</mm:param>
            <mm:listfunction name="components">

            </mm:listfunction>
          </mm:functioncontainer>
        </mm:node>
      </mm:hasrank>

    </div>
  </mm:cloud>
</jsp:root>
