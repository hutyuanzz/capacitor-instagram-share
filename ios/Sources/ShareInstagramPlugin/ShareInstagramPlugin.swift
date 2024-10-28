import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(ShareInstagramPlugin)
public class ShareInstagramPlugin: CAPPlugin, CAPBridgedPlugin {
    public let identifier = "ShareInstagramPlugin"
    
    public let jsName = "ShareInstagram"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "shareToStory", returnType: CAPPluginReturnPromise)
    ]
    private let implementation = ShareInstagram()

    private let instagramURLSchema = "instagram-stories://share"

    @objc func shareToStory(_ call: CAPPluginCall) {
        let appID = call.getString("appID")
         guard let imageURLString = call.getString("imageUrl"),
              let url = URL(string: imageURLString),
              let imageData = try? Data(contentsOf: url) else {
            call.reject("Invalid image URL or failed to load image")
            return
        }

        if let urlSchema = URL(string: "\(instagramURLSchema)?source_application=\(appID)"),
           UIApplication.shared.canOpenURL(urlSchema) {

            let pasteboardItems = [
                ["com.instagram.sharedSticker.backgroundImage": imageData],
            ]

            let pasteboardOptions = [UIPasteboard.OptionsKey.expirationDate: Date().addingTimeInterval(60 * 5)]
            UIPasteboard.general.setItems(pasteboardItems, options: pasteboardOptions)

            UIApplication.shared.open(urlSchema, options: [:], completionHandler: nil)
            
            call.resolve(["message": "Photo shared successfully."])
        } else {
            call.reject("Instagram is not installed.")
        }
    }
}
