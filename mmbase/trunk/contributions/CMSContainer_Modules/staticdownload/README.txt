DESCRIPTION:
This module uses the WGET tool zip download the (live) site and offer it to the user in a zip archive
(Has been included in the 1.3 release of the CMSContainer (modules), after migrating to 1.3, use the CMSC module and
not this one anymore)

CONFIGURATION:

 The wget module has 5 parameters:

 
Download URL 	 The URL from which the created zips can be downloaded. This is webreachable URL to the path specified in the "Store path"
 Live URL	 This is the URL from which the website is downloaded. (e.g.: the live URL of the website)
 Store Path	 The path to a directory on the harddisk of the website on which the created zips are stored, this path should be made webreachable and entered in the "Download URL" setting.
 Temp Path	 The temp path, used to download the website to, this can be a (subdirectory of) the tomcat temp directory
 
 Webapp Name The name of  curren web application , if there is no one it will be blank
 WGet Path	 The path to the wget executable, if wget is on the system path, "wget" will do.


TODO:
\
