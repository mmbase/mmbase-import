<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<HTML>
<HEAD>
   <TITLE>MMBase Package Manager</TITLE>
   <%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
</HEAD>
<mm:cloud>
<mm:import externid="id">unknown</mm:import>
  <mm:functioncontainer>
   <mm:param name="id" value="$id" />
    <mm:booleanfunction set="mmpm" name="signalRemoteChange">
    </mm:booleanfunction>
  </mm:functioncontainer>
</mm:cloud>
