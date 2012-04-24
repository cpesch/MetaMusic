object TRMFormGUI: TTRMFormGUI
  Left = 212
  Top = 844
  BorderIcons = [biSystemMenu]
  BorderStyle = bsSingle
  Caption = 'TRM Generator'
  ClientHeight = 94
  ClientWidth = 345
  Color = clBtnFace
  Font.Charset = DEFAULT_CHARSET
  Font.Color = clWindowText
  Font.Height = -11
  Font.Name = 'MS Sans Serif'
  Font.Style = []
  Menu = mainMenu
  OldCreateOrder = False
  Position = poScreenCenter
  OnShow = FormShow
  PixelsPerInch = 96
  TextHeight = 13
  object Label1: TLabel
    Left = -2
    Top = 9
    Width = 37
    Height = 13
    Alignment = taRightJustify
    AutoSize = False
    Caption = 'File:'
  end
  object Label2: TLabel
    Left = -2
    Top = 48
    Width = 37
    Height = 13
    Alignment = taRightJustify
    AutoSize = False
    Caption = 'TRM:'
  end
  object fileEdit: TEdit
    Left = 38
    Top = 6
    Width = 297
    Height = 21
    ReadOnly = True
    TabOrder = 0
  end
  object trmEdit: TEdit
    Left = 38
    Top = 45
    Width = 297
    Height = 21
    ReadOnly = True
    TabOrder = 1
  end
  object statusBar: TStatusBar
    Left = 0
    Top = 74
    Width = 345
    Height = 20
    Panels = <
      item
        Width = 600
      end>
    SimplePanel = False
  end
  object openDialog: TOpenDialog
    DefaultExt = '*.mp3'
    Filter = 
      'MP3 Files (*.mp3)|*.mp3|Ogg/Vorbis Files (*.ogg)|*.ogg|Wav Files' +
      ' (*.wav)|*.wav|Any File (*.*)|*.*'
    Left = 43
    Top = 69
  end
  object mainMenu: TMainMenu
    Left = 10
    Top = 68
    object File1: TMenuItem
      Caption = '&File'
      object openMenuItem: TMenuItem
        Caption = '&Open...'
        OnClick = openMenuItemClick
      end
      object N1: TMenuItem
        Caption = '-'
      end
      object exitMenuItem: TMenuItem
        Caption = 'E&xit'
        OnClick = exitMenuItemClick
      end
    end
    object edit3: TMenuItem
      Caption = '&Edit'
      object copyMenuItem: TMenuItem
        Caption = '&Copy'
        Enabled = False
        OnClick = copyMenuItemClick
      end
    end
    object Help1: TMenuItem
      Caption = '&Help'
      object aboutMenuItem: TMenuItem
        Caption = '&About...'
        OnClick = aboutMenuItemClick
      end
    end
  end
end
