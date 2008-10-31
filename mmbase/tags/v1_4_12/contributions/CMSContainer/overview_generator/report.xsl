<?xml version="1.0"?>
<xsl:transform
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:template match="cmscproject">
	<html>
	  <head>
	    <title>Report</title>
	  </head>
	  <body>
	    <p>
		<b><xsl:value-of select="name"/></b><br/>
		developer(s):
			<xsl:for-each select="maven/project/developers/developer/name">
				<xsl:value-of select="."/>;
			</xsl:for-each><br/>
		source:
			the <xsl:value-of select="source"/> from the <xsl:value-of select="name"/><br/>
		short description:
			<xsl:value-of select="maven/project/shortDescription"/><br/>
		<u>versions:</u><br/>
			<xsl:for-each select=".">
				* <b><xsl:value-of select="name"/>:</b> <xsl:value-of select="versions"/><br/>
			</xsl:for-each><br/>
		<u>dependencies:</u><br/>
			<xsl:for-each select="maven/project/dependencies/dependency">
				* <b><xsl:value-of select="artifactId | id"/>:</b> <xsl:value-of select="version"/><br/>
			</xsl:for-each><br/>
	    </p>
	    The subproject summary for each subproject:
	    <xsl:apply-templates select="subproject"/>
	  </body>
	</html>
</xsl:template>
<xsl:template match="subproject">
	<p> 
	    system name:<xsl:value-of select="maven/project/artifactId"/><br/>
	    developer(s): 
		<xsl:for-each select="maven/project/developers/developer/name">
			<xsl:value-of select="."/>;
		</xsl:for-each><br/>
		<br/>
	    project: <xsl:value-of select="../name"/><br/>
	    <b>used in :</b><br/>
	      <xsl:call-template name="findprjbydependency">
	        <xsl:with-param name="artifactid"><xsl:value-of select="maven/project/artifactId"/></xsl:with-param>
	      </xsl:call-template>
	      <xsl:call-template name="findprjbydependencysub">
	        <xsl:with-param name="artifactid"><xsl:value-of select="maven/project/artifactId"/></xsl:with-param>
	      </xsl:call-template>
	    Readme:<br/><xsl:value-of select="readme"/>
        </p>
</xsl:template>
<xsl:template name="findprjbydependency">
  <xsl:param name="artifactid"></xsl:param>
	<xsl:for-each select="//cmscproject/maven/project/dependencies/dependency[artifactId/text() = $artifactid]">
		  * <b><xsl:value-of select="../../../../name"/>:</b><xsl:value-of select="version"/><br/> 
	</xsl:for-each><br/>
</xsl:template>
<xsl:template name="findprjbydependencysub">
  <xsl:param name="artifactid"></xsl:param>
	<xsl:for-each select="//cmscproject/subproject/maven/project/dependencies/dependency[artifactId/text() = $artifactid]">
		  * <b><xsl:value-of select="../../../../name"/>:</b><xsl:value-of select="version"/><br/> 
	</xsl:for-each><br/>
</xsl:template>
</xsl:transform>