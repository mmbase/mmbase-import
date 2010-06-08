# Make rocks...

.PHONY: out


out:
	mvn package
	rsync -ra target/site/tlddoc/* mmbase.org:web/tags/mm/calendar


