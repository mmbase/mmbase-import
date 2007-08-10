<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
	<head>
		<title><fmt:message key="channeldelete.title" /></title>
		<link href="../css/main.css" type="text/css" rel="stylesheet" />
		<script src="content.js" type="text/javascript"></script>
		<script src="../utils/window.js" type="text/javascript"></script>
	</head>
	<body>

	<mm:cloud jspvar="cloud" rank="basic user" method='http'>
		<mm:import externid="number" id="parentchannel" jspvar="parentchannel" vartype="Integer" from="parameters" required="true" />

<div class="tabs">
    <div class="tab_active">
        <div class="body">
            <div>
                <a href="#"><fmt:message key="channeldelete.title" /></a>
            </div>
        </div>
    </div>
</div>

<div class="editor">
	<div class="body">

		<p>
			<fmt:message key="channeldelete.warning" />
			<b><mm:node number="$parentchannel"><mm:field name="name" /></mm:node></b>.
		</p>
		<form action="ChannelDelete.do" method="post" onsubmit="return unlinkAll();" name="deleteAllForm">
			<input type="hidden" name="remove" value="unlinkall" /> 
			<input type="hidden" name="number" value="<mm:write referid="parentchannel"/>" /> 
			<ul class="shortcuts">
            	<li class="delete">
					<a href="javascript:document.forms['deleteAllForm'].submit();"><fmt:message key="channeldelete.removeall" /></a>
				</li>
			</ul>
		</form>
		<div style="clear:both; height:10px;"></div>
	
        <div class="ruler_green"><div><fmt:message key="channeldelete.content" /></div></div>

		<mm:import id="lastotype" />

		<mm:node number="$parentchannel">
			<mm:relatednodescontainer path="creationrel,contentelement" searchdirs="source" element="contentelement">
				<mm:sortorder field="contentelement.otype" direction="up" />
				<mm:sortorder field="contentelement.title" direction="up" />

				<mm:listnodes jspvar="node">
					<mm:field name="otype" write="false" id="otype" />
					<mm:field name="number" write="false" id="number" />

					<mm:compare referid="lastotype" value="" inverse="true">
						</tr>
					</mm:compare>
					<mm:compare referid="otype" referid2="lastotype" inverse="true">
						<mm:compare referid="lastotype" value="" inverse="true">
							</table>
						</mm:compare>

						<mm:node referid="otype">
							<br />
							<fmt:message key="recyclebin.type">
								<fmt:param>
									<mm:field name="name" id="nodename">
										<mm:nodeinfo nodetype="$nodename" type="guitype" />
									</mm:field>
								</fmt:param>
							</fmt:message>
						</mm:node>
						<mm:import id="lastotype" reset="true"><mm:write referid="otype" /></mm:import>

						<table class="listcontent">
					</mm:compare>

					<tr class="itemrow" >
						<td><mm:field name="number" /></td>
						<td nowrap>
							<a href="javascript:info('<mm:field name="number" />');">
								<img src="../gfx/icons/info.png" width="16" height="16" alt="<fmt:message key="channeldelete.info" />" />
							</a>
							<a href="javascript:unpublish('<mm:write referid="parentchannel" />','<mm:field name="number" />');">
								<img src="../gfx/icons/delete.png" width="16" height="16" alt="<fmt:message key="channeldelete.unlink" />" />
							</a>
						</td>
						<td width="100%"><mm:field name="title" /></td>

					<mm:last>
					<mm:compare referid="lastotype" value="" inverse="true">
							</tr>
						</table>
					</mm:compare>
					</mm:last>
				</mm:listnodes>
			</mm:relatednodescontainer>
		</mm:node>

	</div>
	<div class="side_block_end"></div>
</div>	

</mm:cloud>

	</body>
	</html:html>
</mm:content>
