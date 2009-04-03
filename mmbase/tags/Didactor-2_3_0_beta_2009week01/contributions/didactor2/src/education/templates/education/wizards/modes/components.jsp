<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <mm:cloud rank="basic user">
    <di:has action="rw" editcontext="componenten" > <!-- dutch -->
      <mm:listnodes type="components" orderby="name">
        <mm:treefile id="file" page="/components/edit.jsp" objectlist="$includePath"
                     referids="_node@component" write="false" />
        <a target="text" href="${file}">
          <mm:field name="name" />
        </a>
        <br />
      </mm:listnodes>
    </di:has>
  </mm:cloud>
</jsp:root>
