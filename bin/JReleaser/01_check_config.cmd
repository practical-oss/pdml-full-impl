@echo off

cd ../../app

rem call jreleaser config --config-file=JReleaser/jreleaser.yml
call jreleaser config ^
    --basedir=../
	rem -D=jreleaser.github.token=...vnevVOezhzXbkNVpeLd28nE1MLjN4
	rem --git-root-search ^
	rem --git-root-search ^
    rem --config-file=app/jreleaser.yml ^
    rem --config-file=jreleaser.yml ^
    rem --basedir=../ ^
    rem --basedir=../../../full-pdml-impl ^

pause
