
XSL=/usr/share/sgml/docbook/stylesheet/xsl/nwalsh/xhtml/docbook.xsl
xsltproc=/usr/bin/xsltproc

mmbase-config.html: mmbase-config.xml $(XSL) $(xsltproc)
	$(xsltproc) $(XSL) mmbase-config.xml > mmbase-config.html

$(XSL):
	sudo apt-get install docbook-xsl

$(xsltproc):
	sudo apt-get install xsltproc