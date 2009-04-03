<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<mm:cloud>
  <html>
    <head>
      <link href="../mmbase/edit/wizard/style/color/wizard.css" type="text/css" rel="stylesheet"/>
      <link href="../mmbase/edit/wizards/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
    </head>
    <body style="overflow:auto;">
      <b>Overzicht werkgroepen</b><!-- DUTCH -->
      <br/>
      <mm:listnodes type="workgroups" orderby="name">
        <li><mm:field name="name" /><br/>
      </mm:listnodes>
    </body>
  </html>
</mm:cloud>

