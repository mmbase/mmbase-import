<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@page isErrorPage="true"
%><%
	String msg = null;
	long ticket = 0;
	
   if(pageTitle.equals("500")){
   
%><%@page import="java.io.*,java.text.*,java.util.*"
%><%
      String contextPath = request.getContextPath();
      ticket = System.currentTimeMillis();
      StringWriter wr = new StringWriter();
      PrintWriter pw = new PrintWriter(wr);
   
      exception.printStackTrace(new PrintWriter(wr));
      
      msg = "EXCEPTION:" + "\n";
      msg += "   requesturl: " + request.getRequestURL() + "\n";
      msg += "   querystring: " + request.getQueryString() + "\n";
      msg += "   method: " + request.getMethod() + "\n";
      msg += "   user: " + request.getRemoteUser()  + "\n";
      msg += wr.toString();
   }
%><%!
   /** the date + time long format */
   private static final SimpleDateFormat DATE_TIME_FORMAT =
      new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
   
   /**
    * Creates String.from given long according to dd-MM-yyyy HH:mm:ss
    * @param date the date to format
    * @return Datestring
    */
   public static String getDateTimeString(long date) {
      return DATE_TIME_FORMAT.format(new Date(date));
   }
%>

<html>
  <head>
      <link rel="stylesheet" type="text/css" href="/nmintra/css/main.css">
      <link rel="stylesheet" type="text/css" href="/nmintra/css/groene_boomrand.css" />
      <title>Foutmelding - <%=pageTitle%>: <%=messageAboutError%></title>
      <meta http-equiv="imagetoolbar" content="no">

      <script type="text/javascript">
      function resizeBlocks() { 
      var MZ=(document.getElementById?true:false); 
      var IE=(document.all?true:false);
      var wHeight = 0;
      var infoPageDiff = 87;
      var navListDiff = 62;
        var smoelenBoekDiff = 378;
      var linkListDiff = 511;
      var rightColumnDiff = 109;
      var minHeight = 300;
      if(IE){ 
        wHeight = document.body.clientHeight;
        if(wHeight>minHeight) {
          if(document.all['infopage']!=null) { 
            document.all['infopage'].style.height = (wHeight>infoPageDiff ? wHeight - infoPageDiff : 0); }
          if(document.all['navlist']!=null) { 
            document.all['navlist'].style.height = (wHeight>navListDiff ? wHeight - navListDiff : 0); }
          if(document.all['smoelenboeklist']!=null) {
            document.all['smoelenboeklist'].style.height = (wHeight>smoelenBoekDiff ? wHeight - smoelenBoekDiff : 0); }
          if(document.all['rightcolumn']!=null) {
            document.all['rightcolumn'].style.height = (wHeight>rightColumnDiff ? wHeight - rightColumnDiff : 0); }
          if(document.all['linklist']!=null) {
            document.all['linklist'].style.height = (wHeight>linkListDiff ? wHeight - linkListDiff : 0); }
        }
      } else if(MZ){
        wHeight = window.innerHeight;
        if(wHeight>minHeight) {
          if(document.getElementById('infopage')!=null) {
            document.getElementById('infopage').style.height= (wHeight>infoPageDiff ? wHeight - infoPageDiff : 0); }
          if(document.getElementById('navlist')!=null) {
            document.getElementById('navlist').style.height= (wHeight>navListDiff ? wHeight - navListDiff : 0); } 
          if(document.getElementById('smoelenboeklist')!=null) {
            document.getElementById('smoelenboeklist').style.height= (wHeight>smoelenBoekDiff ? wHeight - smoelenBoekDiff : 0); } 
          if(document.getElementById('rightcolumn')!=null) {
            document.getElementById('rightcolumn').style.height= (wHeight>rightColumnDiff ? wHeight - rightColumnDiff : 0); } 
          if(document.getElementById('linklist')!=null) {
            document.getElementById('linklist').style.height= (wHeight>linkListDiff ? wHeight - linkListDiff : 0); } 
        }
      }
      return false;
      }
      </script>
  </head>
  <body onLoad="javascript:resizeBlocks();" onResize="javascript:resizeBlocks();">

   <table background="/nmintra/media/styles/groene_boomrand.jpg" cellspacing="0" cellpadding="0" border="0">
<tr>
  <td rowspan="3">
      <img src="/nmintra/media/styles/groene_boomrand_logo.gif" title="www.natuurmonumenten.nl" 
        style="position:absolute;z-index:2;left:2px;top:1px;" border="0">
  </td>
  <td style="width:70%;"><img src="media/spacer.gif" width="1" height="12"></td>
  <td class="header" style="padding-right:10px;padding-top:5px;text-align:right;width:251px;">Natuurmonumenten <a href="/index.jsp?r=17622" class="hover"><span class="red">Intranet</span></a>
  </td>
</tr>
<tr>
   <td style="width:70%;">
   <table border="0" cellspacing="0" cellpadding="0" style="width:100%;">

      
      <tr>
      <td><input type="text" name="search" value="ik zoek op ..." onClick="if(this.value=='ik zoek op ...') { this.value=''; }" style="text-align:left;width:110px;" /></td>
      <td style="padding-left:3px;padding-top:1px;">
        <input type="submit" name="Submit" value="Zoek" style="text-align:center;font-weight:bold;"></td>
      <td style="padding-left:3px;padding-top:1px;">
        <input type="hidden" name="adv">
        <input type="submit" name="AdvSubmit" value="Uitgebreid Zoeken" style="text-align:center;font-weight:bold;width:110px;" onClick="document.searchform.adv.value='adv_search';"></td>
      <td style="width:80%;text-align:right;">

      </td>
      </tr>
   </table>
   </td>
   <td style="padding-right:10px;width:251px;">

   </td>
</tr>
<tr>
   <td style="width:70%;"><img src="/nmintra/media/spacer.gif" width="1" height="12"></td>
   <td style="width:251px;"><img src="/nmintra/media/spacer.gif" width="251" height="12"></td>
</tr>
      <tr>
         <td class="black"><img src="/nmintra/media/spacer.gif" width="195" height="1"></td>

         <td class="black" style="width:70%;"><img src="media/spacer.gif" width="1" height="1"></td>
         <td class="black"><img src="/nmintra/media/spacer.gif" width="251" height="1"></td>
      </tr>
   <tr>
      <td rowspan="2">
<div class="navlist" id="navlist">
  <table border="0" cellpadding="0" cellspacing="0">
      <tr>
          <td><img src="/nmintra/media/spacer.gif" width="158" height="35"></td>

      </tr>
      <tr>
         <td>
            <table border="0" cellpadding="0" cellspacing="0">
               <tr>
                  <td style="padding-left:5px;color:white;height:18px;"></td>
                  <td style="letter-spacing:1px;">
                     <a href="/home/nieuws.htm" class="menuItemActive">Home</a>
                  </td>
               </tr>
                           <tr><td></td><td>&nbsp;</td></tr>
                           <tr><td></td><td>&nbsp;</td></tr>
            </table>
         </td>
      </tr>
   </table>
</div>
</td>

<td ></td>
<td><table border="0" cellpadding="0" cellspacing="0">
    <tr>
        <td style="padding-right:10px;"><img src="/nmintra/media/spacer.gif" width="241" height="6"><br>
        <div align="right"></div></td>

    </tr>
</table></td>
</tr>
<tr>
<td class="transperant" >
<div class="infopage" id="infopage">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr><td style="padding:10px;padding-top:18px;">
  <div align="right" style="letter-spacing:1px;">
      <a href="javascript:history.go(-1);">Terug</a>
  </div>
    
    
<script type="text/javascript">
   function showError() {
      var errorDisplay = document.getElementById('errordiv').style.display;
      if (errorDisplay =='none') {
         document.getElementById('errordiv').style.display='block';
      }
      else {
         document.getElementById('errordiv').style.display='none';
      }
   }
</script>

<table width="744" border="0" cellspacing="0" cellpadding="0" align="center" valign="top">
<tr>
   <td style="vertical-align:top;width:374px;padding:10px;padding-top:0px"><br/>
 