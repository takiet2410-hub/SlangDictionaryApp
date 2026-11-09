@echo off
echo ====================================================================
echo        COMPILING AND PACKAGING SLANG DICTIONARY APP (JAVA 21)
echo ====================================================================

if not exist out mkdir out

echo [1/3] Compiling all Java source files...
javac -encoding UTF-8 -d out src\vn\edu\hcmus\fit\dictionary\util\*.java src\vn\edu\hcmus\fit\dictionary\model\entity\*.java src\vn\edu\hcmus\fit\dictionary\model\index\*.java src\vn\edu\hcmus\fit\dictionary\model\repository\*.java src\vn\edu\hcmus\fit\dictionary\model\event\*.java src\vn\edu\hcmus\fit\dictionary\model\*.java src\vn\edu\hcmus\fit\dictionary\controller\*.java src\vn\edu\hcmus\fit\dictionary\view\component\*.java src\vn\edu\hcmus\fit\dictionary\view\*.java src\vn\edu\hcmus\fit\dictionary\Main.java src\test\*.java

if %ERRORLEVEL% neq 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b %ERRORLEVEL%
)

echo [2/3] Compilation succeeded!
echo [3/3] Creating executable JAR (SlangDictionary.jar)...
jar cfe SlangDictionary.jar vn.edu.hcmus.fit.dictionary.Main -C out vn

if %ERRORLEVEL% equ 0 (
    echo.
    echo ====================================================================
    echo   BUILD SUCCESSFUL! Output: SlangDictionary.jar
    echo ====================================================================
) else (
    echo [ERROR] JAR packaging failed!
)