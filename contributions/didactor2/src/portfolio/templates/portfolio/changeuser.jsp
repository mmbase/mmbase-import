<%--
  This template shows the personal portfolio edit personal data page.
--%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" 
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%><%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" 
%><mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<mm:import externid="change" />
<mm:present referid="change">
  <mm:node number="$user" id="my_user">
    <mm:fieldlist fields="initials,firstname,lastname,address,zipcode,city,telephone,description,remarks,email">
      <mm:fieldinfo type="useinput" />
    </mm:fieldlist>
    <mm:import externid="_handle" from="multipart" />
    <mm:relatednodes type="images">
      <mm:import id="image_present"/>
      <mm:compare referid="_handle" value="" inverse="true">
        <c:if test="${! empty _handle.name}">
          <mm:fieldlist fields="handle"><mm:fieldinfo type="useinput" /></mm:fieldlist>
        </c:if>
      </mm:compare>
    </mm:relatednodes>

    <mm:present referid="image_present" inverse="true">
      <mm:compare referid="_handle" value="" inverse="true">
        <c:if test="${! empty _handle.name}">
        <mm:createnode type="images" id="my_image">
          <mm:field name="title">Foto</mm:field>
        </mm:createnode>
        <mm:createrelation source="my_user" destination="my_image" role="related"/>
        <mm:node referid="my_image">
          <mm:fieldlist nodetype="images" fields="handle">
            <mm:fieldlist fields="handle">
              <mm:fieldinfo type="useinput" />
            </mm:fieldlist>
          </mm:fieldlist>
        </mm:node>
        </c:if>
      </mm:compare>
    </mm:present>
  </mm:node>
  <mm:treeinclude page="/admin/handle_settings.jsp" objectlist="$includePath" referids="$referids" />
</mm:present>

<mm:redirect page="/portfolio/index.jsp"/>
</mm:cloud>
</mm:content>
