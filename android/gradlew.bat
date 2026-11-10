@echo off
setlocal
cd /d "%~dp0"
if not exist "gradle\wrapper\gradle-wrapper.jar" (
  echo Baixando componente do Gradle Wrapper...
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -UseBasicParsing 'https://raw.githubusercontent.com/gradle/gradle/v8.7.0/gradle/wrapper/gradle-wrapper.jar' -OutFile 'gradle\wrapper\gradle-wrapper.jar'"
  if errorlevel 1 (echo ERRO: nao foi possivel baixar o Gradle Wrapper. & exit /b 1)
)
if not exist "gradle\wrapper\gradle-wrapper.jar" (echo ERRO: gradle-wrapper.jar nao encontrado. & exit /b 1)
java -classpath "gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain %*
endlocal
