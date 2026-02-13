10 REM CE-158 Communication Example
20 SETCOM 9600, 8, 1, 0: REM Configure serial port
30 SETDEV "COM": REM Set device to COM port
40 TRANSMIT "AT": REM Send AT command
50 IF INSTAT THEN 70: REM Check for input
60 GOTO 50: REM Wait for response
70 A$=COM$: REM Read response
80 PRINT A$
90 END
