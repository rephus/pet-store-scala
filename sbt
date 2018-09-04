java -Dfile.encoding=UTF8 -XX:MaxPermSize=256M -Xmx1536M -Xss1M -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled -jar `dirname $0`/sbt-launch.jar "$@"

