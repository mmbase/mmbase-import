<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:import externid="type" />
<mm:import externid="scope">none</mm:import>
<%-- forum is only valid in the 'education' scope --%>
<%-- TODO use translate tag for use of more languages --%>
<mm:compare referid="scope" value="education">
  <mm:cloud method="delegate" jspvar="cloud">
  <%@include file="/shared/setImports.jsp" %>
  <mm:compare referid="type" value="div">
    <div class="menuSeparator"> </div>
    <div class="menuItem" id="menuForum">
      <a href="<mm:treefile page="/forum/forum.jsp" objectlist="$includePath" referids="$referids" />" class="menubar">forum</a>
    </div>
  </mm:compare>
  
  <mm:compare referid="type" value="option">
    <option value="<mm:treefile page="/forum/forum.jsp" objectlist="$includePath" referids="$referids" />">
      forum
    </option>
  </mm:compare>
  </mm:cloud>
</mm:compare>
