This is the default mmbase 'datadir'. It can and will be used by MMBase code which for some reason
needs files to be written, and those files are not to be considered temporary.

E.g. if you configure the database with 'blobs on disk' those blobs will on default be stored in this
directory.

The location of this directory can be changed with the 'datadir' property of the mmbase module. So
you could change it in config/modules/mmbaseroot.xml, or, you could e.g. add this to your context
xml:
  <Environment name="mmbase/mmbaseroot/datadir" value="${catalina.base}/data"   type="java.lang.String" />


MMBase will also instruct os-cache to use this directory (if oscache is availabie, and it was not
specified in oscache.properties).

You should make sure that this directory is writeable for the application server.
