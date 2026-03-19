@echo off

cd ../../app

rem call jreleaser config --config-file=JReleaser/jreleaser.yml
call jreleaser config --git-root-search

pause