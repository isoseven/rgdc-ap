@echo off
if "%~1"=="" (
    java -jar %USERPROFILE%\Downloads\rivergreen-ap-1.0-SNAPSHOT.jar 5736
) else (
    java -jar %USERPROFILE%\Downloads\rivergreen-ap-1.0-SNAPSHOT.jar %~1
)