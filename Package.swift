// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "ComItkLocalip",
    platforms: [.iOS(.v14)],
    products: [
        .library(
            name: "ComItkLocalip",
            targets: ["DeviceLocalIpPluginPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "7.0.0")
    ],
    targets: [
        .target(
            name: "DeviceLocalIpPluginPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/DeviceLocalIpPluginPlugin"),
        .testTarget(
            name: "DeviceLocalIpPluginPluginTests",
            dependencies: ["DeviceLocalIpPluginPlugin"],
            path: "ios/Tests/DeviceLocalIpPluginPluginTests")
    ]
)