@ECHO OFF
call clean ..\cmsc
call clean ..\..\CMSContainer_Modules
call clean ..\..\CMSContainer_Portlets

set APPLICATION=%1
IF exist "%APPLICATION%" GOTO okAppName
set APPLICATION=..\..\%1
IF exist "%APPLICATION%" GOTO okAppName
echo Could not find the application with name %~2
GOTO end

:okAppName
	call clean %APPLICATION%

:end
