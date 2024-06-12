# FlightSystem
Minecraft plugin to make flying harder


## Features
- /fly command with customizable settings
- hex colors support
- optimized
- preventions (Fly and elytra glide) in selected worlds (can be multiple)


## Commands
- /fly - switch own fly mode
- /fly <on/off> - set own fly mode
- /fly <switch> <player> - switch else's fly mode
- /fly <on/off> <player> - set else's fly mode
- /fly all <on/all> - set fly mode for all online players (You can enable the use this only from console)
- /fly all switch - switch fly mode for all online players (You can enable the use this only from console)
- /fly reload - reload plugin configurations
- /fly help - help message (Different messages for different senders)


## Permissions
- flightsystem.reload - allows to reload the plugin
- flightsystem.bypass.elytra - allows enabling flight without elytra
- flightsystem.bypass.broken - allows enabling flight with broken elytra
- flightsystem.bypass.damage - disable damage for elytra during flight
- flightsystem.bypass.prevention.elytra - allows bypass elytra glide prevention
- flightsystem.bypass.prevention.fly - allows bypass flight prevention
- flightsystem.fly.switch - includes [ spacefly.fly.on ] & [ spacefly.fly.off ]
- flightsystem.fly.on - allows enabling own flight (only with elites)
- flightsystem.fly.off - allows disabling own flight
- flightsystem.fly.switch.other - includes [ spacefly.fly.on.other ] & [ spacefly.fly.off.other ]
- flightsystem.fly.on.other - allows enable else's flight (Ignoring elytra)
- flightsystem.fly.on.other - allows enable else's flight (Ignoring elytra)
  flightsystem.fly.off.other - allows disable else's flight
- flightsystem.fly.switch.all - includes [ spacefly.fly.on.all ] & [ spacefly.fly.off.all ]
- flightsystem.fly.on.all - allows enabling flight for all online players
- flightsystem.fly.off.all - allows disabling flight for all online players
- flightsystem.staff - includes [ spacefly.fly.on ] [ spacefly.fly.off ] [ spacefly.fly.other.on ] [ spacefly.fly.other.off ], and also all bypasses
- flightsystem.admin - includes all permissions (Like * but only for this plugin)
- flightsystem.help.user - allows you to view the help message for players
- flightsystem.help.staff - allows you to view the help message for staff
- flightsystem.help.admin - allows you to view the help message for admin
