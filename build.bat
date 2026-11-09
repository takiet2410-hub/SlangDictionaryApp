@echo off
chcp 65001 > nul
echo ====================================================================
echo        BIÊN DỊCH VÀ ĐÓNG GÓI SLANG DICTIONARY APP (JAVA 21)
echo ====================================================================

if not exist out mkdir out

echo [1/3] Đang biên dịch toàn bộ mã nguồn Java...
javac -encoding UTF-8 -d out src\vn\edu\hcmus\fit\dictionary\util\*.java src\vn\edu\hcmus\fit\dictionary\model\entity\*.java src\vn\edu\hcmus\fit\dictionary\model\index\*.java src\vn\edu\hcmus\fit\dictionary\model\repository\*.java src\vn\edu\hcmus\fit\dictionary\model\event\*.java src\vn\edu\hcmus\fit\dictionary\model\*.java src\vn\edu\hcmus\fit\dictionary\controller\*.java src\vn\edu\hcmus\fit\dictionary\view\component\*.java src\vn\edu\hcmus\fit\dictionary\view\*.java src\vn\edu\hcmus\fit\dictionary\Main.java src\test\*.java

if %ERRORLEVEL% neq 0 (
    echo [ERROR] Biên dịch thất bại! Vui lòng kiểm tra lại lỗi code.
    pause
    exit /b %ERRORLEVEL%
)

echo [2/3] Biên dịch thành công!
echo [3/3] Đang đóng gói file JAR thực thi (SlangDictionary.jar)...
jar cfe SlangDictionary.jar vn.edu.hcmus.fit.dictionary.Main -C out vn

if %ERRORLEVEL% equ 0 (
    echo.
    echo ====================================================================
    echo   BUILD THÀNH CÔNG! File JAR: SlangDictionary.jar
    echo   Chạy ứng dụng: java -jar SlangDictionary.jar
    echo   Hoặc double click vào file run.bat
    echo ====================================================================
) else (
    echo [ERROR] Đóng gói JAR thất bại!
)
pause
