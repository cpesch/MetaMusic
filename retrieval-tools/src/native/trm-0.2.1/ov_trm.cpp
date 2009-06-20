/****************************************************************************
 
   trm - the TRM digital finger print utilty 
  
   Copyright (C) 2002 Robert Kaye
   Portions Copyright (C) 2000 Emusic.com 

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

   $Id: ov_trm.cpp 9 2003-10-13 12:45:19Z cpesch $

 ***************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "config.h"
#include "trm.h"
#ifdef HAVE_OGGVORBIS

#include <musicbrainz/mb_c.h>
#include <vorbis/vorbisfile.h>

const int decodeSize = 8192;

/* The callback functions were used since the ov_open call simply crashed and burned */
size_t ReadFunc(void *ptr, size_t size, size_t nmemb, void *datasource)
{
	return fread(ptr, size, nmemb, (FILE *)datasource);
}

int SeekFunc(void *datasource, ogg_int64_t offset, int whence)
{
	return fseek((FILE *)datasource, (int)offset, whence);
}


int CloseFunc(void *datasource)
{
	return fclose((FILE *)datasource);
}

long TellFunc(void *datasource)
{
	return ftell((FILE *)datasource);
}

int OggVorbis_generateTRM(char *fileName, char *ascii_sig, unsigned long *durationArg,
						  char *proxyServer, int proxyPort)
{
   OggVorbis_File   vf;
   vorbis_info     *vi;
   int              section, ret, count = 0;
   char            *buffer;
   FILE            *in;
   trm_t            trm;
   bool             done;
   char             sig[17];
   ov_callbacks     callbacks;
   ogg_int64_t      duration;

   memset (ascii_sig, '\0', 37);

   in = fopen(fileName, "rb");
   if (in == NULL)
      return TRM_FILENOTFOUND;

   trm = trm_New();

   if (strlen(proxyServer) > 0 && proxyPort != 0)
   	 trm_SetProxy(trm, proxyServer, proxyPort);

   callbacks.read_func = ReadFunc;
   callbacks.seek_func = SeekFunc;
   callbacks.close_func = CloseFunc;
   callbacks.tell_func = TellFunc;

   memset(&vf, 0, sizeof(vf));
   if (ov_open_callbacks(in, &vf, NULL, 0, callbacks) < 0)
       return TRM_DECODEERR;

   vi = ov_info(&vf, -1);
   trm_SetPCMDataInfo(trm, vi->rate, vi->channels, 16);

   if (durationArg == NULL || *durationArg == 0)
   {
       duration = ov_pcm_total(&vf, -1);
       if (duration > 0)
       {
          if (durationArg)
             *durationArg = (unsigned long)((duration * 1000) / vi->rate);

          duration = duration / vi->rate;
       }
   }
   else
       duration = *durationArg / 1000;

   if (duration > 0)
   {
       //fprintf(stderr, "Duration: %d\n", duration);
       trm_SetSongLength(trm, (long)duration);
   }

   buffer = new char[decodeSize * 2];
   done = false;
   while(!done)
   {
      ret = ov_read(&vf, buffer, decodeSize, 0, 2, 1, &section);
      if (ret == 0)
      {
          break;
      }
      if (ret < 0)
      {
          continue;
      }

      count += ret;
      done = (bool)trm_GenerateSignature(trm, buffer, ret);
   }

   ov_clear(&vf);

   if (ret >= 0)
   {
      ret = trm_FinalizeSignature(trm, sig, NULL);
      ret = (ret == 0) ? TRM_OK : TRM_CANNOTCONNECT;
   }
   else
	  ret = TRM_DECODEERR;

   trm_ConvertSigToASCII (trm, sig, ascii_sig);

   trm_Delete(trm);
   delete buffer;

   return ret;
}

#endif
