<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>

<mm:cloud method="asis">
<mm:notpresent referid="">
  <mm:import externid="showuser" />
</mm:notpresent>

  <mm:node number="$showuser">

    <table>


      <tr><th>User</th></tr>
      <tr>
        <td />
        <td>
          <p>
            Name: <mm:field name="firstname" /> <mm:field name="lastname" />
          </p>
          <p>
            Active user : 
            <mm:relatednodes type="groups" constraints="name='BugTrackerInterested'">
              Yes
            </mm:relatednodes>
          </p>
          <p>
            Commitor : 
            <mm:relatednodes type="groups" constraints="name='BugTrackerCommitors'">
              Yes
              <mm:import id="usercommitor">yes</mm:import>
            </mm:relatednodes>
            <mm:present referid="usercommitor" inverse="true">
              No
            </mm:present>
          </p>
            
        </td>
        <td>
          <mm:relatednodes type="images">
            Photo: <img src="<mm:image template="s(90)" />" />
          </mm:relatednodes>
          Email : <a href="mailto:<mm:field name="email" />"><mm:field name="email" /></a>
        </td>
      </tr>
    </table>
  </mm:node>
</mm:cloud>
