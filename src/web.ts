import { WebPlugin } from '@capacitor/core';
import type { DeviceLocalIpPlugin } from './definitions';

export class DeviceLocalIpWeb extends WebPlugin implements DeviceLocalIpPlugin {
  async getIpAddress(): Promise<{ ip: string | null }> {
    console.warn('DeviceLocalIp plugin non è supportato sul web.');
    return { ip: null };
  }
}
