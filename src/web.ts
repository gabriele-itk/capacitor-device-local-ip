import { WebPlugin } from '@capacitor/core';

import type { DeviceLocalIpPluginPlugin } from './definitions';

export class DeviceLocalIpPluginWeb extends WebPlugin implements DeviceLocalIpPluginPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
