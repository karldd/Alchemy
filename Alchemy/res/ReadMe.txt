Alchemy 
ALPHA 008 PRE




ABOUT --------------------------------
Alchemy is an open drawing project aimed at exploring how we can sketch, 
draw, and create on computers in new ways. Alchemy isn't software for 
creating finished artwork, but rather a sketching environment that focuses 
on the absolute initial stage of the creation process. Experimental in nature, 
Alchemy lets you brainstorm visually to explore an expanded range of ideas 
and possibilities in a serendipitous way.

INSTALLATION -------------------------
Simply copy the Alchemy folder to wherever you keep your applications.
Windows users please make sure the path to Alchemy does not contain any non-standard
characters such as the exclamation mark (!), this has been known to cause problems
launching the application. If your user name has non-standard characters, 
try putting Alchemy directly into the root directory.
The 'modules' (and the 'lib' folder on Windows) folder must remain together 
with the application. Individual modules can be removed if not required, 
however be sure to not mix older modules with newer versions or vice versa.

CREDITS/LICENSE ----------------------
Alchemy was initiated by Karl D.D. Willis & Jacob Hina. 
The source code is released under the GNU General Public License and 
Copyright © 2007-2009 Karl D.D. Willis. 
Please see the 'COPYING' file for the full license.

WEB ----------------------------------
Alchemy website: http://al.chemy.org/
Download Alchemy: http://al.chemy.org/download/
Alchemy Forum: http://al.chemy.org/forum/

Version History
--------------------------------------
ALPHA 008
??.??.2009

CHANGES
+ Persian Localisation added - Thanks to Omid Saadat!

--------------------------------------
ALPHA 007.2
06.05.2009

BUG FIXES
+ Gradient Module Colour fixes
+ Fixed bug where PDF files containing transparency had their colours converted to CMYK and back again, resulting in incorrect colours

--------------------------------------
ALPHA 007.1
27.04.2009

CHANGES
+ JPen Tablet library updated - now with support for 64bit Windows machines

BUG FIXES
+ Corrupt PDF files when using the Gradient module fixed
+ Plugin Comparator error handling added to fix errors on launch
+ Mirror Vista UI fixes

--------------------------------------
ALPHA 007
09.04.2009

FEATURES
+ Pressure Shapes module for drawing shapes using pressure from a tablet
+ Gradient module for adding colour gradients to shapes
+ Load a background image from the view menu to draw on top of

CHANGES
+ Colour button has been streamlined and combined with the background/foreground button
+ Line Weight spinner has been redesigned
+ Sliders and number spinners can now be controlled with the mouse scroll wheel
+ The max/min values of sliders and number spinners can be set by CTRL/COMMAND clicking them and inputing the values in the popup
+ Warning added when using File > New with shapes on the canvas
+ Localisations added for Simplified Chinese/Traditional Chinese/Spanish/Dutch/French/German
  Thanks to Chen Luo/Zafio/Dean/Ika/Seppel Schorsch for those!
+ Pullshapes Size slider added to better control shape size
+ Displace module now behaves better with spine-based (variable line width) shapes
+ Trace Shapes images are now scaled correctly and local images can also be loaded

BUG FIXES
+ Fixed several issues caused by custom cursors stopping Alchemy from launching
+ Fixed a bug causing the tool bar to disappear completely when being reattached
+ Colour selector will now appear in the correct window on dual monitor systems


--------------------------------------
ALPHA 006
11.12.2008

FEATURES
+ Jpeg export functionality added
+ Session file names can now be set in the options/preferences
+ Transparent Fullscreen mode added
+ Colour eye dropper added
+ Pull Shapes module added

CHANGES
+ Colour Switcher module 'Constant' button added, it can now change colours constantly when the pen is dragged
+ Options/Preferences interface redesigned
+ Fullscreen mode now hides the mac menubar
+ Changing the canvas size during a session now changes the canvas size of the session PDF
+ JPen library added (but not yet used), to allow pen pressure/tilt access
+ Camera colour module has been removed because of numerous bugs, it may be readded if we find a good java video library
+ Colour Picker popup and secondary window redesigned and standard across all platforms now
+ Shapes folder added to store PDF shapes used by PullShapes
+ iText PDF library update
+ Eye dropper can now be started with the I shortcut key

BUG FIXES
+ Mirror module transparency bug fix
+ Switch menus opening properly in the specified application, not the default application
+ PDF colour accuracy fix, PDF colour format set to DEVICERGB
+ Shortcuts interface bug fixed
+ Language bundle loading fix 
+ Dialogs added for when the modules folder is missing


--------------------------------------
ALPHA 005
26.06.2008

FEATURES
+ Draw over/under existing shapes mode 
+ Colour Switcher Module
+ Simple interface mode introduced, aimed at kidz!

CHANGES
+ Preferences/Options window added to change inteface mode
+ Modules can now be turned on and off in the preferences/options window
+ A default list of modules can be created in modules/modules.txt

BUG FIXES
+ Session timer now saves correctly only when the canvas has changed
+ Minor interface fixes (especially for Linux)

--------------------------------------
ALPHA 004
03.05.2008

FEATURES
+ Load PDF session
+ Camera Colour affect module
+ Detach Shapes create module
+ Scrawl Shapes create module

CHANGES
+ Module selection shortcuts added
+ Sub Toolbar slider changed to a custom UI element
+ Alchemy now requires Java Version 5+
+ Export transparent PNG files

BUG FIXES
+ Mirror module fixed to work with Camera Colour module
+ Warning dialog added when overwriting files
+ Japanese localisation fixes

--------------------------------------
ALPHA 003.1
26.03.2008
 
FEATURES
+ Hide cursor function

BUG FIXES
+ Keyboard shortcuts (fullscreen, recording) fixed
+ Displace module now working with straight shapes
+ Record indicator is no longer showing off screen on windows

--------------------------------------
ALPHA 003
24.03.2008

FEATURES
+ Colour added - new colour picker in the tool bar
+ Foreground / Background button in the tool bar lets you draw with the background colour
+ Keyboard Shortcuts can now be assigned by the user
+ Displace affect module added

CHANGES
+ Performance has been greatly improved when drawing with many shapes on the canvas
+ Line smoothing can be turned on and off in the settings menu
+ Help system has been changed to the FLOSS manual Alchemy.pdf file in the Alchemy folder
+ Export menu can now create a PNG file as well as a PDF

BUG FIXES
+ Mirror module bug when using median shapes fixed
+ Warning dialog when closing Alchemy now functions properly
+ Trace Shapes now shows a warning dialog when a network connection fails

--------------------------------------
ALPHA 002
02.03.2008

FEATURES
+ The toolbar is now detachable into a seperate palette window
+ Trace Shapes / Speed Shapes / X Shapes modules added
+ Copy function to copy the canvas to the clipboard as a bitmap
+ Alchemy Help added
+ Japanese Localisation
+ Canvas smoothing option added
+ Background colour is now changeable

CHANGES
+ PDF saving functionality improved. PDF files are now viewable throughout
+ Interface improvements, icon changes
+ Random module now has a distortion slider
+ Type Shapes module can now create shapes using the mouse and keyboard
+ More robust microphone detection and more accurate sound levels

BUG FIXES
+ Toolbar hiding functionality improved
+ Toolbar repainting when drawing 'blind' fixed
+ Shortcut key mappings fixed
+ Fullscreen mode on a mac is now stable

--------------------------------------
ALPHA 001
30.01.2008
 
Initial Release