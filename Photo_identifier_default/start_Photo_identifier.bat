@echo off
chcp 65001 >nul

REM Установить переменные для JAR-файла и конфигурационного файла
SET JAR_PATH=C:\Java_Project_finished\Photo_capture\Photo_identifier_default\Photo_identifier.jar
SET CONFIG_FILE=src\main\resources\config.properties

REM Проверка наличия JAR-файла
if not exist %JAR_PATH% (
    echo JAR-файл не найден в папке %JAR_PATH%
    pause
    exit /b
)

REM Проверка существования конфигурационного файла
IF NOT EXIST "%CONFIG_FILE%" (
    echo Первый запуск: Создание конфигурационного файла...
    REM Запуск программы с отображением консоли
    java -jar %JAR_PATH%
) ELSE (
    echo Последующий запуск: Окно консоли не требуется.
    REM Запуск программы без консоли
    javaw -jar %JAR_PATH%
)

