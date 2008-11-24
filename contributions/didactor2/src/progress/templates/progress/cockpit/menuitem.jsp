<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di"
%><mm:import externid="type" />
<mm:import externid="scope">none</mm:import>
<%-- 'progress' is only valid in the 'education' scope --%>
<mm:compare referid="scope" value="education">
  <mm:cloud rank="didactor user">

  <mm:compare referid="type" value="div">
    <div class="menuSeparator"> </div>
    <div class="menuItem" id="menuProgress">
      <mm:treefile page="/progress/index.jsp" objectlist="$includePath" referids="$referids" write="false">
        <a href="${_}" class="menubar"><di:translate key="progress.progress" /></a>
      </mm:treefile>
    </div>
  </mm:compare>

  <mm:compare referid="type" value="option">
    <mm:treefile page="/progress/index.jsp" objectlist="$includePath" referids="$referids" write="false">
      <option value="${_}" class="menubar">
        <di:translate key="progress.progress" />
      </option>
    </mm:treefile>
  </mm:compare>
  </mm:cloud>
</mm:compare>
