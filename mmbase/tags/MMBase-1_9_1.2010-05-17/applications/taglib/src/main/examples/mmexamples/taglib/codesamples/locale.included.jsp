<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" 
%><%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" 
%>
<mm:cloud>
  <p>
    <mm:fieldlist nodetype="object">
      <mm:fieldinfo type="guiname" />,
    </mm:fieldlist>
    <fmt:formatNumber value="1234.56" minFractionDigits="1" maxFractionDigits="1" />
  </p>
</mm:cloud>