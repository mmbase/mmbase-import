@echo off

IF exist "%CATALINA_HOME%\webapps\natmm-internet\index.jsp" GOTO internet
IF exist "%CATALINA_HOME%\webapps\ROOT\nmintra\info.jsp" GOTO intranet
GOTO nothing

:internet
echo Copying templates\natmm to %CATALINA_HOME%\webapps\natmm-internet
xcopy templates\natmm\*.js* %CATALINA_HOME%\webapps\natmm-internet  /S /D /Y
xcopy templates\editors\*.js* %CATALINA_HOME%\webapps\natmm-internet\editors  /S /D /Y
xcopy templates\natmm\*.htm* %CATALINA_HOME%\webapps\natmm-internet  /S /D /Y
GOTO end

:intranet
echo Copying templates\nmintra to %CATALINA_HOME%\webapps\ROOT\nmintra
xcopy templates\nmintra\*.js* %CATALINA_HOME%\webapps\ROOT\nmintra  /S /D /Y
xcopy templates\editors\*.js* %CATALINA_HOME%\webapps\ROOT\editors  /S /D /Y
xcopy templates\mmbase\*.xml %CATALINA_HOME%\webapps\ROOT\mmbase  /S /D /Y
xcopy templates\nmintra\*.htm* %CATALINA_HOME%\webapps\ROOT\nmintra  /S /D /Y
GOTO end

:nothing
echo Nothing good found in %CATALINA_HOME%\webapps !!

:end
echo Done.