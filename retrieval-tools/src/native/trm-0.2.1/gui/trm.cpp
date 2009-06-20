//---------------------------------------------------------------------------

#include <vcl.h>
#pragma hdrstop
USERES("trm.res");
USEFORM("trmform.cpp", TRMFormGUI);
USEFORM("about.cpp", AboutForm);
USELIB("trm.lib");
//---------------------------------------------------------------------------
WINAPI WinMain(HINSTANCE, HINSTANCE, LPSTR, int)
{
    try
    {
         Application->Initialize();
         Application->Title = "TRM Generator";
         Application->CreateForm(__classid(TTRMFormGUI), &TRMFormGUI);
         Application->Run();
    }
    catch (Exception &exception)
    {
         Application->ShowException(&exception);
    }
    return 0;
}
//---------------------------------------------------------------------------
