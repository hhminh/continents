cls
javac -g:none -O -deprecation *.java
REM jar cvfm0 mhgame.jar MANIFEST.MF *.class *.ini images/*.gif sounds/*.wav sounds/*.mid
jar cvfm mhgame.jar MANIFEST.MF *.class *.ini images/*.gif sounds/*.wav sounds/*.mid
javadoc -private -d documents *.java