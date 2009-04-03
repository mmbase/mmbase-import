<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:import externid="type" />
<mm:import externid="scope">none</mm:import>
<%-- agenda is only valid in the 'provider' scope --%>
<%-- TODO use translate tag for use of more languages --%>
<mm:compare referid="scope" value="provider">
  <mm:cloud method="asis">
    <mm:compare referid="type" value="div">
      <div class="menuSeparator"> </div>
      <div class="menuItem" id="menuAgenda">
        <a href="<mm:treefile page="/agenda/index.jsp" objectlist="$includePath" referids="$referids" />" class="menubar">agenda</a>
      </div>
    </mm:compare>
  
  <mm:compare referid="type" value="option">
    <option value="<mm:treefile page="/agenda/index.jsp" objectlist="$includePath" referids="$referids"/>">
      agenda
    </option>
  </mm:compare>
  </mm:cloud>
</mm:compare>
