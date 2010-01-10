<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><mm:log>aaa</mm:log><mm:cloud method="delegate" authenticate="class">
 <mm:log>bb</mm:log>
  <mm:import externid="email" />
  <!--  create the email node -->
  <mm:createnode id="mail1" type="email">
    <mm:setfield name="from">${email}</mm:setfield>
    <mm:setfield name="to">${email}</mm:setfield>
    <mm:setfield name="subject">my first multipart mmbase mail !!</mm:setfield>

    <!-- must introduce 'mimetype' field instead. See Didactor. -->
    <mm:setfield name="body">
      <multipart id="plaintext" type="text/plain" encoding="UTF-8">
        This is plain text !
      </multipart>
      <multipart alt="plaintext" type="text/html" encoding="UTF-8">
        <p>This is html</p>
      </multipart>
      <mm:listnodes type="attachments" max="1">
        <multipart id="${_node}" number="${_node}" ><mm:nodeinfo type="gui" /></multipart>
      </mm:listnodes>
    </mm:setfield>
  </mm:createnode>


  <!-- send the email node -->
  <mm:node referid="mail1">
    <mm:function name="mail" />
  </mm:node>

  <p>Mail ${mail1} was sent</p>

</mm:cloud>

