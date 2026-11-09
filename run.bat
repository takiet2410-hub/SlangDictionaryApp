@echo off
echo ====================================================================
echo             STARTING SLANG DICTIONARY APP (JAVA 21)
echo ====================================================================

if not exist SlangDictionary.jar (
    echo [INFO] SlangDictionary.jar not found. Building now...
    call build.bat
)

java -Dfile.encoding=UTF-8 -jar SlangDictionary.jar