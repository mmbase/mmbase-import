<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud method="asis">
  <%@include file="parameters.jsp" %>
  <%@include file="login.jsp" %>
  <mm:import externid="bugreport" />
  <mm:import externid="commenttype" />

  <mm:node number="$bugreport">
    <mm:present referid="user">
      <table class="list">          
        <tr><th>Adding a user comment</th></tr>
        <tr>
          <td>
            Commenting on bugreport :
            <mm:field name="issue" escape="p" />
            Commenting type :
            <p><mm:write referid="commenttype" /></p>

          <mm:compare referid="commenttype" value="regular">

            <form action="<mm:url referids="parameters,$parameters,bugreport,user@newuser">
                            <mm:param name="btemplate" value="fullview.jsp" />
                            <mm:param name="flap"     value="overview" /></mm:url>" method="post">

	        Title<br /> <input name="newtitle" style="width: 100%" /><br />
                Text<br /> <textarea name="newtext" rows="25" style="width: 100%"></textarea>
               <input type="hidden" name="action" value="addcomment" />
               <input type="submit" value="enter comment" />
           </form>
        </mm:compare>
      </td>
    </tr>
    
  </table>
  </mm:present>
  <mm:notpresent referid="user">
    Adding comments to the bugtracker is only allowed if you are logged in
  </mm:notpresent>
</mm:node>
</mm:cloud>
