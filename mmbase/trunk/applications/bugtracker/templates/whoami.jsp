<table>
  <tr>    
    <mm:present referid="user" inverse="true" >
      <td>
        We have no idea who you are please login !
        <a href="<mm:url referids="parameters,$parameters"><mm:param name="template" value="changeUser.jsp" /></mm:url>">
           <img src="<mm:url page="images/arrow-right.png" />" border="0" />
         </a>
       </td>
     </mm:present>
     <mm:present referid="user">
       <td>
         <mm:node number="$user">
           <p>
             I am <mm:field name="firstname" /> <mm:field name="lastname" /> 
             ( its not me,
             <a href="<mm:url referids="parameters,$parameters"><mm:param name="template" value="changeUser.jsp" /></mm:url>">
             change name
             </a>)
           </p>
           <p>
             I have a new bug and want to report it
             <a href="<mm:url referids="parameters,$parameters,user"><mm:param name="template" value="newBug.jsp" /></mm:url>">
               <img src="<mm:url page="images/arrow-right.png" />" border="0" >
             </a>
           </p>
        </td>
      </mm:node>
    </mm:present>
  </tr>
</table>
