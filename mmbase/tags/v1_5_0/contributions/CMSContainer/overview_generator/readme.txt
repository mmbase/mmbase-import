
Locate to  CMSContainer/overview_generator 
there are 3 approachs for generating report

The first is generating a jar including all dependencies;
   run it with the command "maven gen:single"
   locate to CMSContainer/overview_generator/target,we'll find a jar generated
   This jar file is executable,we can run it with "java -jar jarfilename configfilePath [workfolder] [outputpath]"

   configfilePath:  absolute path ,the file included urls of cvs and svn ; e.g.  D:/project/cmsc/CMSContainer/overview_generator/config.sample.properties
      workfolder :  the path that the source code,checked out , will be put . e.g. d:/
      outputpath :  the path used for gengerating report file.          

      e.g.  (windows)  java -jar cmsc-overview_generator-0.1.jar D:/project/cmsc/CMSContainer/overview_generator/config.sample.properties d:/ d:/

The Second ,generate a jar file ,excluding all dependencies,but all dependencies will be copy to the same folder,
   run it with the command "maven gen:multi"
   we'll find some jar files generated in directory CMSContainer/overview_generator/target

   wo can run it with "java -jar jarfilename configfilePath [workfolder] [outputpath]"

The last method is generate report using maven goal,the following is the step
 
1. config.sample.properties file is used to hold a list of source systems can either be cvs or svn
  e.g :
    #svn https://extranet.finalist.com/svn/nai/trunk trunk username password.
    svn http://svn.apache.org/repos/asf/tomcat/trunk/ trunk 123 123
    #cvs :pserver:guest@cvs.mmbase.org:/var/cvs contributions/CMSContainer guest guest
    cvs :pserver:guest@cvs.mmbase.org:/var/cvs contributions/CMSContainer_Demo guest guest
2. open project.properties ,mofidy the path, there are 3 key/value,
   e.g:
      maven.overview_generator.src = D:/project/cmsc/CMSContainer/overview_generator/config.sample.properties  #the  file configed at  step 2
      maven.overview_generator.dest = D:/project/cmsc/CMSContainer/overview_generator/dest     # the directory which  the source code  be checked out
      maven.overview_generator.reportFileLocation = D:/                     #the directory where report file is in 
   
3.   run it with the command "maven gen:mavenrun"
