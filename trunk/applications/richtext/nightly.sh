
# Damn cvs.mmbase.org does not have xsltproc and it is impossible to install
cd /home/michiel/mmbase/head/applications/richtext
cvs -q up -d -P -A
export JAVA_HOME=/opt/jdk
maven -o --nobanner clean
maven -o --nobanner mmbase-module:install


for i in `/usr/bin/find ~/.maven/repository/mmbase -mmin -10` ; do
    scp $i nightly@cvs.mmbase.org:${i#/home/michiel/}
done
