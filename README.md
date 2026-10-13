# AntiStutter

## The bug

Minecraft 26.3 changed how the client moves other players and mobs between position updates
([MC-311974](https://mojira.dev/MC-311974)). Up to 26.2, the client glided each entity toward its latest position over
3 ticks, which smoothed over small hiccups. 26.3 instead replays the server's updates step by step, at the server's
exact timing, with no buffer. Whenever an update arrives a little late, or the server sends one off its normal
schedule (sprinting, getting hit, jumping), other players visibly hitch. It's most noticeable in PvP.

## What the mod does

AntiStutter puts back the 26.2 behaviour: other players and mobs glide toward their latest position over 3 ticks
again. It's client-side only and only changes how other entities are drawn. Your own movement isn't touched.

## Loka only

The mod is only half the fix, and it only works on [Loka](https://modrinth.com/server/loka) (`play.lokamc.com`):

- Loka's server software sends player positions on a steady schedule instead of firing extra updates mid-movement.
- Our anticheat knows that players with the mod see other entities the 26.2 way.

Other servers don't have either. Worse, their anticheat would expect 26.3's interpolation, so your hits could look
like they're landing on positions you shouldn't be able to see. So the mod stays off unless the server switches it
on. Loka does that when you join. On any other server it does nothing.

<!-- modrinth_exclude.start -->
## Install

- [Fabric Loader](https://fabricmc.net/use/) 0.19.5+ for Minecraft 26.3
- [Fabric API](https://modrinth.com/mod/fabric-api)
- Drop AntiStutter in your `mods` folder.

## For server and anticheat devs

A server opts in by registering the `antistutter:linear_3` plugin channel (on Velocity, the proxy's channel registrar
is enough). The client then switches to 26.2's 3-tick linear interpolation (`LinearInterpolationHandler`, 3 steps)
and registers the channel back, so an anticheat can tell which clients to model that way. Nothing is ever sent on the
channel.

## Building

Needs JDK 25. `./gradlew build` puts the jar in `build/libs/`. To publish to Modrinth, bump `version` in
`gradle.properties` and run `./gradlew modrinth` with `MODRINTH_TOKEN` set.
<!-- modrinth_exclude.end -->

## Credits

Thanks to [SamB440](https://github.com/SamB440) for the original code this is built on. Claude helped put the repo
together.

<!-- modrinth_exclude.start -->
[MIT](LICENSE)
<!-- modrinth_exclude.end -->
