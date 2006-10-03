<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:output method="html"
                version="1.0"
                encoding="utf-8"
                omit-xml-declaration="no"
                standalone="no"
                doctype-public="-//W3C//DTD XHTML 1.0 Transitional//"
                doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
                indent="yes"
                media-type="mimetype"
        />

  <xsl:param name="session_byte_size">(undetermined)</xsl:param>

<xsl:template match="*">
<div style="padding-left:16px;">
    <span class="nodename">&lt;<xsl:value-of select="name()" /></span> <xsl:apply-templates select="@*" />
    <xsl:choose>

        <xsl:when test="not(*) and text()=''">
            <span class="nodename"> /&gt;</span>
        </xsl:when>

        <xsl:otherwise>
            <span class="nodename">&gt;</span>
                <xsl:apply-templates select="*|text()" />
            <span class="nodename">&lt;/<xsl:value-of select="name()" />&gt;</span><br />
        </xsl:otherwise>

    </xsl:choose>
    </div>
</xsl:template>

<xsl:template match="@*">&#160;<xsl:value-of select="name()" />="<span class="attrvalue"><xsl:value-of select="." /></span>"</xsl:template>

<xsl:template match="text()"><span class="text"><xsl:value-of select="." /></span></xsl:template>


<xsl:template match="/debugdata">
    <html>
    <head>
      <title>Editwizard session inspector (debugging)</title>
        <style type="text/css">
            .tab { cursor:hand; color:green; }
            .tab_hot { cursor:hand; color:green; font-weight:bold; }
            .panel_visible {position:absolute; visibility:visible; }
            .panel_hidden {position:absolute; visibility:hidden; }
            .nodename	{ color:blue; }
            .attrvalue	{ color:navy; }
            .text		{ color:black; }
            .debugname	{ color:silver; }
        </style>
    </head>

    <script language="javascript" src="../javascript/tools.js" >
    </script>
    <script language="javascript">
       var selectedTab=0;

       function init() {
          selectedTab = 0;
          var initTab = readCookie_general("selectedTab", 1);
          if (initTab == 0) { initTab = 1; }
          changeVisibility(initTab);
       }

       function changeVisibility(tabno) {
            var obj;


            if (selectedTab > 0) {
                obj = document.getElementById("tab" + selectedTab);
                obj.className = "tab";

                // set object to text-body and hide it
                obj = document.getElementById("panel" + selectedTab);
                obj.className = "panel_hidden";
            }

            if (tabno != selectedTab) {
                // make tabno hot

                obj = document.getElementById("tab" + tabno);
                if (obj) {
                    obj.className = "tab_hot";
                }

                // set object to text-body and show it
                obj = document.getElementById("panel" + tabno);
                    if (obj) {
                        obj.className = "panel_visible";
                        selectedTab = tabno;
                    }
            }
            else {
                selectedTab = 0
            }
            writeCookie_general("selectedTab", selectedTab)
       }
    </script>

    <body style="font-family:verdana;font-size:8pt;" onload="init()">

    <span id="Wizard_{@id}" class="debugwizard">

    <xsl:for-each select="*[@debugname]">
           <span class="tab" id="tab{position()}" onclick="changeVisibility({position()});"><xsl:value-of select="name()" /> <span class="debugname">(<xsl:value-of select="@debugname" />)</span></span> |
    </xsl:for-each><br /><br />

    <xsl:for-each select="*[@debugname]">
           <div id="panel{position()}" class="panel_hidden" style="border-style:solid;border-width:1">
            <xsl:apply-templates select="." />
        </div>
    </xsl:for-each>

    </span>
    Size of session: <xsl:value-of select="$session_byte_size" /> bytes (approximately)<br />
    </body>
    </html>
</xsl:template>


</xsl:stylesheet>
