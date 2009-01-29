@echo off

IF ""%1"" == ""intranet"" GOTO next
IF ""%1"" == ""internet"" GOTO next

GOTO nothing

:next
IF ""%2"" == ""acceptatie"" GOTO doCopy
IF ""%2"" == ""finalist"" GOTO doCopy
IF ""%2"" == ""local"" GOTO doCopy

GOTO nothing

:doCopy
echo Copying files for %1 !!
xcopy conf\%1\filters.%2.properties conf\filters.properties /Y
xcopy conf\%1\nmintraconfig.%2.properties conf\nmintraconfig.properties /Y
xcopy conf\internet\natmmconfig.%2.properties conf\natmmconfig.properties /Y
GOTO end


:nothing
echo Enter 2 parameters: 
echo - 1) internet or intranet, %2 
echo - 2) acceptatie, finalist or local


:end
echo Done.