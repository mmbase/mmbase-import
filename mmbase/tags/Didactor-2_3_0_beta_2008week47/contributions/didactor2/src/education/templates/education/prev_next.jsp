<jsp:root version="2.0"
          xmlns:c="http://java.sun.com/jsp/jstl/core"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <mm:cloud method="asis">
    <div class="prev_next">
      <a href="javascript:parent.previousContent();">
        <img src="${mm:treelink('/gfx/icon_arrow_last.gif', includePath)}" width="14" height="14" border="0"
             title="${di:translate('education.previous')}"
             alt="${di:translate('education.previous')}" />
      </a>
      <a href="javascript:parent.previousContent();" class="path"><di:translate key="education.previous" /></a>
      <jsp:text> </jsp:text>
      <mm:link page="/education/show.jspx">
        <a class="popup"
           href="#"
           onmouseover="this.href='${_}?block=' + document.href_frame;"
           onclick="var el = document.getElementById('contentBodywit');
                    var w = window.open(this.href, '_blank', 'menubar=no,toolbar=no,scrollbars=yes,height=' + el.clientHeight + ',width=' + el.clientWidth);
                    return false;
                    ">
          <di:translate key="education.pop" />
        </a>
      </mm:link>
      <jsp:text> </jsp:text>
      <a href="javascript:parent.nextContent();" class="path"><di:translate key="education.next" /></a>
      <a href="javascript:parent.nextContent();">
        <img src="${mm:treelink('/gfx/icon_arrow_next.gif', includePath)}" width="14" height="14" border="0"
             title="${di:translate('education.next')}"
             alt="${di:translate('education.next')}"  />
      </a>
    </div>
  </mm:cloud>
</jsp:root>
