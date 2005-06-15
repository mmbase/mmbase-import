# $Id: Makefile,v 1.5 2005-06-15 06:47:14 michiel Exp $


KUPU_HOME=..
XSLTPROC = /usr/bin/env xsltproc

XSL_DEBUG = --param debug true\(\)
XSLTPROC_PARAMS = --nonet --novalid --xinclude
XSL_FILE=$(KUPU_HOME)/make.xsl
XSLJSPX_FILE=$(KUPU_HOME)/make-jspx.xsl
I18N=$(KUPU_HOME)/kupu-i18n.jar

LANGS=nl de fr it eo
#type in i18n: msginit -l <locale> -i messages.po


all: index.jspx


index.jspx: mmbase-kupu-i18n.jar dist-mmbase.kupu *.kupu $(KUPU_HOME)/default/*.kupu $(XSLJSPX_FILE) $(XSL_FILE) $(I18N)
	$(XSLTPROC) $(XSLTPROC_PARAMS) -o $@  $(XSLJSPX_FILE) dist-mmbase.kupu

$(I18N):
	@$(MAKE) -C $(KUPU_HOME) kupu-i18n.jar	

.PHONY:
messages: *.js
	mkdir -p i18n
	xgettext -j --from-code=UTF-8 -L java -k_ -p i18n *.js 
	for LOCALE in $(LANGS) ; do \
	   msgmerge --update --backup=off i18n/$${LOCALE}.po i18n/messages.po; \
	done
	msgen i18n/messages.po > i18n/en.po

mmbase-kupu-i18n.jar: i18n/*.po
	mkdir -p resourcebundle
	for LOCALE in $(LANGS) ; do \
	    msgfmt --java2  -D . -d resourcebundle -r org.mmbase.kupu.Messages -l $${LOCALE} i18n/$${LOCALE}.po; \
	done
	jar cf $@ -C resourcebundle org

clean:
	rm -f index.jspx
	rm -f mmbase-kupu-i18n.jar
	rm -rf resourcebundle
	@$(MAKE) -C $(KUPU_HOME) clean
