# FontGenerator
Simple font creation application

## Dependencies
This project requires at least Java 8 to be installed, the recommended version can be downloaded [here](https://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html).

The rest of the dependencies can be installed via pip

```$bash
$ pip install fontmake pyclipper==1.0.6
```

Note: For Development you will also want to download the [recommended JDK version](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).

## Installation
Download the latest jar file from our [releases page](https://github.com/codeWonderland/FontGenerator/releases).

## Usage
Once the dependencies are in place, navigate to the jar file you downloaded earlier in any console emulator (terminals, command prompts, you get it), and run the following command:

```bash
java -jar FontGenerator.jar
```

### Keybindings
Within the app itself, there are a number of keybindings available to make the process of creating fonts easier, including:
- `ctr-r` to reset the current character
- `ctr-s` to save the current character
- left and right arrows to swap the current symbol
- up and down arrows to change to uppercase and lowercase respectively

The intention of these additions are to have them available for any kind of peripheral pen buttons, to streamline the font creation process

## Additional Notes
This program was meant to be used with a tablet and pen interface, but as long as you can click and drag any mouse or mouse-simulating peripheral will do.

