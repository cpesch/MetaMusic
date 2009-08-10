/****************************************************************************
 
   trm - the TRM digital finger print utilty 
  
   Copyright (C) 2002 Robert Kaye

   This program is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public License
   as published by the Free Software Foundation; either version 2
   of the License, or (at your option) any later version. 
   
   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details. 
   
   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software 
   Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111, USA.

   $Id: trm.h 9 2003-10-13 12:45:19Z cpesch $

 ***************************************************************************/

#ifndef __TRM_TRM_H_
#define __TRM_TRM_H_

/* Return codes returned by the XXX_generateTRM functions */
#define TRM_OK             0
#define TRM_FILENOTFOUND  -1
#define TRM_DECODEERR     -2
#define TRM_CANNOTCONNECT -3
#define TRM_OTHERERROR    -4

#ifdef __cplusplus
extern "C"
{
#endif
    
  /* For each of these functions, the duration argument can be used to either
     override the length of the file itself (if its only a partial file), or
     to retrieve the length of the file. Your options are:

       1. Pass in NULL for duration, and the function will calculate the
          duration of the file from the file.
       2. Pass in a pointer to an unsigned long and the value pointed by that
          unsigned long is 0. In this case the function will calculate the
          length of the file and return the length in the value pointed to
          by duration.
       3. Pass in a pointer to an unsigned long and the value is non-zero. This
          will cause the function to skip the duration calculation and use
          the passed in duration for the TRM generation.

     In each case, the duration passed in or returned from these functions
     is in milliseconds!

  */
  int MP3_generateTRM(char *fileName, char *ascii_sig, unsigned long *duration, 
                      char *proxyServer, int proxyPort);
  int OggVorbis_generateTRM(char *fileName, char *ascii_sig, unsigned long *duration,
                      char *proxyServer, int proxyPort);
  int Wav_generateTRM(char *fileName, char *ascii_sig, unsigned long *duration,
                      char *proxyServer, int proxyPort);

#ifdef __cplusplus
}
#endif

#endif
