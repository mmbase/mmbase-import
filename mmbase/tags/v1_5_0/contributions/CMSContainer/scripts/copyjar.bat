:: 
:: This software is OSI Certified Open Source Software.
:: OSI Certified is a certification mark of the Open Source Initiative.
:: 
:: The license (Mozilla version 1.0) can be read at the MMBase site.
:: See http://www.MMBase.org/license
:: 

:: ----------------------------------------------------------------------------
:: CMSC copy-jar Batch script
:: 
:: Required ENV vars:
:: JAVA_HOME - location of a JDK home dir
:: MAVEN_HOME - location of maven's installed home dir
:: CATALINA_HOME - location of a Tomcat home dir
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

:: ----- Execute The Requested Command ---------------------------------------
echo Using JAVA_HOME:       %JAVA_HOME%
echo Using MAVEN_HOME:      %MAVEN_HOME%
echo Using BUILD_OPTS:      %BUILD_OPTS%
echo Using CATALINA_HOME:   %CATALINA_HOME%
IF ""%1"" == """" GOTO usage
IF ""%2"" == """" GOTO usage
GOTO processCopy

:usage
	echo Usage:  copyjar application webapp-name
	GOTO end

:processCopy
	set APPLICATION=%~1
	IF exist "%APPLICATION%" GOTO okAppName
	set APPLICATION=..\%~1
	IF exist "%APPLICATION%" GOTO okAppName
	echo Could not find the application with name %~1
	GOTO usage

	:okAppName
		set WEBAPP=%~2
		IF exist "%CATALINA_HOME%\webapps\%WEBAPP%" GOTO copyApp
			echo Could not find the web application with name %WEBAPP% in Tomcat
			GOTO usage

	:copyApp
		pushd "%APPLICATION%"
		call maven %BUILD_OPTS% clean jar:install
		popd
		echo APPLICATION "%APPLICATION%" TO "%WEBAPP%"
		xcopy /D /S /Y "%APPLICATION%\target\*.jar" "%CATALINA_HOME%\webapps\%WEBAPP%\WEB-INF\lib"
		GOTO end

:exit
	exit /b 1

:end
	endlocal
 	