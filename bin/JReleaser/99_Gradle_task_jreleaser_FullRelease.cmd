@echo off

rem echo ERROR WITH VERSION 1.16
rem echo OK WITH VERSION 1.15
rem echo Native JRelease OK WITH 1.16
rem pause

cd ..

call ..\gradlew jreleaserFullRelease --stacktrace
rem call gradlew :pdml-companion:jreleaserFullRelease --stacktrace

pause
