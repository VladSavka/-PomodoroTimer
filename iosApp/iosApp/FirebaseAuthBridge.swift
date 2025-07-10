import UIKit
import FirebaseAuth
import FirebaseCore
import GoogleSignIn

@objcMembers
class FirebaseAuthBridge: NSObject {
    
    @objc static let shared = FirebaseAuthBridge()
    
    private var authStateDidChangeHandle: AuthStateDidChangeListenerHandle?

    @objc func signInWithGoogle() {
        guard
            let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
            let rootVC = windowScene.windows.first(where: { $0.isKeyWindow })?.rootViewController
        else {
            print("❌ No root view controller found")
            return
        }

        guard let clientID = FirebaseApp.app()?.options.clientID else {
            print("❌ Failed to get iOS client ID from Firebase")
            return
        }

        let config = GIDConfiguration(clientID: clientID)
        GIDSignIn.sharedInstance.configuration = config

        GIDSignIn.sharedInstance.signIn(withPresenting: rootVC) { result, error in
            if let error = error {
                print("❌ Google Sign-In failed: \(error.localizedDescription)")
                return
            }

            guard let user = result?.user,
                  let idToken = user.idToken?.tokenString else {
                print("❌ Missing ID token")
                return
            }

            let accessToken = user.accessToken.tokenString

            let credential = GoogleAuthProvider.credential(
                withIDToken: idToken,
                accessToken: accessToken
            )

            Auth.auth().signIn(with: credential) { authResult, error in
                if let error = error {
                    print("❌ Firebase sign-in failed: \(error.localizedDescription)")
                } else {
                    print("✅ Signed in to Firebase successfully")
                }
            }
        }
    }

    
    @objc func signOut() {
        do {
            try Auth.auth().signOut()
            GIDSignIn.sharedInstance.signOut()
            print("✅ Signed out")
        } catch {
            print("❌ Sign-out error: \(error)")
        }
    }
    
    func observeAuthState(_ callback: @escaping (Bool) -> Void) {
          Auth.auth().addStateDidChangeListener { _, user in
              callback(user != nil)
          }
      }
}


