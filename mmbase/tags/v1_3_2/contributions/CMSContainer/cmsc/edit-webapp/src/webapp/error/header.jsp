<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="nl" lang="nl">
<head><!-- Versie: <cmsc:version/> -->
<title><%=(request.getAttribute("title") != null)? request.getAttribute("title"): ""%></title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="icon" href="<cmsc:staticurl page='/favicon.ico' />" type="image/x-icon" />
<link rel="shortcut icon" href="<cmsc:staticurl page='/favicon.ico' />" type="image/x-icon" />
<link href="<cmsc:staticurl page='/editors/css/main.css'/>" type="text/css" rel="stylesheet" />
<style type="text/css" xml:space="preserve">
   body { behavior: url(<cmsc:staticurl page='/editors/css/hover.htc)'/>;}
</style>
</head>
<body>
<div class="side_block" style="width:100%">
   <div class="header">
      <div class="title">${title}</div>
      <div class="header_end"></div>
   </div>
<div class="body">
<img src="<cmsc:staticurl page='/editors/gfx/error.png'/>" align="left" style="padding: 7px;" alt="error" />