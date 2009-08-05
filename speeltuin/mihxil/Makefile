# Buildfile to generate the NOS specific mmbase-taglib.tld (and .html)
# Michiel Meeuwissen

MMBASESRC=/home/michiel/mmbase/src/org/mmbase/bridge/jsp/taglib

MMBASEAPP=/home/michiel/mmbase/build/mmbase/mmbase-webapp
MMBASEWEB-INF=${MMBASEAPP}/WEB-INF
MMBASEDOC=${MMBASEAPP}/mmdocs

XML2TLD=${MMBASESRC}/xml2tld.xslt
XML2HTML=${MMBASESRC}/xml2html.xslt
XML2JSP=${MMBASESRC}/xml2jsp.xslt
TAGLIBXML=${MMBASESRC}/mmbase-taglib.xml
TAGLIBTLD=${MMBASEWEB-INF}/mmbase-taglib.tld
TAGLIBHTML=${MMBASEAPP}/mmdocs/mmbase-taglib.html
TAGLIBJSP=${MMBASEBUILD}/mmbase-taglib.jsp

mmbase-taglib-omroep.xml: ${XML2TLD} ${XML2HTML} ${TAGLIBXML} mmbase2omroep.xslt Makefile ${MMBASEDOC} ${MMBASEWEB-INF}
	xsltproc mmbase2omroep.xslt ${TAGLIBXML} > mmbase-taglib-omroep.xml
	#xsltproc xml2tld.xslt mmbase-taglib.xml > ../../../../../../build/mmbase/web/mmbase-taglib.tld
	#xsltproc xml2html.xslt mmbase-taglib.xml > ../../../../../../build/mmbase/web/mmbase-taglib.html
	xsltproc ${XML2TLD}  mmbase-taglib-omroep.xml > ${TAGLIBTLD}
	#xsltproc --param basedir "'${MMBASEBUILD}/'" ${XML2HTML} mmbase-taglib-omroep.xml > ${TAGLIBHTML}
	#xsltproc ${XML2JSP} mmbase-taglib-omroep.xml > ${TAGLIBJSP}

simple: ${XML2TLD} ${XML2HTML} ${TAGLIBXML} Makefile ${MMBASEDOC}/taglib ${MMBASEWEB-INF}
	xsltproc ${XML2TLD} ${TAGLIBXML} > ${TAGLIBTLD}
	xsltproc --param basedir "'${MMBASEDOC}'" ${XML2HTML} ${TAGLIBXML} > ${TAGLIBHTML}

xalan:  ${XML2HTML} ${TAGLIBXML} Makefile ${MMBASEDOC} ${MMBASEWEB-INF}
	java org.apache.xalan.xslt.Process -PARAM basedir "${MMBASEBUILD}/" -XSL ${XML2HTML} -IN file:///${TAGLIBXML}  -OUT ${TAGLIBHTML}
	#java org.apache.xalan.xslt.Process -PARAM basedir "${MMBASEBUILD}/" -XSL test.xslt -IN ${TAGLIBXML}  > ${TAGLIBHTML}


${MMBASEDOC}:
	mkdir -p ${MMBASEDOC}

${MMBASEDOC}/taglib:
	mkdir -p ${MMBASEDOC}/taglib

${MMBASEWEB-INF}:
	mkdir -p ${MMBASEWEB-INF}
