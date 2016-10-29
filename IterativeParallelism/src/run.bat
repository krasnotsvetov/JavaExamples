javac ru\ifmo\ctddev\krasnotsvetov\iterativeparallelism\IterativeParallelism.java
pause
set salt=
set /P salt="salt:"
java -cp *;. info.kgeorgiy.java.advanced.concurrent.Tester list ru.ifmo.ctddev.krasnotsvetov.iterativeparallelism.IterativeParallelism %salt%
pause