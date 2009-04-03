<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:import externid="type" />
<mm:import externid="scope">none</mm:import>
<%-- 'address' is only valid in the 'provider' scope --%>
<mm:compare referid="scope" value="provider">
  <mm:cloud method="delegate" jspvar="cloud">
  <%@include file="/shared/setImports.jsp" %>
  <mm:compare referid="type" value="div">
    <div class="menuSeparator"> </div>
    <div class="menuItem" id="menuAddressbook">
      <a href="<mm:treefile page="/address/index.jsp" objectlist="$includePath" referids="$referids">
      	<mm:param name="sf">lastname,firstname</mm:param>
	<mm:param name="so">up,up</mm:param>
	</mm:treefile>" class="menubar"><di:translate key="address.menuitemaddressbook" /></a>
    </div>
  </mm:compare>
  
  <mm:compare referid="type" value="option">
    <option value="<mm:treefile page="/address/index.jsp" objectlist="$includePath" referids="$referids"/>">
      <di:translate key="address.menuitemaddressbook" />
    </option>
  </mm:compare>
  </mm:cloud>
</mm:compare>
