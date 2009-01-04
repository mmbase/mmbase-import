@ECHO OFF

set APPLICATION=%1
IF exist "%APPLICATION%" GOTO okAppName
set APPLICATION=..\..\%1
IF exist "%APPLICATION%" GOTO okAppName
echo Could not find the application with name %~2
GOTO end

:okAppName
	call build build cmsc ..\..\CMSContainer_Modules ..\..\CMSContainer_Portlets
	:: stop when error occurred in loop
	IF NOT "%ERRORLEVEL%" == "0" goto :EOF
	
	call build build %APPLICATION%
:end
