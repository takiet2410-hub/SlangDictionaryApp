@echo off
chcp 65001 > nul
echo ====================================================================
echo             CHẠY TOÀN BỘ BỘ KIỂM THỬ TỰ ĐỘNG VÀ BENCHMARK
echo ====================================================================

if not exist out (
    echo [INFO] Đang biên dịch mã nguồn và kiểm thử...
    call build.bat
)

echo.
echo ====================================================================
echo [MILESTONE 2 TEST] TẦNG MODEL & INDEX ENGINE
echo ====================================================================
java -ea -Dfile.encoding=UTF-8 -cp out test.Milestone2Test

echo.
echo ====================================================================
echo [MILESTONE 3 TEST] TẦNG CONTROLLER & NGHIỆP VỤ
echo ====================================================================
java -ea -Dfile.encoding=UTF-8 -cp out test.Milestone3Test

echo.
echo ====================================================================
echo [MILESTONE 4 TEST] TẦNG VIEW SWING
echo ====================================================================
java -ea -Dfile.encoding=UTF-8 -cp out test.Milestone4Test

echo.
echo ====================================================================
echo [MILESTONE 5 TEST] BENCHMARK 100.000 TỪ & PHỤC HỒI CACHE
echo ====================================================================
java -ea -Dfile.encoding=UTF-8 -cp out test.Milestone5Test

echo.
echo ====================================================================
echo                HOÀN TẤT TOÀN BỘ KIỂM THỬ!
echo ====================================================================
pause
