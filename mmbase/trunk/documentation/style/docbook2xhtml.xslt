<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:doc="http://nwalsh.com/xsl/documentation/1.0" exclude-result-prefixes="doc" version="1.0">

  <xsl:import href="[BASESTYLESHEET]" />
  <xsl:output 
    method="xml"
    encoding="UTF-8" 
    indent="no" 
    doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" 
    doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" />

  <xsl:template name="user.footer.content">
    <hr />
      <p>
        This is part of the <a href="http://www.mmbase.org">MMBase</a> documentation.
    </p>
  </xsl:template>
</xsl:stylesheet>