
# These commands can be used to build MMBase in docker (but without act)
# 'make mvn'

mvn:
	docker run -v $(shell pwd):/mmbase -v ${HOME}/.m2:/root/.m2 -w /mmbase  -it mmbase/build build-all.sh install

bash:
	docker run -v $(shell pwd):/mmbase -v ${HOME}/.m2:/root/.m2 -w /mmbase  -it mmbase/build bash
