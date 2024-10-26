// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorPay2stayInstagramShare",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "CapacitorPay2stayInstagramShare",
            targets: ["ShareInstagramPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", branch: "main")
    ],
    targets: [
        .target(
            name: "ShareInstagramPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/ShareInstagramPlugin"),
        .testTarget(
            name: "ShareInstagramPluginTests",
            dependencies: ["ShareInstagramPlugin"],
            path: "ios/Tests/ShareInstagramPluginTests")
    ]
)
