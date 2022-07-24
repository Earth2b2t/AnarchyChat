# AnarchyChat

![thumbnail](https://user-images.githubusercontent.com/26406334/165263241-ccd61c5c-775e-430f-a89f-84f2dcacc171.png)

A simple chat management plugin

## Features

* Hard to abuse because ignore list is name based
* High performance thanks to H2 SQL
* Disables vanilla /tell command
* Placeholder support

## Commands

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

## Screenshots

/ignorelist

![image](https://user-images.githubusercontent.com/26406334/158069985-9f8e6bbe-7526-4ee5-8e70-84e726c02b01.png)

/mute

![image](https://user-images.githubusercontent.com/26406334/158069845-34667f6e-5c91-4c13-8d13-43ea30184dae.png)

## PlaceholderAPI support

You can use PlaceholderAPI or pre-defined placeholders instead.

Open config.yml and change format however you like.

```yaml
format: "[{world}]<{playerName} {player_uuid}>{message}"
```

Result will be:

```
[world]<Toshimichi0915 1f29089b-7a33-4506-82b2-3637e0f8f82e>test
```

Note that {player_uuid} is automatically replaced by %player_uuid% before being passed to PlaceholderAPI.
Although both %player_uuid% and {player_uuid} should work, {player_uuid} is more preferred.

Pre-defined placeholders:

| String       | Description      |
|--------------|------------------|
| {playerName} | Player's name    |
| {world}      | Player's world   |
| {message}    | Player's message |
