# Events package
In this package, the different events of the bot are stored. If you wanna add one, go into the ``register/EventRegistry.java`` class, add it and create the file here

## ``MessageCreateEventListener.java``
Called when a message is posted on a channel. It parse the commands. Do not touch the parsing system if you don't want to break the system...

## ``ServerJoinEventListener.java``
Called when the bot joins a new server. It creates the channel and the category into the server

## ``ServerLeaveEventListener.java``
Called when the bot leaves a server. It deletes all the information of this server

## ``ServerVoiceChannelMemberJoinEventListener.java``
Called when an user joins a vocal channel. If it's the create channel, it creates a private room and moves the user inside. Else, it do ``NOTHING``

## ``ServerVoiceChannelMembedLeaveEventListener.java``
Called when an user leaves a vocal channel. If the channel is in the category and if the user count is 0, the channel is deleted
