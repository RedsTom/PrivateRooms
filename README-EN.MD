# PrivateRooms

## How to edit ?

### 1 : Clone project 

Execute the ``git clone https://github.com/RedsTom/PrivateRooms.git`` command or download the zip file

### 2 : Open project with your IDE
### → 2.1 Idea
With Intellij Idea, open the folder downloaded and import the gradle project

### → 2.2 Eclipse
With Eclipse, open the parent folder as workspace and import the gradle project

### 3 : Setup the config.toml
At the first run, a file called ``config.toml`` will be created at the root of your project : open it and write your token inside :
```toml
[bot]
langs = ["en", "fr"]
token = "TOKEN HERE" <- Write your token here
```
### 4 : Start coding !
Enjoy modifying PrivateRooms

## How to use it ?

Prerequisites : Install the JDK version 8 or 1.8 : 
[Official Website](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html) 
[Adopt OpenJDK](https://adoptopenjdk.net/?variant=openjdk8&jvmVariant=hotspot) 
[Amazon Corretto](https://docs.aws.amazon.com/corretto/latest/corretto-8-ug/downloads-list.html)

### 1 : Clone project 

Execute the ``git clone https://github.com/RedsTom/PrivateRooms.git`` command or download the zip file

### 2 : Build the project
### → 2.1 : Windows
### ⇢ 2.1.1 Open a CMD in the folder
You can follow the following instructions
![win](http://ql.redstom.fr/zer56zrdsAE/dev/win.gif)

### ⇢ 2.1.2 Build the project
Write ``gradlew shadowJar`` in the cmd. When the build is finished, you can close the cmd
![win](http://ql.redstom.fr/zer56zrdsAE/dev/win2.gif)

### ⇢ 2.1.3 Go into the ``build\libs`` folder and reopen a cmd
![win](http://ql.redstom.fr/zer56zrdsAE/dev/win3.gif)
![win](http://ql.redstom.fr/zer56zrdsAE/dev/win4.gif)

### ⇢ 2.1.4 Run ``java -jar [jar file name]`` and, when an error appear, close the cmd
![win](http://ql.redstom.fr/zer56zrdsAE/dev/win5.gif)

### ⇢ 2.1.5 Now, open the ``config.toml`` file and provide your token : 
```toml
[bot]
langs = ["en", "fr"]
token = "TOKEN HERE" <- Write your token here
```
You must also copy the ``.lang`` files in this folder. (Obsolete gif)
![win](http://ql.redstom.fr/zer56zrdsAE/dev/win6.gif)

### ⇢ 2.1.6 Now, in your CMD into the folder ``build\libs``, you can rerun the jar file like in the step ``2.1.4``. And your bot is ready to work :)
![win](http://ql.redstom.fr/zer56zrdsAE/dev/win7.gif)

### → 2.2 Linux / Mac
### ⇢ Follow the same steps, but open the Terminal and go to folder manually
