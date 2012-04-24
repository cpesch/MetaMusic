#!/bin/sh

gcc discid.c -c -o discid.o -fPIC -Wall -O2 -I${JAVA_HOME}/include/ -I${JAVA_HOME}/include/solaris

gcc discid.o -shared -o libdiscidsol.so
