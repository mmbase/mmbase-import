<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<mm:import externid="currentitem"/>
<mm:import externid="currentfolder"/>
<mm:import externid="callerpage"/>
<mm:import externid="typeof"/>
<mm:import externid="action1" />
<mm:import externid="action2" />
  <mm:present referid="action1">
    <mm:import id="action1text"><di:translate key="workspace.save" /></mm:import>
    <mm:compare referid="action1" referid2="action1text">
      <mm:node number="$currentitem" >
        <mm:import externid="_handle_name" from="multipart"/>
        <mm:import externid="_handle_type" from="multipart"/>
        <mm:import externid="_handle_size" from="multipart"/>
        <mm:import externid="_title"  id="title" from="multipart"/>
        <mm:import externid="_url"  id="url" from="multipart"/>
	    <mm:import externid="_description"  id="description" jspvar="des" from="multipart"/>
	    <mm:import id="name"  externid="_name" from="multipart"/>
	    <mm:import id="filename"  externid="_filename" from="multipart"/>
	    <mm:setfield name="description"><mm:write referid="description"/></mm:setfield>
	    <mm:isempty referid="url">
	      <mm:setfield name="title"><mm:write referid="title"/></mm:setfield>
	      <mm:setfield name="filename"><mm:write referid="filename"/></mm:setfield>
	      <% long currentDate = System.currentTimeMillis() / 1000; %>
	      <mm:setfield name="date"><%=currentDate%></mm:setfield>
	      <mm:setfield name="filename"><mm:write referid="_handle_name"/></mm:setfield>
	      <mm:setfield name="mimetype"><mm:write referid="_handle_type"/></mm:setfield>
	      <mm:setfield name="size"><mm:write referid="_handle_size"/></mm:setfield>
	      <mm:fieldlist fields="handle">
	        <mm:fieldinfo type="useinput" />
	      </mm:fieldlist>
	    </mm:isempty>
	    <mm:isnotempty referid="url">
	       <mm:setfield name="name"><mm:write referid="name"/></mm:setfield>
	       <mm:setfield name="url"><mm:write referid="url"/></mm:setfield>
	    </mm:isnotempty>
	  </mm:node>
      <mm:redirect referids="$referids,currentfolder,currentitem,typeof" page="/workspace/index.jsp"/> 
    </mm:compare>
  </mm:present>

<%-- Check if the back button is pressed --%>
  <mm:present referid="action2">
    <mm:import id="action2text"><di:translate key="workspace.back" /></mm:import>
    <mm:compare referid="action2" referid2="action2text">
      <mm:node number="$currentitem">
        <mm:redirect referids="$referids,currentfolder,typeof" page="/workspace/index.jsp"/>
      </mm:node>
    </mm:compare>
  </mm:present>
</mm:cloud>   
