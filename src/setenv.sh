#!/bin/bash

echo $(basename $0)
echo $1

if [ "$(basename $0)" = "setenv.sh" ] && [ "$1" = "" ]; then
    . setenv.sh setenv
    exit 1
fi

echo "Setting up environment ..."

export JAVA_HOME=/opt/java/jdk1.5.0
export ANT_HOME="${PWD}/bin/ant"
export PATH=$ANT_HOME/bin:$PATH
export ANT_OPTS="-Xmx384m"
export IDEA_HOME="/home/andrep/Apps/idea"
export PATH="${JAVA_HOME}/bin:${PWD}/bin:${PATH}"

echo IDEA_HOME: $IDEA_HOME

which java javac ant rmiregistry
ant -version
java -version
ant help



