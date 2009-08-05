<table  class="list">
  <tr><th width="50">Bug #</th><th>state</th><th>time</th><th>issue</th><th>&nbsp;</th></tr>
  <mm:listnodes id="bugreport" type="bugreports" orderby="time" directions="down" max="15" offset="$noffset">
    <tr <mm:even>class="even"</mm:even>>
      <td>#<mm:field name="bugid" /></td>
      <td>
        <mm:field name="bstatus">
          <mm:compare value="1">Open</mm:compare>
          <mm:compare value="2">Accepted</mm:compare>
          <mm:compare value="3">Rejected</mm:compare>
          <mm:compare value="4">Pending</mm:compare>
          <mm:compare value="5">Integrated</mm:compare>
          <mm:compare value="6">Closed</mm:compare>
        </mm:field>
      </td>
      <td style="font-family: monospace">
        <mm:field name="time">
          <mm:time format=":LONG.LONG" />
        </mm:field>
      </td>
      <td>
        <mm:field name="issue" />
      </td>
      <td>
        <a href="<mm:url referids="parameters,$parameters,bugreport"><mm:param name="btemplate" value="fullview.jsp" /></mm:url>"><img src="<mm:url page="images/arrow-right.png" />" border="0" align="middle"></a>
      </td>
    </tr>
  </mm:listnodes>
</tr>
</table>
</form>

