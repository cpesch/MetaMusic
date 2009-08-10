@echo off
cls

set JAVA_HOME=c:\programme\java\jdk1.6.0_12
set JACOBGEN_HOME=..\..\..\thirdparty\jacob\jacobgen-0.10\
set CLASSPATH=%CLASSPATH%;%JAVA_HOME%\lib\dt.jar;%JACOBGEN_HOME%\lib\jacobgen.jar;%JACOBGEN_HOME%\lib\viztool.jar
set PATH=%PATH%;%JACOBGEN_HOME%\lib

REM $ bin/run_jacobgen.bat -destdir:"..\jacob\samples" -listfile:"jacobgenlog.txt" -package:com.jacobgen.microsoft.msword "C:\Program Files\Microsoft Office\OFFICE11\MSWORD.OLB"
REM java com.jacob.jacobgen.Jacobgen -destdir:"C:\p4\navigation\google-earth\src\java" -listfile:"..\doc\googleearth-jacobgen.log" -package:slash.navigation.googleearth.binding "C:\Programme\Google\Google Earth\googleearth.exe"

java com.jacob.jacobgen.Jacobgen -destdir:"..\java" -listfile:"..\doc\itunes-jacobgen.log" -package:slash.metamusic.itunes.com.binding "C:\Programme\iTunes\iTunes.exe"
pause
