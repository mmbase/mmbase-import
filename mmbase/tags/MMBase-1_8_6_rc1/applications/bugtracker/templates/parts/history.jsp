  <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="98%">    
    <tr>
      <th>Date</th><th>Status</th><th>Type</th><th>Commitor</th>
    </tr>
    <!-- the rest of the reports -->
    <mm:relatednodes type="bugreportupdates" orderby="time" directions="down">
      <tr>
        <td colspan="1">
          <a href="<mm:url referids="parameters,$parameters"><mm:param name="btemplate" value="showUpdate.jsp" /><mm:param name="updatereport"><mm:field name="number" /></mm:param></mm:url>" target="update">
              <mm:field name="time" >
                <mm:time format="HH:mm:ss, EE d MM yyyy" />
              </mm:field>
          </a>
        </td>
        <td colspan="1">
          <mm:field name="bstatus"><%-- sigh...--%>
            <mm:compare value="1">Open</mm:compare>
            <mm:compare value="2">Accepted</mm:compare>
            <mm:compare value="3">Rejected</mm:compare>
            <mm:compare value="4">Pending</mm:compare>
            <mm:compare value="5">Integrated</mm:compare>
            <mm:compare value="6">Closed</mm:compare>
          </mm:field>
        </td>
        <mm:related path="rolerel,users" fields="rolerel.role,users.firstname,users.lastname" max="1">
          <td colspan="1">
            <mm:field name="rolerel.role">
              <mm:compare value="submitter">Submitted</mm:compare>
              <mm:compare value="updater">Update</mm:compare>
            </mm:field>
          </td>
          <td colspan="1">
            <a href="<mm:url referids="parameters,$parameters"><mm:param name="btemplate" value="showUser.jsp" /><mm:param name="showuser"><mm:field name="users.number" /></mm:param></mm:url>">
                <mm:field name="users.firstname" />
                <mm:field name="users.lastname" />
            </a>
          </td>
        </mm:related>
      </tr>
    </mm:relatednodes>
    <!-- end of the reports -->
</table>
