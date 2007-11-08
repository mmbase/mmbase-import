<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8" %>
<%@ include file="../globals.jsp" %>

<fmt:setBundle basename="cmsc-site" scope="request"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <link href="../css/toolbar.css" rel="stylesheet" type="text/css">
</head>
<div id="menu">
    <ul>
        <c:set var="hs"><fmt:message key="toolbar.hidetree"/></c:set>
        <li><input type="button" class="tlink4" onclick="return switchChannelPanel(this);" value="${hs}"/></li>
        <li><a href="${param.pagepath}" class="tlink1" target="pcontent"
               onclick="selectMenu(this.parentNode)"><fmt:message key="toolbar.editpage"/></a></li>
        <li><a href="PageEdit.do?number=${param.number}" class="tlink2" target="pcontent"
               onclick="selectMenu(this.parentNode)"><fmt:message key="toolbar.properties"/></a></li>
        <li><a href="${param.pagepath}?mode=preview" class="tlink3" target="pcontent"
               onclick="selectMenu(this.parentNode)"><fmt:message key="toolbar.preview"/></a></li>
        <mm:haspage page="/editors/publish-remote">
            <mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp">
                <mm:hasrank minvalue="administrator">
                    <li><a href="../workflow/publish.jsp?number=${param.number}" class="tlink5" target="pcontent"
                           onclick="selectMenu(this.parentNode)"><fmt:message key="toolbar.publish"/></a></li>
                </mm:hasrank>
            </mm:cloud>
        </mm:haspage>
    </ul>
</div>

<script>
    function switchChannelPanel(element) {
        if (window.top.bottompane.oldChannelsCols) {
            var oldChannelsCols = window.top.bottompane.oldChannelsCols;
            window.top.bottompane.sitemanagement

            var channelsCols = window.top.bottompane.document.body.cols;
            if (channelsCols == '0,*') {
                element.value = "<fmt:message key="toolbar.hidetree" />";
            }
            else {
                element.value = "<fmt:message key="toolbar.showtree" />";
            }
            window.top.bottompane.oldChannelsCols = channelsCols;
            window.top.bottompane.document.body.cols = oldChannelsCols;
        }
    }

    var selected;
    function selectMenu(item) {
        if (selected != undefined && selected != item) {
            selected.className = '';
        }
        item.className = 'active';
        selected = item;
    }
</script>
</html>