# Copyright (C) 2001 Robert Kaye
# 
# $Id: Makefile.am 9 2003-10-13 12:45:19Z cpesch $
#
AUTOMAKE_OPTIONS = foreign

bin_PROGRAMS = trm
trm_SOURCES = mp3_trm.cpp main.cpp ov_trm.cpp wav_trm.cpp mbid3.cpp
trm_LDADD = -lm @ID3_LIBS@

clean:
	rm -f $(top_srcdir)/trm *.o

cvsclean: distclean
	rm -f configure Makefile.in install-sh missing mkinstalldirs

install:
	$(INSTALL) -s -m 0655 trm $(prefix)/bin	
