:: 
:: This software is OSI Certified Open Source Software.
:: OSI Certified is a certification mark of the Open Source Initiative.
:: 
:: The license (Mozilla version 1.0) can be read at the MMBase site.
:: See http://www.MMBase.org/license
:: 

:: ----------------------------------------------------------------------------
:: CMSC Build Batch script
:: 
:: Required ENV vars:
:: JAVA_HOME - location of a JDK home dir
:: MAVEN_HOME - location of maven's installed home dir
:: 
:: Optional ENV vars
:: BUILD_OPTS - parameters passed to Maven
:: BATCH_ECHO - set to 'on' to enable the echoing of the batch commands
:: ----------------------------------------------------------------------------
@echo off
@IF "%BATCH_ECHO%" == "on"  echo %BATCH_ECHO%

setlocal

:: Make sure prerequisite environment variables are set

IF NOT "%JAVA_HOME%" == "" GOTO gotJavaHome
echo The JAVA_HOME environment variable is not defined
echo This environment variable is needed to run this program
GOTO exit

:gotJavaHome
	IF NOT exist "%JAVA_HOME%\bin\java.exe" GOTO noJavaHome
	IF NOT exist "%JAVA_HOME%\bin\javaw.exe" GOTO noJavaHome
	IF NOT exist "%JAVA_HOME%\bin\jdb.exe" GOTO noJavaHome
	IF NOT exist "%JAVA_HOME%\bin\javac.exe" GOTO noJavaHome
	GOTO okJavaHome

:noJavaHome
	echo The JAVA_HOME environment variable is not defined correctly
	echo This environment variable is needed to run this program
	echo NB: JAVA_HOME should point to a JDK not a JRE
	GOTO exit

:okJavaHome
	IF NOT "%MAVEN_HOME%" == "" GOTO gotMavenHome
	echo The MAVEN_HOME environment variable is not defined
	echo This environment variable is needed to run this program
	GOTO exit

:gotMavenHome
	IF NOT exist "%MAVEN_HOME%\bin\maven.bat" GOTO noMavenHome
	GOTO okMavenHome

:noMavenHome
	echo The MAVEN_HOME environment variable is not defined correctly
	echo This environment variable is needed to run this program
	GOTO exit

:okMavenHome

:: ----- Execute The Requested Command ---------------------------------------

echo Using MAVEN_HOME:      %MAVEN_HOME%
echo Using JAVA_HOME:       %JAVA_HOME%
echo Using BUILD_OPTS:      %BUILD_OPTS%

IF ""%1"" == ""build"" GOTO processApplications
IF ""%1"" == ""clean"" GOTO processApplications
IF ""%1"" == ""cleanbuild"" GOTO processApplications
IF ""%1"" == ""deploy-tomcat"" GOTO processApplications

:usage
	echo Usage:  build command applications...
	echo commands:
	echo   build             Builds files for an application
	echo   clean             Removes build files for an application
	echo   cleanbuild        Removes and builds files for an application
	echo   deploy-tomcat     Deploy cmsc war-file to tomcat
	GOTO end

:: ----- Process The Command for all applications ----------------------------

:processApplications
	FOR %%A IN (%*) DO IF NOT "%1" == "%%~A" CALL :processApp "%1" "%%~A"
	GOTO end

:processApp
	:: stop when error occured in loop
	IF NOT "%ERRORLEVEL%" == "0" goto :EOF
	setlocal
	
	set APPLICATION=%~2
	IF exist "%APPLICATION%" GOTO okAppName
	set APPLICATION=..\%~2
	IF exist "%APPLICATION%" GOTO okAppName
	echo Could not find the application with name %~2
	GOTO usage
	
	:okAppName
		echo APPLICATION %APPLICATION%
		
		IF ""%~1"" == ""build"" GOTO doBuild
		IF ""%~1"" == ""clean"" GOTO doClean
		IF ""%~1"" == ""cleanbuild"" GOTO doCleanBuild
		IF ""%~1"" == ""deploy-tomcat"" GOTO doDeployTomcat

:: ----- Sub routines for every command ---------------------------------------

:doBuild
	cd %APPLICATION%
	call maven %BUILD_OPTS% multiproject:install
	GOTO :EOF

:doClean
	cd %APPLICATION%
	call maven %BUILD_OPTS% multiproject:clean
	GOTO :EOF

:doCleanBuild
	cd %APPLICATION%
	call maven %BUILD_OPTS% multiproject:clean multiproject:install
	GOTO :EOF

:doDeployTomcat
	IF NOT "%CATALINA_HOME%" == "" GOTO gotCatalinaHome
	echo The CATALINA_HOME environment variable is not defined
	echo This environment variable is needed to run this program
	GOTO exit

	:gotCatalinaHome
		IF NOT exist "%CATALINA_HOME%\bin\catalina.bat" GOTO noCatalinaHome
		GOTO okCatalinaHome
	
	:noCatalinaHome
		echo The CATALINA_HOME environment variable is not defined correctly
		echo This environment variable is needed to run this program
		GOTO end
	
	:okCatalinaHome
		:: remove all expanded directories	
		FOR /D %%A IN (%CATALINA_HOME%\webapps\*.*) DO CALL :removeDir "%%~A"

		echo Removing directory %CATALINA_HOME%\work
		rmdir /S /Q %CATALINA_HOME%\work
		
		copy %APPLICATION%\war\target\*.war %CATALINA_HOME%\webapps\
		GOTO :EOF

:removeDir
	:: Do not remove tomcat webapps
	IF "%CATALINA_HOME%\webapps\balancer" == "%~1" GOTO :EOF
	IF "%CATALINA_HOME%\webapps\jsp-examples" == "%~1" GOTO :EOF
	IF "%CATALINA_HOME%\webapps\ROOT" == "%~1" GOTO :EOF
	IF "%CATALINA_HOME%\webapps\servlets-examples" == "%~1" GOTO :EOF
	IF "%CATALINA_HOME%\webapps\tomcat-docs" == "%~1" GOTO :EOF
	IF "%CATALINA_HOME%\webapps\webdav" == "%~1" GOTO :EOF
	
	echo Removing directory %~1
	rmdir /S /Q %~1
	GOTO :EOF

:exit
	exit /b 1

:end
	endlocal
