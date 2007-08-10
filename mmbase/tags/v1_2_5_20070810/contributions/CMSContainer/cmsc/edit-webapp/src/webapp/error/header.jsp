<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="nl" lang="nl">
<head><!-- Versie: <cmsc:version/> -->
<title><%=(request.getAttribute("title") != null)? request.getAttribute("title"): ""%></title>
<link href="<cmsc:staticurl page='/editors/css/main.css'/>" type="text/css" rel="stylesheet" />
</head>
<body>
<div class="side_block" style="width:100%">
   <div class="header">
      <div class="title">${title}</div>
      <div class="header_end"></div>
   </div>
<div class="body">
<img src="<cmsc:staticurl page='/editors/gfx/error.png'/>" align="left" style="padding: 7px;"/>