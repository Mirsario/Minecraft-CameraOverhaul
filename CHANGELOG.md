# Navigation
(Click on version number to jump to its changelog.)

| Version									| Release Date |
| ----------------------------------------- | ------------ |
| [1.3.1](#131)								| `2022.07.23` |
| [1.3.0](#130)								| `2021.10.21` |
| [1.2.2](#122)								| `2020.12.28` |
| [1.2.1](#121)								| `2020.12.26` |
| [1.2.0](#120)								| `2020.12.25` |
| [1.1.0](#110)								| `2020.12.24` |
| [1.0.1](#101)								| `2020.12.06` |
| [1.0.0](#100)								| `2020.11.30` |

# 1.4.0
- Added compatibility with Minecraft `1.19.3`+.
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