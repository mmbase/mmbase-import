
# This is in speeltuin, hence not in nightly build. I like in in the maven repository though

cd /home/michiel/mmbase/head/applications/statistics
cvs -q up -d -P -A
export JAVA_HOME=/opt/jdk
maven --nobanner clean
maven --nobanner mmbase-module:install


for i in `/usr/bin/find ~/.maven/repository/mmbase -mmin -10` ; do
    scp $i nightly@cvs.mmbase.org:${i#/home/michiel/}
done
