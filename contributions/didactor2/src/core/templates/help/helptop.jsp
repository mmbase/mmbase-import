<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
<body>
  <di:hasrole role="contenteditor">
    <a href="<mm:url page="/help/opleiding_editor.htm"/>" target="helpcontent">Didactor cursusontwikkelhelp</a> 
  </di:hasrole>
  <di:hasrole role="contenteditor" inverse="true">
    <di:hasrole role="courseeditor">
      <a href="<mm:url page="/help/opleiding_editor.htm"/>" target="helpcontent">Didactor cursusontwikkelhelp</a> 
    </di:hasrole>
  </di:hasrole>
  <di:hasrole role="teacher">
    <a href="<mm:url page="/help/frame_docent.html"/>" target="helpcontent">Didactor docenten help</a>
  </di:hasrole>
  <di:hasrole role="student">
    <a href="<mm:url page="/help/frame_student.html"/>" target="helpcontent">Didactor studenten help</a>
  </di:hasrole>
  <a href="<mm:url page="/help/didactor_help.htm"/>" target="helpcontent">Algemeen QRC</a>
</body>
</mm:cloud>
</mm:content>
