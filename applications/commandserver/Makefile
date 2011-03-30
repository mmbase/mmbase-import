
.PHONY:
run:
	java -jar `ls target/mmbase-commandserver-*` --maxDuration 1 --threads 1 1679

.PHONY:
test:
	java -cp `ls target/mmbase-commandserver-*` org.mmbase.util.MagickClient Makefile convert -list format
	#java -cp `ls target/mmbase-commandserver-*` org.mmbase.util.MagickClient Makefile sleep 2
