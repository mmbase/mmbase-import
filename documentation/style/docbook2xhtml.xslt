<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:doc="http://nwalsh.com/xsl/documentation/1.0" exclude-result-prefixes="doc" version="1.0">

	<xsl:import href="[BASESTYLESHEET]" />

	<xsl:output
		method="xml"
		encoding="UTF-8"
		indent="no"
		doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
		doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" />


    <!-- our own parameters, set by org.mmbase.util.XSLTransformer -->
	<xsl:param name="root">./</xsl:param>
    <xsl:param name="filename" />


    <!-- 
      ============================
      Override some things of nwalsh 
    -->


	<xsl:param name="toc.section.depth">5</xsl:param>

    <!-- some chunking options: -->
    <xsl:param name="base.dir" select="'./'" />

      <!-- rather ridiculous hack with substrings, 
           but root.filename may not be '' of nwalsh, and this ensures unique filenames

           if not something of the root.filename is taken to the 'html.ext', then there a good chance
           that there will be more then one ar01s05.html e.g. or giving an 'id' must be obligated.
        -->
    <xsl:param name="root.filename" select="substring($filename, 1, 1)" /> <!-- aaarch, starting at 1 and not at 0!! -->
    <xsl:param name="html.ext" select="concat(substring($filename, 2), '.html')" />
    <xsl:param name="use.id.as.filename" select="'1'"/>
    <xsl:param name="chunk.quietly" select="1" /> <!-- set to 0 if you want to know what's happening -->


    <!-- use a custom css -->
	<xsl:param name="html.stylesheet"><xsl:value-of select="$root" />style/documentation.css</xsl:param>


    <!-- this is for the floating toc on the right -->
	<xsl:template name="user.header.content">
		<div class="navigation">
          <img src="{$root}style/logo.png" alt="mmbase logo" />
			<h2><xsl:value-of select="//*/title" /></h2>
			<xsl:for-each select="//section|//sect1">
				<xsl:if test="not(ancestor::section)">
					<p>
						<a>
                          <xsl:attribute name="href">
                            <xsl:call-template name="href.target">
                              <xsl:with-param name="object" select="."/>
                            </xsl:call-template>
                          </xsl:attribute>
							<xsl:value-of select="title" />
						</a>
					</p>
				</xsl:if>
			</xsl:for-each>
			<hr />
			<p><a href="http://www.mmbase.org">MMBase</a></p>
            <xsl:if test="not(($root = './') and ($filename = 'index'))"> 
              <p><a href="{$root}index.html">home</a></p>
            </xsl:if>
		</div>
	</xsl:template>

    <!-- MMBase specific footer on every page -->
	<xsl:template name="user.footer.content">
		<hr />
		<p>
			This is part of the <a href="http://www.mmbase.org">MMBase</a> documentation.
		</p>
	</xsl:template>

</xsl:stylesheet>