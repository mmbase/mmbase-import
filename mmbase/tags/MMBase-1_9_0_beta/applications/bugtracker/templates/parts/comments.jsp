<table class="list">
  <tr>
    <th>Statement from the maintainer</th>
    <td>
      <mm:field name="rationale">
        <mm:isnotempty><mm:write escape="p,links" /></mm:isnotempty>
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
              <mm:field name="title" escape="inline,links" />
            </th>
          </tr>
          <tr>
            <td>&nbsp;</td>
            <td><mm:field name="body" escape="p,links" /></td>
          </tr>        
        </mm:relatednodes>
      </table>
      <mm:present referid="commentsfound" inverse="true">
        No user comments yet
      </mm:present>
      <mm:present referid="user">
      Add comment 
      <a href="<mm:url referids="parameters,$parameters,bugreport"><mm:param name="btemplate" value="addComment.jsp" /><mm:param name="commenttype" value="regular" /></mm:url>">
        <img src="<mm:url page="images/arrow-right.png" />" border="0" />
      </a>
      </mm:present>
      <mm:present referid="user" inverse="true">
        To add comments please login
      </mm:present>
    </td>
  </tr>
</table>
