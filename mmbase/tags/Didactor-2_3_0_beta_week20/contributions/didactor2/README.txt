DIDACTOR 2.3

This is the Didactor 2.3 distribution, released on .....

There are two ways to build the software: building it in this directory
based on the 'build.xml' file, or creating a custom build including custom
templates and components. These two processes will be described below.

Note: use Ant 1.6.5 or higher!

=== NORMAL BUILD ===

To build the software, do the following:
- inspect 'build.properties' and include only the components you need
- edit 'configure.properties' to set your machine configuration information

Run 'ant war', deploy the generated .war file in Tomcat

=== CUSTOM BUILD ===

1.  Create a new directory, and copy the 'build-custom.xml' file to this
directory, and rename it to 'build.xml'.

2.  Edit the 'build.xml' file, and replace the 'didactor2.zip.url' property
value with the location of the didactor source zipfile.

3.  Create a 'src/' subdirectory, and place the extra components you have
in this directory. Make sure you use the didactor standard for directories,
so that the build process can include your components.

4. Create a 'providers/' subdirectory, and place the extra templates you
have in this directory. Make sure you use the didactor standard for 
directories, so that the build process can include your components.

5. Create a 'build.properties' file that includes includes the components
you want to compile (both Didactor components and your custom ones), and
the providers you want to include.
Also include all the machine configuration options. You can use the 
provided 'build.properties' and 'configuration.properties' as a skeleton.

An example filesystem layout for a custom build would be:

/build.xml (this is the copied 'build-custom.xml' from the source zip)
/build.properties (containing a line 'components=core,mycomponent', and a line 'providers=myprovider)
/providers/myprovider/webinf/config/translations/core.nl_myprovider.properties (some custom translations)
/providers/myprovider/templates/css/base.css (custom CSS layout)
/src/mycomponent/config/application/DidactorMycomp.xml
/src/mycomponent/config/application/DidactorMycomp/components.xml
/src/mycomponent/config/components/mycomp.xml
/src/mycomponent/java/nl/mycompany/didactor/components/MyComp.java

6. Execute 'ant war' to build a complete war file.
