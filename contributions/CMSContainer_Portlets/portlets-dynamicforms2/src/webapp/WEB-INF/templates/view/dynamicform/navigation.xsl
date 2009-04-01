<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="navigation">
		<xsl:for-each select="description">
			<p>
				<h4>
					<xsl:value-of disable-output-escaping="yes" select="@title"/>
				</h4>
				<xsl:value-of disable-output-escaping="yes" select="."/>
			</p>
		</xsl:for-each>
		<fieldset class="lastfieldset">
			<div class="spacer">
				<script type="text/javascript">
				function readonlysubmit(){
					document.forms['<xsl:value-of disable-output-escaping="yes" select="$NAMESPACE"/>forms'].submit();
					return false;
				}
				  function setstepvalue(stepvalue) {
					 document.getElementById('activeStep').value=stepvalue;
					 return true;
				  }
				  function backStepping() {
					 document.getElementById('backStep').value='true';
					 return false;
				  }
				  </script>
				<input type="hidden" name="activeStep" id="activeStep"/>
				<input type="hidden" name="editpath" id="editpath" value="{//formstep/@editpath}"/>
				<input type="hidden" name="backStep" id="backStep" value="false"/>
				<xsl:apply-templates select="navitem"/>
			</div>
		</fieldset>
	</xsl:template>
  
	<xsl:template match="navitem[@guitype='backbutton']">
		<span class="{@class}">
		  <xsl:choose>
		    <xsl:when test="@imageurl">
			<input name="{@title}" type="image" src="{@imageurl}" alt="{@title}">
				<xsl:attribute name="onclick">backStepping();setstepvalue('<xsl:value-of select="@step"/>');</xsl:attribute>
			</input>
			</xsl:when>
			<xsl:otherwise>
			<input name="{@title}" type="submit" value="{@title}" class="submit">
				<xsl:attribute name="onclick">backStepping();setstepvalue('<xsl:value-of select="@step"/>');</xsl:attribute>
			</input>
			</xsl:otherwise>
			</xsl:choose>
		</span>
	</xsl:template>

	<xsl:template match="navitem[not(@guitype) or @guitype='']">
		<span class="{@class}">
		  <xsl:choose>
		    <xsl:when test="@imageurl">
			<input name="{@title}" type="image" src="{@imageurl}" alt="{@title}">
				<xsl:attribute name="onclick">setstepvalue('<xsl:value-of select="@step"/>');</xsl:attribute>
			</input>
			</xsl:when>
			<xsl:otherwise>
			<input name="{@title}" type="submit" value="{@title}" class="submit">
				<xsl:attribute name="onclick">setstepvalue('<xsl:value-of select="@step"/>');</xsl:attribute>
			</input>
			</xsl:otherwise>
			</xsl:choose>
		</span>
	</xsl:template>
	
</xsl:stylesheet>
