@echo off
if "%1"=="" goto error1
if not "%1"=="" goto para2

:error1
echo ["Usage: generate_report.sh configfile workingfolder reportlocation"]
echo ["you need set the parameter : configfile !"]
goto end

:error2
echo ["Usage: generate_report.sh configfile workingfolder reportlocation"]
echo ["you need set the parameter : workingfolder !"]
goto end

:error3
echo ["Usage: generate_report.sh configfile workingfolder reportlocation"]
echo ["you need set the parameter : reportlocation !"] 
goto end

:para2
if "%2"=="" goto error2
if not "%2"=="" goto para3

:para3
if "%3"=="" goto error3
if not "%3"=="" goto ant

:ant 
ant -lib lib report -Dconfigfile="%1" -Dworkingfolder="%2" -Dreportlocation="%3"
goto end

:end