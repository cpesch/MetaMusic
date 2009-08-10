/****************************************************************************
 
   trm - the TRM digital finger print utilty 
  
   Copyright (C) 2001, 2002 Bertrand Petit 
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

   $Id: mp3_trm.cpp 9 2003-10-13 12:45:19Z cpesch $

 ***************************************************************************/

#include "config.h"

/****************************************************************************
 * madlld.c -- A simple program decoding an mpeg audio stream to 16-bit     *
 * PCM from stdin to stdout. This program is just a simple sample           *
 * demonstrating how the low-level libmad API can be used.                  *
 *--------------------------------------------------------------------------*
 * (c) 2001, 2002 Bertrand Petit                                            *
 *                                                                          *
 * Redistribution and use in source and binary forms, with or without       *
 * modification, are permitted provided that the following conditions       *
 * are met:                                                                 *
 *                                                                          *
 * 1. Redistributions of source code must retain the above copyright        *
 *    notice, this list of conditions and the following disclaimer.         *
 *                                                                          *
 * 2. Redistributions in binary form must reproduce the above               *
 *    copyright notice, this list of conditions and the following           *
 *    disclaimer in the documentation and/or other materials provided       *
 *    with the distribution.                                                *
 *                                                                          *
 * 3. Neither the name of the author nor the names of its contributors      *
 *    may be used to endorse or promote products derived from this          *
 *    software without specific prior written permission.                   *
 *                                                                          *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS''       *
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED        *
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A          *
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR       *
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,             *
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT         *
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF         *
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND      *
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,       *
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT       *
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF       *
 * SUCH DAMAGE.                                                             *
 *                                                                          *
 ****************************************************************************/
#ifdef HAVE_LIBMAD

/****************************************************************************
 * Includes                                                                 *
 ****************************************************************************/
#include <stdio.h>
#include <string.h>
#include <errno.h>
#ifdef WIN32
#include <winsock.h>
#endif

#include "trm.h"
#include "mad.h"
#include <musicbrainz/mb_c.h>

/****************************************************************************
 * Global variables.                                                        *
 ****************************************************************************/
const char *ProgName;

/****************************************************************************
 * Return an error string associated with a mad error code.                 *
 ****************************************************************************/
/* Mad version 0.14.2b introduced the mad_stream_errorstr() function.
 * For previous library versions a replacement is provided below.
 */
#if (MAD_VERSION_MAJOR>=1) || ((MAD_VERSION_MAJOR==0) && (((MAD_VERSION_MINOR==14) && (MAD_VERSION_PATCH>=2)) || (MAD_VERSION_MINOR>14)))
#define MadErrorString(x) mad_stream_errorstr(x)
#else
static const char *
MadErrorString (const struct mad_stream *Stream)
{
    switch (Stream->error)
        {
            /* Generic unrecoverable errors. */
        case MAD_ERROR_BUFLEN:
            return ("input buffer too small (or EOF)");
        case MAD_ERROR_BUFPTR:
            return ("invalid (null) buffer pointer");
        case MAD_ERROR_NOMEM:
            return ("not enough memory");

            /* Frame header related unrecoverable errors. */
        case MAD_ERROR_LOSTSYNC:
            return ("lost synchronization");
        case MAD_ERROR_BADLAYER:
            return ("reserved header layer value");
        case MAD_ERROR_BADBITRATE:
            return ("forbidden bitrate value");
        case MAD_ERROR_BADSAMPLERATE:
            return ("reserved sample frequency value");
        case MAD_ERROR_BADEMPHASIS:
            return ("reserved emphasis value");

            /* Recoverable errors */
        case MAD_ERROR_BADCRC:
            return ("CRC check failed");
        case MAD_ERROR_BADBITALLOC:
            return ("forbidden bit allocation value");
        case MAD_ERROR_BADSCALEFACTOR:
            return ("bad scalefactor index");
        case MAD_ERROR_BADFRAMELEN:
            return ("bad frame length");
        case MAD_ERROR_BADBIGVALUES:
            return ("bad big_values count");
        case MAD_ERROR_BADBLOCKTYPE:
            return ("reserved block_type");
        case MAD_ERROR_BADSCFSI:
            return ("bad scalefactor selection info");
        case MAD_ERROR_BADDATAPTR:
            return ("bad main_data_begin pointer");
        case MAD_ERROR_BADPART3LEN:
            return ("bad audio data length");
        case MAD_ERROR_BADHUFFTABLE:
            return ("bad Huffman table select");
        case MAD_ERROR_BADHUFFDATA:
            return ("Huffman data overrun");
        case MAD_ERROR_BADSTEREO:
            return ("incompatible block_type for JS");

            /* Unknown error. This swich may be out of sync with libmad's
             * defined error codes.
             */
        default:
            return ("Unknown error code");
        }
}
#endif



/*
 * The following utility routine performs simple rounding, clipping, and
 * scaling of MAD's high-resolution samples down to 16 bits. It does not
 * perform any dithering or noise shaping, which would be recommended to
 * obtain any exceptional audio quality. It is therefore not recommended to
 * use this routine if high-quality output is desired.
 */

static signed int scale(mad_fixed_t sample)
{
  /* round */
  sample += (1L << (MAD_F_FRACBITS - 16));

  /* clip */
  if (sample >= MAD_F_ONE)
    sample = MAD_F_ONE - 1;
  else if (sample < -MAD_F_ONE)
    sample = -MAD_F_ONE;

  /* quantize */
  return sample >> (MAD_F_FRACBITS + 1 - 16);
}


/****************************************************************************
 * Main decoding loop. This is where mad is used.                           *
 ****************************************************************************/
#define INPUT_BUFFER_SIZE   (5*8192)
#define OUTPUT_BUFFER_SIZE  8192    /* Must be an integer multiple of 4. */
static int GenTRMFromFile (FILE * InputFp, char *ascii_sig, int duration,
					       char *proxyServer, int proxyPort)
{
    struct mad_stream Stream;
    struct mad_frame Frame;
    struct mad_synth Synth;
    mad_timer_t Timer;
    unsigned char InputBuffer[INPUT_BUFFER_SIZE],
    OutputBuffer[OUTPUT_BUFFER_SIZE], *OutputPtr = OutputBuffer;
    const unsigned char *OutputBufferEnd = OutputBuffer + OUTPUT_BUFFER_SIZE;
    int Status = TRM_OTHERERROR, i, got_trm = 0;
    unsigned long FrameCount = 0;

    trm_t trm;
    char signature[17];

    /* First the structures used by libmad must be initialized. */
    mad_stream_init (&Stream);
    mad_frame_init (&Frame);
    mad_synth_init (&Synth);
    mad_timer_reset (&Timer);


    /* now the musicbrainz stuff */
    trm = trm_New ();

	if (strlen(proxyServer) > 0 && proxyPort != 0)
		 trm_SetProxy(trm, proxyServer, proxyPort);

    /* Decoding options can here be set in the options field of the
     * Stream structure.
     */

    /* This is the decoding loop. */
    do
    {
        /* The input bucket must be filled if it becomes empty or if
         * it's the first execution of the loop.
         */
        if (Stream.buffer == NULL || Stream.error == MAD_ERROR_BUFLEN)
        {
            size_t ReadSize, Remaining;
            unsigned char *ReadStart;

            /* {1} libmad may not consume all bytes of the input
             * buffer. If the last frame in the buffer is not wholly
             * contained by it, then that frame's start is pointed by
             * the next_frame member of the Stream structure. This
             * common situation occurs when mad_frame_decode() fails,
             * sets the stream error code to MAD_ERROR_BUFLEN, and
             * sets the next_frame pointer to a non NULL value. (See
             * also the comment marked {2} bellow.)
             *
             * When this occurs, the remaining unused bytes must be
             * put back at the beginning of the buffer and taken in
             * account before refilling the buffer. This means that
             * the input buffer must be large enough to hold a whole
             * frame at the highest observable bit-rate (currently 448
             * kb/s). XXX=XXX Is 2016 bytes the size of the largest
             * frame? (448000*(1152/32000))/8
             */
            if (Stream.next_frame != NULL)
            {
                Remaining = Stream.bufend - Stream.next_frame;
                memmove (InputBuffer, Stream.next_frame, Remaining);
                ReadStart = InputBuffer + Remaining;
                ReadSize = INPUT_BUFFER_SIZE - Remaining;
            }
            else
                ReadSize = INPUT_BUFFER_SIZE,
            ReadStart = InputBuffer, Remaining = 0;

            /* Fill-in the buffer. If an error occurs print a message
             * and leave the decoding loop. If the end of stream is
             * reached we also leave the loop but the return status is
             * left untouched.
             */
            ReadSize = fread (ReadStart, 1, ReadSize, InputFp);
            if (ReadSize <= 0)
            {
                if (ferror (InputFp))
                {
                    Status = TRM_DECODEERR;
                }
                break;
            }

            /* Pipe the new buffer content to libmad's stream decoder
             * facility.
             */
            mad_stream_buffer (&Stream, InputBuffer, ReadSize + Remaining);
            Stream.error = MAD_ERROR_NONE;
        }

        /* Decode the next mpeg frame. The streams is read from the
         * buffer, its constituents are break down and stored the the
         * Frame structure, ready for examination/alteration or PCM
         * synthesis. Decoding options are carried in the Frame
         * structure from the Stream structure.
         *
         * Error handling: mad_frame_decode() returns a non zero value
         * when an error occurs. The error condition can be checked in
         * the error member of the Stream structure. A mad error is
         * recoverable or fatal, the error status is checked with the
         * MAD_RECOVERABLE macro.
         *
         * {2} When a fatal error is encountered all decoding
         * activities shall be stopped, except when a MAD_ERROR_BUFLEN
         * is signaled. This condition means that the
         * mad_frame_decode() function needs more input to achieve
         * it's work. One shall refill the buffer and repeat the
         * mad_frame_decode() call. Some bytes may be left unused at
         * the end of the buffer if those bytes forms an incomplete
         * frame. Before refilling, the remainign bytes must be moved
         * to the begining of the buffer and used for input for the
         * next mad_frame_decode() invocation. (See the comments marked
         * {1} earlier for more details.)
         *
         * Recoverable errors are caused by malformed bit-streams, in
         * this case one can call again mad_frame_decode() in order to
         * skip the faulty part and re-sync to the next frame.
         */
        if (mad_frame_decode (&Frame, &Stream))
        {
            if (MAD_RECOVERABLE (Stream.error))
            {
                continue;
            }
            else if (Stream.error == MAD_ERROR_BUFLEN)
                continue;
            else
            {
                Status = TRM_DECODEERR;
                break;
            }
        }
        /* The characteristics of the stream's first frame is printed
         * on stderr. The first frame is representative of the entire
         * stream.
         */
        if (FrameCount == 0)
        {
            /* Always use 2 channels, since the code below converts
               mono files to stereo */
            trm_SetPCMDataInfo (trm, Frame.header.samplerate, 2, 16);
            trm_SetSongLength(trm, (long)duration);
            //fprintf(stderr, "Duration: %d\n", duration);
        }

        /* Accounting. The computed frame duration is in the frame
         * header structure. It is expressed as a fixed point number
         * whole data type is mad_timer_t. It is different from the
         * samples fixed point format and unlike it, it can't directly
         * be added or substracted. The timer module provides several
         * functions to operate on such numbers. Be careful there, as
         * some functions of mad's timer module receive some of their
         * mad_timer_t arguments by value!
         */
        FrameCount++;
        mad_timer_add (&Timer, Frame.header.duration);

        /* Once decoded the frame is synthesized to PCM samples. No errors
         * are reported by mad_synth_frame();
         */
        mad_synth_frame (&Synth, &Frame);

        /* Synthesized samples must be converted from mad's fixed
         * point number to the consumer format. Here we use unsigned
         * 16 bit big endian integers on two channels. Integer samples
         * are temporarily stored in a buffer that is flushed when
         * full.
         */
        for (i = 0; i < Synth.pcm.length; i++)
        {
            signed int Sample;

            /* Left channel */
            Sample = scale(Synth.pcm.samples[0][i]);

            *(OutputPtr++) = (Sample >> 0) & 0xff;
            *(OutputPtr++) = (Sample >> 8) & 0xff;

            /* Right channel. If the decoded stream is monophonic then
             * the right output channel is the same as the left one.
             */
            if (MAD_NCHANNELS (&Frame.header) == 2)
                Sample = scale(Synth.pcm.samples[1][i]);

            *(OutputPtr++) = (Sample >> 0) & 0xff;
            *(OutputPtr++) = (Sample >> 8) & 0xff;

            /* Flush the buffer if it is full. */
            if (OutputPtr == OutputBufferEnd)
            {
                OutputPtr = OutputBuffer;

                if (trm_GenerateSignature(trm, (char *)OutputBuffer, OUTPUT_BUFFER_SIZE))
                {
                    if (trm_FinalizeSignature(trm, signature, NULL))
                        Status = TRM_CANNOTCONNECT;
                    else
                        Status = TRM_OK;

                    got_trm = 1;
                    break;
                }
            }
        }
    }
    while (!got_trm);

    /* Mad is no longer used, the structures that were initialized must
     * now be cleared.
     */
    mad_synth_finish (&Synth);
    mad_frame_finish (&Frame);
    mad_stream_finish (&Stream);

    /* If the output buffer is not empty and no error occured during
     * the last write, then flush it.
     */
    if (!got_trm && OutputPtr != OutputBuffer)
    {
        size_t BufferSize = OutputPtr - OutputBuffer;
        trm_GenerateSignature(trm, (char *)OutputBuffer, BufferSize);
	}
	if (!got_trm)
	{
        if (trm_FinalizeSignature(trm, signature, NULL) == 0)
            Status = TRM_OK;
        else
            Status = TRM_CANNOTCONNECT;
    }

    trm_ConvertSigToASCII (trm, signature, ascii_sig);
    trm_Delete (trm);

    return (Status);
}

int MP3_generateTRM(char *fileName, char *ascii_sig, unsigned long *durationArg, 
					char *proxyServer, int proxyPort)
{
    FILE         *file;
    int           ret;
    int           duration, bitrate, stereo, samplerate;
    musicbrainz_t mb;

    memset (ascii_sig, '\0', 37);

    if (durationArg == NULL || *durationArg == 0)
    {
        mb = mb_New();
        ret = mb_GetMP3Info(mb, fileName, &duration, &bitrate, &stereo, &samplerate); 
        mb_Delete (mb);
    }
    else
        duration = *durationArg;

    if (!ret || duration == 0)
        return TRM_DECODEERR;

    file = fopen (fileName, "rb");
    if (file == NULL)
    {
        return TRM_FILENOTFOUND;
    }
    ret = GenTRMFromFile (file, ascii_sig, duration / 1000, proxyServer, proxyPort);
    fclose(file);

	if (durationArg)
		*durationArg = (unsigned long)duration;

	return ret;
}

#endif
