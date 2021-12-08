set VECTORCAST_DIR=C:\VCAST_2018_SP1\
set VECTOR_LICENSE_FILE=%VECTORCAST_DIR%\FLEXlm\vector-DESKTOP-E4B1JSV.lic
REM set VECTOR_LICENSE_FILE=27000@<LICENSE_SERVER>
REM Configurazione directory del compilatore
set PATH=%PATH%;%VECTORCAST_DIR%
REM Fine configurazione
REM Entra nella dir degli Env 
%VECTORCAST_DIR%\FLEXlm\lmgrd.exe -c %VECTOR_LICENSE_FILE%
rem RCaputo I'll wait 15 seconds to let lmgrd server start correctly
timeout 15