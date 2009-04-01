:: 
:: This software is OSI Certified Open Source Software.
:: OSI Certified is a certification mark of the Open Source Initiative.
:: 
:: The license (Mozilla version 1.0) can be read at the MMBase site.
:: See http://www.MMBase.org/license
:: 

:: ----------------------------------------------------------------------------
:: CMSC copy-web Batch script
:: 
:: Required ENV vars:
:: CATALINA_HOME - location of a Tomcat home dir
:: 
:: Optional ENV vars
:: BATCH_ECHO - set to 'on' to enable the echoing of the batch commands
:: ----------------------------------------------------------------------------
@echo off
@IF "%BATCH_ECHO%" == "on"  echo %BATCH_ECHO%

setlocal

:: Make sure prerequisite environment variables are set
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
echo Using CATALINA_HOME:      %CATALINA_HOME%
IF ""%1"" == """" GOTO usage
IF ""%2"" == """" GOTO usage
GOTO processCopy

:usage
	echo Usage:  copyweb application webapp-name
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
		echo APPLICATION %APPLICATION% TO %WEBAPP%
		xcopy /D /S /Y /EXCLUDE:copyweb.exclude "%APPLICATION%\src\webapp" "%CATALINA_HOME%\webapps\%WEBAPP%"
		xcopy /D /S /Y /EXCLUDE:copyweb.exclude "%APPLICATION%\config" "%CATALINA_HOME%\webapps\%WEBAPP%\WEB-INF\config"
		GOTO end

:exit
	exit /b 1

:end
	endlocal
 	