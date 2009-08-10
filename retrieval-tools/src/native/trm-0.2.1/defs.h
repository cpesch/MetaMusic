#ifndef DEFS_H
#define DEFS_H

#include <string>

using namespace std;

struct Metadata
{
    string artist;
    string album;
    string track;
    int    trackNum;
    string trackId;
    string fileTrm;
    string fileName;
    unsigned long duration;

    Metadata::Metadata(void) { trackNum = 0; };
    Metadata &operator=(const Metadata &other)
    {
        artist = other.artist;
        album = other.album;
        track = other.track;
        trackNum = other.trackNum;
        trackId = other.trackId;
        fileTrm = other.fileTrm;
        fileName = other.fileName;
        duration = other.duration;

        return *this;
    };

    bool operator==(const Metadata &other)
    {
        if (artist == other.artist &&
            album == other.album &&
            track == other.track &&
            trackNum == other.trackNum &&
            trackId == other.trackId &&
            fileTrm == other.fileTrm &&
            fileName == other.fileName &&
            duration == other.duration)
            return true;

        return false;
    };

    void clear(void)
    {
        artist = "";
        album = "";
        track = "";
        trackNum = 0;
        fileTrm = "";
        fileName = "";
        duration = 0;
    }
};

#endif
