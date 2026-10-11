# AntiStutter

Reintroduce 26.2's normal entity interpolation.

Minecraft 26.3 replaced the client's entity interpolation with a stepped model that replays the server's position
history at the server's own timing. With no slack for network jitter, other players visibly stutter, which is
especially noticeable in PvP ([MC-311974](https://mojira.dev/MC-311974)).

AntiStutter is a small client-side Fabric mod that puts back the 26.2 behaviour: each living entity glides linearly
toward its latest received position over 3 ticks.

## Made to pair with Loka's server fork

On its own, don't expect AntiStutter to do much. It was written specifically to pair with
[Slice](https://github.com/LokaMC/Slice/tree/26.3), Loka's fork of Paper. Slice handles the server side of this bug:
it stops sending other players' positions off their normal 2-tick schedule. The two together are what make it work.

On other servers the mod still loads and switches back to 26.2's interpolation, but it hasn't been tested there.

## Requirements

- Minecraft 26.3
- [Fabric Loader](https://fabricmc.net/use/) 0.19.5+
- [Fabric API](https://modrinth.com/mod/fabric-api)
- Java 25+

## For server and anticheat developers

The mod registers the play channel `antistutter:linear_3`. Fabric API announces it to the server in
`minecraft:register` when the client joins. Velocity re-sends a client's registered channels to each backend on
server switch, so the announcement survives proxy hops.

If a client has registered this channel, model its view of other living entities as the pre-26.3 3-tick linear
interpolation (`LinearInterpolationHandler`, 3 steps) instead of 26.3's stepped interpolation. That's the same model
older clients use when they connect through ViaVersion.

The mod sends no data on this channel and ignores anything sent to it.

## How it works

One mixin makes `SteppedInterpolationHandler.create` return `LinearInterpolationHandler.create(entity, 3)` on the
client. `LivingEntity` is the only caller, and nothing in the client depends on the stepped handler type.

## Building

Gradle must run on JDK 25:

```
./gradlew build
```

The jar is written to `build/libs/`.

## License

[MIT](LICENSE)
