#!/bin/bash -e
# resolve links - $0 may be a softlink
if [ -z "$COINSTACK_SIGNON_HOME" ];then
  PRG="$0"
  while [ -h "$PRG" ] ; do
    ls=$(ls -ld "$PRG")
    link=$(expr "$ls" : '.*-> \(.*\)$')
    if expr "$link" : '/.*' > /dev/null; then
      PRG="$link"
    else
      PRG=$(dirname "$PRG")/"$link"
    fi
  done

  cd "$(dirname "$PRG")" || exit 1
  SCRIPT_HOME="$(pwd)"
  export SCRIPT_HOME
  cd - &>/dev/null || exit 1
fi

${SCRIPT_HOME}/gradlew clean build installDist
