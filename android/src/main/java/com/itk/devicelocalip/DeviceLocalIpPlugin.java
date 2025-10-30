package com.itk.devicelocalip;

import com.getcapacitor.Logger;

public class DeviceLocalIpPlugin {

    public String echo(String value) {
        Logger.info("Echo", value);
        return value;
    }
}
