<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud method="asis"> 
  <%@include file="parameters.jsp" %>
  <mm:notpresent referid="message">
    <mm:import externid="message"/>
  </mm:notpresent>
<mm:present referid="message">
<script>
function getObject(name){
  if (document.getElementById) {
    return document.getElementById(name).style;
  } else if (document.all) {
    return document.all[name].style;
  } else if (document.layers) {
    return document.layers[name];
  } else {
    return false;
  }
}
function hide(name){
  var o = getObject(name);
  if (o.style){
    o.style.visibility ='hidden';
  } else {
    o.visibility ='hidden';
  }
}
</script>
  
<%--
<div name="messagelayer" id="messagelayer" style="z-index: 100;position: absolute;border: 1px solid black; width: 300px;top 20px;left: 20px">
--%>
<div name="messagelayer" id="messagelayer" style="border: 1px solid black; width: 300px;top 20px;left: 20px">
<div style="border: 2px dotted red; background: yellow;font-weight: bold;color: red">
  <table  class="list">
    <tr><th>feedback message</th></tr>
    
    <tr>	
      <td>
        <p>
          <mm:compare referid="message" value="login">
            Login completed and browser linked (cookies) with this
            account, press ok to return to the bugtracker.
          </mm:compare>
          <mm:compare referid="message" value="failedlogin">
             ** login failed wrong account or password ?**
          </mm:compare>
          <mm:compare referid="message" value="email">
            A account was indeed found with that email address  
            mailed the account name and password to it.
          </mm:compare>
          <mm:compare referid="message" value="emailnotfound">
            No account found under that email address
            maybe it was a different one ? or you don't have
            a account yet ?
          </mm:compare>
          <mm:compare referid="message" value="newuser">
            A account was created and password was mailed
            Check  your mail and use the account info to login.
          </mm:compare>
          <mm:compare referid="message" value="reportdeleted">
            Bugreport was deleted from the database
          </mm:compare>
          <mm:compare referid="message" value="newbug">
            The bug was inserted into the bugtracker, you
            are its submitter meaning you can change/delete
            aspects of this report until its picked up by one
            of the maintainers.
            Thanks for reporting the bug we will report back to
            you using email when its status is changed
            
          </mm:compare>
          <mm:compare referid="message" value="maintaineradded">
              The maintainer was added to the bug report
          </mm:compare>
          <mm:compare referid="message" value="maintainerremoved">
              The maintainer was removed from the bug report
          </mm:compare>
          <mm:compare referid="message" value="myselfinterestedadded">
              You where added as interested to the bug report
          </mm:compare>
          <mm:compare referid="message" value="removedmyselfinterested">
              You where removed as interested to the bug report
          </mm:compare>
          <mm:compare referid="message" value="updatebug">
            The bug report was updated. Thanks for reporting the change we will report back to
            you using email when its status is changed
          </mm:compare>
        </p>
      </td>
    </tr>
    <tr>
      <td>
        <center>
        <a href="#" onClick="hide('messagelayer'); return true;">OK</a>
        </td>
      </tr>
    </table>
</div>
</div>
<script type="text/javascript">
 setTimeout('hide("messagelayer")',10000);
</script>
</mm:present>
</mm:cloud>
