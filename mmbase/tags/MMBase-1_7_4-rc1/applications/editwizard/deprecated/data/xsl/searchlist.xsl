<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl  ="http://www.w3.org/1999/XSL/Transform"
  xmlns:node ="org.mmbase.bridge.util.xml.NodeFunction"
>
  <!--
  searchlist.xls

  @since  MMBase-1.6
  @author Kars Veling
  @author Michiel Meeuwissen
  @version $Id: searchlist.xsl,v 1.1 2003-12-19 11:09:48 nico Exp $
  -->

  <xsl:import href="xsl/baselist.xsl" />

  <xsl:template match="pages">
      <xsl:choose>
        <xsl:when test="page[@previous='true']">
          <a class="pagenav" title="{$tooltip_previous}{@currentpage-1}"
             href="javascript:browseTo({page[@previous='true']/@start});"><xsl:call-template name="prompt_previous" /></a><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="prompt_previous" /><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
        </xsl:otherwise>
      </xsl:choose>

      <xsl:apply-templates select="page" />

      <xsl:choose>
        <xsl:when test="page[@next='true']">
          <a class="pagenav" title="{$tooltip_next}{@currentpage+1}"
             href="javascript:browseTo({page[@next='true']/@start});"><xsl:call-template name="prompt_next" /></a>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="prompt_next" />
        </xsl:otherwise>
      </xsl:choose>
  </xsl:template>

  <xsl:template match="page">
    <a class="pagenav" title="{$tooltip_goto}{position()}"
       href="javascript:browseTo({@start});" ><xsl:value-of select="position()" /></a><xsl:text></xsl:text>
  </xsl:template>


  <xsl:template match="page[@current='true']">
    <span class="pagenav-current"><xsl:value-of select="position()" /><xsl:text> </xsl:text></span>
  </xsl:template>


  <xsl:template name="form"> 
    <form>
      <div class="searchresult">
        <table class="searchresult"><!-- IE is too stupid to understand div.searchresult table -->
          <xsl:if test="not(object)">
            <tr>
              <td><xsl:call-template name="prompt_no_results" /></td>
            </tr>
          </xsl:if>
          <xsl:for-each select="object">
            <tr>
              <xsl:choose>
                <xsl:when test="(position() mod 2) = 0">
                  <xsl:attribute name="class">even</xsl:attribute>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:attribute name="class">odd</xsl:attribute>
                </xsl:otherwise>
              </xsl:choose>
              
              <td number="{@number}" onClick="doclick_search(this);" id="item_{@number}">
                <input type="checkbox" style="position:absolute; top:0; left:0; visibility:hidden;" name="{@number}" did="{@number}" id="cb_{@number}" />
                <xsl:choose>
                  <xsl:when test="@type='images'">
                    <xsl:value-of select="node:function($cloud, string(@number), 'gui()')" disable-output-escaping="yes" />
                    <b><xsl:value-of select="field[1]" /></b><br />
                    <xsl:value-of select="field[2]" /><br />
                  </xsl:when>
                  <xsl:when test="@type='audioparts'">
                    <a href="{$ew_context}/rastreams.db?{@number}" title="{$tooltip_audio}"><xsl:call-template name="prompt_audio" /></a>
                    <label for="cb_{@number}" style="cursor:hand;"><xsl:apply-templates select="field" /></label>
                  </xsl:when>
                  <xsl:when test="@type='videoparts'">
                    <a href="{$ew_context}/rmstreams.db?{@number}" title="{$tooltip_video}"><xsl:call-template name="prompt_video" /></a>
                    <label for="cb_{@number}" style="cursor:hand;"><xsl:apply-templates select="field" /></label>
                  </xsl:when>
                  <xsl:otherwise>
                    <label for="cb_{@number}" style="cursor:hand;"><xsl:apply-templates select="field" /></label>
                  </xsl:otherwise>
                </xsl:choose>
              </td>
            </tr>
          </xsl:for-each>
      </table>
      </div>
      <table class="pagenav">
        <tr>
          <td align="left" valign="top" colspan="2" style="background-color: white">
            
            <xsl:apply-templates select="pages" />
            <xsl:if test="/list/@showing">
              <xsl:text> </xsl:text> <span class="pagenav"><xsl:call-template name="prompt_more_results" /></span>
            </xsl:if>
            <xsl:if test="not(/list/@showing)">
              <xsl:text> </xsl:text> <span class="pagenav"><xsl:call-template name="prompt_result_count" /></span>
            </xsl:if>
            
          </td>
        </tr>
        <tr>
          <td align="left" valign="top">
            <input type="button" name="cancel" value="{$tooltip_cancel_search}" onclick="parent.removeModalIFrame();" style="width:199; height:24; background-color:buttonface;" />
          </td>
          <td align="right" valign="top">
            <input type="button" name="ok" value="{$tooltip_end_search}" onclick="dosubmit();" style="width:199; height:24; background-color:buttonface;" />
          </td>
        </tr>
      </table>
    </form>
  </xsl:template>
          
  <xsl:template match="list">
    <html>
      <head>
        <meta http-equiv="Content-Type"  content="text/html; charset=utf-8" />
        <title>Search Results</title>
        <link rel="stylesheet" type="text/css" href="../style/searchlist.css" />
        <xsl:call-template name="extrastyle" /> <!-- see base.xsl -->
      </head>
      <body onload="window.focus(); preselect(selected);">
        <script language="javascript">
        <xsl:text disable-output-escaping="yes">
        <![CDATA[
        <!--
         ]]>
        </xsl:text>
            parent.status = "<xsl:value-of select="tooltip_select_search" />";
            var listpage = "<xsl:value-of disable-output-escaping="yes" select="$listpage" />";
        <xsl:text disable-output-escaping="yes">
        <![CDATA[
            var searchtype = getParameter_general("type", "objects");
            var searchterm = getParameter_general("searchterm", "nothing");
            var cmd = getParameter_general("cmd", "");
            var selected = getParameter_general("selected", "");

            function preselect(didlist) {
                didlist = "|" + didlist + "|";
                var f = document.forms[0];
                for (var i=0; i<f.elements.length; i++) {
                    var e = f.elements[i];
                    if (e.type.toLowerCase() == "checkbox") {
                        if (didlist.indexOf("|" + e.getAttribute("did") + "|") > -1) doclick_search(document.getElementById("item_" + e.getAttribute("did")));
                    }
                }
            }

            function getParameter_general(name, defaultValue) {
                // finds a parameter in the search-string of the location (...?.=..&.=..&.=..)
                // if not found this function returns the specified defaultValue
                // the defaultValue parameter is not required
                var qa = unescape(document.location.search).substring(1, document.location.search.length).split("&");
                for (var i=0; i<qa.length; i++) {
                    if (qa[i].indexOf(name + "=") == 0) return qa[i].substring(name.length+1, qa[i].length);
                }
                return defaultValue;
            }

            function doclick_search(el) {
                var cb = document.getElementById("cb_" + el.getAttribute("number"));
                cb.checked = ! cb.checked;
                if(cb.checked) {
                   if (el.parentNode.className == "odd") { 
                      el.parentNode.className = "selected_odd";
                   } else {
                      el.parentNode.className = "selected_even";
                   }
                } else {
                   if (el.parentNode.className == "selected_odd") { 
                      el.parentNode.className = "odd";
                   } else {
                      el.parentNode.className = "even";
                   }
                }
            }

            function dosubmit() {
                selected = buildSelectedList();
                parent.doAdd(selected, cmd);
            }

            function buildSelectedList() {
                var s = selected + "|";
                var f = document.forms[0];
                for (var i=0; i<f.elements.length; i++) {
                    var e = f.elements[i];
                    if (e.type.toLowerCase() == "checkbox") {
                        if (e.checked) {
                            // Add it if it's not already in the list.
                            if (s.indexOf("|" + e.getAttribute("did") + "|") == -1) s += e.getAttribute("did") + "|";
                        } else {
                            // Remove it if it's not selected anymore.
                            var pos = s.indexOf("|" + e.did);
                            if (pos > -1) s = s.substring(0,pos) + s.substring(pos + e.did.length + 1);
                        }
                    }
                }
                return s.substring(0, s.length - 1);;
            }

            function browseTo(start) {
                selected = buildSelectedList();
                var href = listpage;
                 href += "&popupid=search&template=xsl/searchlist.xsl&start="+start+"&selected="+selected+"&cmd="+cmd;

                document.location.replace(href);
                return false;
            }
        // -->
        ]]>
        </xsl:text>
        </script>
        <xsl:call-template name="form" />
      </body>
    </html>
  </xsl:template>

  <xsl:template match="field">
    <xsl:value-of select="." disable-output-escaping="yes" /><xsl:text> </xsl:text>
  </xsl:template>



</xsl:stylesheet>

