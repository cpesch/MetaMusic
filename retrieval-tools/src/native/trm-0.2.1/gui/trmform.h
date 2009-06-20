//---------------------------------------------------------------------------

#ifndef trmformH
#define trmformH
//---------------------------------------------------------------------------
#include <Classes.hpp>
#include <Controls.hpp>
#include <StdCtrls.hpp>
#include <Forms.hpp>
#include <Dialogs.hpp>
#include <Menus.hpp>
#include <ComCtrls.hpp>
//---------------------------------------------------------------------------
#define WM_INITAPP  (WM_USER + 1)
//---------------------------------------------------------------------------
class TTRMFormGUI : public TForm
{
__published:	// IDE-managed Components
    TEdit *fileEdit;
    TEdit *trmEdit;
    TLabel *Label1;
    TOpenDialog *openDialog;
    TMainMenu *mainMenu;
    TMenuItem *File1;
    TMenuItem *Help1;
    TMenuItem *openMenuItem;
    TMenuItem *N1;
    TMenuItem *exitMenuItem;
    TMenuItem *aboutMenuItem;
    TLabel *Label2;
    TMenuItem *edit3;
    TMenuItem *copyMenuItem;
    TStatusBar *statusBar;
    void __fastcall exitMenuItemClick(TObject *Sender);
    void __fastcall openMenuItemClick(TObject *Sender);
    void __fastcall aboutMenuItemClick(TObject *Sender);
    void __fastcall copyMenuItemClick(TObject *Sender);
    void __fastcall FormShow(TObject *Sender);
    void __fastcall initPlayer(TMessage &Message);
    
private:	// User declarations
    void __fastcall openFile(const AnsiString &fileName);
    void __fastcall setStatus(const AnsiString &status);
    void __fastcall WMDropFiles(TWMDropFiles &Message);
public:		// User declarations

  BEGIN_MESSAGE_MAP
    MESSAGE_HANDLER(WM_DROPFILES,TWMDropFiles,WMDropFiles)
    MESSAGE_HANDLER(WM_INITAPP, TMessage, initPlayer);
  END_MESSAGE_MAP(TForm)

    __fastcall TTRMFormGUI(TComponent* Owner);
};
//---------------------------------------------------------------------------
extern PACKAGE TTRMFormGUI *TRMFormGUI;
//---------------------------------------------------------------------------
#endif
