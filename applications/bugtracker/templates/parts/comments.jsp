<table class="list">
  <tr>
    <th>Statement from the maintainer</th>
    <td>
      <mm:field name="rationale">
        <mm:isnotempty><mm:write escape="p" /></mm:isnotempty>
        <mm:isempty>no maintainer statement yet</mm:isempty>
      </mm:field>
    </td>
  </tr>
  <tr>
    <th>User comments</th>
    <td>
      <table border="0">
        <mm:relatednodes role="rolerel" type="comments">
          <tr>
            <td valign="top" width="200">
              <mm:relatednodes type="users" >
                <mm:field name="firstname" /> <mm:field name="lastname" />
              </mm:relatednodes>
            </td>
            <th valign="top" width="430">
              <mm:first><mm:import id="commentsfound" /></mm:first>
              <mm:field name="title" />
            </th>
          </tr>
          <tr>
            <td>&nbsp;</td>
            <td><mm:field name="body" escape="p" /></td>
          </tr>        
        </mm:relatednodes>
      </table>
      <mm:present referid="commentsfound" inverse="true">
        No user comments yet
      </mm:present>
      Add comment 
      <a href="<mm:url referids="bugreport"><mm:param name="template" value="addComment.jsp" /><mm:param name="commenttype" value="regular" /></mm:url>">
        <img src="<mm:url page="images/arrow-right.gif" />" border="0" />
      </a>
    </td>
  </tr>
</table>
