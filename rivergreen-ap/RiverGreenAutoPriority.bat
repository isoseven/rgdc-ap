@echo off
if "%~1"=="" (
    start javaw -jar %USERPROFILE%\Downloads\rivergreen-ap-1.0-SNAPSHOT.jar 5736
) else (
    start javaw -jar %USERPROFILE%\Downloads\rivergreen-ap-1.0-SNAPSHOT.jar %~1
)
