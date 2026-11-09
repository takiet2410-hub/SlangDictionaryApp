@echo off
echo ====================================================================
echo             RUNNING ALL AUTOMATED TESTS AND BENCHMARK
echo ====================================================================

if not exist out (
    echo [INFO] Compiling source code and tests...
    call build.bat
)

echo.
echo ====================================================================
echo [MILESTONE 2 TEST] MODEL AND INDEX ENGINE
echo ====================================================================
java -ea -Dfile.encoding=UTF-8 -cp out test.Milestone2Test

echo.
echo ====================================================================
echo [MILESTONE 3 TEST] CONTROLLER AND BUSINESS LOGIC
echo ====================================================================
java -ea -Dfile.encoding=UTF-8 -cp out test.Milestone3Test

echo.
echo ====================================================================
echo [MILESTONE 4 TEST] SWING VIEW LAYER
echo ====================================================================
java -ea -Dfile.encoding=UTF-8 -cp out test.Milestone4Test

echo.
echo ====================================================================
echo [MILESTONE 5 TEST] BENCHMARK 100,000 WORDS AND CACHE RECOVERY
echo ====================================================================
java -ea -Dfile.encoding=UTF-8 -cp out test.Milestone5Test

echo.
echo ====================================================================
echo                   ALL TESTS FINISHED!
echo ====================================================================
pause