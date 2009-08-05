As the cmsc core is convert to packaging with war, but the modules and portlets are not convert. so the mmbase-mobules of modules and portlets also be used in these archetypes.
If this solution is satisfy, we would convert modules and portlets to packaging with war too.

usage:
1. repeat maven1 to process the CMSC core,modules and portlets.
2. on the path of contributions\CMSContainer\cmsc input command: mvn clean install
   which will packaging CMSC core to war.
3. the important is install archetypes in your repos.
   on each of the three archetypes, just like war-community-archetype, 
   input command: mvn install
   the archetype will install in your local repos.
4. generate from archetypes
   select or create a directory which not have pom.xml
   input command: mvn archetype:generate -DarchetypeCatalog=local
   then you will select the archetype and input some info follow the prompt.
5. after you create project from archetype , you can package and deploy them by the command    of: mvn package
   especially,the war-livestaging archetype is multiple modules, war-live and war-staging      are dependence war ,so you should use command : mvn install to package them.

