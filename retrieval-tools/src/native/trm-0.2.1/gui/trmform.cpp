//---------------------------------------------------------------------------

#include <vcl.h>
#include <Clipbrd.hpp>
#include <winsock.h>
#pragma hdrstop

#include "trmform.h"
#include "about.h"
#include "trm.h"
//---------------------------------------------------------------------------
#pragma package(smart_init)
#pragma resource "*.dfm"
TTRMFormGUI *TRMFormGUI;
//---------------------------------------------------------------------------
__fastcall TTRMFormGUI::TTRMFormGUI(TComponent* Owner)
    : TForm(Owner)
{
    WSADATA sGawdIHateMicrosoft;

    WSAStartup(0x0002,  &sGawdIHateMicrosoft);
}
//---------------------------------------------------------------------------
void __fastcall TTRMFormGUI::exitMenuItemClick(TObject *Sender)
{
    Close();
    WSACleanup();
}
//---------------------------------------------------------------------------
void __fastcall TTRMFormGUI::openMenuItemClick(TObject *Sender)
{
    if(openDialog->Execute())
        openFile(openDialog->FileName);
}

//---------------------------------------------------------------------------

void __fastcall TTRMFormGUI::setStatus(const AnsiString &status)
{
    statusBar->Panels->Items[0]->Text = status;
    Application->ProcessMessages();
}

//---------------------------------------------------------------------------

void __fastcall TTRMFormGUI::openFile(const AnsiString &fileName)
{
    TCursor    saveCursor;
    bool       isMP3 = false, isOv = false, isWav = false;
    char      *ptr;

    fileEdit->Text = ExtractFileName(fileName);
    trmEdit->Text = "";
    Application->ProcessMessages();

    saveCursor = Screen->Cursor;
    Screen->Cursor = crHourGlass;
    setStatus("Generating TRM...");

    ptr = strrchr(fileName.c_str(), '.');
    if (ptr && stricmp(ptr, ".mp3") == 0)
       isMP3 = true;

    if (ptr && stricmp(ptr, ".ogg") == 0)
       isOv = true;

    if (ptr && stricmp(ptr, ".wav") == 0)
       isWav = true;

    copyMenuItem->Enabled = false;
    if (isMP3 == 0 && isOv == 0 && isWav == 0)
    {
        MessageDlg("Cannot determine the file type. The file must end in "
                   ".mp3, .wav or .ogg.", mtError, TMsgDlgButtons() << mbOK, 0);
        setStatus("Cannot determine file type.");
    }
    else
    {
        char          ascii_sig[100];
        int           ret;
        unsigned long duration;

        if (isMP3)
           ret = MP3_generateTRM(fileName.c_str(), ascii_sig, &duration);
        else
        if (isOv)
           ret = OggVorbis_generateTRM(fileName.c_str(), ascii_sig, &duration);
        else
        if (isWav)
           ret = Wav_generateTRM(fileName.c_str(), ascii_sig, &duration);
        else
            ret = -1;

        if (ret == 0)
        {
            trmEdit->Text = ascii_sig;
            copyMenuItem->Enabled = true;
            setStatus("TRM generated.");
        }
        else
        {
            AnsiString msg;
            switch(ret)
            {
               case TRM_FILENOTFOUND:
                  msg = "Cannot open selected file.";
                  break;
               case TRM_DECODEERR:
                  msg = "Cannot decode the selected file. Please make sure the "
                        "file is a valid MP3, Ogg/Vorbis or WAV file.";
                  break;
               case TRM_CANNOTCONNECT:
                  msg = "Cannot connect to the TRM signature server. Please make "
                        "sure you have an active connection to the Internet.";
                  break;
               case TRM_OTHERERROR:
                  msg = "An unknown error occurred while trying to generate the TRM.";
                  break;
            }
            MessageDlg(msg, mtError, TMsgDlgButtons() << mbOK, 0);
            setStatus("TRM generation failed.");
        }
    }

    Screen->Cursor = saveCursor;
}

//---------------------------------------------------------------------------

void __fastcall TTRMFormGUI::aboutMenuItemClick(TObject *Sender)
{
    TAboutForm *aboutForm;

    aboutForm = new TAboutForm(this);
    aboutForm->ShowModal();
    delete aboutForm;
}

//---------------------------------------------------------------------------

void __fastcall TTRMFormGUI::copyMenuItemClick(TObject *Sender)
{
    Clipboard()->SetTextBuf(trmEdit->Text.c_str());
}

//---------------------------------------------------------------------------

void __fastcall TTRMFormGUI::FormShow(TObject *Sender)
{
     DragAcceptFiles(Handle, true);
     setStatus("TRM Generator ready.");

     PostMessage(Handle, WM_INITAPP, 0, 0);
}

//---------------------------------------------------------------------------

void __fastcall TTRMFormGUI::initPlayer(TMessage &Message)
{
     if (_argc > 1)
         openFile(AnsiString(_argv[1]));
}

//---------------------------------------------------------------------------

void __fastcall TTRMFormGUI::WMDropFiles(TWMDropFiles &Message)
{
   int  ilFilesCount, ilStrLength;
   char caFileName[MAX_PATH];

   // Anzahl der Dateien bestimmen:
   ilFilesCount=DragQueryFile((HDROP)Message.Drop,-1,NULL,0);

   if (ilFilesCount > 0)
   {
     // Länge des Dateinamens bestimmen:
     ilStrLength=DragQueryFile((HDROP)Message.Drop,0,NULL,0);
     // Dateinamen in char-Array caFileName übernehmen:
     DragQueryFile((HDROP)Message.Drop, 0, caFileName, ilStrLength+1);
     // ...und ab in die Listbox:
     openFile(caFileName);
   }
}

