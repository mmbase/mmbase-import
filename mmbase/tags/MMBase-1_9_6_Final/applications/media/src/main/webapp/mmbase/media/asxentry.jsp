<mm:context>
<entry>
<Title><mm:field name="title" /> - <mm:field name="subtitle" /> </Title>
<Abstract><mm:field name="intro" /> -- <mm:field name="body" /></Abstract>
<Ref href = "<mm:field name="url(asf)" />" />
<mm:field name="start">
  <mm:compare value="" inverse="true">
    <STARTTIME VALUE="<mm:field name="gui(start)" />"/>
  </mm:compare>
</mm:field>
<mm:field name="stop">
  <mm:compare value="" inverse="true">
    <DURATION VALUE="<mm:field name="duration()" />"/>
  </mm:compare>
</mm:field>
</entry>
</mm:context>
