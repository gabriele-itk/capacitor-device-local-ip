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
        @Permission(strings = { Manifest.permission.ACCESS_WIFI_STATE }, alias = "wifi"),
        @Permission(strings = { Manifest.permission.ACCESS_NETWORK_STATE }, alias = "network")
})
public class DeviceLocalIpPlugin extends Plugin {

    @PluginMethod
    public void getIpAddress(PluginCall call) {
        JSObject ret = new JSObject();
        String ip = getLocalIp(getContext());
        ret.put("ip", ip);
        call.resolve(ret);
    }

    @PluginMethod
    public void isHotspotActive(PluginCall call) {
        JSObject ret = new JSObject();
        boolean isActive = checkHotspotStatus(getContext());
        ret.put("isActive", isActive);
        call.resolve(ret);
    }

    private boolean checkHotspotStatus(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm == null) {
                android.util.Log.d("DeviceLocalIp", "ConnectivityManager non disponibile");
                return false;
            }

            // Verifica se la rete attiva è metered (tipico degli hotspot)
            boolean isMetered = cm.isActiveNetworkMetered();
            android.util.Log.d("DeviceLocalIp", "Rete attiva metered: " + isMetered);

            // Verifica aggiuntiva: controlla se ci sono interfacce hotspot attive
            boolean hasHotspotInterface = hasActiveHotspotInterface();
            android.util.Log.d("DeviceLocalIp", "Interfaccia hotspot trovata: " + hasHotspotInterface);

            // Se la rete è metered E abbiamo un'interfaccia hotspot, l'hotspot è attivo
            boolean hotspotActive = isMetered && hasHotspotInterface;
            android.util.Log.d("DeviceLocalIp", "Hotspot attivo: " + hotspotActive);
            
            return hotspotActive;
        } catch (Exception e) {
            android.util.Log.e("DeviceLocalIp", "Errore nel controllo hotspot", e);
            return false;
        }
    }

    private boolean hasActiveHotspotInterface() {
        try {
            java.util.Enumeration<java.net.NetworkInterface> interfaces = java.net.NetworkInterface
                    .getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
                java.net.NetworkInterface intf = interfaces.nextElement();
                String name = intf.getName();

                // Interfacce tipiche degli hotspot Android
                if (name.contains("swlan") || name.contains("ap") || name.contains("softap")
                        || name.contains("wlan1")) {

                    // Verifica se l'interfaccia è attiva e ha indirizzi IP
                    if (intf.isUp() && !intf.isLoopback()) {
                        for (InetAddress addr : java.util.Collections.list(intf.getInetAddresses())) {
                            if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                                android.util.Log.d("DeviceLocalIp", "Interfaccia hotspot attiva: " + name + " con IP: " + addr.getHostAddress());
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        } catch (Exception e) {
            android.util.Log.e("DeviceLocalIp", "Errore nel controllo interfacce hotspot", e);
            return false;
        }
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
