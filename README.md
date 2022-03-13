# AnarchyChat

A simple chat management plugin

# Features

* Hard to abuse because ignore list is name based
* High performance thanks to H2 SQL
* Disables vanilla /tell command

# Commands

| Command            | Description                                 | Permission                       | Aliases                      |
|--------------------|---------------------------------------------|----------------------------------|------------------------------|
| /ignore            | Ignore a player                             | anarchychat.ignore (default)     |                              |
| /ignorehard        | Ignore a player permanently                 | anarchychat.ignorehard (default) |                              |
| /ignorelist        | Show ignored players                        | anarchychat.ignorelist (default) |                              |
| /tell              | Send message to a player                    | anarchychat.tell (default)       | /message, /msg, /whisper, /w |
| /reply             | Reply to a player                           | anarchychat.reply (default)      | /r                           |
| /ignorelang <lang> | Set language(en_us or ja_jp)                | anarchychat.ignorelang (default) |                              |
| /mute <name>       | Mute a player                               | anarchychat.mute (op)            |                              |
| /pmute <name>      | Prevent a player from using private message | anarchychat.pmute (op)           |                              |
| /mutelist          | Show muted players                          | anarchychat.mutelist (op)        |                              |
