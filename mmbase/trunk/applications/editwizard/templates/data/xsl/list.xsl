<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  version="1.0">
  <!--
  list.xls
  @since  MMBase-1.6
  @author Kars Veling
  @author Michiel Meeuwissen
  @version $Id: list.xsl,v 1.13 2002-07-11 12:08:36 pierre Exp $
  -->

  <xsl:import href="baselist.xsl" />

  <xsl:param name="wizardtitle"><xsl:value-of select="list/object/@type" /></xsl:param>
  <xsl:param name="title"><xsl:value-of select="$wizardtitle" /></xsl:param>
  <xsl:param name="deletable">false</xsl:param>
  <xsl:param name="creatable">true</xsl:param>

  <xsl:param name="deleteprompt"><xsl:call-template name="prompt_delete_confirmation" /></xsl:param>
  <xsl:param name="deletedescription"><xsl:value-of select="$tooltip_delete" /></xsl:param>


  <!-- ================================================================================
       The following things can be overriden to customize the appearance of list
       ================================================================================ -->

  <xsl:template name="style"> <!-- It can be usefull to add a style, change the title -->
    <title><xsl:value-of select="$wizardtitle" /> - <xsl:value-of select="$title" /></title>
    <link rel="stylesheet" type="text/css" href="../style.css" />
    <xsl:call-template name="extrastyle" /> <!-- override base.xsl for this -->
  </xsl:template>

  <xsl:template name="body"> <!-- You can put stuff before and after then. Don't forget to call 'bodycontent' -->
    <body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="window.focus();">
      <xsl:call-template name="bodycontent" />
    </body>
  </xsl:template>

  <xsl:template name="superhead"><!-- The first row of the the body's table -->
    <tr>
      <td valign="bottom" colspan="2" align="center">
        <table width="100%" cellspacing="0" cellpadding="0" border="0">
          <tr>
            <td class="superhead" align="right"><nobr><xsl:value-of select="$title" /></nobr></td>
          </tr>
        </table>
      </td>
    </tr>
  </xsl:template>

  <xsl:template name="bodycontent"><!-- If you are really crazy, you can even go for overriding the bodycontent -->
    <!-- I think all elements must have a class here, then you can customize then the appearance by putting another css -->
    <table cellspacing="0" cellpadding="3" border="0" width="615">
      <xsl:call-template name="superhead" />
      <tr>
        <td><img width="1" src="{$mediadir}nix.gif" height="5" /></td>
      </tr>
      <tr>
        <td width="124"></td>
        <td width="558" valign="top" colspan="2" class="listcanvas" align="left">
          <div title="{$tooltip_edit_list}" class="subhead">
            <nobr><xsl:value-of select="$title" />(<xsl:value-of select="@count" /> items)</nobr>
          </div>
          <br />
            <xsl:call-template name="dolist" />
            <xsl:if test="$creatable='true'">
              <br />
                <div width="100%" align="right">
                  <a href="{$wizardpage}&amp;wizard={$wizard}&amp;objectnumber=new" title="{$tooltip_new}"><xsl:call-template name="prompt_new" /></a>
               </div>
             </xsl:if>
            </td>
          </tr>
          <tr>
            <td width="124"></td>
            <td colspan="20">
              <div>
                <xsl:if test="count(/*/pages/page)>1"><xsl:apply-templates select="/*/pages" /><br /><br /></xsl:if>
                </div>
              </td>
          </tr>

          <tr class="itemrow" ><td colspan="2" align="center" >
            <a href="{$listpage}&amp;remove=true" title="{$tooltip_index}"><xsl:call-template name="prompt_index"/></a>
            -
            <a href="{$listpage}&amp;logout=true&amp;remove=true" title="{$tooltip_logout}"><xsl:call-template name="prompt_logout"/></a>
          </td></tr>
        </table>
  </xsl:template>


  <!-- javascript, I don't know if you want to override _that_...  -->
  <xsl:template name="javascript">
    <script language="javascript">
      <xsl:text disable-output-escaping="yes"><![CDATA[<!--
   var cancelClick = false;

   function objMouseOver(el) {
   el.className="itemrow-hover";
   }

   function objMouseOut(el) {
   el.className="itemrow";
   }

   function objClick(el) {
   if (cancelClick) {
   cancelClick=false;
   return;
   }
   var href = el.getAttribute("href")+"";
   if (href.length<10) return;
   document.location=href;
   }

   function doDelete(prompt) {
   var conf;
   if (prompt && prompt!="") {
   conf = confirm(prompt);
   } else conf=true;
   cancelClick=true;
   return conf;
   }
   -->
]]></xsl:text>
      </script>
  </xsl:template>

  <!-- ================================================================================
       The following is functionality. You probably don't want to override it.
       ================================================================================ -->

  <xsl:template match="pages">
    <span class="pagenav">
      <xsl:choose>
        <xsl:when test="page[@previous='true']">
          <a class="pagenav" title="{$tooltip_previous}{@currentpage-1}" href="{$listpage}&amp;start={page[@previous='true']/@start}"><xsl:call-template name="prompt_previous" /></a><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="prompt_previous" /><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
        </xsl:otherwise>
      </xsl:choose>

      <xsl:apply-templates select="page" />

      <xsl:choose>
        <xsl:when test="page[@next='true']">
          <a class="pagenav" title="{$tooltip_next}{@currentpage+1}" href="{$listpage}&amp;start={page[@next='true']/@start}"><xsl:call-template name="prompt_next" /></a>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="prompt_next" />
        </xsl:otherwise>
      </xsl:choose>
    </span>
  </xsl:template>

  <xsl:template match="page">
    <a class="pagenav" title="{$tooltip_goto}{position()}" href="{$listpage}&amp;start={@start}"><xsl:value-of select="position()" /></a><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
  </xsl:template>


  <xsl:template match="page[@current='true']">
    <xsl:value-of select="position()" /><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
  </xsl:template>

  <xsl:template match="object">
     <tr>
         <xsl:if test="@mayedit='true'">
           <xsl:attribute name="class">itemrow</xsl:attribute>
           <xsl:attribute name="onMouseOver">objMouseOver(this);</xsl:attribute>
           <xsl:attribute name="onMouseDown">objClick(this);</xsl:attribute>
           <xsl:attribute name="onMouseOut">objMouseOut(this);</xsl:attribute>
           <xsl:attribute name="href"><xsl:value-of select="$wizardpage" />&amp;wizard=<xsl:value-of select="$wizard" />&amp;objectnumber=<xsl:value-of select="@number" /></xsl:attribute>
         </xsl:if>
         <xsl:if test="@mayedit='false'">
           <xsl:attribute name="class">itemrow-disabled</xsl:attribute>
         </xsl:if>
         <xsl:if test="$deletable='true'">
           <td class="deletebutton">
           <xsl:if test="@maydelete='true'">
            <a href="{$deletepage}&amp;wizard={$wizard}&amp;objectnumber={@number}" title="{$deletedescription}" onmousedown="cancelClick=true;" onclick="return doDelete('{$deleteprompt}');" ><xsl:call-template name="prompt_delete" /></a><img src="{$mediadir}nix.gif" width="10" height="1" />
           </xsl:if>
           </td>
         </xsl:if>
         <td valign="top"><xsl:value-of select="@index" />.<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
         </td>
         <xsl:apply-templates select="field" />
     </tr>
     <tr><td><img src="{$mediadir}nix.gif" width="1" height="3" /></td></tr>
  </xsl:template>


  <xsl:template name="dolist"><!-- returns the actual list as a table -->
    <table border="0" cellspacing="0" cellpadding="0">
      <xsl:if test="object[@number&gt;0]">
        <tr>
          <xsl:if test="$deletable='true'"><td></td></xsl:if>
          <td class="tableheader">#</td>
          <xsl:for-each select="object[1]/field">
            <td class="tableheader"><xsl:value-of select="@name" /><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
            </td><td><img src="{$mediadir}nix.gif" width="4" height="1" /></td>
          </xsl:for-each>
        </tr>
      </xsl:if>
      <xsl:apply-templates select="object[@number&gt;0]" />
    </table>
  </xsl:template>


  <xsl:template match="list"><!-- this it the entry-point, I suppose -->
    <html>
      <head>
        <meta HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=utf-8" /><!-- We require doing everything in UTF-8 -->
        <xsl:call-template name="style" />
        <xsl:call-template name="javascript" />
      </head>
      <xsl:call-template name="body" />
    </html>
  </xsl:template>

  <xsl:template match="field">
    <xsl:if test="position()>1"><td valign="top"><nobr><xsl:value-of disable-output-escaping="yes" select="." /></nobr></td></xsl:if>
    <xsl:if test="position()=1"><td valign="top" width="99%"><xsl:value-of select="." /><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td></xsl:if>
    <td><img src="{$mediadir}nix.gif" width="4" height="1" /></td>
  </xsl:template>

</xsl:stylesheet>