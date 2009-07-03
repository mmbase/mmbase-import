
M2=$(HOME)/.m2/repository
mmbase=1.9-SNAPSHOT

export CLASSPATH = target/classes:$(M2)/org/mmbase/mmbase/$(mmbase)/mmbase-$(mmbase).jar:$(M2)/org/mmbase/mmbase-media/$(mmbase)/mmbase-media-$(mmbase)-classes.jar:target/mmbase-streams-$(mmbase)-classes.jar:$(M2)/net/sf/fmj/fmj-nojmf/2009-04-16/fmj-nojmf-2009-04-16.jar:$(M2)/javax/media/jmf/2.1.1e/jmf-2.1.1e.jar




.PHONY:
samples: samples/mahnamahna.mpg samples/Kort-stormpje.mov samples


samples/mahnamahna.mpg:
	mkdir -p samples
	(cd samples ; wget http://www.toly.net/downloads/mahnamahna.mpg)


samples/Kort-stormpje.mov:
	mkdir -p samples
	(cd samples ; wget http://www.toly.net/downloads/Kort-stormpje.mov)

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

test.recognize: samples/mahnamahna.mpg
	java org.mmbase.streams.transcoders.FFMpegRecognizer samples/mahnamahna.mpg
	java org.mmbase.streams.transcoders.FFMpegRecognizer samples/test.mp3
	java org.mmbase.streams.transcoders.FFMpegRecognizer samples/test.jpeg


transcode:
	java Transcode samples/mahnamahna.mpg  -o results/mahnamahna.avi

clean:
	rm -r results
