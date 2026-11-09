@echo off
chcp 65001 > nul
setlocal enabledelayedexpansion

echo ====================================================================
echo        CÔNG CỤ ĐÓNG GÓI BÀI NỘP TỰ ĐỘNG - DAMH#1 (JAVA SWING)
echo ====================================================================

set /p MSSV="Nhập Mã Số Sinh Viên của bạn (VD: 22120000): "
if "%MSSV%"=="" (
    echo [LỖI] MSSV không được để trống!
    pause
    exit /b 1
)

set /p DIEM="Nhập Điểm tự đánh giá (Mặc định: 100): "
if "%DIEM%"=="" set DIEM=100

set ZIP_NAME=%MSSV%_%DIEM%.zip
set TEMP_DIR=package_temp_%MSSV%

echo.
echo [1/5] Đang build lại file JAR mới nhất...
call build.bat

echo.
echo [2/5] Đang chuẩn bị thư mục nén tạm thời: %TEMP_DIR%...
if exist %TEMP_DIR% rmdir /s /q %TEMP_DIR%
mkdir %TEMP_DIR%
mkdir %TEMP_DIR%\src
mkdir %TEMP_DIR%\data
mkdir %TEMP_DIR%\git_commits_screenshots

echo [3/5] Đang sao chép các tệp tin theo quy định bài nộp...
xcopy /s /e /y src %TEMP_DIR%\src > nul
xcopy /s /e /y data %TEMP_DIR%\data > nul
copy SlangDictionary.jar %TEMP_DIR%\ > nul
copy DanhGia_LinkVideo.txt %TEMP_DIR%\%MSSV%_DanhGia.txt > nul
copy README.md %TEMP_DIR%\ > nul
copy PROJECT_BLUEPRINT.md %TEMP_DIR%\ > nul
copy git_log.txt %TEMP_DIR%\git_commits_screenshots\ > nul
if exist git_commits_screenshots xcopy /s /e /y git_commits_screenshots %TEMP_DIR%\git_commits_screenshots > nul
copy build.bat %TEMP_DIR%\ > nul
copy run.bat %TEMP_DIR%\ > nul
copy test.bat %TEMP_DIR%\ > nul

echo [4/5] Đang nén thành file %ZIP_NAME% bằng PowerShell...
if exist %ZIP_NAME% del %ZIP_NAME%
powershell -Command "Compress-Archive -Path '%TEMP_DIR%\*' -DestinationPath '%ZIP_NAME%'"

echo [5/5] Dọn dẹp thư mục tạm...
rmdir /s /q %TEMP_DIR%

echo.
echo ====================================================================
echo   ĐÓNG GÓI BÀI NỘP HOÀN TẤT!
echo   Tên file: %ZIP_NAME%
echo   Định dạng: MSSV_SoDiemTuDanhGia.zip theo đúng yêu cầu đề bài.
echo ====================================================================
pause
