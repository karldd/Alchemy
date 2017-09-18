# UPDATE - Alchemy is no longer maintained.
This repository is for archival purposes.

----

# Alchemy
Alchemy is an open drawing project aimed at exploring how we can sketch, draw, and create on computers in new ways. Alchemy isn’t software for creating finished artwork, but rather a sketching environment that focuses on the absolute initial stage of the creation process. Experimental in nature, Alchemy lets you brainstorm visually to explore an expanded range of ideas and possibilities in a serendipitous way.

## Downloads
Applications releases can be found in the [releases folder](releases).
We support Windows, Mac OSX, and Linux. Alchemy requires the Java Runtime Environment version 1.5 or above.

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
