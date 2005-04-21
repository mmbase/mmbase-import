#rm -f *.html
#xsltproc  --param use.id.as.filename '1' --param root.filename 'editwizard-doc' ../../../eclipse.xsl editwizard-doc.xml
#xsltproc  --stringparam use.id.as.filename 1 --stringparam root.filename editwizard-doc ../../../docbook-xsl/eclipse/eclipse.xsl  editwizard-doc.xml
#xsltproc  ../../../eclipse.xsl editwizard-doc.xml


rm -rf out

doit(){
 mkdir -p out/$OUT
 cp $IN out/$OUT/documentation.xml
 cp style.css out/$OUT/
 cp -r `dirname $IN`/media out/$OUT/media
 (
 cd out/$OUT
 xsltproc \
 	--stringparam use.id.as.filename 1 \
 	--stringparam eclipse.plugin.name $OUT \
 	--stringparam eclipse.plugin.id $OUT \
 	--stringparam html.stylesheet style.css  \
 	--stringparam eclipse.plugin.provider MMBase.org \
	../../mmbase-docbook.xsl documentation.xml  
 )
}

IN=documentation/administrators/security-framework.xml
OUT=org.mmbase.documentation.security
doit

IN=documentation/administrators/cloud-security.xml
OUT=org.mmbase.documentation.security.cloud
doit

IN=documentation/administrators/context-security.xml
OUT=org.mmbase.documentation.security.context
doit

IN=documentation/administrators/configuration.xml
OUT=org.mmbase.documentation.configuration
doit

IN=documentation/backenddevelopers/storage/configuration.xml
OUT=org.mmbase.documentation.configuration.storage
doit

IN=documentation/administrators/install-tomcat.xml
OUT=org.mmbase.documentation.deploy.tomcat
doit

IN=documentation/documentation.xml
OUT=org.mmbase.documentation.about
doit

IN=documentation/glossary.xml
OUT=org.mmbase.documentation.glossary
doit

IN=documentation/backenddevelopers/codingstandards.xml
OUT=org.mmbase.documentation.codingstandards
doit

IN=documentation/backenddevelopers/custombuilderclass.xml
OUT=org.mmbase.documentation.extending
doit

IN=documentation/informationanalysts/builders.xml
OUT=org.mmbase.documentation.extending.builder
doit

IN=documentation/informationanalysts/relations.xml
OUT=org.mmbase.documentation.extending.relations
doit

IN=documentation/informationanalysts/applications.xml
OUT=org.mmbase.documentation.extending.mmapps1
doit


IN=documentation/backenddevelopers/dbsq.xml
OUT=org.mmbase.documentation.core.query
doit

IN=documentation/backenddevelopers/rmmci.xml
OUT=org.mmbase.documentation.remoting.rmmci
doit

IN=documentation/frontenddevelopers/xmlimporter/overview.xml
OUT=org.mmbase.documentation.remoting.xmlimporter
doit

IN=documentation/frontenddevelopers/dovexmlapi.xml
OUT=org.mmbase.documentation.remoting.dove
doit

IN=documentation/backenddevelopers/storage/index.xml
OUT=org.mmbase.documentation.storage
doit

IN=documentation/frontenddevelopers/editwizard/editwizard-doc.xml
OUT=org.mmbase.documentation.editwizard
doit

IN=documentation/frontenddevelopers/editwizard/editwizard-frontend.xml
OUT=org.mmbase.documentation.editwizard.frontend
doit

IN=documentation/frontenddevelopers/editwizard/editwizard-reference.xml
OUT=org.mmbase.documentation.editwizard.reference
mkdir -p out/$OUT
cp -r documentation/frontenddevelopers/editwizard/reference out/$OUT/reference
doit

IN=documentation/frontenddevelopers/taglib/include.xml
OUT=org.mmbase.documentation.taglib.templating
doit

IN=documentation/frontenddevelopers/taglib/taglib-tutorial.xml
OUT=org.mmbase.documentation.taglib.tutorial
doit

IN=documentation/general/architecture.xml
OUT=org.mmbase.documentation.architecture
doit

IN=documentation/modules/email.xml
OUT=org.mmbase.documentation.email
doit

#documentation/managers/mmbase4managers.xml
#documentation/mmbase.org/templates/configuration.xml
#documentation/mmbase.org/templates/functional-reference.xml
#documentation/mmbase.org/templates/index.xml
#documentation/mmbase.org/templates/installation.xml
#documentation/mmbase.org/templates/technical-reference.xml
#documentation/mmbase.org/community.xml
#documentation/mmbase.org/mmcmissionstatement.xml
#documentation/mmbase.org/projectproposal.xml
#documentation/modules/i18n.xml
#documentation/modules/images-and-attachments.xml

