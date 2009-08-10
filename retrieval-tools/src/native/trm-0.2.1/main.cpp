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

   $Id: main.cpp 9 2003-10-13 12:45:19Z cpesch $

 ***************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <musicbrainz/mb_c.h>
#include <musicbrainz/browser.h>
#include "config.h"
#include "defs.h"
#include "trm.h"
#include "mbid3.h"

#ifndef WIN32
#define stricmp(s,d) strcasecmp(s,d)
#include <unistd.h>
#else
#include <winsock.h>
#include <io.h>
#endif

enum FileType { eUnknown, eMP3, eOggVorbis, eWav };

char *EscapeFormValue(const char *form_value)
{
    int i, form_value_length, extra_length;
    char *escaped_value, *ptr;

    form_value_length = strlen(form_value);
    for (i = 0, extra_length = 0; i < form_value_length; ++i)
    {
        switch(form_value[i])
        {
            case '"':
                extra_length += 5;
                break;
            case '&':
                extra_length += 4;
                break;
            case '<':
            case '>':
                extra_length += 3;
                break;
        }
    }

    if (extra_length == 0)
    {
        // This is necessary since the caller must free the memory.
        return strdup(form_value);
    }

    escaped_value = (char *)malloc(form_value_length + extra_length + 1);
    for (i = 0, ptr = escaped_value; i < form_value_length; ++i)
    {
        switch(form_value[i])
        {
            case '"':
                strcpy(ptr, "&quot;");
                ptr += 6;
                break;
            case '&':
                strcpy(ptr, "&amp;");
                ptr += 5;
                break;
            case '<':
                strcpy(ptr, "&lt;");
                ptr += 4;
                break;
            case '>':
                strcpy(ptr, "&gt;");
                ptr += 4;
                break;
            default:
                *(ptr++) = form_value[i];
        }
    }
    *ptr = 0;

    return escaped_value;
}

bool CreateLookupPage(const char *fileName, Metadata &data, 
                      const char *server)
{
    FILE *out;
    char *escapedUrl, *temp;

    out = fopen(fileName, "wt");
    if (!out)
        return false;

    fprintf(out,
            "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n"
            "<HTML><HEAD><TITLE>MusicBrainz Lookup</TITLE></HEAD>\n"
            "<BODY onLoad=\"document.forms[0].submit()\">\n"
            "<center>\n"
            "<h3>MusicBrainz Loookup</h3><p>\n"
            "Looking up file at MusicBrainz.\n"
            "<FORM METHOD=\"POST\"\n"
            "      ACTION=\"http://%s/taglookup.html\"\n"
            "      ENCTYPE=\"application/x-www-form-urlencoded\" "
            "      class=\"formstyle\">\n", server);

    temp = EscapeFormValue(data.artist.c_str());
    fprintf(out, 
            "  <INPUT TYPE=\"hidden\" NAME=\"artist\" VALUE=\"%s\">\n",
            temp);
    free(temp);

    temp = EscapeFormValue(data.album.c_str());
    fprintf(out, 
            "  <INPUT TYPE=\"hidden\" NAME=\"album\" VALUE=\"%s\">\n",
            temp);
    free(temp);

    temp = EscapeFormValue(data.track.c_str());
    fprintf(out, 
            "  <INPUT TYPE=\"hidden\" NAME=\"track\" VALUE=\"%s\">\n",
            temp);
    free(temp);

    temp = EscapeFormValue(data.trackId.c_str());
    fprintf(out, 
            "  <INPUT TYPE=\"hidden\" NAME=\"trackid\" VALUE=\"%s\">\n",
            temp);
    free(temp);

    temp = EscapeFormValue(data.fileName.c_str());
    fprintf(out, 
            "  <INPUT TYPE=\"hidden\" NAME=\"filename\" VALUE=\"%s\">\n",
            temp);
    free(temp);

    fprintf(out, 
            "  <INPUT TYPE=\"hidden\" NAME=\"duration\" VALUE=\"%d\">\n"
            "  <INPUT TYPE=\"hidden\" NAME=\"tracknum\" VALUE=\"%d\">\n"
            "</form>\n"
            "</center>\n"
            "</body>\n"
            "</html>\n", data.duration, data.trackNum);

    fclose(out);


    return true;
}

void ReadMetadata(char *fileName, Metadata &data, FileType type, unsigned long duration)
{
    char     *server;
    int       port;
    string    file("/tmp/lookup.html");

    data.fileName = string(fileName);
    data.duration = duration;
    switch(type)
    {
        case eMP3:
        {
#ifdef HAVE_LIBID3
            ID3 id3(true, true);

            id3.read(string(fileName), data);
#endif
            break;
        }
        case eOggVorbis:
        {
            ; // Not implemented
        }
        case eWav:
        {
            ; // Not much to do -- has no metadata.
        }
        default:
            printf("This filetype is currently not supported.\n");
            break;
    }
}

void TagLookup(char *fileName, FileType type, unsigned long duration)
{
    Metadata  data;
    char     *server;
    int       port;
    string    file("/tmp/lookup.html");

    if (getenv("MB_SERVER"))
        server = getenv("MB_SERVER");
    else
        server = "musicbrainz.org";

    ReadMetadata(fileName, data, type, duration); 

    printf("  Artist: '%s'\n", data.artist.c_str());
    printf("   Album: '%s'\n", data.album.c_str());
    printf("   Track: '%s'\n", data.track.c_str());
    printf("TrackNum: '%d'\n", data.trackNum);
    printf(" TrackId: '%s'\n", data.trackId.c_str());
    printf("Duration: '%d'\n", data.duration);

    CreateLookupPage(file.c_str(), data, server);
#ifdef WIN32
	ShellExecute(NULL, "open", file.c_str(), NULL, NULL, SW_SHOWNORMAL);
#else
    LaunchBrowser(file.c_str(), "mozilla");
#endif
}

void PrintMetadata(char *fileName, FileType type, unsigned long duration)
{
    Metadata  data;
    string    file("/tmp/lookup.html");

    ReadMetadata(fileName, data, type, duration); 

    printf("%s\n", data.artist.c_str());
    printf("%s\n", data.album.c_str());
    printf("%s\n", data.track.c_str());
    printf("%d\n", data.trackNum);
    printf("%d\n", data.duration);
}

int main(int argc, char *argv[])
{
    char          sig[37], *ptr;
    int           index = 1, isLookup = 0, printID3 = 0;
    unsigned long duration = 0;
    FileType      type = eUnknown;

    if (argc < 2)
    {
        fprintf(stderr,"usage: trm [-m|-o|-w|-l|-d] "
                       "<mp3 | ogg/vorbis | wav file>\n");
        fprintf(stderr,"Options:\n  -m   Input file is mp3\n");
        fprintf(stderr,"  -i   Print out track metadata along with trm id\n");
        fprintf(stderr,"  -o   Input file is Ogg/Vorbis\n");
        fprintf(stderr,"  -w   Input file is WAV\n");
        fprintf(stderr,"  -l   Lookup file at MusicBrainz\n");
        fprintf(stderr,"  -d   Specify the duration of the file. Use only\n"
                       "       if the input file is a partial file.\n");
        return -1;
    }

#ifdef WIN32
    WSADATA  sGawdIHateMicrosoft;
    WSAStartup(0x0002,  &sGawdIHateMicrosoft);
#endif

    for(; index < argc; index++)
    {
        if (stricmp(argv[index], "-m") == 0)
           type = eMP3;
        else
        if (stricmp(argv[index], "-o") == 0)
           type = eOggVorbis;
        else
        if (stricmp(argv[index], "-w") == 0)
           type = eWav;
        else 
        if (stricmp(argv[index], "-l") == 0)
           isLookup = 1;
        else
        if (stricmp(argv[index], "-i") == 0)
           printID3 = 1;
        else
        if (stricmp(argv[index], "-d") == 0)
           duration = atoi(argv[++index]);
        else
           break;
    }

    if (index >= argc)
    {
        fprintf(stderr, "error: no lookup file specified.\n");
        exit(-1);
    }
    if (access(argv[index], 0))
    {
        fprintf(stderr, "Cannot open file %s\n", argv[index]);
        return (-1);
    }

    if (type == eUnknown)
    {
        ptr = strrchr(argv[index], '.');
        if (ptr && stricmp(ptr, ".mp3") == 0)
           type = eMP3;

        if (ptr && stricmp(ptr, ".ogg") == 0)
           type = eOggVorbis;

        if (ptr && stricmp(ptr, ".wav") == 0)
           type = eWav;

        if (ptr == NULL && type == eUnknown)
        {
            fprintf(stderr, "error: cannot determine audio file type.\n");
            fprintf(stderr, "       use -m, -o, -w to set the file type.\n");
            return(-1); 
        }                             
    }

    if (isLookup)
    {
#ifdef HAVE_LIBID3
        TagLookup(argv[index], type, duration);
#else
        fprintf(stderr, "error: lookup (id3) support not built into application\n");
#endif
    }
    else
    switch(type)
    {
        case eMP3:
        {
#ifdef HAVE_LIBMAD
            int ret;

            ret = MP3_generateTRM(argv[index], sig, &duration, "", 0);
            if (ret == 0)
                printf("%s\n", sig);
            else
                fprintf(stderr, "error: failed to generate TRM id\n");
#else
            fprintf(stderr, "error: mp3 support not built into application\n");
#endif
            break;
        }
        case eOggVorbis:
        {
#ifdef HAVE_OGGVORBIS
            int ret;

            ret = OggVorbis_generateTRM(argv[index], sig, &duration, "", 0);
            if (ret == 0)
                printf("%s\n", sig);
            else
                fprintf(stderr, "error: failed to generate TRM id\n");
#else
            fprintf(stderr, "error: ogg/vorbis support not built into application\n");
#endif
            break;
        }
        case eWav:
        {
            int ret;

            ret = Wav_generateTRM(argv[index], sig, &duration, "", 0);
            if (ret == 0)
                printf("%s\n", sig);
            else
                fprintf(stderr, "error: failed to generate TRM id\n");
            break;
        }
    }

    if (printID3)
    {
#ifdef HAVE_LIBID3
        PrintMetadata(argv[index], type, duration);
#else
        fprintf(stderr, "error: lookup (id3) support not built into application\n");
#endif
    }

#ifdef WIN32
    WSACleanup();
#endif

    return 0;
}
