# default solaris
# where is java
JAVAHOME=/usr/java
# OS type (same as subdir in java tree)
CC=gcc
OS=solaris
LDFLAGS = -dy -G 


#other Operating systems.
#
# Linux
#JAVAHOME=/usr/local/jdk1.1.3
#OS=linux
#LDFLAGS = --shared


BASE=setUser
CFILES	= \
	setit.c

OFILES	= $(CFILES:.c=.o) 
SOFILES	= $(CFILES:.c=.so)
SOFILE = lib$(BASE).so

CINCLUDE= -I$(JAVAHOME)/include -I$(JAVAHOME)/include/$(OS) -I$(JAVAHOME)/include/genunix
CFLAGS	= $(CINCLUDE)
RM	= rm -f
LD	= ld


.SUFFIXES: $(SUFFIXES) .so .java

#.o.so:	$(OFILES)
#	$(LD) $(LDFLAGS) -o $@ $<
#	chmod a+x $@

.java.h: $(BASE).java
	javah -jni $(BASE)

$(SOFILE): $(OFILES)
	$(LD) $(LDFLAGS) -o $@ $(OFILES)
	chmod a+x $@

all: $(BASE).h $(OFILES) $(SOFILE)

clean:
	-$(RM) *.o *.so $(BASE).h 
