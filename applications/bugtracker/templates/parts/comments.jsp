<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="98%">
  <tr>
    <th>Statement from the maintainer</th>
    <td>
      <mm:field name="rationale">
        <mm:isnotempty>
          <mm:write escape="p" />
        </mm:isnotempty>
        <mm:isempty>
          no maintainer statement yet
        </mm:isempty>
      </mm:field>
    </td>
  </tr>
  <tr>
    <th>User comments</th>
    <td>
      <table border="0">
        <mm:related path="rolerel,comments">
          <tr>
            <td valign="top" width="200">
              <mm:node element="comments">
                <mm:relatednodes type="users" >
                  <mm:field name="firstname" /> <mm:field name="lastname" />
                </mm:relatednodes>
              </mm:node>
            </td>
            <th valign="top" width="430">
              <mm:first><mm:import id="commentsfound" /></mm:first>
              <mm:field name="comments.title" />
            </th>
            </tr><tr><td>&nbsp;</td><td>
            <mm:field name="comments.body" escape="p" />            
          </mm:related>
        </td>
      </tr>
    </table>
    <mm:present referid="commentsfound" inverse="true">
      No user comments yet
    </mm:present>
    Add comment <a href="<mm:url referids="portal?,page?,bugreport,base" page="$base/addComment.jsp?commenttype=regular" />"><img src="<mm:url page="$base/images/arrow-right.gif" />"></a>
  </td>
</tr>
</table>
