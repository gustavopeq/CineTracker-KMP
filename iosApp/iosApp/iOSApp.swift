import SwiftUI
import GoogleSignIn
import ComposeApp

@main
struct iOSApp: App {

    init() {
        GoogleSignInHelper.configure()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    if url.scheme == "com.projects.cinetracker" && url.host == "auth-callback" {
                        handleAuthCallback(url: url)
                    } else {
                        GoogleSignInHelper.handle(url: url)
                    }
                }
        }
    }

    private func handleAuthCallback(url: URL) {
        guard let fragment = url.fragment else { return }
        let params = Dictionary(
            uniqueKeysWithValues: fragment
                .split(separator: "&")
                .compactMap { pair -> (String, String)? in
                    let parts = pair.split(separator: "=", maxSplits: 1)
                    guard parts.count == 2 else { return nil }
                    return (String(parts[0]), String(parts[1]))
                }
        )
        if params["type"] == "recovery", let accessToken = params["access_token"] {
            RecoveryHandler.shared.handleRecoveryCallback(accessToken: accessToken)
        }
    }
}
