<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:include href="dynamicform/edit/formstep.xsl"/>
    <xsl:include href="dynamicform/readonly/formstep.xsl"/>

    <xsl:param name="URLCONTEXT">/</xsl:param>
    <xsl:param name="RENDERURL" />
    <xsl:param name="ACTIONURL" />
    <xsl:param name="NAMESPACE" />

	<xsl:template match="form">
	 
	<style type="text/css">
	  @import url('<xsl:value-of select="$URLCONTEXT"/>css/dynamicforms.css');
	</style>
	
		<xsl:apply-templates select="formstep[@active='true' ]"/>
	</xsl:template>
</xsl:stylesheet>
