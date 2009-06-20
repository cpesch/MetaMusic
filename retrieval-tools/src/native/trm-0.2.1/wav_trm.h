/****************************************************************************
 
   trm - the TRM digital finger print utilty 
  
   Copyright (C) 2001, 2002 Bertrand Petit                                          *
   Portions Copyright (C) 2002 Myers Carpenter
   Portions Copyright (C) 2002 Robert Kaye

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

   $Id: wav_trm.h 9 2003-10-13 12:45:19Z cpesch $

 ***************************************************************************/

#ifndef _WAV_TRM_H_ 
#define _WAV_TRM_H_ 

#include <string>
#include <musicbrainz/mb_c.h>
#include "trmgenbase.h"

/*-------------------------------------------------------------------------*/

class TRMWavGenerator : public TRMGeneratorBase
{
   public:

                    TRMWavGenerator(const string &fileName);
                   ~TRMWavGenerator(void);

     int            Generate(string &trmId, unsigned long *duration,
                             char *proxyServer, int proxyPort);

   private:

     unsigned       samplerate;
     unsigned char  trmid[37];
     unsigned char  trmidRaw[17];
     trm_t          trm;
};

#endif


