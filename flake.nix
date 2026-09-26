{
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { nixpkgs, flake-utils, ... }: (let
    lib = nixpkgs.lib // flake-utils.lib;
  in ({}
    # Per architecture.
    // (lib.eachDefaultSystem (system: let
      pkgs = import nixpkgs {
        inherit system;
        config.allowUnsupportedSystem = true;
      };

      # wine = pkgs.wineWow64Packages.stagingFull;
      mkEnv = { headless }: rec {
        jdk = (if headless then pkgs.jdk25_headless else pkgs.jdk25);
        packages = [
          jdk
          pkgs.curl
          pkgs.wget
          pkgs.p7zip
        ];
        ldLibGfxLines = lib.makeLibraryPath [
          # X11
          pkgs.libxi
          pkgs.libx11
          pkgs.libxcursor
          pkgs.libxrandr
          pkgs.libxkbcommon
          # Wayland
          pkgs.wayland
          # OpenGL
          pkgs.libGL
          # Vulkan
          pkgs.vulkan-loader
          # Audio
          pkgs.alsa-lib
          # Input
          pkgs.udev
        ];
        sharedHook = ''
          export JAVA_HOME="${jdk}"
          export LD_LIBRARY_PATH=${ldLibGfxLines}:$LD_LIBRARY_PATH
        '';
      };
    in rec {

      # Dev Shells

      # Access via `nix develop .#graphical` / `nix develop .#headless`.
      devShells = {
        default = devShells.graphical;
        headless = let env = mkEnv { headless = true; }; in pkgs.mkShell {
          inherit (env) packages;
          shellHook = env.sharedHook + ''
            echo "Headless development environment initialized."
            echo "Use the 'graphical' environment if you intend to launch game clients."
          '';
        };
        graphical = let env = mkEnv { headless = false; }; in pkgs.mkShell {
          inherit (env) packages;
          shellHook = env.sharedHook + ''
            echo "Graphical development environment initialized!"
          '';
        };
      };
    }))

    # Architecture-agnostic.
    // ({
      lib = {
          
      };
    })
  ));
}
