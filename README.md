[![](https://img.shields.io/github/v/release/sladkoff/minecraft-prometheus-exporter?style=flat-square)](https://github.com/sladkoff/minecraft-prometheus-exporter/releases/latest)
[![](https://img.shields.io/github/issues-raw/sladkoff/minecraft-prometheus-exporter?style=flat-square)](https://github.com/sladkoff/minecraft-prometheus-exporter/issues)
[![](https://img.shields.io/github/license/sladkoff/minecraft-prometheus-exporter?style=flat-square)](https://github.com/sladkoff/minecraft-prometheus-exporter/blob/master/LICENSE.md)
[![](https://img.shields.io/badge/Donate-Buy%20me%20a%20coffee-orange.svg?style=flat-square)](https://www.buymeacoffee.com/sldk)

# Minecraft Prometheus Exporter

A **Bukkit plugin** which exports Minecraft server stats for Prometheus.

## Quick Start

Drop the the prometheus-exporter.jar into your Bukkit plugins directory and start your Minecraft server.

After startup, the Prometheus metrics endpoint should be available at ``localhost:9225/metrics`` (assuming localhost is the server hostname).

The metrics port can be customized in the plugin's config.yml (a default config will be created after the first use).

## Prometheus config

Add the following job to the ``scrape_configs`` section of your Prometheus configuration:

### Single server

```yml
- job_name: 'minecraft'
  static_configs:
    - targets: ['localhost:9225']
      labels:
        server_name: 'my-awesome-server'
```

### Multiple servers

You can use labels in your Prometheus scrape configuration to distinguish between multiple servers:

```yml
- job_name: 'minecraft'
  static_configs:
    - targets: ['localhost:9225']
      labels:
        server_name: 'server1'
    - targets: ['localhost:9226']
      labels:
        server_name: 'server2'
```

## Import Grafana Dashboard

1. Navigate to Grafana -> Dashboards -> Import
1. Paste in or upload [minecraft-server-dashboard.json](https://raw.githubusercontent.com/sladkoff/minecraft-prometheus-exporter/master/dashboards/minecraft-server-dashboard.json)
1. Update "JVM Memory Used" to reflect your server max memory (Default 8G)
1. Edit (bottom right widget) -> Options -> Gauage -> Max

## Available metrics

These are the stats that are currently exported by the plugin.

Label | Description
------------ | -------------
mc_players_total | Unique players on server (online + offline)
mc_loaded_chunks_total | Chunks loaded per world
mc_players_online_total | Online players per world
mc_entities_total | Entities loaded per world (living + non-living)
mc_jvm_memory | JVM memory usage
mc_tps | Server tickrate (TPS)

## Player metrics (experimental!)

:warning: **The following feature is against Prometheus best-practices and is not recommended for production servers!**

There is an option to export per-player statistics like the number of blocks mined, mobs killed, items used, etc. 
The amount of data stored in Prometheus can dramatically increase when this is enabled as individual time-series 
will be generated for each player that has ever been seen on the server. The statistic collection may also have an
impact on the Minecraft server performance for bigger servers but it has not been measured or tested.

On the other hand this should be quite safe for small private servers with limited players.

You can enable the experimental player export in the config.yaml.

```yaml
enable_metrics:
  player_online: true
  player_statistic: true
```

This will enable the additional metrics.

Label | Description
------------ | -------------
mc_player_statistic | Player statistics
mc_player_online | Online state by player name

There's a sample [dashboard](https://raw.githubusercontent.com/sladkoff/minecraft-prometheus-exporter/master/dashboards/minecraft-players-dashboard.json) 
available to get you started.

## Collect metrics about your own plugin

You can easily collect metrics about your own plugin.

### Include the Prometheus dependency

```xml
<dependency>
    <groupId>io.prometheus</groupId>
    <artifactId>simpleclient_common</artifactId>
    <version>...</version>
</dependency>
```

### Collect metrics

This pseudo code shows how you would count invocations of a plugin command.

```java
public class MyPluginCommand extends PluginCommand {

  // Register your counter
  private Counter commandCounter = Counter.build()
            .name("mc_my_plugin_command_invocations_total")
            .help("Counter for my plugin command invocations")
            .register();

  @Override
  public boolean execute​(CommandSender sender, String commandLabel, String[] args) {

    // Increment your counter;
    commandCounter.inc(); 

    // Do other stuff

    return true;
  }

}
```
