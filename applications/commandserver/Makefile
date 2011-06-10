
.PHONY:
run:
	java -jar `ls target/mmbase-commandserver-*` --maxDuration 100 --threads 1 1679

.PHONY:
test:
	#java -cp `ls target/mmbase-commandserver-*` org.mmbase.util.MagickClient Makefile convert -list format
	#java -cp `ls target/mmbase-commandserver-*` org.mmbase.util.MagickClient Makefile sleep 2
	java -cp `ls target/mmbase-commandserver-*` org.mmbase.util.MagickClient /tmp/Bas.jpg convert - /tmp/bas2.png
