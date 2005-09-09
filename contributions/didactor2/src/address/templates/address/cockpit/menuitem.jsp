<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:import externid="type" />
<mm:import externid="scope">none</mm:import>
<fmt:bundle basename="nl.didactor.component.address.AddressMessageBundle">
<%-- 'address' is only valid in the 'provider' scope --%>
<mm:compare referid="scope" value="provider">
  <mm:cloud loginpage="/login.jsp" jspvar="cloud">
  <%@include file="/shared/setImports.jsp" %>
  <mm:compare referid="type" value="div">
    <div class="menuSeperator"> </div>
    <div class="menuItem" id="menuAddressbook">
      <a href="<mm:treefile page="/address/index.jsp" objectlist="$includePath" referids="$referids">
      	<mm:param name="sf">lastname,firstname</mm:param>
	<mm:param name="so">up,up</mm:param>
	</mm:treefile>" class="menubar"><fmt:message key="MENUITEMADDRESSBOOK" /></a>
    </div>
  </mm:compare>
  
  <mm:compare referid="type" value="option">
    <option value="<mm:treefile page="/address/index.jsp" objectlist="$includePath" referids="$referids"/>">
      <fmt:message key="MENUITEMADDRESSBOOK" />
    </option>
  </mm:compare>
  </mm:cloud>
</mm:compare>
</fmt:bundle>
