/* --------------------------------------------------------------------------

   TRM from WAV Generator

   Copyright (C) 2002 Robert Kaye
   Portions (C) 2001 "John Cantrill" <thejohncantrill@hotmail.com>

   Large chunks of this code were grabbed from the Public Domain
   mp3 analisys portions of Bitzi's Bitcollider.

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
   Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

     $Id: wav_trm.cpp 9 2003-10-13 12:45:19Z cpesch $

----------------------------------------------------------------------------*/
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <assert.h>
#include <errno.h>
#ifdef WIN32
#include <windows.h>
#endif
#pragma hdrstop

#include <musicbrainz/mb_c.h>
#include "trm.h"
#include "wav_trm.h"

#define DB Debug_v("%s:%d", __FILE__, __LINE__);

#ifndef WIN32
#define WAVE_FORMAT_PCM 1
typedef unsigned int DWORD;
typedef unsigned char BYTE;
#define MAKEFOURCC(ch0,ch1,ch2,ch3) ((DWORD)(BYTE)(ch0) | ((DWORD)(BYTE)(ch1) << 8) | ((DWORD)(BYTE)(ch2) << 16) | ((DWORD)(BYTE)(ch3) << 24 ))
struct WAVEFORMAT
{
   unsigned short     wFormatTag;
   unsigned short     nChannels;
   DWORD              nSamplesPerSec;
   DWORD              nAvgBytesPerSec;
   unsigned short     nBlockAlign;
};
#endif

#define min(a,b) ((a) < (b) ? (a) : (b))

#ifdef WIN32
    typedef __int64   mb_int64_t;
#else				            
    typedef long long mb_int64_t;
#endif

/*-------------------------------------------------------------------------*/

const int BUFFER_LEN = 4096;

/*-------------------------------------------------------------------------*/

int Wav_generateTRM(char *fileName, char *ascii_sig, unsigned long *durationArg,
					char *proxyServer, int proxyPort)
{
    TRMWavGenerator *wavGen;
    string           trm;
    int              ret;

    memset (ascii_sig, '\0', 37);
    wavGen = new TRMWavGenerator(string(fileName));
    ret = wavGen->Generate(trm, durationArg, proxyServer, proxyPort);
    if (ret == TRM_OK)
    {
        strcpy(ascii_sig, trm.c_str());
        delete wavGen;
        return TRM_OK;
    }
    delete wavGen;

    return ret;
}

TRMWavGenerator::TRMWavGenerator(const string &fileName)
                :TRMGeneratorBase(fileName)
{
    trm = trm_New();
}

TRMWavGenerator::~TRMWavGenerator(void)
{
}

int TRMWavGenerator::Generate(string &trmId, unsigned long *durationArg,
                              char *proxyServer, int proxyPort)
{
    FILE          *source;
    unsigned char  buffer[100], *copyBuffer;
    unsigned int   bytes;
    unsigned long  ulRIFF;
    unsigned long  ulLength;
    unsigned long  ulWAVE;
    unsigned long  ulType;
    unsigned long  ulCount;
    unsigned long  ulLimit;
    bool           haveWaveHeader = false;
    unsigned long  waveSize = 0;
    WAVEFORMAT     waveFormat;
    int            toRead;
    mb_int64_t     fileLen = 0;

    source = fopen(fileName.c_str(), "rb");
    if (source == NULL)
    {
       errorString = strdup("File not found");
       fclose(source);
       return TRM_FILENOTFOUND;
    }

    fseek(source, 0, SEEK_END);
    fileLen = ftell(source);
    fseek(source, 0, SEEK_SET);

    if (fread(buffer, 1, 12, source) != 12)
    {
       errorString = strdup("File is too short");
       fclose(source);
       return TRM_DECODEERR;
    }

    ulRIFF = (unsigned long)(((unsigned long *)buffer)[0]);
    ulLength = (unsigned long)(((unsigned long *)buffer)[1]);
    ulWAVE = (unsigned long)(((unsigned long *)buffer)[2]);

    if(ulRIFF != MAKEFOURCC('R', 'I', 'F', 'F') ||
       ulWAVE != MAKEFOURCC('W', 'A', 'V', 'E'))
    {
       errorString = strdup("File is not in WAVE format");
       fclose(source);
       return TRM_DECODEERR;
    }

    // Run through the bytes looking for the tags
    ulCount = 0;
    ulLimit = ulLength - 4;
    while (ulCount < ulLimit && waveSize == 0)
    {
       if (fread(buffer, 1, 8, source) != 8)
       {
          errorString = strdup("File is too short");
          fclose(source);
          return TRM_DECODEERR;
       }

       ulType   = (unsigned long)(((unsigned long *)buffer)[0]);
       ulLength = (unsigned long)(((unsigned long *)buffer)[1]);
       switch (ulType)
       {
          // format
          case MAKEFOURCC('f', 'm', 't', ' '):
             if (ulLength < sizeof(WAVEFORMAT))
             {
                errorString = strdup("File is too short");
                fclose(source);
                return TRM_DECODEERR;
             }

             if (fread(&waveFormat, 1, ulLength, source) != ulLength)
             {
                errorString = strdup("File is too short");
                fclose(source);
                return TRM_DECODEERR;
             }

             if (waveFormat.wFormatTag != WAVE_FORMAT_PCM)
             {
                errorString = strdup("Unsupported WAV format");
                fclose(source);
                return TRM_DECODEERR;
             }
             haveWaveHeader = true;

             ulCount += ulLength;
             break;

          // data
          case MAKEFOURCC('d', 'a', 't', 'a'):
             waveSize = ulLength;
             break;

          default:
             fseek(source, ulLength, SEEK_CUR);
             break;

       }
    }

    if (!haveWaveHeader)
    {
       errorString = strdup("Could not find WAV header");
       fclose(source);
       return TRM_DECODEERR;
    }

	if (strlen(proxyServer) > 0 && proxyPort != 0)
		 trm_SetProxy(trm, proxyServer, proxyPort);

    trm_SetPCMDataInfo(trm, waveFormat.nSamplesPerSec,
                            waveFormat.nChannels,
                            (waveFormat.nBlockAlign / waveFormat.nChannels) * 8);

    fileLen -= (mb_int64_t)ftell(source);
    fileLen /= waveFormat.nChannels;
    fileLen /= (waveFormat.nBlockAlign / waveFormat.nChannels);

	 if (durationArg == NULL || *durationArg == 0)
    {
       if (durationArg)
     		 *durationArg = (fileLen * 1000) / waveFormat.nSamplesPerSec;

       fileLen /= waveFormat.nSamplesPerSec;
       trm_SetSongLength(trm, fileLen);
       //fprintf(stderr, "Duration: %d\n", fileLen);
    }
    else
    {
       //fprintf(stderr, "Duration: %d\n", *durationArg / 1000);
       trm_SetSongLength(trm, *durationArg / 1000);
    }

    copyBuffer = (unsigned char*)malloc(BUFFER_LEN);
    if (copyBuffer == NULL)
    {
       errorString = strdup("Cannot allocate buffer space.");
       return TRM_OTHERERROR;
    }

    for(;;)
    {
        toRead = min(waveSize, (unsigned long)BUFFER_LEN);
        if (toRead <= 0)
           break;

        bytes = fread(copyBuffer, 1, toRead, source);
        if (bytes <= 0)
           break;

        if (trm_GenerateSignature(trm, (char *)copyBuffer, bytes))
        {
           break;
        }
        waveSize -= toRead;
    }
    free(copyBuffer);
    fclose(source);

    trmId = string("");

    int ret = trm_FinalizeSignature(trm, (char *)trmidRaw, NULL);
    ret = (ret == 0) ? TRM_OK : TRM_CANNOTCONNECT;

    trm_ConvertSigToASCII(trm, (char *)trmidRaw, (char *)trmid);
    trm = NULL;
    trm_Delete(trm);

    trmId = string((char *)trmid);

    return ret;
}


