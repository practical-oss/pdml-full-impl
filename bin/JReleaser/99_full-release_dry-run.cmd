@echo off

cd ../../app

call jreleaser full-release ^
    --dry-run ^
    --basedir=../
	rem -D=jreleaser.github.token=...vnevVOezhzXbkNVpeLd28nE1MLjN4
    rem --config-file=jreleaser.yml ^
	rem --git-root-search
    rem --basedir=../../../full-pdml-impl ^

pause
