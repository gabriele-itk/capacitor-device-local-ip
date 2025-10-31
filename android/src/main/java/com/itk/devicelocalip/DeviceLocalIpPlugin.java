package com.itk.devicelocalip;

import android.Manifest;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.PluginMethod;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.List;

@CapacitorPlugin(name = "DeviceLocalIp", permissions = {
        @Permission(strings = { Manifest.permission.ACCESS_WIFI_STATE }, alias = "wifi")
})
public class DeviceLocalIpPlugin extends Plugin {

    @PluginMethod
    public void getIpAddress(PluginCall call) {
        JSObject ret = new JSObject();
        String ip = getLocalIp(getContext());
        ret.put("ip", ip);
        call.resolve(ret);
    }

    private String getLocalIp(Context context) {
        try {
            java.util.Enumeration<java.net.NetworkInterface> interfaces = java.net.NetworkInterface
                    .getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
                java.net.NetworkInterface intf = interfaces.nextElement();
                String name = intf.getName();

                // Hotspot swlan0, ap0, softap0, wlan1
                if (name.contains("swlan") || name.contains("ap") || name.contains("softap")
                        || name.contains("wlan1")) {
                    for (InetAddress addr : java.util.Collections.list(intf.getInetAddresses())) {
                        if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                            android.util.Log.d("DeviceLocalIp",
                                    " IP hotspot trovato su " + name + ": " + addr.getHostAddress());
                            return addr.getHostAddress();
                        }
                    }
                }
            }

            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Network activeNetwork = cm.getActiveNetwork();

            if (activeNetwork != null) {
                LinkProperties lp = cm.getLinkProperties(activeNetwork);
                if (lp != null) {
                    List<InetAddress> addresses = lp.getLinkAddresses().stream()
                            .map(la -> la.getAddress())
                            .toList();
                    for (InetAddress addr : addresses) {
                        if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                            android.util.Log.d("DeviceLocalIp", " IP rete Wi-Fi trovato: " + addr.getHostAddress());
                            return addr.getHostAddress();
                        }
                    }
                }
            }

            WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            int ipInt = wm.getConnectionInfo().getIpAddress();
            String ip = Formatter.formatIpAddress(ipInt);
            android.util.Log.d("DeviceLocalIp", " Fallback IP: " + ip);
            return ip;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
