<div class="contentBody">
  <%@ include file="getmyfeedback.jsp" %>

  <form name="newdocform" action="<mm:treefile page="/pop/index.jsp" objectlist="$includePath" 
          referids="$referids,currentfolder,currentcomp">
        </mm:treefile>" method="post">
    <input type="hidden" name="command" value="savedoc">
    <input type="hidden" name="returnto" value="<mm:write referid="returnto"/>">
    <input type="hidden" name="myfeedback1" value="<mm:write referid="myfeedback1"/>">
    <input type="hidden" name="myfeedback2" value="<mm:write referid="myfeedback2"/>">
    <input type="hidden" name="addnode">
    <table width="80%" border="0" class="popGreyTableHeader">
      <tr>
        <td colspan="3">Competentie: <mm:node number="$currentcomp"><mm:field name="name"/></mm:node></td>
      </tr>
    </table>
    Voeg documenten uit je ontwikkelingsgericht portfolio toe<br/>
    <mm:list nodes="$user" path="people,portfolios,folders,attachments" constraints="portfolios.type='0'">
      <mm:first><select name="docnumber" class="popcompformselect"></mm:first>
        <option value="<mm:field name="attachments.number"/>"><mm:field name="folders.name"/> > <mm:field name="attachments.title"/></option>
      <mm:last></select></mm:last>
    </mm:list>
    <input type="submit" class="formbutton" value="Voeg toe" onClick="newdocform.addnode.value=newdocform.docnumber.value">
    <br/>
    <br/>
    Voeg mappen uit je ontwikkelingsgericht portfolio toe<br/>
    <mm:list nodes="$user" path="people,portfolios,folders" constraints="portfolios.type='0'">
      <mm:first><select name="foldernumber" class="popcompformselect"></mm:first>
        <option value="<mm:field name="folders.number"/>"><mm:field name="folders.name"/></option>
      <mm:last></select></mm:last>
    </mm:list>
    <input type="submit" class="formbutton" value="Voeg toe" onClick="newdocform.addnode.value=newdocform.foldernumber.value">
    <br/>
    <br/>
    <input type="submit" class="formbutton" value="terug" onClick="newdocform.command.value='continue'">
  </form>
</div>