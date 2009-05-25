Automated (junit) tests for mmbase. Things you might like to know.

- Every test should have an entry in build.xml in this directory.

- The run.all target of this build.xml is run every night on mmbase.org

- The config directory defines a mmbase.config directory, including everything which is needed to
  run the tests.

  - an empty mmbase (class path is constructed in directory build/lib) running hsql (the work files
    of hsql are put in a directory 'work')

  - several logging configurations can be chosen (three different log.xml are present now)
     - if test-cases fail you might change logging configuration to explore what is wrong.
     - you might want to require that no warn/error logging are issued. There is log configuration
       present which converts those into test-case-errors.
	 - this is currently configured in implementation of the specific test suites
	 -
  - three applications are auto-deploy in this test-install:
     - General.xml
     - BridgeTests.xml (aa, bb, cc builders with all kind of fields)
     - MyNews.xml      (based on MyNews from core, but auto deploy (and perhaps more?))


- The test-script are implemented using ant, but use artifacts from maven (using
  'install-dependency.xml').

- You can also, if available on your system, use 'make' which calls ant with options to make it less verbose:
  eg:
  -make all
  -make bridge
  ...
