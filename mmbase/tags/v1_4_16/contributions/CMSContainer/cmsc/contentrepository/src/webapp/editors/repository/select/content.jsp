<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="../globals.jsp" %>
<%@page import="com.finalist.cmsc.repository.RepositoryUtil" %>
<%@page import="com.finalist.cmsc.security.*" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="content.title">
	<script src="content.js" type="text/javascript"></script>
	<script type="text/javascript">
		function selectElement(element, title, url) {
			window.top.opener.selectElement(element, title, url);
			window.top.close();
		}
	</script>
</cmscedit:head>
	<body>
<mm:cloud jspvar="cloud" rank="basic user" loginpage="../../login.jsp">
	  <mm:import externid="parentchannel" jspvar="parentchannel" vartype="Integer" from="parameters" required="true"/>

<mm:node number="$parentchannel" jspvar="parentchannelnode">

    <div class="tabs">
        <div class="tab_active">
            <div class="body">
                <div>
                    <a href="#"><fmt:message key="content.title" /></a>
                </div>
            </div>
        </div>
    </div>
	<div class="editor">
		<div class="body">
		
	<p>
		<fmt:message key="content.channel" >
		 	<fmt:param ><mm:field name="name"/></fmt:param>
		 </fmt:message>	
	</p>

	<div style="clear:both; height:10px;"></div>
	</div>	
  <div class="ruler_green"><div><fmt:message key="content.content" /></div></div>
  <div class="body">
	 <mm:import id="lastotype"/>
 
	 

<mm:relatednodescontainer path="contentrel,contentelement" searchdirs="destination" element="contentelement">
	<mm:sortorder field="contentelement.otype" direction="up" />
	<mm:sortorder field="contentrel.pos" direction="up" />
	
	<mm:listnodes jspvar="node">
		<mm:field name="otype" write="false" id="otype"/>
		<mm:field name="number" write="false" id="number"/>
		<mm:field name="number" write="false" id="relnumber"/>

		<mm:compare referid="lastotype" value="" inverse="true">
		   </tr>
		</mm:compare>
		<mm:compare referid="otype" referid2="lastotype" inverse="true">
		   <mm:compare referid="lastotype" value="" inverse="true">
			  </table>
		   </mm:compare>

		   <mm:node referid="otype">
			  <br/>
			  <fmt:message key="content.type" >
			 	<fmt:param><mm:field name="name" id="nodename"><mm:nodeinfo nodetype="$nodename" type="guitype"/></mm:field></fmt:param>
			 </fmt:message>
		   </mm:node>
		   <mm:import id="lastotype" reset="true"><mm:write referid="otype"/></mm:import>
		   <mm:import id="newotype">true</mm:import>

		   <table class="listcontent">
		</mm:compare>

		<tr>
		<td>
		   <mm:field name="number"/>
		</td>
		<td nowrap>
        	<a href="javascript:info('<mm:field name="number" />')"><img src="../../gfx/icons/info.png" width="16" height="16" alt="<fmt:message key="content.info" />"/></a>
			<mm:import id="url">javascript:selectElement('<mm:field name="number"/>', '<mm:field name="title" escape="js-single-quotes"/>', '<cmsc:staticurl page="/content/" /><mm:field name="number"/>');</mm:import>
        	<a href="<mm:write referid="url"/>"><img src="../../gfx/icons/link.png" width="16" height="16" alt="<fmt:message key="content.link" />"/></a>
		</td>
		<td width="100%">
		   <mm:field name="title"/>
		</td>
		<mm:present referid="newotype">
		   <td></td>
		</mm:present>

		<mm:import id="lastnumber" reset="true"><mm:write referid="number"/></mm:import>
		<mm:import id="lastrelnumber" reset="true"><mm:write referid="relnumber"/></mm:import>

		<mm:remove referid="newotype"/>

		<mm:last>
		   <mm:compare referid="lastotype" value="" inverse="true">
			  </tr></table>
		   </mm:compare>
		</mm:last>

	</mm:listnodes>
</mm:relatednodescontainer>


		</div>
		<div class="side_block_end"></div>
	</div>	
</mm:node>
</mm:cloud>
	</body>
</html:html>
</mm:content>