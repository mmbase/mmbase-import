<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl  ="http://www.w3.org/1999/XSL/Transform"
>
  <!--
  exception.xsl

  @since  MMBase-1.6.4
  @author Pierre van Rooden
  @version $Id: exception.xsl,v 1.1 2003-12-19 11:09:53 nico Exp $
  -->

  <xsl:import href="xsl/base.xsl" />   

  <xsl:template name="errortitle">
    DON'T PANIC - But Something Went Wrong
  </xsl:template>
    
  <xsl:template name="errormessage">
    <h2>DON'T PANIC!</h2>
    <h3>But Something Went Wrong</h3>
    <p>
      An error occurred in the editwizards. This may be caused because you have insufficient rigths to make changes,
      because your edit-session expired, or because the editwizard definition has a bug.
    </p>
    <p>
      When reporting the error, pass the error message (in red, below) and if so requested the expanded message to the
      responsible party.
    </p>
  </xsl:template>
  
  <xsl:template match="error">
    <html>
      <head>
        <title><xsl:call-template name="errortitle" /></title>
      </head>
      <body>
        <xsl:call-template name="errormessage" />
          <xsl:if test="$referrer!=''">
            <p><a href="{$rootreferrer}">Return Home.</a></p>
          </xsl:if>
          <h3 style="color:#ff0000;">Error: <xsl:value-of disable-output-escaping="yes" select="exception" /></h3>
          <h3>Expanded error:</h3>
          <small><pre>
          <xsl:value-of select="stacktrace" />
          </pre></small>
        </body>
      </html>
  </xsl:template>

</xsl:stylesheet>

