import { registerPlugin } from '@capacitor/core';

import type { DeviceLocalIpPluginPlugin } from './definitions';

const DeviceLocalIpPlugin = registerPlugin<DeviceLocalIpPluginPlugin>('DeviceLocalIpPlugin', {
  web: () => import('./web').then((m) => new m.DeviceLocalIpPluginWeb()),
});

export * from './definitions';
export { DeviceLocalIpPlugin };
