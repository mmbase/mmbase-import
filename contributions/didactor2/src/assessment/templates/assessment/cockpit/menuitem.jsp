<jsp:root
    xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
    xmlns:di="http://www.didactor.nl/ditaglib_1.0"
    version="2.0">

  <div class="menuSeparator assessment"><jsp:text> </jsp:text></div>
  <div class="menuItem assessment" id="menu_assessment">
    <mm:treefile page="/assessment/index.jsp" objectlist="$includePath" referids="${referids}"
                 write="false"
                 >

      <a href="${_}" class="menubar"><di:translate key="assessment.education_menu_item_assessment" /></a>
    </mm:treefile>
  </div>
</jsp:root>

