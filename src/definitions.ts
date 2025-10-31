export interface DeviceLocalIpPlugin {
  getIpAddress(): Promise<{ ip: string | null }>;
}
