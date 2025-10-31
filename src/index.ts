import { registerPlugin } from '@capacitor/core';
import type { DeviceLocalIpPlugin } from './definitions';

const DeviceLocalIp = registerPlugin<DeviceLocalIpPlugin>('DeviceLocalIp', {
  web: () => import('./web').then(m => new m.DeviceLocalIpWeb()),
});

export * from './definitions';
export { DeviceLocalIp };
