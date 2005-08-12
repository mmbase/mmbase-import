<div class="contentBody">
  <%@ include file="getmyfeedback.jsp" %>

  <form name="newdocform" action="<mm:treefile page="/pop/index.jsp" objectlist="$includePath" 
          referids="$popreferids,currentfolder,currentcomp">
        </mm:treefile>" method="post">
    <input type="hidden" name="command" value="savedoc">
    <input type="hidden" name="returnto" value="<mm:write referid="returnto"/>">
    <input type="hidden" name="myfeedback1" value="<mm:write referid="myfeedback1"/>">
    <input type="hidden" name="myfeedback2" value="<mm:write referid="myfeedback2"/>">
    <input type="hidden" name="addnode" value="-1">
    <table width="80%" border="0" class="popGreyTableHeader">
      <tr>
        <td colspan="3"><fmt:message key="Competence"/>: <mm:node number="$currentcomp"><mm:field name="name"/></mm:node></td>
      </tr>
    </table>

    <mm:remove referid="listnotempty"/>    
    <mm:list nodes="$student" path="people,portfolios,folders,attachments" constraints="portfolios.type='0'">
      <mm:first>
        <mm:import id="listnotempty">1</mm:import>
        <fmt:message key="PortfolioAddDoc"/><br/>
        <select name="docnumber" class="popcompformselect">
      </mm:first>
        <option value="<mm:field name="attachments.number"/>"><mm:field name="folders.name"/> > <mm:field name="attachments.title"/></option>
      <mm:last>
        </select>
        <input type="submit" class="formbutton" value="<fmt:message key="AddButton"/>" onClick="newdocform.addnode.value=newdocform.docnumber.value">
      </mm:last>
    </mm:list>
    <mm:notpresent referid="listnotempty">
      <fmt:message key="PortfolioNotContainDoc"/><br/>
    </mm:notpresent>
    <br/>
    <br/>
    <mm:remove referid="listnotempty"/>    
    <mm:list nodes="$student" path="people,portfolios,folders" constraints="portfolios.type='0'">
      <mm:first>
        <mm:import id="listnotempty">1</mm:import>
        <fmt:message key="PortfolioAddFolder"/><br/>
        <select name="foldernumber" class="popcompformselect">
      </mm:first>
        <option value="<mm:field name="folders.number"/>"><mm:field name="folders.name"/></option>
      <mm:last>
        </select>
        <input type="submit" class="formbutton" value="<fmt:message key="AddButton"/>" onClick="newdocform.addnode.value=newdocform.foldernumber.value">
      </mm:last>
    </mm:list>
    <mm:notpresent referid="listnotempty">
      <fmt:message key="PortfolioNotContainFolder"/><br/>
    </mm:notpresent>
    <br/>
    <br/>
    <mm:remove referid="listnotempty"/>
    <mm:list nodes="$student" path="people,portfolios,folders,urls" constraints="portfolios.type='0'">
      <mm:first>
        <mm:import id="listnotempty">1</mm:import>
        <fmt:message key="PortfolioAddUrl"/><br/>
        <select name="urlnumber" class="popcompformselect">
      </mm:first>
        <option value="<mm:field name="urls.number"/>"><mm:field name="folders.name"/> > <mm:field name="urls.name"/></option>
      <mm:last>
        </select>
        <input type="submit" class="formbutton" value="<fmt:message key="AddButton"/>" onClick="newdocform.addnode.value=newdocform.urlnumber.value">
      </mm:last>
    </mm:list>
    <mm:notpresent referid="listnotempty">
      <fmt:message key="PortfolioNotContainUrl"/><br/>
    </mm:notpresent>
    <br/>
    <br/>
    <mm:remove referid="listnotempty"/>
    <mm:list nodes="$student" path="people,portfolios,folders,pages" constraints="portfolios.type='0'">
      <mm:first>
        <mm:import id="listnotempty">1</mm:import>
        <fmt:message key="PortfolioAddPage"/><br/>
        <select name="pagenumber" class="popcompformselect">
      </mm:first>
        <option value="<mm:field name="pages.number"/>"><mm:field name="folders.name"/> > <mm:field name="pages.name"/></option>
      <mm:last>
        </select>
        <input type="submit" class="formbutton" value="<fmt:message key="AddButton"/>" onClick="newdocform.addnode.value=newdocform.pagenumber.value">
      </mm:last>
    </mm:list>
    <mm:notpresent referid="listnotempty">
      <fmt:message key="PortfolioNotContainPage"/><br/>
    </mm:notpresent>
    <br/>
    <br/>
    <mm:remove referid="listnotempty"/>
    <mm:list nodes="$student" path="people,portfolios,folders,chatlogs" constraints="portfolios.type='0'">
      <mm:first>
        <mm:import id="listnotempty">1</mm:import>
        <fmt:message key="PortfolioAddChatlog"/><br/>
        <select name="chatlognumber" class="popcompformselect">
      </mm:first>
        <option value="<mm:field name="chatlogs.number"/>"><mm:field name="folders.name"/> > <mm:field name="chatlogs.date"/></option>
      <mm:last>
        </select>
        <input type="submit" class="formbutton" value="<fmt:message key="AddButton"/>" onClick="newdocform.addnode.value=newdocform.chatlognumber.value">
      </mm:last>
    </mm:list>
    <mm:notpresent referid="listnotempty">
      <fmt:message key="PortfolioNotContainChatlog"/><br/>
    </mm:notpresent>
    <br/>
    <br/>
    <input type="submit" class="formbutton" value="<fmt:message key="BackButtonLC"/>" onClick="newdocform.command.value='continue'">
  </form>
</div>