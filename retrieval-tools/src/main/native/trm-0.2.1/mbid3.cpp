//---------------------------------------------------------------------------
// Portions (C) EMusic.com 2000
//---------------------------------------------------------------------------
#include "config.h"

#ifdef HAVE_LIBID3

#include <stdio.h>
#include "mbid3.h"

#define DB Debug_v("%s:%d", __FILE__, __LINE__);
const int iDataFieldLen = 255;

//---------------------------------------------------------------------------

struct ID3v1
{
    char id[3];
    char track[30];
    char artist[30];
    char album[30];
    char year[4];
    char comment[28];
    char zero;
    char trackNum;
    char genre;
};

//---------------------------------------------------------------------------

ID3::ID3(bool writeV1, bool writeV2)
{
    this->writeV1 = writeV1;
    this->writeV2 = writeV2;
}

//---------------------------------------------------------------------------

bool ID3::read(const string &file, Metadata &data)
{
    ID3Tag     *pTag;
    ID3Frame   *pFrame;
    char       *pData;
    char       *ptr;
    ID3Field   *pField;

    pTag = ID3Tag_New();
    int ret = ID3Tag_Link(pTag, file.c_str());
    if (ret <= 0)
    {

       ID3Tag_Delete(pTag);
       return readV1(file, data);
    }

    pData = new char[iDataFieldLen];

    pFrame = ID3Tag_FindFrameWithID(pTag, ID3FID_TITLE);
    if (pFrame)
    {
        pData[0] = 0;
        pField = ID3Frame_GetField(pFrame, ID3FN_TEXT);
        ID3Field_GetASCII(pField, pData, iDataFieldLen);
        if (strlen(pData) > 0)
           data.track = string(pData);
    }

    pFrame = ID3Tag_FindFrameWithID(pTag, ID3FID_ALBUM);
    if (pFrame)
    {
        pData[0] = 0;
        pField = ID3Frame_GetField(pFrame, ID3FN_TEXT);
        ID3Field_GetASCII(pField, pData, iDataFieldLen);
        if (strlen(pData) > 0)
           data.album = string(pData);
    }

    // Pull artist out of LEADARTIST if it exists
    pFrame = ID3Tag_FindFrameWithID(pTag, ID3FID_LEADARTIST);
    if (pFrame)
    {
        pData[0] = 0;
        pField = ID3Frame_GetField(pFrame, ID3FN_TEXT);
        ID3Field_GetASCII(pField, pData, iDataFieldLen);
        if (strlen(pData) > 0)
           data.artist = string(pData);
    }
    else // No?, Ok, try to pull artist out of BAND if it exists
    pFrame = ID3Tag_FindFrameWithID(pTag, ID3FID_BAND);
    if (pFrame)
    {
        pData[0] = 0;
        pField = ID3Frame_GetField(pFrame, ID3FN_TEXT);
        ID3Field_GetASCII(pField, pData, iDataFieldLen);
        if (strlen(pData) > 0)
           data.artist = string(pData);
    }

    pFrame = ID3Tag_FindFrameWithID(pTag, ID3FID_TRACKNUM);
    if (pFrame)
    {
        pData[0] = 0;
        pField = ID3Frame_GetField(pFrame, ID3FN_TEXT);
        ID3Field_GetASCII(pField, pData, iDataFieldLen);
        if (strlen(pData) > 0)
           data.trackNum = atoi(pData);
    }

    // Loop over all the tags to see if we have multiple Unique id tags
    ID3TagIterator *iter;

    iter = ID3Tag_CreateIterator(pTag);
    if (iter)
    {
        for(;;)
        {
            pFrame = ID3TagIterator_GetNext(iter);
            if (pFrame == NULL)
               break;

            if (ID3Frame_GetID(pFrame) == ID3FID_UNIQUEFILEID)
            {
                pData[0] = 0;
                pField = ID3Frame_GetField(pFrame, ID3FN_OWNER);
                ID3Field_GetASCII(pField, pData, iDataFieldLen);
                if (strcmp(pData, "musicbrainz") == 0)
                {
                   pField = ID3Frame_GetField(pFrame, ID3FN_DATA);
                   pData[0] = 0;
                   ID3Field_GetBINARY(pField, (unsigned char *)pData, 
                                      iDataFieldLen);
                   if (pData[0] != 0)
                   {
                       data.trackId = pData;
                       break;
                   }
                }
            }
        }

        ID3TagIterator_Delete(iter);
    }

    delete [] pData;
    ID3Tag_Delete(pTag);

    return true;
}

//---------------------------------------------------------------------------

bool ID3::write(const string  &fileName,
                const Metadata    &data)
{
    ID3Tag     *pTag;
    ID3Frame   *pFrame;
    char        pData[255];
    char       *ptr;
    ID3Field   *pField;
    int         flag = 0;

    if (writeV1)
        flag |= ID3TT_ID3V1;
    if (writeV2)
        flag |= ID3TT_ID3V2;

    if (flag == 0)
       return true;    

    pTag = ID3Tag_New();
    int ret = ID3Tag_Link(pTag, fileName.c_str());
	if (ret < 0)
	{
		ID3Tag_Delete(pTag);
		return false;
	}

    pFrame = ID3Tag_FindFrameWithID(pTag, ID3FID_TITLE);
    if (!pFrame)
    {
        pFrame = ID3Frame_NewID(ID3FID_TITLE);
        pField = ID3Frame_GetField(pFrame, ID3FN_TEXT);
        ID3Field_SetASCII(pField, data.track.c_str());
        ID3Tag_AttachFrame(pTag, pFrame);
    }
    else
    {
        pField = ID3Frame_GetField(pFrame, ID3FN_TEXT);
        ID3Field_SetASCII(pField, data.track.c_str());
    }

    pFrame = ID3Tag_FindFrameWithID(pTag, ID3FID_ALBUM);
    if (!pFrame)
    {
        pFrame = ID3Frame_NewID(ID3FID_ALBUM);
        pField = ID3Frame_GetField(pFrame, ID3FN_TEXT);
        ID3Field_SetASCII(pField, data.album.c_str());
        ID3Tag_AttachFrame(pTag, pFrame);
    }
    else
    {
        pField = ID3Frame_GetField(pFrame, ID3FN_TEXT);
        ID3Field_SetASCII(pField, data.album.c_str());
    }

    pFrame = ID3Tag_FindFrameWithID(pTag, ID3FID_LEADARTIST);
    if (!pFrame)
    {
        pFrame = ID3Frame_NewID(ID3FID_LEADARTIST);
        pField = ID3Frame_GetField(pFrame, ID3FN_TEXT);
        ID3Field_SetASCII(pField, data.artist.c_str());
        ID3Tag_AttachFrame(pTag, pFrame);
    }
    else
    {
        pField = ID3Frame_GetField(pFrame, ID3FN_TEXT);
        ID3Field_SetASCII(pField, data.artist.c_str());
    }

    char text[10];
    sprintf(text, "%d", data.trackNum);
    pFrame = ID3Tag_FindFrameWithID(pTag, ID3FID_TRACKNUM);
    if (!pFrame)
    {
        pFrame = ID3Frame_NewID(ID3FID_TRACKNUM);
        pField = ID3Frame_GetField(pFrame, ID3FN_TEXT);
        ID3Field_SetASCII(pField, text);
        ID3Tag_AttachFrame(pTag, pFrame);
    }
    else
    {
        pField = ID3Frame_GetField(pFrame, ID3FN_TEXT);
        ID3Field_SetASCII(pField, text);
    }

    ID3TagIterator *iter;
    bool wroteId = false;
    iter = ID3Tag_CreateIterator(pTag);
    if (iter)
    {
        for(;;)
        {
            pFrame = ID3TagIterator_GetNext(iter);
            if (pFrame == NULL)
               break;

            if (ID3Frame_GetID(pFrame) == ID3FID_UNIQUEFILEID)
            {
                pData[0] = 0;
                pField = ID3Frame_GetField(pFrame, ID3FN_OWNER);
                ID3Field_GetASCII(pField, pData, iDataFieldLen);
                if (strcmp(pData, "musicbrainz") == 0)
                {
                    pField = ID3Frame_GetField(pFrame, ID3FN_DATA);
                    ID3Field_SetBINARY(pField, 
                                       (unsigned char *)data.trackId.c_str(),
                                       data.trackId.size() + 1);
                    wroteId = true;
                    break;
                }
            }
        }

        ID3TagIterator_Delete(iter);
    }

    if (!wroteId)
    {
        pFrame = ID3Frame_NewID(ID3FID_UNIQUEFILEID);
        pField = ID3Frame_GetField(pFrame, ID3FN_OWNER);
        ID3Field_SetASCII(pField, "musicbrainz");

        pField = ID3Frame_GetField(pFrame, ID3FN_DATA);
        ID3Field_SetBINARY(pField, (unsigned char *)data.trackId.c_str(), 
                           data.trackId.size() + 1);
        ID3Tag_AttachFrame(pTag, pFrame);
    }

    ID3_Err err = ID3Tag_UpdateByTagType(pTag, flag);
    ID3Tag_Delete(pTag);

    setError(err);
    return (err == ID3E_NoError);
}

void ID3::setError(ID3_Err err)
{
    switch(err)
    {
        case ID3E_NoError:
            errString = "No error";
        break;
        case ID3E_NoMemory:
            errString = "No available memory";
        break;
        case ID3E_NoData:
            errString = "No data to parse";
        break;
        case ID3E_BadData:
            errString = "Improperly formatted data";
        break;
        case ID3E_NoBuffer:
            errString = "No buffer to write to";
        break;
        case ID3E_SmallBuffer:
            errString = "Buffer is too small";
        break;
        case ID3E_InvalidFrameID:
            errString = "Invalid frame id";
        break;
        case ID3E_FieldNotFound:
            errString = "Requested field not found";
        break;
        case ID3E_UnknownFieldType:
            errString = "Unknown field type";
        break;
        case ID3E_TagAlreadyAttached:
            errString = "Tag is already attached to a file";
        break;
        case ID3E_InvalidTagVersion:
            errString = "Invalid tag version";
        break;
        case ID3E_NoFile:
            errString = "No file to parse";
        break;
        case ID3E_ReadOnly:
            errString = "Attempting to write to a read-only file";
        break;
        case ID3E_zlibError:
            errString = "Error in compression/uncompression";
        break;
        default:
            errString = "Unknown error";
        break;
    }
}

bool ID3::readV1(const string &file, Metadata &data)
{
    FILE  *mp3;
    ID3v1  id3v1;

    mp3 = fopen(file.c_str(), "rb");
    if (mp3 == NULL)
       return false;

    memset(&id3v1, 0, sizeof(id3v1));
    fseek(mp3, -128, SEEK_END);

    if (fread(&id3v1, sizeof(char), 128, mp3) < 128)
    {
        fclose(mp3);
        return false;
    }

    fclose(mp3);
    if (id3v1.id[0] != 'T' || id3v1.id[1] != 'A' || id3v1.id[2] != 'G')
        return false;

    id3v1.artist[29] = 0;
    id3v1.album[29] = 0;
    id3v1.track[29] = 0;

    data.artist = id3v1.artist;
    data.album = id3v1.album;
    data.track = id3v1.track;
    if (id3v1.zero == 0)
       data.trackNum = id3v1.trackNum;
    else
       data.trackNum = 0;

    return true;
}

#endif
