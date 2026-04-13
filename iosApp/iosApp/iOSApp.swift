import SwiftUI
import GoogleSignIn

@main
struct iOSApp: App {

    init() {
        GoogleSignInHelper.configure()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    GoogleSignInHelper.handle(url: url)
                }
        }
    }
}
