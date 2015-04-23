#! /bin/sh

TOMCAT_PATH=/home/nandana/servers/ldp4ro

echo cleaning the old files
if [ -e "$TOMCAT_PATH/webapps/ldp4j.war" ]; then
  echo "Deleting the file $TOMCAT_PATH/webapps/ldp4j.war"
  rm $TOMCAT_PATH/webapps/ldp4j.war
fi
if [ -e "$TOMCAT_PATH/webapps/ldp4j" ]; then
  echo "Deleting the directory $TOMCAT_PATH/webapps/ldp4j"
  rm -rf $TOMCAT_PATH/webapps/ldp4j
fi
if [ -e "$TOMCAT_PATH/webapps/ldp4ro.war" ]; then
  echo "Deleting the file $TOMCAT_PATH/webapps/ldp4ro.war"
  rm $TOMCAT_PATH/webapps/ldp4ro.war
fi
if [ -e "$TOMCAT_PATH/webapps/ldp4ro" ]; then
  echo "Deleting the directory $TOMCAT_PATH/webapps/ldp4ro"
  rm -rf $TOMCAT_PATH/webapps/ldp4ro
fi

echo "Copying the the LDP4j webapp to $TOMCAT_PATH/webapps"
cp ../ldp4j-generic/target/ldp4j.war $TOMCAT_PATH/webapps

echo "Copying the the LDP4ROs webapp to $TOMCAT_PATH/webapps"
cp ../ldp4ro-webapp/target/ldp4ro-webapp-0.0.1-SNAPSHOT.war $TOMCAT_PATH/webapps/ldp4ro.war

echo "Staring the server .."
/home/nandana/servers/ldp4ro/bin/catalina.sh run