# AntiStutter

Reintroduce 26.2's normal entity interpolation on the [Loka](https://modrinth.com/server/loka) server.

Minecraft 26.3 replaced the client's entity interpolation with a stepped model that replays the server's position
history at the server's own timing. With no slack for network jitter, other players visibly stutter, which is
especially noticeable in PvP ([MC-311974](https://mojira.dev/MC-311974)).

AntiStutter is a small client-side Fabric mod that puts back the 26.2 behaviour: each living entity glides linearly
toward its latest received position over 3 ticks.

## Only works on Loka

AntiStutter is built for Loka (`play.lokamc.com`) and only does anything there. It's one half of the fix. The other
half is server-side changes in Loka's server software, which stop other players' positions being sent off their normal
2-tick schedule, plus matching rules in Loka's anticheat.

The mod switches itself on only when the server tells it to. On every other server it stays off: the game behaves
exactly as if the mod weren't installed, and the mod doesn't announce itself to the server.

## Requirements

- Minecraft 26.3
- [Fabric Loader](https://fabricmc.net/use/) 0.19.5+
- [Fabric API](https://modrinth.com/mod/fabric-api)
- Java 25+

## For server and anticheat developers

The mod is switched on by a handshake on the play channel `antistutter:linear_3`:

1. The server registers the channel in `minecraft:register`. Behind Velocity, registering it with the proxy's channel
   registrar is enough: Velocity sends its channels to the client every time the client enters play, including after
   each server switch.
2. The client then switches to 3-tick linear interpolation for every entity it creates from then on, and registers
   the channel back. Velocity re-sends a client's channels to each backend on server switch.

If a client has registered this channel, model its view of other living entities as the pre-26.3 3-tick linear
interpolation (`LinearInterpolationHandler`, 3 steps) instead of 26.3's stepped interpolation. That's the same model
older clients use when they connect through ViaVersion. If the server never registers the channel, the client stays
on vanilla interpolation and never registers the channel.

Neither side sends data on the channel, and the mod ignores anything sent to it.

## How it works

While the current server has registered the channel, one mixin makes `SteppedInterpolationHandler.create` return
`LinearInterpolationHandler.create(entity, 3)` on the client. `LivingEntity` is the only caller, and nothing in the client depends on the stepped handler type.

<!-- modrinth_exclude.start -->
Loka's server side lives in [Slice](https://github.com/LokaMC/Slice/tree/26.3), Loka's fork of Paper.

## Building

Gradle must run on JDK 25:

```
./gradlew build
```

The jar is written to `build/libs/`.

To publish a new version to Modrinth, bump `version` in `gradle.properties`, set `MODRINTH_TOKEN` to a personal
access token with the `CREATE_VERSION` scope, and run:

```
./gradlew modrinth
```

`./gradlew modrinthSyncBody` replaces the Modrinth description with this README (minus the excluded parts). It needs
the `PROJECT_WRITE` scope.
<!-- modrinth_exclude.end -->

## Credits

- [SamB440](https://github.com/SamB440) wrote the original code snippet this mod is built on.
- Claude helped put the repository together.

<!-- modrinth_exclude.start -->
## License

[MIT](LICENSE)
<!-- modrinth_exclude.end -->
