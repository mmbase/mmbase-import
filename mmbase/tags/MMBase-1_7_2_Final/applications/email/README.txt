Email application allows you to send emails from mmbase, it can be used in several ways and can mail to one several or groups of users. It also allows more complex mails that as multipart mail's and dynamicly generated mails.

web-app = the web-app you installed mmbase-webapp in for example tomcat/webapps/ROOT or tomcat/webapps/mmbase-webbapp for tomcat

How to install:

build the applications with : ant email (from the applications dir)

copy config/modules/* to your web-app/WEB-INF/config/modules/

copy config/builders/* to your web-app/WEB-INF/config/builders/

copy build/mmbase-email.jar to your web-app/WEB-INF/lib/

Install the examples in a place you like :

copy examples/* to our web-app/emailexamples/ (for example)

setup your mailhosts in your application server for example
in tomcat do (in this example its the ROOT app):

        <!-- Tomcat Root Context -->
          <Context path="" docBase="ROOT" debug="0">

          <Resource name="mail/Session" auth="Container"
                    type="javax.mail.Session"/>
          <ResourceParams name="mail/Session">
            <parameter>
              <name>mail.smtp.host</name>
              <value>smtp.xs4all.nl</value>
            </parameter>
          </ResourceParams>
          <ResourceLink name="linkToGlobalResource"
                    global="simpleValue"
                    type="java.lang.Integer"/>
        </Context>

restart your application server or webapp.

Now you can use the examples found you installed !!

**** DON'T FORGET TO CHANGE THE TO AND FROM IN EACH EXAMPLE***

at best i will get more mail, but probably your isp will block
your mail.
 
