# Navigation
(Click on version number to jump to its changelog.)

| Version                               | Release Date |
|---------------------------------------|--------------|
| [Work In Progress](#work-in-progress) | `n/a`        |
| [2.0.0](#200)                         | `2025-01-27` |
| [1.4.1](#141)                         | `2024-06-18` |
| [1.4.0](#140)                         | `2022-12-27` |
| [1.3.1](#131)                         | `2022-07-23` |
| [1.3.0](#130)                         | `2021-10-21` |
| [1.2.2](#122)                         | `2020-12-28` |
| [1.2.1](#121)                         | `2020-12-26` |
| [1.2.0](#120)                         | `2020-12-25` |
| [1.1.0](#110)                         | `2020-12-24` |
| [1.0.1](#101)                         | `2020-12-06` |
| [1.0.0](#100)                         | `2020-11-30` |

# Work In Progress

**Localization:**
- German localization by `@Lucanoria` ([PR #70](<https://github.com/Mirsario/Minecraft-CameraOverhaul/pull/70>)).

# 2.0.0
The mod has been rewritten for easier maintenance & scaling. Single-JAR version agnosticism based on 'reflection spam' has been sacrificed in favor of classic separate JARs tied to specific MC version ranges, but still produced out of a single codebase using preprocessors. An effort will still be made to support as many versions of Minecraft as possible, out of one codebase.

**NeoForge & Forge Support:**
- Introduced official support for **NeoForge**, from `1.20.6` to `1.21.2` and newer!
- Introduced official support for the now-legacy **Forge**, from `1.17` to `1.20.5`. Pull Requests adding support for even older versions are welcome.

**Features:**
- New `Screen Shakes` feature. Explosions, lightning, thunder, and many other events will now shake players' cameras, using a top-notch implementation featuring simplex noise and exponential decay.
- New `Camera Sway` feature. Noise-based camera offsets that kick in once you don't move your camera and legs for a short moment. Configurable with `Intensity`, `Frequency`, `Fade-In Delay`, `Fade-In Length`, and `Fade-Out Length`.
- Completely reworked the `Yaw Delta Roll` feature, now known as `Mouselook Roll`. Configurable in 3 components - `Intensity`, `Accumulation`, and `Decay Smoothness`.
- The mod's features now apply when using minecarts, boats, horses, and other rideables. Values for those are configured separately.

**Configuration:**
- Configuration expanded and reworked. Many more separate values now exist for walking, swimming, and elytra flight. The `cameraoverhaul.json` file has been replaced with `cameraoverhaul.toml`, which is much more human-writable, as every present field there is now properly commented. ModMenu-based in-game configuration now makes use of categories.
- Added a separate toggle for the mod's effects while in third person.

**Localization:**
- Russian config localization, courtesy of `@mpustovoi`.

**Fixes:**
- Fixed the mod using incorrect values for time deltas, leading to less smooth interpolation. Oopsie doopsie!
- Fixed issue [#16](https://github.com/Mirsario/Minecraft-CameraOverhaul/issues/16) (Strafing affects elytra in a very jiggery manner).
- Fixed issue [#34](https://github.com/Mirsario/Minecraft-CameraOverhaul/issues/34) (The Configure button doesn't show up in Mod Menu).
- Fixed issue [#36](https://github.com/Mirsario/Minecraft-CameraOverhaul/issues/36) (Cannot read field "enabled" because "config" is null).
- Fixed issue [#40](https://github.com/Mirsario/Minecraft-CameraOverhaul/issues/40) (Better Third Person Incompatibility).
- Fixed issue [#43](https://github.com/Mirsario/Minecraft-CameraOverhaul/issues/43) (Toggling perspective (F5) causes camera to shake).
- Fixed issue [#57](https://github.com/Mirsario/Minecraft-CameraOverhaul/issues/57) (View rocks side to side when going across a ice block highway with boat).
- Fixed the camera sometimes shaking when riding in the second seat of a boat in multiplayer.

# 1.4.1
- Added compatibility with Minecraft `1.20.6`+ and `1.21`+ (Thanks, **@sam-mccarthy**!)
- ClothConfig2 is now recommended instead of required, same as ModMenu.

# 1.4.0

- Added compatibility with Minecraft `1.19.3`+.
- Replaced all use of linear interpolation with a framerate-independent damping algorithm. Effects will no longer reduce in intensity on higher framerates, and will react to lagspikes slightly better.
- New default settings are a bit more balanced and friendlier to the average player.
- Mouselook roll's rotation target decay is now a separate setting.
- Improved logging clarity.

# 1.3.1

- Fixed in-game configuration screens not working in `1.19+` Minecraft versions.
- Fixed away incompatibilities with `Quilt` by ceasing use of obsolete types.
- `Cloth Config` is no longer included with the mod, since it's not version-agnostic as this mod is. It must now be installed separately.

# 1.3.0

- Compatibility with `1.17` and `1.18` Minecraft versions (Thanks, **IMS212**!)
- Compatibility with Fabric Loader `0.12`+ (Thanks, **IMS212**!)
    Strafing Roll Factor setting is now separate for elytra flight and swimming (Thanks, **TheMrEngMan**!)
- Elytra flight camera roll is now reversed by default (Thanks, **TheMrEngMan**!).
- Made interpolation speeds for mouselook, horizontal and vertical movement-based rotations configurable. This means that you can now change how smooth all rotations are.
- Lowered default interpolation speed of horizontal movement from `1.0` to `0.25`, of vertical movement from `1.0` to `0.75`. I find this to be way less nausea-inducing.

# 1.2.2

- Removed debugging log spam that shouldn't have been left in...

# 1.2.1

- Fixed a crash with optifabric, which was accidentally introduced with 1.2.0 improvements.

# 1.2.0

- Now supports all official fabric minecraft versions with 1 universal binary. That is, from `1.14` to the latest `1.17` snapshots!
- Pitch (and unused yaw) rotations no longer desync with projections of `ImmersivePortals`' portals. Roll rotations still do.

# 1.1.0

- Added support for ModMenu in-game configuration (Thanks, **altrisi**!)
- Now uses `1.16.4` mappings instead of `1.16.3`'s.

# 1.0.1

- Marked the mod as client-side only, which should make it be ignored on servers (Thanks, **altrisi**!)

# 1.0.0

- Initial release.