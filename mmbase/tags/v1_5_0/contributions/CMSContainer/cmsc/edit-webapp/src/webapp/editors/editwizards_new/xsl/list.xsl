<?xml version="1.0" encoding="utf-8"?>
<!--
   Stylesheet for customizing the wizard layout of the edit wizards.

   Author: Nico Klasens
   Created: 25-07-2003
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

   <!-- Import original stylesheet -->
   <xsl:import href="ew:xsl/list.xsl"/>
   <xsl:import href="templatesi18n:xsl/prompts-cmsc.xsl"/>

  <xsl:template name="colorstyle">
    <link rel="stylesheet" type="text/css" href="{$ew_context}{$templatedir}style/color/list.css" />
  </xsl:template>

  <xsl:template name="extrastyle">
    <link rel="stylesheet" type="text/css" href="{$ew_context}{$templatedir}style/extra/list.css" />
    <style type="text/css" xml:space="preserve">
      body { behavior: url(../../../../editors/css/hover.htc);}
    </style>
  </xsl:template>

  <xsl:template match="list">
    <xsl:call-template name="searchbox" />
    <tr class="listcanvas">
      <td>
        <xsl:call-template name="dolist" />
      </td>
    </tr>
    <xsl:if test="count(/*/pages/page) &gt; 1">
      <tr class="pagescanvas">
        <td>
          <div>
            <xsl:apply-templates select="/*/pages" />
            <br />
            <br />
          </div>
        </td>
      </tr>
    </xsl:if>
    <tr class="buttoncanvas">
      <td>
        <xsl:if test="$searchfields=&apos;&apos; and $creatable=&apos;true&apos;">
          <br />
          <div width="100%" align="left">
            <xsl:if test="$createprompt">
              <div style="width: 200px;">
                <xsl:value-of select="$createprompt" />
              </div>
            </xsl:if>
            <a
              href="{$wizardpage}&amp;wizard={$wizard}&amp;objectnumber=new&amp;origin={$origin}"
              title="{$tooltip_new}"  class="expand_button">
              <xsl:call-template name="prompt_new" />
            </a>
          </div>
        </xsl:if>
      </td>
    </tr>
    <xsl:call-template name="listlinks" />
  </xsl:template>

  <xsl:template name="listlinks" />

	<!-- yeah right don't think so -->
  <xsl:template name="search-fields-default" />

  <xsl:template match="object">
    <tr>
      <xsl:if test="@mayedit=&apos;true&apos;">
          <xsl:choose>
            <xsl:when test="(position() mod 2) = 0">
              <xsl:attribute name="class">itemrow even</xsl:attribute>
            </xsl:when>
            <xsl:otherwise>
              <xsl:attribute name="class">itemrow odd</xsl:attribute>
            </xsl:otherwise>
          </xsl:choose>
        <xsl:attribute name="onMouseDown">objClick(this);</xsl:attribute>
        <xsl:attribute name="href"><xsl:value-of select="$wizardpage" />&amp;wizard=<xsl:value-of select="$wizard" />&amp;objectnumber=<xsl:value-of select="@number" />&amp;origin=<xsl:value-of select="$origin" /></xsl:attribute>
      </xsl:if>
      <xsl:if test="@mayedit=&apos;false&apos;">
        <xsl:attribute name="class">itemrow-disabled</xsl:attribute>
      </xsl:if>
      <xsl:if test="$deletable=&apos;true&apos;">
        <td class="deletebutton">
          <xsl:if test="@maydelete=&apos;true&apos;">
            <a
              href="{$deletepage}&amp;wizard={$wizard}&amp;objectnumber={@number}"
              title="{$deletedescription}"
              onmousedown="cancelClick=true;"
              onclick="return doDelete(&apos;{$deleteprompt}&apos;);"
               class="imgbuttonremove">
              <xsl:call-template name="prompt_delete" />
            </a>
          </xsl:if>
        </td>
      </xsl:if>
      <td class="number">
        <xsl:value-of select="@index" />
        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
      </td>
      <xsl:apply-templates select="field" />
    </tr>
  </xsl:template>

  <xsl:template name="prompt_delete">
  </xsl:template>
  
</xsl:stylesheet>
