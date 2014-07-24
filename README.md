PlayerProxies
=============

This mod adds and tweaks a bunch of random stuffs that I felt was missing from the game

Content
--
###vanilla tweaks
- Option to disable the ender pearl fall damage
- Rewrote the beacon mechanics
    - uses a guiless mechanic, it's potion effect depends on what item you insert in the beacon (up to 4, just right click the beacon) and the level of the beacon
    - you can use blocs of coal as a beacon base, but I really don't recommend it. Except if you're evil
    - [dev] you can add a "beacon recipe" using IMC, look at nf.fr.ephys.playerproxies.common.core.IMCHandler
- most of the features are configurable

###additions
- Universal interface
    - Can connect to your inventory and your enderchest (even if you're not online).
        - place a vanilla enderchest on the interface
    - Can also connect to tile entities
    - [dev-only] IMC allows you to add your own handler to match a specific entity or tile entity and expose its inventory, look at nf.fr.ephys.playerproxies.common.core.IMCHandler
- Proximity sensor, detects nearby entities and outputs a redstone signal.
- Fluid diffuser: pipe in fluids, plug in power, enjoy
- Handheld potion diffuser: found in pyramids, makes you consume any (non-instant non-splash) potion in your inventory if you don't have its effect already
- Sylladex, place an item in it and it will emulate a player inventory (which means the item will tick, exemple uses would be activating the auto-repair ability of TiC tools even if they're not in your inventory)
- Gravitational Field Handler, WIP block, changes the gravity level
- Biome scanner/replicator, will duplicate a biome
- /nickname command that fucking uses player.getDisplayName, ffs devs stop overwriting chat messages to change the nickname when forge has a hook to change the display name of anyone, and that even changes the name over your head

###future
I still have a lot of features planned but I also want to polish the existing ones
I'm also only working on 1.7.10+ for now, no more bugfixes for 1.6 sorry, move on
Releases
--
Releases are on github in the release tab (http://github.com/Ephys/PlayerProxies/releases)

Modpacks
--
You are allowed to use this mod in any modpack

License
--
[WTFPL], but I won't do any support for modified versions of the mod

[WTFPL]:http://www.wtfpl.net/