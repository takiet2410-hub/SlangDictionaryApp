@echo off
setlocal enabledelayedexpansion

echo ====================================================================
echo        AUTOMATED SUBMISSION PACKAGING TOOL - DAMH#1
echo ====================================================================

set /p MSSV="Enter your Student ID (MSSV) [e.g. 22120000]: "
if "%MSSV%"=="" (
    echo [ERROR] Student ID cannot be empty!
    pause
    exit /b 1
)

set /p SCORE="Enter Self-Evaluation Score [Default: 100]: "
if "%SCORE%"=="" set SCORE=100

set ZIP_NAME=%MSSV%_%SCORE%.zip
set TEMP_DIR=package_temp_%MSSV%

echo.
echo [1/5] Building latest JAR...
call build.bat

echo.
echo [2/5] Preparing temporary folder: %TEMP_DIR%...
if exist %TEMP_DIR% rmdir /s /q %TEMP_DIR%
mkdir %TEMP_DIR%
mkdir %TEMP_DIR%\src
mkdir %TEMP_DIR%\data
mkdir %TEMP_DIR%\git_commits_screenshots

echo [3/5] Copying required project submission files...
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

echo [4/5] Creating zip package %ZIP_NAME%...
if exist %ZIP_NAME% del %ZIP_NAME%
powershell -Command "Compress-Archive -Path '%TEMP_DIR%\*' -DestinationPath '%ZIP_NAME%'"

echo [5/5] Cleaning up temporary directory...
rmdir /s /q %TEMP_DIR%

echo.
echo ====================================================================
echo   PACKAGING COMPLETE!
echo   Submission file created: %ZIP_NAME%
echo ====================================================================
pause