<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <mm:cloud method="asis" >
    <di:has  action="rw" editcontext="cursuseditor">
      <div class="coursemanagement">
        <mm:import externid="scope"/>
        <mm:compare referid="scope" value="provider">
          <div class="menuSeparator"><jsp:text> </jsp:text></div>
          <div class="menuItem" id="coursemanagement">
            <mm:import from="request" externid="component" />
            <mm:treefile page="/education/wizards/index.jsp"
                         objectlist="$includePath"  write="false" >
              <a href="${_}" class="menubar ${component eq 'education.wizards' ? 'active' : ''}" >
                <di:translate key="education.coursemanagement" />
              </a>
            </mm:treefile>
          </div>
        </mm:compare>
      </div>
    </di:has>
  </mm:cloud>
</jsp:root>
