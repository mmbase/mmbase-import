<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di"
%><mm:cloud method="asis">
<jsp:directive.include file="/shared/setImports.jsp" />
<div class="applicationMenubarCockpit" style="white-space: nowrap">
  <mm:node number="$user" notfound="skipbody">
    <div class="menuItemApplicationMenubar">
      <a title="<di:translate key="core.logout" />" href="<mm:treefile page="/logout.jsp" objectlist="$includePath" referids="$referids"/>" class="menubar"><di:translate key="core.logout" /> <mm:field name="firstname"/> <mm:field name="suffix"/> <mm:field name="lastname"/></a>
    </div>

    <div class="menuSeparatorApplicationMenubar"></div>

    <div class="menuItemApplicationMenubar">
      <a title="<di:translate key="core.configuration" />" href="<mm:treefile page="/admin/index.jsp" objectlist="$includePath" referids="$referids" />" class="menubar"><di:translate key="core.configuration" /></a>
    </div>

    <div class="menuSeparatorApplicationMenubar"></div>
    <div class="menuItemApplicationMenubar">
      <a title="<di:translate key="core.print" />" href="javascript:printThis();"  class="menubar"><di:translate key="core.print" /></a>
    </div>


    <!-- region cms help and faq -->
    <mm:node number="$provider" notfound="skipbody">
      <mm:relatednodescontainer path="settingrel,components">
        <mm:constraint field="components.name" value="cmshelp"/>
        <mm:relatednodes>
          <mm:import id="showcmshelp" />
        </mm:relatednodes>
      </mm:relatednodescontainer>
      <mm:relatednodescontainer path="settingrel,components">
        <mm:constraint field="components.name" value="faq"/>
        <mm:relatednodes>
          <mm:import id="showfaq" />
        </mm:relatednodes>
      </mm:relatednodescontainer>
    </mm:node>

    <mm:cloudinfo type="user" />
    <!-- WTF -->
    <mm:present referid="education">
      <mm:notpresent referid="showcmshelp">
        <mm:node number="$education" notfound="skip">
          <mm:relatednodescontainer path="settingrel,components">
            <mm:constraint field="components.name" value="cmshelp"/>
            <mm:relatednodes>
              <mm:import id="showcmshelp" />
            </mm:relatednodes>
          </mm:relatednodescontainer>
        </mm:node>
      </mm:notpresent>
      <mm:notpresent referid="showfaq">
        <mm:node number="$education" notfound="skip">
          <mm:relatednodescontainer path="settingrel,components">
            <mm:constraint field="components.name" value="faq"/>
            <mm:relatednodes>
              <mm:import id="showfaq" />
            </mm:relatednodes>
          </mm:relatednodescontainer>
        </mm:node>
      </mm:notpresent>
    </mm:present>

    <mm:present referid="showcmshelp" >
      <mm:node number="component.cmshelp" notfound="skipbody">
          <mm:treeinclude page="/cmshelp/cockpit/rolerelated.jsp" objectlist="$includePath" referids="$referids" >
             <mm:param name="scope">education</mm:param>
          </mm:treeinclude>
      </mm:node>
    </mm:present>

    <mm:present referid="showfaq" >
  	  <mm:node number="component.faq" notfound="skipbody">
          <mm:treeinclude page="/faq/cockpit/rolerelated.jsp" objectlist="$includePath" referids="$referids" />
  	  </mm:node>
    </mm:present>
    <!-- end of region cms help and faq -->

 <script type="text/javascript">
<!--
function printThis() {
    if (frames && frames['content']) {
        if (frames['content'].focus)
            frames['content'].focus();
        if (frames['content'].print)
            frames['content'].print();
    }
    else if (window.print) {
        window.print();
    }
}
//-->
</script>

</mm:node>
<!-- please do not remove &nbsp; otherwise there will be overlapping of divs if there is no cloud access -->
&nbsp;
  </div>
</mm:cloud>
