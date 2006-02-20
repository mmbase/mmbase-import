<div class="contentBody">
  <%@ include file="getmyfeedback.jsp" %>

  <form name="newdocform" action="<mm:treefile page="/pop/index.jsp" objectlist="$includePath" 
          referids="$popreferids,currentfolder,currentcomp">
        </mm:treefile>" method="post">
    <input type="hidden" name="popcmd" value="savedoc">
    <input type="hidden" name="returnto" value="<mm:write referid="returnto"/>">
    <input type="hidden" name="myfeedback1" value="<mm:write referid="myfeedback1"/>">
    <input type="hidden" name="myfeedback2" value="<mm:write referid="myfeedback2"/>">
    <input type="hidden" name="addnode" value="-1">
    <table width="80%" border="0" class="popGreyTableHeader">
      <tr>
        <td colspan="3"><di:translate key="pop.competence" />: <mm:node number="$currentcomp"><mm:field name="name"/></mm:node></td>
      </tr>
    </table>

    <mm:remove referid="listnotempty"/>    
    <mm:list nodes="$student" path="people,portfolios,folders,attachments" constraints="portfolios.type='0'">
      <mm:first>
        <mm:import id="listnotempty">1</mm:import>
        <di:translate key="pop.portfolioadddoc" /><br/>
        <select name="docnumber" class="popcompformselect">
      </mm:first>
        <option value="<mm:field name="attachments.number"/>"><mm:field name="folders.name"/> > <mm:field name="attachments.title"/></option>
      <mm:last>
        </select>
        <input type="submit" class="formbutton" value="<di:translate key="pop.addbutton" />" onClick="newdocform.addnode.value=newdocform.docnumber.value">
      </mm:last>
    </mm:list>
    <mm:notpresent referid="listnotempty">
      <di:translate key="pop.portfolionotcontaindoc" /><br/>
    </mm:notpresent>
    <br/>
    <br/>
    <mm:remove referid="listnotempty"/>    
    <mm:list nodes="$student" path="people,portfolios,folders" constraints="portfolios.type='0'">
      <mm:first>
        <mm:import id="listnotempty">1</mm:import>
        <di:translate key="pop.portfolioaddfolder" /><br/>
        <select name="foldernumber" class="popcompformselect">
      </mm:first>
        <option value="<mm:field name="folders.number"/>"><mm:field name="folders.name"/></option>
      <mm:last>
        </select>
        <input type="submit" class="formbutton" value="<di:translate key="pop.addbutton" />" onClick="newdocform.addnode.value=newdocform.foldernumber.value">
      </mm:last>
    </mm:list>
    <mm:notpresent referid="listnotempty">
      <di:translate key="pop.portfolionotcontainfolder" /><br/>
    </mm:notpresent>
    <br/>
    <br/>
    <mm:remove referid="listnotempty"/>
    <mm:list nodes="$student" path="people,portfolios,folders,urls" constraints="portfolios.type='0'">
      <mm:first>
        <mm:import id="listnotempty">1</mm:import>
        <di:translate key="pop.portfolioaddurl" /><br/>
        <select name="urlnumber" class="popcompformselect">
      </mm:first>
        <option value="<mm:field name="urls.number"/>"><mm:field name="folders.name"/> > <mm:field name="urls.name"/></option>
      <mm:last>
        </select>
        <input type="submit" class="formbutton" value="<di:translate key="pop.addbutton" />" onClick="newdocform.addnode.value=newdocform.urlnumber.value">
      </mm:last>
    </mm:list>
    <mm:notpresent referid="listnotempty">
      <di:translate key="pop.portfolionotcontainurl" /><br/>
    </mm:notpresent>
    <br/>
    <br/>
    <mm:remove referid="listnotempty"/>
    <mm:list nodes="$student" path="people,portfolios,folders,pages" constraints="portfolios.type='0'">
      <mm:first>
        <mm:import id="listnotempty">1</mm:import>
        <di:translate key="pop.portfolioaddpage" /><br/>
        <select name="pagenumber" class="popcompformselect">
      </mm:first>
        <option value="<mm:field name="pages.number"/>"><mm:field name="folders.name"/> > <mm:field name="pages.name"/></option>
      <mm:last>
        </select>
        <input type="submit" class="formbutton" value="<di:translate key="pop.addbutton" />" onClick="newdocform.addnode.value=newdocform.pagenumber.value">
      </mm:last>
    </mm:list>
    <mm:notpresent referid="listnotempty">
      <di:translate key="pop.portfolionotcontainpage" /><br/>
    </mm:notpresent>
    <br/>
    <br/>
    <mm:remove referid="listnotempty"/>
    <mm:list nodes="$student" path="people,portfolios,folders,chatlogs" constraints="portfolios.type='0'">
      <mm:first>
        <mm:import id="listnotempty">1</mm:import>
        <di:translate key="pop.portfolioaddchatlog" /><br/>
        <select name="chatlognumber" class="popcompformselect">
      </mm:first>
        <option value="<mm:field name="chatlogs.number"/>"><mm:field name="folders.name"/> > <mm:field name="chatlogs.date"/></option>
      <mm:last>
        </select>
        <input type="submit" class="formbutton" value="<di:translate key="pop.addbutton" />" onClick="newdocform.addnode.value=newdocform.chatlognumber.value">
      </mm:last>
    </mm:list>
    <mm:notpresent referid="listnotempty">
      <di:translate key="pop.portfolionotcontainchatlog" /><br/>
    </mm:notpresent>
    <br/>
    <br/>
    <input type="submit" class="formbutton" value="<di:translate key="pop.backbuttonlc" />" onClick="newdocform.popcmd.value='continue'">
  </form>
</div>
