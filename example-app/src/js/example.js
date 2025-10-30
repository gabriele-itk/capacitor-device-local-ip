import { DeviceLocalIpPlugin } from 'com-itk-localip';

window.testEcho = () => {
    const inputValue = document.getElementById("echoInput").value;
    DeviceLocalIpPlugin.echo({ value: inputValue })
}
