#!/bin/bash

CLASSPATH=bin
for f in lib/*.jar ; do
  CLASSPATH="$CLASSPATH:$f"
done

java -cp $CLASSPATH at.okfn.uncomtrade.UNComtrade "$@"
