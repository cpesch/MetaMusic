#!/bin/sh

gcc discid.c -c -o discid.o -fPIC -Wall -O2 -I${JAVA_HOME}/include/ -I${JAVA_HOME}/include/linux

gcc discid.o -shared -o libdiscidlnx.so


gcc ../trm_wrapper.c -c -o trm_wrapper.o -fPIC -Wall -O2 -I${JAVA_HOME}/include/ -I${JAVA_HOME}/include/linux

OBJ=`ls ../trm-0.2.1/*.o`

gcc $OBJ trm_wrapper.o -lstdc++ -lmad -lid3 -logg -lvorbis -lvorbisfile -lmusicbrainz -lc -lz -shared -o libtrmlnx.so

rm *.o
