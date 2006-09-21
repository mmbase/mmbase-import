<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="/taglibs.jsp" %>
<%@page import="nl.leocms.util.*" %>
<mm:cloud jspvar="cloud">
<html>
<head>
   <title>ELLE Magazine - Fashion Show Coverage with Designer Spring Styles from New York Fashion Week, Astrology, Beauty Trends</title>
   <meta name="keywords" content="">
   <meta name="description" content="Online home of ELLE – features complete runway / fashion show coverage, fashion trend reports, beauty trend reports, style essentials, shopping guides, illuminating astrology, and more">
   <link rel="stylesheet" type="text/css" href="css/content.css">

<script language="JavaScript" type="text/javascript">

function clearTxt(id)
{

document.getElementById(id).value = "";
}


</script>

<!--<script language="javascript" src="custom/exitpop.js"></script>-->

<script language="javascript" src="scripts/inc_frame_java.js" type="text/javascript"></script>
<script language="javascript" src="scripts/inc_global_java.js" type="text/javascript"></script>
</head>
<body bgcolor="#ffffff" leftmargin=0 rightmargin=0 topmargin=0 marginheight=0 marginwidth=0>
<form name='elle_ad_attr' ID="Form1" style="margin: 0px; margin-top: 0px; margin-right: 0px; margin-bottom: 0px; margin-left: 0px;">
   <input type='hidden' name='elle_section' value='1' ID="Hidden1">
   <input type='hidden' name='elle_article' value='toc' ID="Hidden2">
   <input type='hidden' name='elle_pagetype' value='Default' ID="Hidden3">
   <input type='hidden' name='elle_page_number' value='Default' ID="Hidden4">
   <input type="hidden" name="elle_season" value="0">
   <input type="hidden" name="elle_designer" value="0">
   <input type="hidden" name="elle_phototype" value="0">
   <input type="hidden" name="elle_model" value="0">
   <input type="hidden" name="elle_celebrity" value="0">
</form>
<script language="javascript" src="scripts/ad_array.js" type="text/javascript"></script>
<!-- homepage template -->
 <script language="JavaScript"> 
 <!-- 
 if (document.images) { 
   nav_promos_events_off      = new Image(); 
   nav_promos_events_off.src  = "media/1025200517351155.gif"; 
   nav_promos_events_on       = new Image(); 
   nav_promos_events_on.src   = "media/1025200517351389.gif"; 
   nav_sweeps_off             = new Image(); 
   nav_sweeps_off.src         = "media/1025200517351588.gif"; 
   nav_sweeps_on              = new Image(); 
   nav_sweeps_on.src          = "media/1025200517351639.gif"; 
   nav_forums_off             = new Image(); 
   nav_forums_off.src         = "media/1025200517350806.gif"; 
   nav_forums_on              = new Image(); 
   nav_forums_on.src          = "media/1025200517350921.gif"; 
   nav_about_us_off        = new Image(); 
   nav_about_us_off.src       = "media/1025200517350570.gif"; 
   nav_about_us_on            = new Image(); 
   nav_about_us_on.src     = "media/1025200517350681.gif"; 
   nav_search_off             = new Image(); 
   nav_search_off.src         = "media/search.gif"; 
   nav_search_on              = new Image(); 
   nav_search_on.src          = "media/0927200518462776.gif"; 
   nav_go_off              = new Image(); 
   nav_go_off.src             = "media/0927200518462375.gif"; 
   nav_go_on                  = new Image(); 
   nav_go_on.src           = "media/0927200518472711.gif"; 
   nav_back_off            = new Image(); 
   nav_back_off.src        = "media/1101200518414044.gif"; 
   nav_back_on             = new Image(); 
   nav_back_on.src         = "media/1101200518414339.gif"; 
   nav_next_off            = new Image(); 
   nav_next_off.src        = "media/1101200518413722.gif"; 
   nav_next_on             = new Image(); 
   nav_next_on.src         = "media/1101200518413878.gif"; 
   nav_more_off            = new Image(); 
   nav_more_off.src        = "media/more.jpg"; 
   nav_more_on             = new Image(); 
   nav_more_on.src         = "media/more_on.jpg"; 
   nav_archives_off        = new Image(); 
   nav_archives_off.src       = "media/archives.jpg"; 
   nav_archives_on            = new Image(); 
   nav_archives_on.src     = "media/archives_on.jpg"; 
   nav_fashion_off         = new Image(); 
   nav_fashion_off.src     = "media/1028200512355339.gif"; 
   nav_fashion_on          = new Image(); 
   nav_fashion_on.src         = "media/1028200512355453.gif"; 
   nav_shop_off            = new Image(); 
   nav_shop_off.src        = "media/1028200512360325.gif"; 
   nav_shop_on             = new Image(); 
   nav_shop_on.src         = "media/1028200512360518.gif"; 
   nav_runway_off             = new Image(); 
   nav_runway_off.src         = "media/1028200512355960.gif"; 
   nav_runway_on              = new Image(); 
   nav_runway_on.src          = "media/1028200512360016.gif"; 
   nav_beauty_off             = new Image(); 
   nav_beauty_off.src         = "media/1028200512355039.gif"; 
   nav_beauty_on              = new Image(); 
   nav_beauty_on.src          = "media/1028200512355111.gif"; 
   nav_bfeatnews_off       = new Image(); 
   nav_bfeatnews_off.src      = "media/0923200516374907.gif"; 
   nav_bfeatnews_on        = new Image(); 
   nav_bfeatnews_on.src    = "media/0923200516443466.gif"; 
   nav_astrology_off          = new Image(); 
   nav_astrology_off.src      = "media/1028200512354602.gif"; 
   nav_astrology_on           = new Image(); 
   nav_astrology_on.src       = "media/1028200512354890.gif"; 
   nav_books_off              = new Image(); 
   nav_books_off.src          = "media/0923200516375091.gif"; 
   nav_books_on               = new Image(); 
   nav_books_on.src        = "media/0923200516443608.gif"; 
   nav_in_the_mag_off         = new Image(); 
   nav_in_the_mag_off.src     = "media/1028200512355632.gif"; 
   nav_in_the_mag_on          = new Image(); 
   nav_in_the_mag_on.src      = "media/1028200512355728.gif"; 
   } 
 function rollover(sel,img) { 
   if (document.images) { 
      document.images[sel].src = eval(img + ".src") 
   } 
 } 
 //--> 
 </script> 
<% String language = request.getParameter("languages");
   if (language==null) { language = "eng"; } %>
<table width="991" border="0" cellspacing="0" cellpadding="0" summary="logo, newsletter signup, page title, and search" ID="Table1">
  <tr>
    <td width="271" align="left" valign="middle" bgcolor="#000000"><img src="media/1028200514555001.gif" width="271" height="64" border="0"></td>
    <td width="720" align="left" valign="middle" bgcolor="#000000"><!-- advertisement 1 --></td>
  </tr>
  <tr>
     <td valign="center" align="left" background="media/1028200514555233.gif" colspan="2" height="29">
       <table cellSpacing=0 cellPadding=0>
       <tr>
        <form method="post" action="/searcher.asp?section_id=43&article_id=0" ID="Form1">
            <mm:list nodes="elle_root" path="rubriek,posrel,pagina" fields="pagina.number" orderby="posrel.pos" directions="up">
               <mm:first inverse="true"><td align="center"><img src="media/divider.gif" border="0"></td></mm:first>
                   <mm:node element="pagina" jspvar="dummy" notfound="skipbody">
                     <td width="109" align="center"><nobr>
                      <% String value = LocaleUtil.getField(dummy,"titel",language); %>
                         <mm:last>
                            <input class="inputGrey" maxLength="50" size="15" value=" <%= value %> " name="search_value">
                             </td><td width="109" align="left" valign="middle">
                            <input value="GO" type="submit" style="cursor: hand;font-family: Arial; font-size: 10px; color: #CFCFCF; font-weight: bold; border-style: solid; border-width: 0; width: 15; height: 29;background-position: center; background: url('media/1028200514555233.gif')" name="submit">
                            <input type="image" height="7" width="4" src="media/go.gif" align="absMiddle" border="0" name="submit">
                         </mm:last>  
                         <mm:last inverse="true">
                           <a href="#" class="menuitem"><font face="Arial" size="1"><b>
                          <%= value.toUpperCase() %></b></font></a>
                        </mm:last>  
                   </nobr>
                  </td>
               </mm:node>
            </mm:list>
        </form>
        <form method="post">
            <td width="109" align="right">
               <select class="inputGrey" style="width:90px" name="languages" onchange="document.forms[2].submit();">
                  <option value="eng" <% if (language.equals("eng")) {%>selected<% } %>>English</option>
                  <option value="fra" <% if (language.equals("fra")) {%>selected<% } %>>Fran&ccedil;ais</option>
                  <option value="de" <% if (language.equals("de")) {%>selected<% } %>>Deutsch</option>
                  <option value="nl" <% if (language.equals("nl")) {%>selected<% } %>>Nederlands</option>
               </select>
            </td>
       </form>
       </tr></table>
      </td>
  </tr>
</table>
<table width="991" border="0" cellpadding="0" cellspacing="0" bgcolor="#FAFAFA" summary="left nav, main content, and 336x600 banner" ID="Table2">
  <tr valign="top">
    <td width="429" class="hpTd1">
      <object
        classid='clsid:D27CDB6E-AE6D-11cf-96B8-444553540000'
        codebase='http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=5,0,30,0' width='431' height='441'>
        <param name=movie value='swf/NewYorkFashionWeekDay8v4<%= (!language.equals("eng") ? language : "" ) %>.swf'>
        <param name='quality' value='high'> 
          <param name='menu' value='0'>
          <embed src='swf/NewYorkFashionWeekDay8v4<%= (!language.equals("eng") ? language : "" ) %>.swf' quality='high' width='431' height='441' name='menu'
            TYPE='application/x-shockwave-flash' PLUGINSPAGE='http://www.macromedia.com/go/getflashplayer'></embed>
      </object>

    </td>
    <td width="311" class="hpTd2">
      <a href="http://www.elle.com/fashionessentials/9407/two-tone-shoes.html"><img src="media/919200675726.jpg" border="0"></a>

      <img src="media/shim.gif" width="9" height="6" border="0"><br>
      <a href="http://www.elle.com/features/9400/style-scout-banana-republic.html"><img src="media/9182006162442.jpg" border="0"></a>

      <img src="media/shim.gif" width="9" height="6" border="0"><br>
      <a href="http://www.elle.com/features/9176/transition-in-style.html"><img src="media/99200613319.jpg" border="0"></a>

    </td>
     
    <td width="251" class="hpTd3"> 
      <script>
//<!--
   var adstring   =  "<SCRIPT LANGUAGE='JavaScript1.1' SRC='http://ad.doubleclick.net/adj/elle.lana.com/;kw=samsung_elle;sz=233x54;tile=1;loc=right;ord="  + myNum798 + "?'></" + "script>";
   document.write(adstring)
      if ((!document.images && navigator.userAgent.indexOf("Mozilla/2.") >= 0)  || navigator.userAgent.indexOf("WebTV")>= 0) {
document.write('<A HREF="http://ad.doubleclick.net/jump/elle.lana.com/;kw=samsung_elle;sz=233x54;tile=1;loc=right;ord=" +myNum798+ "?" TARGET="_blank">');
document.write('<IMG SRC="http://ad.doubleclick.net/ad/elle.lana.com/;kw=samsung_elle;sz=233x54;tile=1;loc=right;ord=" +myNum798+ "?" WIDTH="233" HEIGHT="54" BORDER="0" ALT=""></A>');
      }
//-->
</script>
<NOSCRIPT><A HREF="http://ad.doubleclick.net/jump/elle.lana.com/;kw=samsung_elle;sz=233x54;tile=1;loc=right;ord=[timestamp]?" TARGET="_blank"><IMG SRC="http://ad.doubleclick.net/ad/elle.lana.com/;kw=samsung_elle;sz=233x54;tile=1;loc=right;ord=[timestamp]?" WIDTH="233" HEIGHT="54" BORDER="0" ALT=""></A></NOSCRIPT>
      <table width="100%"  border="0" cellspacing="0" cellpadding="0" summary="subscription table">
<form action="http://www.email-publisher.com/survey/" method="post" target="New_Window">
  <input type="hidden" value="elle@elle.email-publisher.com" name="lists" />
  <tr><td colspan="2"><img src="media/11820061941.gif" border="0" alt="divider"></td></tr>
  <tr valign="top">
  <mm:list nodes="elle_root" path="rubriek,posrel,pagina,posrel,linklijst" max="1">
     <mm:node element="linklijst" jspvar="ll">
        <td>
            <mm:related path="lijstcontentrel,images" constraints="<%= "images.bron = '" + language + "'"%>">
               <mm:node element="images">
                  <img src=<mm:image/> width="92" height="126" border="0">
               </mm:node>
            </mm:related>
        </td>
        <td>
         <p><img src="media/shim.gif" width="6" height="9"><span style="font:Verdana;font-weight:bold;font-size:14px;font-variant:small-caps"><%= LocaleUtil.getField(ll,"naam",language) %></span><br>
            <mm:related path="lijstcontentrel,link" orderby="lijstcontentrel.pos" directions="up">
               <mm:node element="link" jspvar="dummy" notfound="skipbody">
                  <mm:last inverse="true">
                     <% 
                     // *** these are the links
                     String color = "#4B4B4B"; 
                     %>
                     <mm:first><% color = "#A81B32"; %></mm:first>
                     <% String value = LocaleUtil.getField(dummy,"titel",language); %>
                     <img src="media/shim.gif" width="9" height="9"><a href="<mm:field name="url"/>" target="_blank"><%
                        %><img src="media/clickhere.gif" height="7" width="4" align="absMiddle" border="0"></a><%
                        %><a href="<mm:field name="url"/>" style="text-decoration: none;" target="_blank">
								<font face="Nina" size="1.5" color="<%= color %>">
								<span style="font-variant:small-caps;"><%= value.toUpperCase() %></span></font></a><br>
                  </mm:last>
                  <mm:last>
                     <%
                     // *** this is the subscribe from
                     String sTitel = LocaleUtil.getField(dummy,"titel",language); 
                     String sDescr = LocaleUtil.getField(dummy,"omschrijving",language);
                     String sAltText = LocaleUtil.getField(dummy,"alt_tekst",language);
                     %>
                     <img alt=divider src="media/divider_h.gif" border=0 width="142" height="5"><br/>
                     <img src="media/shim.gif" width="9" height="9"><font face="Tahoma" size="1"><b><%= sTitel.toUpperCase() %></b></font><br/>
                     <img src="media/shim.gif" width="9" height="9"><input class="inputGrey" maxlength="50" size="20" value="<%= sDescr %>" name="email"><br/>
                     <img src="media/shim.gif" width="9" height="9"><input type="submit" value="<%= sAltText.toUpperCase() %>" style="border:0px solid #FAFAFA; font-family: Tahoma; font-weight: bold; font-size: 10px; padding: 0; background-color: #FAFAFA; cursor: hand; width:<%= sAltText.length()*6 + 4 %>px"><%
                     %><input type=image src="media/clickhere.gif" height="7" width="4" align="absMiddle" border="0" name="submit">                     
                  </mm:last>
               </mm:node>  
           </mm:related>
      </p>
     </td>
     </mm:node>
  </mm:list>
</tr><tr><td colspan="2"><img src="media/11820061941.gif" border="0" alt="divider"></td></tr>
</form>
</table>
      <a href="http://www.elle.com/shoppingguides/8835/shop-october-2006.html"><img src="media/1-homepage_unit.jpg" width="233" height="62" border="0" alt=""></a>
      <a href="http://www.elle.com/shoppingguides/8835/shop-october-2006.html"><img src="media/2-1purple_ad.jpg" width="233" border="0" alt=""></a>
      <a href="http://www.elle.com/shoppingguides/8835/shop-october-2006.html"><img width="233" height="62" border="0" alt="" src="media/919200611415.jpg"></a>
    </td>
  </tr>
</table>
<table width="991" border="0" cellspacing="0" cellpadding="0" summary="footer" ID="Table3">
  <tr>
    <td colspan="3" align="left" valign="middle" bgcolor="#7E7E7E"><img src="media/shim.gif" width="991" height="1" border="0"></td>
  </tr>
   <tr valign="top">
    <td width="417" bgcolor="#A8A8A8" class="hpFoot1a"> 
      <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td width="25%" rowspan="3" valign="top"> 
            <img src="media/1028200512362770.gif" border="0">
          </td>
          <td valign="top"> 
            <select name="select" size="1" class="formfields1" onChange="window.open(this.options[this.selectedIndex].value,'_top')">
               <option selected>Spring 2007 Ready-To-Wear</OPTION>
               <option value="http://www.elle.com/collections/9215/31-phillip-lim-spring-2007-ready-to-wear-collections.html">3.1 Phillip Lim</option>
               <option value="http://www.elle.com/collections/9387/a-la-disposition-spring-2007-ready-to-wear-collections.html">A La Disposition</option>
            </select>
            <br><br>
          </td>
        </tr>
        <tr> 
          <td valign="top"> 
            <select name="select" size="1" class="formfields1" onChange="window.open(this.options[this.selectedIndex].value,'_top')">
               <option selected>Fall 2006 Haute Couture</option>
               <option value="http://www.elle.com/collections/8945/adeline-andre-fall-2006-haute-couture-collection.html">Adeline Andre</option>
               <option value="http://www.elle.com/collections/8949/anne-valerie-hash-fall-2006-haute-couture-collection.html">Anne Valérie Hash</option>
            </select>
            <br><br>
          </td>
        </tr>
      <tr> 
          <td valign="top"> 
            <select name="select" size="1" class="formfields1" onChange="window.open(this.options[this.selectedIndex].value,'_top')">
               <option selected>Fall  2006 Ready-To-Wear</option>
               <option value="http://www.elle.com/collections/8384/fall-2006-ready-to-wear-af-vandevorst-collections.html">A.F. Vandevorst</option>
               <option value="http://www.elle.com/collections/8119/fall-2006-ready-to-wear-abaet-collections.html">Abaeté</option>
            </select>
            <br><br>
          </td>
        </tr>
      <tr> 
          <td colspan="2" valign="top"><img src="media/shim.gif" width="411" height="9" border="0">
          </td>
        </tr>
      </table>
    </td>
    <td width="292" bgcolor="#A8A8A8" class="hpFoot1b"> 
      <a href="http://www.elle.com/video/9359/fashion-video.html"><img alt="ELLE Fashion Videos Home Page" src="media/918200613182.jpg" border="0" /></a>
    </td>
    <td width="237" bgcolor="#A8A8A8" class="hpFoot1c"> 
      <a href="http://ad.doubleclick.net/clk;48598691;6248430;y?http://www.sephora.com/browse/product.jhtml?id=P155435&amp;cm_mmc=other-_-elle-_-email%2b20060718-_-VH1musicbag" target="_blank"><img height="49" alt="0609hp_savethemusic.gif" src="media/9152006125941.gif" width="236" border="0" /></a><br />
      <a href="http://www.surveys.com/s.aspx?start&amp;project=MRIEL062" target="_blank"><img alt="Accessories Survey" src="media/824200614399.gif" border="0" /></a><br />
      <a href="http://www.elle.com/magazine/7974/do-ask-do-tell-april-2006.html"><img src="media/5122006122457.gif" border="0" /></a>
    </td>
  </tr>
  <tr>
    <td colspan="3" bgcolor="#A8A8A8">
      <a href="http://www.elle.com/promotions/" onmouseover="rollover('nav_promos_events','nav_promos_events_on')" onmouseout="rollover('nav_promos_events','nav_promos_events_off')"><img src="media/1025200517351155.gif" name="nav_promos_events" alt="" border="0" width="88" height="22"></a><a href="http://www.elle.com/sweepstakes/" onmouseover="rollover('nav_sweeps','nav_sweeps_on')" onmouseout="rollover('nav_sweeps','nav_sweeps_off')"><img src="media/1025200517351588.gif" name="nav_sweeps" alt="" border="0" width="77" height="22"></a>
      <a href="http://www.elle.com/idealbb/default.asp" onmouseover="rollover('nav_forums','nav_forums_on')" onmouseout="rollover('nav_forums','nav_forums_off')"><img src="media/1025200517350806.gif" name="nav_forums" alt="" border="0" width="55" height="22"></a><a href="http://www.elle.com/aboutus/" onmouseover="rollover('nav_about_us','nav_about_us_on')" onmouseout="rollover('nav_about_us','nav_about_us_off')"><img src="media/1025200517350570.gif" name="nav_about_us" alt="" border="0" width="62" height="22"></a>
    </td>
  </tr>
  <tr>
    <td colspan="3" align="left" valign="middle" bgcolor="#7E7E7E"><img src="media/shim.gif" width="991" height="1" border="0"></td>
  </tr>
  <tr>
   <td colspan="3" align="left" valign="middle" bgcolor="#949494" class="footer">
      <img src="media/shim.gif" width="9" height="23" align="absmiddle"> <a href="http://aboutus.hfmus.com/copyright.asp" target="_blank" class="footer">
      Copyright&copy; 2006 Hachette Filipacchi Media, U.S., Inc.</a> / <a href="http://aboutus.hfmus.com/terms.asp" target="_blank" class="footer">
      Terms &amp; Conditions</a> / <a href="http://aboutus.hfmus.com/privacy.asp" target="_blank" class="footer">
      Privacy Policy – Your Privacy Rights </a> / <a href="http://www.elle.com/sitemap/" class="footer">Site Map</a> / 
      <a href="http://www.elle.com/designers/" class="footer">Fashion Designers</a> / 
      <a href="http://www.elle.com/models/" class="footer">Fashion Models</a> / 
      <a href="http://www.elle.com/features/9189/elle-at-new-york-fashion-week.html" class="footer">New York Fashion Week</a>
    </td>
  </tr>
</table>

<br>
<br>


<!-- START OF SmartSource Data Collector TAG -->
<!-- Copyright 2002 NetIQ Corporation -->
<!-- V6.0 -->
<!-- Old Tag -->

<script language="JavaScript">
<!--
function dcs_6_0(dcs_URI,dcs_QRY,dcs_EXT)
{
   var dCurrent = new Date();
   var P = "";
   //P+="http"+(window.location.protocol.indexOf('https:')==0?'s':'')+"://63.240.88.247/dcs.gif?";
   if (window.location.protocol.indexOf('https:')==0)
   {
   P+="https://secure.hfmus.com/dcs.gif?";
   }
else
   {
   P+="http://63.240.88.247/dcs.gif?";
   }
   P+="dcsuri="+escape(dcs_URI);
   P+="&dcsqry="+escape(dcs_QRY);
   if ((window.document.referrer != "") && (window.document.referrer != "-"))
   {
      if (!(navigator.appName == "Microsoft Internet Explorer" && parseInt(navigator.appVersion) < 4) )
      {
         P+="&dcsref="+escape(window.document.referrer);
      }
   }
   P+=dcs_EXT;
   P+="&dcssip=www.elle.com";
   P+="&dcsdat="+escape(dCurrent.getTime());
   document.write('<IMG BORDER="0" NAME="DCSIMG" WIDTH="1" HEIGHT="1" SRC="'+P+'">');
}
//-->
</script>
<script language="JavaScript">
<!--
function dcsExtend(N,V)
{
   dcsEXT+="&"+N+"="+escape(V);
}
function dcsMeta() 
{
   var F=false;
   var myDocumentElements;
   if (document.all)
   {
      F = true;
      myDocumentElements=document.all.tags("meta");
   }
   if (!F && document.documentElement)
   {
      F = true;    
      myDocumentElements=document.getElementsByTagName("meta");
   }
   if (F)
   {
      for (var i=1; i<=myDocumentElements.length;i++)
      {
         myMeta=myDocumentElements.item(i-1);
         if (myMeta.name.indexOf('WT.')==0)
            dcsExtend(myMeta.name,myMeta.content);
      }
   }  
}

var dcsURI=window.location.pathname;
var dcsQRY=window.location.search;
var dcsEXT="";
dcsExtend ("cmsSectionIndex","home")
dcsExtend ("cmsSection","home")
dcsExtend ("cmsArticle","home")
dcsExtend ("cmsPage","1")
dcsMeta();
dcs_6_0(dcsURI,dcsQRY,dcsEXT);
//-->
</script>
<noscript>
<img border="0" name="dcsimg" width="1" height="1" SRC="http://63.240.88.247/njs.gif?dcsuri=/nojavascript">
</noscript>
<script src="http://web.elle.com/elle.js" type="text/javascript"></script>

</body>
</html>

</mm:cloud>

