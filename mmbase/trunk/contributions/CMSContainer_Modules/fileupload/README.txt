DESCRIPTION:
This module allows for uploading and managing large files to the application server. It automatically creates an object of type "file" in the database for each uploaded file. The file can than be attached to a regular content element using the included search_contentelement_posrel_file.xml wizard.

CONFIGURATION:
 - 

TODO:
 - Some text is not yet included in resourcebundles
 - A servlet to provide easy access to files


Key: fileupload.urlprefix
Value: /fileupload

Key: fileupload.storepath
Value: /home/tomcat/