#!/usr/local/bin/bash
	# java org.mmbase.remote.startMMService /tmp/test.properties 2>&1 | cat >>service.log
	java org.mmbase.remote.startMMService runner1 5282 service/mmcounter.properties service/mmcounter2.properties
