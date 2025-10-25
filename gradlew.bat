\
@ECHO OFF
SET DIR=%~dp0
SET CLASSPATH=%DIR%gradle\wrapper\gradle-wrapper.jar
SET JAVA_EXE=java
IF NOT EXIST "%CLASSPATH%" (
  ECHO Missing gradle-wrapper.jar (Android Studio will regenerate it on sync).
)
"%JAVA_EXE%" -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
