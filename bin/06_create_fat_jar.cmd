@echo off

cd ../app
call ..\gradlew shadowJar
echo Fat jar stored in build/fatJar/ (if no error)

pause
