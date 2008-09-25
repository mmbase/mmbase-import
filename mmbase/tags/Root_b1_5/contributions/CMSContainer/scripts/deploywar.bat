:: 
:: This software is OSI Certified Open Source Software.
:: OSI Certified is a certification mark of the Open Source Initiative.
:: 
:: The license (Mozilla version 1.0) can be read at the MMBase site.
:: See http://www.MMBase.org/license
:: 

:: ----------------------------------------------------------------------------
:: CMSC deploy-war Batch script
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
GOTO processApplications

:usage
	echo Usage:  deployweb applications...
	GOTO end

:processApplications
	FOR %%A IN (%*) DO CALL :processApp "%%~A"

	:: remove all expanded directories	
	FOR /D %%A IN (%CATALINA_HOME%\webapps\*.*) DO CALL :removeDir "%%~A"

	echo Removing directory %CATALINA_HOME%\work
	rmdir /S /Q %CATALINA_HOME%\work

	GOTO end

:removeDir
	:: Do not remove tomcat webapps

	IF "%CATALINA_HOME%\webapps\ROOT" == "%~1" GOTO :EOF
	:: Tomcat 5.x
	IF "%CATALINA_HOME%\webapps\balancer" == "%~1" GOTO :EOF
	IF "%CATALINA_HOME%\webapps\jsp-examples" == "%~1" GOTO :EOF
	IF "%CATALINA_HOME%\webapps\servlets-examples" == "%~1" GOTO :EOF
	IF "%CATALINA_HOME%\webapps\tomcat-docs" == "%~1" GOTO :EOF
	IF "%CATALINA_HOME%\webapps\webdav" == "%~1" GOTO :EOF
	:: Tomcat 6.x
	IF "%CATALINA_HOME%\webapps\docs" == "%~1" GOTO :EOF
	IF "%CATALINA_HOME%\webapps\examples" == "%~1" GOTO :EOF
	IF "%CATALINA_HOME%\webapps\host-manager" == "%~1" GOTO :EOF
	IF "%CATALINA_HOME%\webapps\manager" == "%~1" GOTO :EOF
	
	echo Removing directory %~1
	rmdir /S /Q %~1
	GOTO :EOF

:processApp
	:: stop when error occured in loop
	IF NOT "%ERRORLEVEL%" == "0" goto :EOF
	setlocal

	set APPLICATION=%~1
	IF exist "%APPLICATION%" GOTO okAppName
	set APPLICATION=..\%~1
	IF exist "%APPLICATION%" GOTO okAppName
	echo Could not find the application with name %~1
	GOTO usage

	:okAppName
		IF exist "%APPLICATION%\target\" GOTO processWar
		PUSHD "%APPLICATION%"
		FOR /D %%B IN (*.*) DO CALL :processApp "%%~B"
		POPD
		GOTO :EOF
	
	:processWar
		PUSHD "%APPLICATION%\target"
		FOR %%C in (*.war) DO CALL :copyWar "%%~C" 
		POPD
		GOTO :EOF
	
	:copyWar
		echo copy WAR %~1
 		xcopy /D /Y %~1 %CATALINA_HOME%\webapps
		GOTO :EOF

:exit
	exit /b 1

:end
	endlocal