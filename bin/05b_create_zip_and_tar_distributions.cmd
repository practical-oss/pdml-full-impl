@echo off

cd ../app
rem Change to use the fat jar
rem call ..\gradlew distZip
call ..\gradlew assembleDist
echo Compressed files stored in build/distributions/ (if no error)

pause
