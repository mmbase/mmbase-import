DESCRIPTION:
This module allows for uploading and managing large files to the application server. It automatically creates an object of type "file" in the database for each uploaded file. The file can than be attached to a regular content element using the included search_contentelement_posrel_file.xml wizard.



CONFIGURATION:
The module will add a File upload configuration page to the modules section of the CMSc. On this page you can edit the settings for the module. You should adjust those settings to your CMSc project. Available settings are:

Key: fileupload.urlprefix
Example value: /fileupload
Description: 

Key: fileupload.storepath
Example value: /home/tomcat/
Description: The directory on the server in which the uploaded files are stored. Ensure that the application server has the proper rights on this folder to read/write/delete files.

Key: fileupload.allowedmimetypes
Example value: text/html,plain/text
Description: A comma seperated list of MIME-types that can be uploaded.



TODO:
 - Some text is not yet included in resourcebundles
 - A servlet to provide easy access to files