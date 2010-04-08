
# the maven plugin seems not to be able to do sufficiently
# It'll cost me lots of time to fix the plugin or find work arounds.

# This works well, and is only a few lines.
# Make rocks...

.PHONY: clean


out:
	java -jar taglibrarydoc-1_3/tlddoc.jar  target/mmbase-searchrelate-1.9-SNAPSHOT.war
	rsync -ra out/* mmbase.org:web/tags/mm/searchrelate


clean:
	rm -r out
