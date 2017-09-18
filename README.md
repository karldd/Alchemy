# UPDATE - Alchemy is no longer maintained.
This repository is for archival purposes.

----

# Alchemy
Alchemy is an open drawing project aimed at exploring how we can sketch, draw, and create on computers in new ways. Alchemy isn’t software for creating finished artwork, but rather a sketching environment that focuses on the absolute initial stage of the creation process. Experimental in nature, Alchemy lets you brainstorm visually to explore an expanded range of ideas and possibilities in a serendipitous way.

![Alchemy Interface](https://i.gyazo.com/6a582f15e12f58e49c684957323accc0.gif)

## Installation

### Download
Applications releases can be found in the [releases folder](releases).
We support Windows, Mac OSX, and Linux. Alchemy requires the Java Runtime Environment version 1.5 or above.

### Install
Firstly delete your old version of Alchemy, by moving the Alchemy folder to the trash.
Then simply copy the Alchemy folder to wherever you keep your applications.

Windows users please make sure the path to Alchemy does not contain any non-standard characters such as the exclamation mark (!), this has been known to cause problems launching the application. If your user name has non-standard characters, try putting Alchemy directly into the root directory.

The 'modules' (and the 'lib' folder on Windows) folder must remain together with the application. Individual modules can be removed if not required, however be sure to not mix older modules with newer versions or vice versa.

### Documentation
Alchemy is now documented exclusively using the [Floss Manuals](http://en.flossmanuals.net/Alchemy). For a detailed and visual look at how to use Alchemy, including screenshots, example drawings, and full installation instructions:

[Alchemy Manual](http://en.flossmanuals.net/Alchemy/), also available in [Farsi](http://fa.flossmanuals.net/alchemy) and [Finnish](http://fi.flossmanuals.net/Alchemy/Introduction).

## Features

### Interaction
The Alchemy drawing canvas has an intentionally reduced level of functionality. No undo, no selecting, and no editing. Interaction focuses instead on the output of a great number of good, bad, strange and beautiful shapes. 

To take a good look at what Alchemy can (and can not) do, check out our [videos](https://www.youtube.com/user/AlchemyOrg).

### Modules
Alchemy consists of a growing number of ‘modules’ that can be added or removed at will. Using a given module you can do things like:

- Shout at the computer. Use your voice to control the width of a line or the form of a shape.
- Draw ‘blind’. Turn off the canvas display and explore what shapes emerge from the ‘darkness’.
- Create random shapes. Generate shapes that can be used as a starting point for characters, spaceships, or whatever shape you see in the ‘clouds’.
- Mirror draw. Draw mirrored symmetrical forms in realtime.
- Randomise. Mess up and distort shapes.

## Global Features
Other global features place focus on the ‘process’ of drawing, letting you do things like:

- Record a drawing ’session’. Automatically save the contents of the canvas to a page in a PDF file at set intervals.
- Auto-clear the canvas. Start drawing on a clean slate at set intervals. Force yourself to start over fresh.
- Switch the canvas. Automatically open your sketch in a more ‘conventional’ drawing application, either as a bitmap or vector file.
- Avoid distraction. Alchemy has a very minimal interface, just a simple toolbar that dissapears magically, and a fullscreen mode to block everything else out.

## About

### Who, Where, Why
Alchemy was intiated by [Karl D.D. Willis](http://www.darcy.co.nz/) and Jacob Hina as a way to explore and experiment with alternative ways of drawing. Karl has developed other experimental drawing systems such Light Tracer and TwelvePixels, while Jacob created the original ‘Symmskribbl’ mirror drawing application and is constantly making marks on paper. Alchemy is currently being developed in Tokyo, Japan, with the generous support of the [Exploratory Software Project](https://www.ipa.go.jp/english/humandev/third.html).

### Technical
Alchemy is built in Java and primarily uses the Java 2D API. It also uses a number of other open source libraries such as the Java Plugin Framework for loading modules, JPen for communicating with pen tablets, iText for PDF export, PDF Renderer for viewing PDF files, and Batik for SVG Export. Individual modules may have other requirements such as an internet connection or a microphone installed.

### License and Contributing
Alchemy is released under the GNU General Public License, making it free for everyone to use and contribute too. We are actively seeking people to develop Java modules for Alchemy and will post documentation about this online for the full public release.

### Code
We use the NetBeans IDE and include the Netbeans project files in the repository. Using the build.xml file supplied and NetBeans, everything should compile straight away on all platforms. The only dependency is Lauch4j to create .exe files for distribution on Windows.

### Modules
Information on how to write a module to extend Alchemy can be found in the [modules folder](modules).

### Javadocs
The Alchemy API documentation can be found in the [javadocs folder](javadocs).
