<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<HTML>
<HEAD>
   <TITLE>MMBase Bugtracker</TITLE>
   <%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
</HEAD>
<mm:cloud>
<mm:import externid="sbugid" jspvar="sbugid" />
<mm:import externid="sissue" jspvar="sissue" />
<mm:import externid="sstatus" jspvar="sstatus" />
<mm:import externid="stype" jspvar="stype" />
<mm:import externid="sarea" jspvar="sarea" />
<mm:import externid="sversion" jspvar="sversion" />
<mm:import externid="spriority" jspvar="spriority" />
<mm:import externid="offset" jspvar="offset">0</mm:import>
<mm:import externid="where" jspvar="where" />
<mm:import externid="flap">search</mm:import>

<mm:import externid="cw" from="cookie" />
<mm:import externid="ca" from="cookie" />
<mm:present referid="ca">
        <mm:present referid="cw">
			<mm:listnodes type="users" constraints="account='$ca' and password='$cw'" max="1">
				<mm:import id="user"><mm:field name="number" /></mm:import>
			</mm:listnodes>
        </mm:present>
</mm:present>


<BODY BACKGROUND="images/back.gif" TEXT="#42BDAD" BGCOLOR="#00425B" LINK="#42BDAD" ALINK="#42BDAD" VLINK="#42BDAD">
<!-- first the selection part -->

<TABLE  cellspacing=1 cellpadding=3 border=0 width="100%">
<FORM ACTION="index.jsp" METHOD="POST">

<TR>

		<TD WIDTH="50"><IMG SRC="images/trans.gif" WIDTH="50" HEIGHT="1"></TD>
		<TD BGCOLOR="#00425A" COLSPAN="8">
		 <center>BugTracker 1.2imnotfinishedyet - Daniel Ockeloen
		</TD>
</TR>
</TABLE>

<%@ include file="mainparts/flaps_index.jsp" %>


<mm:compare referid="flap" value="search"><%@ include file="mainparts/search.jsp" %></mm:compare>
<mm:compare referid="flap" value="lastchanges"><%@ include file="mainparts/lastchanges.jsp" %></mm:compare>
<mm:compare referid="flap" value="stats"><%@ include file="mainparts/statistics.jsp" %></mm:compare>
<mm:compare referid="flap" value="mysettings"><%@ include file="mainparts/mysettings.jsp" %></mm:compare>
<mm:compare referid="flap" value="mybug"><%@ include file="mainparts/mybug.jsp" %></mm:compare>


</mm:cloud>
</BODY>
</HTML>
