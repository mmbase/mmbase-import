<mm:functioncontainer nodemanager="news" name="showparameter">
  <mm:param name="numberparam"  value="10" />
  <mm:param name="integerparam" value="10" />
  <mm:param name="collectionparam" value="a,b,c" />
  <mm:param name="mapparam">
    <mm:param name="a" value="B" />
    <mm:param name="c" value="C" />
    <mm:param name="d">
      <mm:param name="a" value="B" />
      <mm:param name="c" value="C" />
    </mm:param>
  </mm:param>
  <mm:listfunction>
    <p><mm:write /></p>
  </mm:listfunction>
</mm:functioncontainer>
<hr />
<p>
  Accidentely. Not only functions accepts parameters. Also, for example 'escapers' can.
</p>
<mm:escaper id="vervang">
  <mm:escaper type="regexps">
    <mm:param name="patterns">
      <mm:param name="aaa" value="bbb" />
      <mm:param name="ccc" value="ddd" />
    </mm:param>
  </mm:escaper>
  <mm:escaper referid="uppercase" />
  <mm:escaper type="substring">
    <mm:param name="from" value="2" />
  </mm:escaper>
</mm:escaper>
<p>
  <mm:write value="aba aaa b ccc " escape="vervang" />
</p>