DESCRIPTION
This portlet integrates JForum into CMSC.

INSTALL GUIDE

To install JForum Portlet, there are a few steps you should do.

Step 1 :
Add the dependencies below to your maven project.xml. 
Also check for duplicated dependencies and removed them.

<!-- Hibernate dependencies (for Community Module) -->
   <dependency>
      <groupId>asm</groupId>
      <artifactId>asm</artifactId>
      <version>1.5.3</version>
      <type>jar</type>
      <properties>
         <war.bundle>${war.bundle}</war.bundle>
      </properties>
   </dependency>
   <dependency>
      <groupId>antlr</groupId>
      <artifactId>antlr</artifactId>
      <version>2.7.6</version>
      <type>jar</type>
      <properties>
         <war.bundle>${war.bundle}</war.bundle>
      </properties>
   </dependency>
   <dependency>
      <groupId>cglib</groupId>
      <artifactId>cglib</artifactId>
      <version>2.1.3</version>
      <type>jar</type>
      <properties>
         <war.bundle>${war.bundle}</war.bundle>
      </properties>
   </dependency>
   <dependency>
      <groupId>dom4j</groupId>
      <artifactId>dom4j</artifactId>
      <version>1.6.1</version>
      <type>jar</type>
      <properties>
         <war.bundle>${war.bundle}</war.bundle>
      </properties>
   </dependency>
   <dependency>
      <groupId>hibernate</groupId>
      <artifactId>ejb3-persistence</artifactId>
      <version>3.0</version>
      <type>jar</type>
      <properties>
         <war.bundle>${war.bundle}</war.bundle>
      </properties>
   </dependency>
   <dependency>
      <groupId>hibernate</groupId>
      <artifactId>hibernate</artifactId>
      <version>3.2.5.GA</version>
      <type>jar</type>
      <properties>
         <war.bundle>${war.bundle}</war.bundle>
      </properties>
   </dependency>
   <dependency>
      <groupId>hibernate</groupId>
      <artifactId>hibernate-entitymanager</artifactId>
      <version>3.3.1.GA</version>
      <type>jar</type>
      <properties>
         <war.bundle>${war.bundle}</war.bundle>
      </properties>
   </dependency>
   <dependency>
      <groupId>hibernate</groupId>
      <artifactId>hibernate-annotations</artifactId>
      <version>3.3.0.GA</version>
      <type>jar</type>
      <properties>
         <war.bundle>${war.bundle}</war.bundle>
      </properties>
   </dependency>
   <dependency>
      <groupId>hibernate</groupId>
      <artifactId>hibernate-commons-annotations</artifactId>
      <version>3.0.0.GA</version>
      <type>jar</type>
      <properties>
         <war.bundle>${war.bundle}</war.bundle>
      </properties>
   </dependency>
   <dependency>
      <groupId>javax.transaction</groupId>
      <artifactId>jta</artifactId>
      <version>1.0.1B</version>
      <type>jar</type>
      <properties>
         <war.bundle>${war.bundle}</war.bundle>
      </properties>
   </dependency>

   <!-- Spring dependencies (for Community Module) -->
   <dependency>
      <groupId>springframework</groupId>
      <artifactId>spring</artifactId>
      <version>2.5.1</version>
      <type>jar</type>
      <properties>
         <war.bundle>${war.bundle}</war.bundle>
      </properties>
   </dependency>
   <dependency>
      <groupId>springframework</groupId>
      <artifactId>spring-test</artifactId>
      <version>2.5.1</version>
      <type>jar</type>
      <properties>
         <war.bundle>${war.bundle}</war.bundle>
      </properties>
   </dependency>
   <dependency>
      <groupId>springframework</groupId>
      <artifactId>spring-webmvc-struts</artifactId>
      <version>2.5.1</version>
      <type>jar</type>
      <properties>
         <war.bundle>${war.bundle}</war.bundle>
      </properties>
   </dependency>

   <!-- Acegi Security dependencies (for Community Module) -->
   <dependency>
      <groupId>acegisecurity</groupId>
      <artifactId>acegi-security</artifactId>
      <version>1.0.6</version>
      <type>jar</type>
      <properties>
         <war.bundle>${war.bundle}</war.bundle>
      </properties>
   </dependency>


     <!--begin dependency by JForum portlet. -->

     <dependency>
         <groupId>freemarker</groupId>
         <artifactId>freemarker</artifactId>
         <version>2.3.13</version>
         <type>jar</type>
         <properties>
             <war.bundle>${war.bundle}</war.bundle>
         </properties>
     </dependency>
     <dependency>
         <groupId>htmlparser</groupId>
         <artifactId>htmlparser</artifactId>
         <version>1.5</version>
         <type>jar</type>
         <properties>
            <war.bundle>${war.bundle}</war.bundle>
         </properties>
     </dependency>

     <dependency>
         <groupId>quartz</groupId>
         <artifactId>quartz</artifactId>
         <version>1.5.1</version>
         <type>jar</type>
         <properties>
             <war.bundle>${war.bundle}</war.bundle>
         </properties>
     </dependency>
     <dependency>
         <groupId>c3p0</groupId>
         <artifactId>c3p0</artifactId>
         <version>0.9.1</version>
         <type>jar</type>
         <properties>
             <war.bundle>${war.bundle}</war.bundle>
         </properties>
     </dependency>
     <dependency>
         <groupId>com.octo.captcha</groupId>
         <artifactId>jcaptcha-all</artifactId>
         <version>1.0-RC-2.0.1</version>
         <type>jar</type>
         <properties>
             <war.bundle>${war.bundle}</war.bundle>
         </properties>
     </dependency>
     <dependency>
         <groupId>lucene</groupId>
         <artifactId>lucene-core</artifactId>
         <version>2.3.2</version>
         <type>jar</type>
         <properties>
             <war.bundle>${war.bundle}</war.bundle>
         </properties>
     </dependency>
     <dependency>
         <groupId>lucene</groupId>
         <artifactId>lucene-highlighter</artifactId>
         <version>2.3.2</version>
         <type>jar</type>
         <properties>
             <war.bundle>${war.bundle}</war.bundle>
         </properties>
     </dependency>
     <dependency>
         <groupId>lucene</groupId>
         <artifactId>lucene-analyzers</artifactId>
         <version>2.3.2</version>
         <type>jar</type>
         <properties>
             <war.bundle>${war.bundle}</war.bundle>
         </properties>
     </dependency>
    <dependency>
         <groupId>jforum</groupId>
         <artifactId>jforum</artifactId>
         <version>2.1.8</version>
         <type>jar</type>
        <properties>
             <war.bundle>${war.bundle}</war.bundle>
        </properties>
     </dependency>
     <dependency>
         <groupId>javamail</groupId>
         <artifactId>mail</artifactId>
         <version>1.4</version>
         <type>jar</type>
        <properties>
             <war.bundle>${war.bundle}</war.bundle>
        </properties>
     </dependency>
     <dependency>
         <groupId>concurrent</groupId>
         <artifactId>concurrent</artifactId>
         <version>1.3.2</version>
         <type>jar</type>
        <properties>
             <war.bundle>${war.bundle}</war.bundle>
        </properties>
     </dependency>
     
   <!-- jboss -->
   <dependency>
      <groupId>jboss</groupId>
      <artifactId>jboss-cache</artifactId>
      <version>1.2.2</version>
      <type>jar</type>
      <properties>
         <war.bundle>${war.bundle}</war.bundle>
      </properties>
   </dependency>
   <dependency>
      <groupId>jboss</groupId>
      <artifactId>jboss-common</artifactId>
      <version>4.0.2</version>
      <type>jar</type>
      <properties>
         <war.bundle>${war.bundle}</war.bundle>
      </properties>
   </dependency>
   <dependency>
      <groupId>jboss</groupId>
      <artifactId>jboss-j2ee</artifactId>
      <version>4.0.2</version>
      <type>jar</type>
      <properties>
         <war.bundle>${war.bundle}</war.bundle>
      </properties>
   </dependency>
   <dependency>
      <groupId>jboss</groupId>
      <artifactId>jboss-jmx</artifactId>
      <version>4.0.2</version>
      <type>jar</type>
      <properties>
         <war.bundle>${war.bundle}</war.bundle>
      </properties>
   </dependency>
   <dependency>
      <groupId>jboss</groupId>
      <artifactId>jboss-minimal</artifactId>
      <version>4.0.2</version>
      <type>jar</type>
      <properties>
         <war.bundle>${war.bundle}</war.bundle>
      </properties>
   </dependency>
   <dependency>
      <groupId>jboss</groupId>
      <artifactId>jboss-system</artifactId>
      <version>4.0.2</version>
      <type>jar</type>
      <properties>
         <war.bundle>${war.bundle}</war.bundle>
      </properties>
   </dependency>
   <dependency>
      <groupId>dwr</groupId>
      <artifactId>dwr</artifactId>
      <version>1.1.3</version>
      <type>jar</type>
      <properties>
         <war.bundle>${war.bundle}</war.bundle>
      </properties>
   </dependency>
   <dependency>
      <groupId>jgroups</groupId>
      <artifactId>jgroups-all</artifactId>
      <version>2.2.9.1</version>
      <type>jar</type>
      <properties>
         <war.bundle>${war.bundle}</war.bundle>
      </properties>
   </dependency>
   <!--end dependency by JForum portlet.-->


Step 2: Add below elements to web.xml

   <listener>
      <listener-class>net.jforum.ForumSessionListener</listener-class>
   </listener>

   <filter>
      <filter-name>clickstream-jforum</filter-name>
      <filter-class>net.jforum.util.legacy.clickstream.ClickstreamFilter</filter-class>
   </filter>
   <filter-mapping>
      <filter-name>clickstream-jforum</filter-name>
      <url-pattern>*.page</url-pattern>
   </filter-mapping>
   <!-- JForum Controller -->
   <servlet>
      <servlet-name>jforum</servlet-name>
      <servlet-class>net.jforum.JForum</servlet-class>
      <init-param>
         <param-name>development</param-name>
         <param-value>true</param-value>
      </init-param>
   </servlet>

   <servlet-mapping>
      <servlet-name>jforum</servlet-name>
      <url-pattern>*.page</url-pattern>
   </servlet-mapping>


Step 3  : Configure DataSouce

   Add DataSouce in the context xml file,e.g.: the name should be "jdbc/jforum",create database "jforum"

    <Resource name="jdbc/jforum" auth="Container" type="javax.sql.DataSource"
      removeAbandoned="true" 
      removeAbandonedTimeout="60"
      logAbandoned="true" 
      maxActive="10"
      maxIdle="1" 
      maxWait="10000" 
      username="root" 
      password="1234"
      driverClassName="com.mysql.jdbc.Driver" 
      url="jdbc:mysql://localhost:3306/jforum" />


Step 4: If you like to use Single Sign On (SSO)
   - Enable the portlet-login in the portlet
   - configure it into a view at a page


Step 5: If you run CMSc application in the Staging/Live mode.
5.1. Add a DataSource in both Staging and Live context.xml files. They are equal.
  <Resource name="jdbc/jforum" auth="Container" type="javax.sql.DataSource"
      removeAbandoned="true" 
      removeAbandonedTimeout="60"
      logAbandoned="true" 
      maxActive="10"
      maxIdle="1" 
      maxWait="10000" 
      username="root" 
      password="1234"
      driverClassName="com.mysql.jdbc.Driver"
      factory="org.apache.commons.dbcp.BasicDataSourceFactory"
      url="jdbc:mysql://localhost:3306/jforum" />

      
5.2 Add a property named "system.stagingpath" in live. The value is the url of staging 
   like http://localhost:8080/cmsc-demo-staging
   
5.3 Check the WEB-INF/config/SystemGlobals.properties, be sure that 
 database.connection.implementation is "net.jforum.DataSourceConnection", 
 and not "net.jforum.PooledConnection" (without the quotes)
 
5.4 Deploy it. If successful, it will create a new file jforum-custom.conf in WEB-INF/config
 It should have read/write access to that directory.

