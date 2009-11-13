
all=build/1.result
.PHONY:
all: $(all)

build:
	mkdir build

build/%.result:  %.xml %.expected.result ../head.xslt Makefile build
	xsltproc ../head.xslt $< > $@
	diff $@ $*.expected.result


.PHONY:
clean:
	rm -f $(all)
