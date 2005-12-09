<%--
  This template shows the personal portfolio edit personal data page.
--%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<mm:import externid="change" />
<mm:present referid="change">
  <mm:node number="$user" id="my_user">
    <mm:fieldlist fields="initials,firstname,lastname,address,zipcode,city,telephone,description,remarks,email">
      <mm:fieldinfo type="useinput" />
    </mm:fieldlist>
    <mm:import externid="_handle_size">0</mm:import>
    <mm:relatednodes type="images">
      <mm:import id="image_present"/>
      <mm:isgreaterthan  referid="_handle_size" value="0">
        <mm:fieldlist fields="handle"><mm:fieldinfo type="useinput" /></mm:fieldlist>
      </mm:isgreaterthan>
    </mm:relatednodes>

    <mm:present referid="image_present" inverse="true">
      <mm:isgreaterthan  referid="_handle_size" value="0">
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
      </mm:isgreaterthan>
    </mm:present>
  </mm:node>
  <mm:treeinclude page="/admin/handle_settings.jsp" objectlist="$includePath" referids="$referids" />
</mm:present>

<mm:redirect page="/portfolio/index.jsp"/>
</mm:cloud>
</mm:content>
