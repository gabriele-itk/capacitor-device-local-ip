export interface DeviceLocalIpPluginPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
