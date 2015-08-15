VERSION 5.00
Begin VB.Form frmIntro 
   BorderStyle     =   0  'None
   Caption         =   "Continents - The Game (by Minh Hoang)"
   ClientHeight    =   840
   ClientLeft      =   0
   ClientTop       =   0
   ClientWidth     =   3840
   ClipControls    =   0   'False
   ControlBox      =   0   'False
   Icon            =   "frmIntro.frx":0000
   LinkTopic       =   "Form1"
   MaxButton       =   0   'False
   MinButton       =   0   'False
   ScaleHeight     =   840
   ScaleWidth      =   3840
   ShowInTaskbar   =   0   'False
   StartUpPosition =   2  'CenterScreen
   Begin VB.Image imgIntro 
      BorderStyle     =   1  'Fixed Single
      Height          =   810
      Left            =   0
      Picture         =   "frmIntro.frx":0442
      Top             =   0
      Width           =   3810
   End
End
Attribute VB_Name = "frmIntro"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = True
Attribute VB_Exposed = False
Private Declare Function ShellExecute Lib "shell32.dll" Alias "ShellExecuteA" (ByVal hWnd As Long, ByVal lpOperation As String, ByVal lpFile As String, ByVal lpParameters As String, ByVal lpDirectory As String, ByVal nShowCmd As Long) As Long

Private Sub imgIntro_Click()
Cnt = 0
Res = 32
While Cnt < 10 And Res <= 32
    Res = ShellExecute(Me.hWnd, "", "javaw", "-jar JarLoader.jar mhgame.jar", App.Path, 0)
Wend
End
End Sub
