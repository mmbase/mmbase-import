<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
  <mm:cloud loginpage="/login.jsp" jspvar="cloud">
    <%@include file="/shared/setImports.jsp"%>

    <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
    <html>
      <head>
        <title>Roles editen</title>
      </head>
      <body>
        <mm:listnodes type="educations">
          <mm:field name="name"/><br />
          <mm:related path="rolerel,people" fields="people.firstname,people.lastname,people.username">
            - <mm:field name="people.firstname"/>
            - <mm:field name="people.lastname"/>
            - <mm:field name="people.username"/>
            <br />
            <mm:node element="rolerel">
              <mm:related path="related,roles" fields="roles.name">
              - <b><mm:field name="roles.name"/></b>
              </mm:related>
            </mm:node>
            <br/>
          </mm:related>
          
        </mm:listnodes>
      </body>
    </html>
  </mm:cloud>
</mm:content>