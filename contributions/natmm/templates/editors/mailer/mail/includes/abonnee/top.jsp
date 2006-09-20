<%@page import="java.util.*,nl.leocms.util.*,nl.leocms.util.tools.*,nl.leocms.applications.*" %>
<%@ page language="java" contentType="text/html; charset=utf-8" import="java.util.*" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%-- weet niet of d'r wat omvalt zonder session is true ?? @ page session="true" --%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>LifeLine Mail</title>
<link rel="stylesheet" type="text/css" href="css/main.css" media="screen" />
</head>
<body>
<%-- config options --%>
<mm:import id="listsize">15</mm:import>
