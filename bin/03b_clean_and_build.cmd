@echo off

cd ../app
call ..\gradlew clean
call ..\gradlew build

pause
