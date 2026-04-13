import GoogleSignIn
import ComposeApp
import CryptoKit
import UIKit

enum GoogleSignInHelper {

    static func configure() {
        let bridge = GoogleSignInBridge.shared
        GIDSignIn.sharedInstance.configuration = GIDConfiguration(
            clientID: bridge.iosClientId,
            serverClientID: bridge.webClientId
        )

        bridge.onSignInRequest = {
            guard let windowScene = UIApplication.shared.connectedScenes.first(where: {
                $0 is UIWindowScene
            }) as? UIWindowScene,
                  let rootViewController = windowScene.keyWindow?.rootViewController else {
                bridge.completeSignIn(idToken: nil, nonce: nil, error: "No root view controller available")
                return
            }

            let rawNonce = randomNonce()
            let hashedNonce = sha256(rawNonce)

            GIDSignIn.sharedInstance.signIn(
                withPresenting: rootViewController,
                hint: nil,
                additionalScopes: nil,
                nonce: hashedNonce
            ) { result, error in
                if let error = error {
                    bridge.completeSignIn(idToken: nil, nonce: nil, error: error.localizedDescription)
                    return
                }
                guard let idToken = result?.user.idToken?.tokenString else {
                    bridge.completeSignIn(idToken: nil, nonce: nil, error: "No ID token in Google result")
                    return
                }
                bridge.completeSignIn(idToken: idToken, nonce: rawNonce, error: nil)
            }
        }
    }

    static func handle(url: URL) -> Bool {
        return GIDSignIn.sharedInstance.handle(url)
    }

    private static func randomNonce(length: Int = 32) -> String {
        let charset = Array("0123456789ABCDEFGHIJKLMNOPQRSTUVXYZabcdefghijklmnopqrstuvwxyz-._")
        var result = ""
        var remainingLength = length
        while remainingLength > 0 {
            let randoms: [UInt8] = (0..<16).map { _ in
                var random: UInt8 = 0
                let status = SecRandomCopyBytes(kSecRandomDefault, 1, &random)
                if status != errSecSuccess { fatalError("Unable to generate nonce") }
                return random
            }
            for random in randoms {
                if remainingLength == 0 { break }
                if random < charset.count {
                    result.append(charset[Int(random)])
                    remainingLength -= 1
                }
            }
        }
        return result
    }

    private static func sha256(_ input: String) -> String {
        let data = Data(input.utf8)
        let hash = SHA256.hash(data: data)
        return hash.compactMap { String(format: "%02x", $0) }.joined()
    }
}
