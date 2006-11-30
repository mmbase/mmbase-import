<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%>
<mm:import externid="includePath" required="true" />
<mm:cloud method="delegate">
  <p>
    <a href="javascript:parent.previousContent();"><img src="<mm:treefile write="true" page="/gfx/icon_arrow_last.gif" objectlist="$includePath" />" width="14" height="14" border="0" title="<di:translate key="education.previous" />" alt="<di:translate key="education.previous" />" /></a>
    <a href="javascript:parent.previousContent();" class="path"><di:translate key="education.previous" /></a>
    <img src="gfx/spacer.gif" width="15" height="1" title="" alt="" /><a href="javascript:parent.nextContent();" class="path"><di:translate key="education.next" /></a>
    <a href="javascript:parent.nextContent();"><img src="<mm:treefile write="true" page="/gfx/icon_arrow_next.gif" objectlist="$includePath" />" width="14" height="14" border="0" title="<di:translate key="education.next" />" alt="<di:translate key="education.next" />" /></a>
  </p>
</mm:cloud>
