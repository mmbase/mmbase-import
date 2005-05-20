<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%@page language="java" contentType="text/html;charset=utf-8" session="false"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%>

<mm:cloud>
<mm:import externid="image"/>
<mm:import externid="template"></mm:import>
<mm:import externid="comment"></mm:import>
<mm:node number="$image">
<table width="100%">
  <tr>
    <td valign="top">
    <mm:write referid="comment"/>
    </td>
    <td align="right" valign="top">
    template="<mm:write referid="template"/>"
    </td>
  </tr>
  <tr>
    <td colspan="2" class="pane">
        <img src="<mm:image template="$template"/>" border="0"/>
    </td>
  </tr>
</table>
</mm:node>
</mm:cloud>
