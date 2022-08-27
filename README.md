<h3 align="center">
  <img src="https://avatars.githubusercontent.com/u/78621926?s=200&v=4" width="75"><br/>
  Graven - Développement<br/>
  This project is under the <a href="https://choosealicense.com/licenses/gpl-3.0/">GNU GPL v3</a> license<br/><br/>
</h3>

# <p align="center">`PrivateRooms`</p>

All the projects in the <code>GravenDev</code> organisation are used by the discord server <code>
discord.gg/graven</code> both by the moderators and the members.
Most of the contributors are part of the staff but the members are also allowed to contribute.
  
---
## Global informations

| Global informations |                                                                                                                                                                                                                                                                       |
|---------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Description         | PrivateRooms the vocal chat management system of the Graven - Développement discord server. It can create on-demand vocal chats, allows user to edit those rooms and save the current configuration as a template for an eventual later use of the same configuration |
| Collaborators       | <img src="https://avatars.githubusercontent.com/u/44524788?v=4" alt="drawing" width="25"/> [RedsTom](https://github.com/RedsTom), <img src="https://avatars.githubusercontent.com/u/43293180?v=4" alt="drawing" width="25"/> [desaleo](https://github.com/desaleao), <img src="https://avatars.githubusercontent.com/u/57500708?v=4" alt="drawing" width="25"/> [baptistedph](https://github.com/baptistedph), <img src="https://avatars.githubusercontent.com/u/42160606?v=4" alt="drawing" width="25"/> [Tom-mp4](https://github.com/Tom-mp4)      |
| Version             | 3.0                                                                                                                                                                                                                                                                   |

---

## Progression
| Progression            |             |
|------------------------|-------------|
| Current State          | 🛠️ WIP     |
| Estimated release date | ~09/15/2022 |

--- 
## Host it yourself
The PrivateRooms bot is a publicly available bot but can be hosted on your own server.
It's not that complicated, but it needs few steps to get it to work.

### 0. Prerequisites
Check your JDK version :
```bash
java -version
javac -version
```
Both should be "Java 18".

### 1. Build the project
Run this command : 
```bash
gradlew shadowJar
```
If all went well, the executable jar file should be in the `build/libs` directory.

### 2. First launch
Before launching the bot first, you have the copy `languages/` folder in you jar folder.
Then launch the jar file, this might create a `config.toml` configuration file containing this :
```toml
token = "TOKEN HERE"

[database] # The database must be a postgresql database
host = "localhost"
port = 5432
username = "postgres"
password = "postgres"
database = "postgres"
```
Fill the configuration file with your contents and relaunch the jar file. The bot should start and 
allow you to use it!
---
