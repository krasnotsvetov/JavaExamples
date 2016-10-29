javac ru\ifmo\ctddev\krasnotsvetov\iterativeparallelism\IterativeParallelism.java
javac ru\ifmo\ctddev\krasnotsvetov\mapper\ParallelMapperImpl.java
pause
set salt=
set /P salt="salt:"
java -cp *;. info.kgeorgiy.java.advanced.mapper.Tester list ru.ifmo.ctddev.krasnotsvetov.mapper.ParallelMapperImpl,ru.ifmo.ctddev.krasnotsvetov.iterativeparallelism.IterativeParallelism %salt%
pause