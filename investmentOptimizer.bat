@echo off

cd /d "%~dp0"

if "%*"=="" (
    call gradlew.bat run
) else (
    call gradlew.bat run --args="%*"
)

