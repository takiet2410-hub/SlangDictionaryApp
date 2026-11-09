@echo off
chcp 65001 > nul
echo ====================================================================
echo        KHỞI CHẠY SLANG DICTIONARY APP (JAVA 21)
echo ====================================================================

if not exist SlangDictionary.jar (
    echo [INFO] Chưa tìm thấy SlangDictionary.jar. Đang tiến hành build...
    call build.bat
)

java -Dfile.encoding=UTF-8 -jar SlangDictionary.jar
