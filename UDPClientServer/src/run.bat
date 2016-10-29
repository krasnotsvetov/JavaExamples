javac ru\ifmo\ctddev\krasnotsvetov\udpapp\HelloUDPServer.java
pause
set salt=
set /P salt="salt:"
java -cp *;. info.kgeorgiy.java.advanced.hello.Tester server ru.ifmo.ctddev.krasnotsvetov.udpapp.HelloUDPServer %salt%
pause