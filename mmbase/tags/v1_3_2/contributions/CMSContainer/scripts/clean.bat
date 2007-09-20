:: 
:: This software is OSI Certified Open Source Software.
:: OSI Certified is a certification mark of the Open Source Initiative.
:: 
:: The license (Mozilla version 1.0) can be read at the MMBase site.
:: See http://www.MMBase.org/license
:: 

:: ----------------------------------------------------------------------------
:: CMSC clean Batch script
:: 
:: Required ENV vars:
:: 
:: Optional ENV vars
:: BATCH_ECHO - set to 'on' to enable the echoing of the batch commands
:: ----------------------------------------------------------------------------
@echo off
@IF "%BATCH_ECHO%" == "on"  echo %BATCH_ECHO%

:: Use local variables
IF "%OS%"=="Windows_NT" SETLOCAL

:: Check command line arguments and Windows version
ECHO.%1 | FIND "/" >NUL
IF NOT ERRORLEVEL 1 IF /I NOT "%~1"=="/L" GOTO Syntax
ECHO.%1 | FIND "?" >NUL
IF NOT ERRORLEVEL 1 GOTO Syntax
ECHO.%1 | FIND "*" >NUL
IF NOT ERRORLEVEL 1 GOTO Syntax
IF NOT "%OS%"=="Windows_NT" GOTO Syntax
IF NOT "%~1"=="" IF /I NOT "%~1"=="/L" IF NOT EXIST "%~1" GOTO Syntax

:: Go to start directory
SET StartDir=%CD%
IF NOT "%~1"=="" IF /I NOT "%~1"=="/L" SET StartDir=%~1
PUSHD "%StartDir%"
IF ERRORLEVEL 1 GOTO Syntax

:: Display for every subdirectory
:SubDirs
FOR /D %%A IN (*.*) DO CALL :List "%%~A"

:: Done
POPD
GOTO End

:List
IF "%~1"=="config" GOTO:EOF
IF "%~1"=="docs" GOTO:EOF
IF "%~1"=="examples" GOTO:EOF
IF "%~1"=="resources" GOTO:EOF
IF "%~1"=="src" GOTO:EOF
IF "%~1"=="target" GOTO :RemoveDir
PUSHD "%~1"
FOR /D %%A IN (*.*) DO CALL :List "%%~A"
POPD
GOTO:EOF

:RemoveDir
echo %CD% %~1
RMDIR /S /Q %~1
GOTO:EOF

:Syntax
ECHO.
ECHO Usage:  Clean
ECHO.

:End
IF "%OS%"=="Windows_NT" ENDLOCAL
