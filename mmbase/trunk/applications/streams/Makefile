
#M2=$(HOME)/.m2/repository
#mmbase=1.9-SNAPSHOT

#export CLASSPATH = target/classes:$(M2)/org/mmbase/mmbase/$(mmbase)/mmbase-$(mmbase).jar:$(M2)/org/mmbase/mmbase-media/$(mmbase)/mmbase-media-$(mmbase)-classes.jar:target/mmbase-streams-$(mmbase)-classes.jar:$(M2)/net/sf/fmj/fmj-nojmf/2009-04-16/fmj-nojmf-2009-04-16.jar:$(M2)/javax/media/jmf/2.1.1e/jmf-2.1.1e.jar




.PHONY:
samples: samples/basic.mpg samples/basic.mov samples/basic.mp3 samples/basic.mp4 samples/basic.png


samples/%:
	mkdir -p samples
	(cd samples ; curl -o $* http://openbeelden1.tuxic.nl/beta/files/TESTS/$*)



samples/mahnamahna.broken: samples/mahnamahna.mpg
	cp samples/mahnamahna.mpg samples/mahnamahna.broken


.PHONY:
test.theora: samples/mahnamahna.mpg
	mkdir -p results
	java -cp $(CLASSPATH) org.mmbase.streams.transcoders.FFMpeg2TheoraTranscoder samples/mahnamahna.mpg  results/mahnamahna.ogv

test.avi: samples/mahnamahna.mpg
	mkdir -p results
	java org.mmbase.streams.transcoders.FFMpegTranscoder samples/mahnamahna.mpg  results/mahnamahna.avi

test.h264: samples/mahnamahna.mpg
	mkdir -p results
	java org.mmbase.streams.transcoders.FFMpegTranscoder samples/mahnamahna.mpg  results/mahnamahna.h264

test.recognize: samples/mahnamahna.mpg samples/mahnamahna.broken
#	java org.mmbase.streams.transcoders.FFMpegRecognizer samples/mahnamahna.mpg
#	java org.mmbase.streams.transcoders.FFMpegRecognizer samples/test.mp3
#	java org.mmbase.streams.transcoders.FFMpegRecognizer samples/test.jpeg
#	java org.mmbase.streams.transcoders.FFMpegRecognizer samples/mahnamahna.broken
#	java org.mmbase.streams.transcoders.FFMpegRecognizer samples/odd.ogg
	java org.mmbase.streams.transcoders.FFMpegRecognizer audio:samples/testaudio.ogg


transcode:
	java Transcode samples/mahnamahna.mpg  -o results/mahnamahna.avi

clean:
	rm -r results
