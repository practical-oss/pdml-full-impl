@echo off

cd ../app
call ..\gradlew installDist
echo Distribution stored in build/install/ (if no error)

pause
