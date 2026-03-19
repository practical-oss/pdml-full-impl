@echo off

cd ../../module

call jreleaser full-release --dry-run --git-root-search

pause