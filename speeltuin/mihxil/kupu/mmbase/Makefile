# $Id: Makefile,v 1.1 2005-06-08 17:32:01 michiel Exp $


KUPU_HOME=..
XSLTPROC = /usr/bin/env xsltproc

XSL_DEBUG = --param debug true\(\)
XSLTPROC_PARAMS = --nonet --novalid --xinclude
XSL_FILE=$(KUPU_HOME)/make.xsl
XSLJSPX_FILE=$(KUPU_HOME)/make-jspx.xsl
I18N=$(KUPU_HOME)/kupu-i18n.jar

all: index.jspx


index.jspx: dist-mmbase.kupu *.kupu $(KUPU_HOME)/default/*.kupu $(XSLJSPX_FILE) $(XSL_FILE) $(I18N)
	$(XSLTPROC) $(XSLTPROC_PARAMS) -o $@  $(XSLJSPX_FILE) dist-mmbase.kupu

$(I18N):
	@$(MAKE) -C $(KUPU_HOME) kupu-i18n.jar	

clean:
	rm -f index.jspx
	@$(MAKE) -C $(KUPU_HOME) clean
