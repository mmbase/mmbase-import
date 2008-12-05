<?xml version="1.0" encoding="utf-8"?>
<!--
   Stylesheet for customizing the wizard layout of the edit wizards.

   Author: Nico Klasens
   Created: 25-07-2003
   Version: $Revision$
-->
<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:node="org.mmbase.bridge.util.xml.NodeFunction"
  extension-element-prefixes="node">

   <!-- Import original stylesheet -->
   <xsl:import href="ew:xsl/searchlist.xsl"/>

  <xsl:template name="extrastyle">
    <link rel="stylesheet" type="text/css" href="{$ew_context}{$templatedir}style/extra/searchlist.css" />
    <style type="text/css" xml:space="preserve">
      body { behavior: url(../../../../editors/css/hover.htc);}
    </style>
  </xsl:template>

  <xsl:template name="colorstyle">
    <link rel="stylesheet" type="text/css" href="{$ew_context}{$templatedir}style/color/searchlist.css" />
  </xsl:template>

  <xsl:template name="bodycontent" >
    <xsl:call-template name="body" />
  </xsl:template>
  
  <xsl:template match="list">
    <form>
      <div id="searchresult" class="searchresult">
        <!-- IE is too stupid to understand div.searchresult table -->
        <table class="searchresult" cellspacing="0">
          <xsl:if test="not(object)">
            <tr>
              <td>
                <xsl:call-template name="prompt_no_results" />
              </td>
            </tr>
          </xsl:if>
          <xsl:for-each select="object">
            <tr number="{@number}" id="item_{@number}">
              <xsl:choose>
                <xsl:when test="@maylink=&apos;true&apos;">
                  <xsl:attribute name="onClick">doclick_search(this);</xsl:attribute>
                  <xsl:choose>
                    <xsl:when test="(position() mod 2) = 0">
                      <xsl:attribute name="class">even</xsl:attribute>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:attribute name="class">odd</xsl:attribute>
                    </xsl:otherwise>
                  </xsl:choose>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:choose>
                    <xsl:when test="(position() mod 2) = 0">
                      <xsl:attribute name="class">even-disabled</xsl:attribute>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:attribute name="class">odd-disabled</xsl:attribute>
                    </xsl:otherwise>
                  </xsl:choose>
                </xsl:otherwise>
              </xsl:choose>
              <td style="display: none;">
                <input
                  type="checkbox"
                  style="visibility: hidden;"
                  name="{@number}"
                  did="{@number}"
                  id="cb_{@number}" />
              </td>
              <xsl:choose>
                <xsl:when test="@type=&apos;images&apos;">
                  <td>
                    <img
                      src="{node:function($cloud, string(@number), concat(&apos;servletpath(&apos;, $cloudkey, &apos;,cache(&apos;, $listimagesize, &apos;))&apos;))}"
                      hspace="0"
                      vspace="0"
                      border="0"
                      title="{field[@name=&apos;description&apos;]}" />
                    <br />
                    <a
                      href="{node:function($cloud, string(@number), concat(&apos;servletpath(&apos;, $cloudkey,&apos;)&apos;))}"
                      target="_new">
                      <xsl:call-template name="prompt_image_full" />
                    </a>
                  </td>
                  <td>
                    <xsl:for-each select="field">
                       <xsl:if test="string-length(text())>0">
                          <xsl:choose>
                             <xsl:when test="position() = 1">
                             <b>
                               <xsl:value-of select="." />
                             </b>
                             </xsl:when>
                             <xsl:otherwise>
                                <br />
                                <xsl:value-of select="." />
                             </xsl:otherwise>
                          </xsl:choose>
                       </xsl:if>
                    </xsl:for-each>
                  </td>
                </xsl:when>
                <xsl:when test="@type=&apos;audioparts&apos;">
                  <td>
                    <a target="_blank" href="{node:function($cloud, string(field/@number), &apos;url()&apos;)}">
                      <xsl:call-template name="prompt_audio" />
                    </a>
                  </td>
                  <xsl:apply-templates select="field" />
                </xsl:when>
                <xsl:when test="@type=&apos;videoparts&apos;">
                  <td>
                    <a target="_blank" href="{node:function($cloud, string(field/@number), &apos;url()&apos;)}">
                      <xsl:call-template name="prompt_video" />
                    </a>
                  </td>
                  <xsl:apply-templates select="field" />
                </xsl:when>
                <xsl:otherwise>
                  <xsl:apply-templates select="field" />
                </xsl:otherwise>
              </xsl:choose>
            </tr>
          </xsl:for-each>
        </table>
      </div>
      
      <xsl:call-template name="searchnavigation" />
    </form>
  </xsl:template>
  
  <xsl:template name="searchnavigation">
    <div id="searchnavigation" class="searchnavigation">
      <div>
        <xsl:apply-templates select="pages" />
        
        <xsl:if test="/list/@showing">
          <xsl:text></xsl:text>
          <span class="pagenav">
            <xsl:call-template name="prompt_more_results" />
          </span>
        </xsl:if>
        <xsl:if test="not(/list/@showing)">
          <xsl:text></xsl:text>
          <span class="pagenav">
            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
            <xsl:call-template name="prompt_result_count" />
          </span>
        </xsl:if>
      </div>
      <div class="page_buttons_seperator">
        <div>
        </div>
      </div>
    <div class="page_buttons">
    
      <div class="button">
        <div class="body_last">
          <xsl:call-template name="searchokbutton" />
        </div>
      </div>        
      <div class="button">      
        <div class="button_body">
          <xsl:call-template name="searchcancelbutton" />
        </div>
      </div>
      <div class="begin">
      </div>
    </div>
    </div>
  </xsl:template>
  
  <xsl:template name="searchcancelbutton">
    <a
      name="cancel"
      value="{$tooltip_cancel_search}"
      onclick="closeSearch();">Cancel</a>
  </xsl:template>
  
  <xsl:template name="searchokbutton">
    <a
      name="ok"
      value="{$tooltip_end_search}"
      onclick="dosubmit();">OK</a>
  </xsl:template>
  
  <xsl:template match="field">
    <td>
      <xsl:call-template name="writeCurrentField" />
    </td>
  </xsl:template>
  
  <xsl:template match="pages">
    <xsl:if test="page[@previous=&apos;true&apos;]">
      <a class="pagenav"
        title="{$tooltip_previous}{@currentpage-1}"
        href="javascript:browseTo({page[@previous=&apos;true&apos;]/@start});">
        <xsl:call-template name="prompt_previous" />
      </a>
      <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
    </xsl:if>
    
    <xsl:apply-templates select="page" />
    
    <xsl:if test="page[@next=&apos;true&apos;]">
      <a class="pagenav"
        title="{$tooltip_next}{@currentpage+1}"
        href="javascript:browseTo({page[@next=&apos;true&apos;]/@start});">
        <xsl:call-template name="prompt_next" />
      </a>
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="page">
    <a class="pagenav" title="{$tooltip_goto}{position()}" href="javascript:browseTo({@start});">
      <xsl:value-of select="@number" />
    </a>
    <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
    <xsl:text />
  </xsl:template>
  
  <xsl:template match="page[@current=&apos;true&apos;]">
    <span class="pagenav-current">
      <xsl:value-of select="@number" />
      <xsl:text></xsl:text>
    </span>
    <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
  </xsl:template>
</xsl:stylesheet>
