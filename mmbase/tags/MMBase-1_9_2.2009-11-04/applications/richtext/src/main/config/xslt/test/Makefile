
all=build/1.result build/2.result build/3.result build/4.result build/5.result build/6.result
.PHONY:
all: $(all)

build:
	mkdir build

build/%.result:  %.xml %.expected.result ../2rich.xslt ../mmxf2rich.xslt Makefile build
	xsltproc ../2rich.xslt $< > $@
	diff $@ $*.expected.result


.PHONY:
clean:
	rm -f $(all)
