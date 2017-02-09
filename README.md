# Minecraft Prometheus Exporter

A **Bukkit plugin** which exports Minecraft server stats for Prometheus.

## Quick Start

Drop the the prometheus-exporter.jar into your Bukkit plugins directory and start your Minecraft server.

After startup, the Prometheus metrics endpoint should be available at ``localhost:9225/metrics`` (assuming localhost is the server hostname).

The metrics port can be customized in the plugin's config.yml (a default config will be created after the first use).

## Prometheus config

Add the following job to the ``scrape_configs`` section of your Prometheus configuration:

```yml
- job_name: 'minecraft'
  static_configs:
    - targets: ['localhost:9225']
```

## Available metrics

These are the stats that are currently exported by the plugin.

Label | Description
------------ | -------------
mc_players_total | Online and offline players
mc_loaded_chunks_total | Chunks loaded per world
mc_players_online_total | Online players per world
mc_entities_total | Entities loaded per world
mc_living_entities_total | Living entities loaded per world