import { auth, googleProvider, firestore } from "./firebase-init.js";
import { signInWithPopup, onAuthStateChanged, signOut as firebaseSignOut } from "https://www.gstatic.com/firebasejs/11.10.0/firebase-auth.js";
import { doc, setDoc } from "https://www.gstatic.com/firebasejs/11.10.0/firebase-firestore.js";

window.myAppJsFirebase = {
    signInWithGooglePopup: async () => {
        try {
            const result = await signInWithPopup(auth, googleProvider);
            return { success: true, uid: result.user.uid, email: result.user.email };
        } catch (error) {
            let msg = error.message || "Unknown error";
            if (error.code === 'auth/popup-closed-by-user' || error.code === 'auth/cancelled-popup-request') {
                msg = 'Popup closed or cancelled.';
            }
            return { success: false, error: msg };
        }
    },
    signOut: async () => {
        try {
            await firebaseSignOut(auth);
            return { success: true };
        } catch (error) {
            return { success: false, error: error.message || "Unknown error" };
        }
    },
    observeAuthState: (kotlinCallback) => {
        return onAuthStateChanged(auth, (user) => {
            kotlinCallback(!!user, user?.uid, user?.email);
        });
    },
    createUser: async function(id, email) {
        try {
            const userRef = doc(firestore, "users", id);
            await setDoc(userRef, { email: email });
            console.log("[Firebase] User document successfully written.");
            return { success: true };
        } catch (error) {
            console.error("[Firebase] Error writing user document:", error);
            return { success: false, error: error.message || "Unknown error" };
        }
    }
};

window.myAppJsHelpers = {
    getBooleanProperty: function(obj, propName) {
        if (obj && typeof obj[propName] === 'boolean') {
            return obj[propName];
        }
        return null;
    },
    getStringProperty: function(obj, propName) {
        if (obj && typeof obj[propName] === 'string') {
            return obj[propName];
        }
        return null;
    },
};

console.log("JS: Firebase bridge for Kotlin initialized.");
