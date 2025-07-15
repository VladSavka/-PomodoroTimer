import { auth, googleProvider, firestore } from "./firebase-init.js";
import {
    signInWithPopup,
    onAuthStateChanged,
    signOut as firebaseSignOut
} from "https://www.gstatic.com/firebasejs/11.10.0/firebase-auth.js";
import {
    doc,
    setDoc,
    getDoc,
    deleteDoc,
    collection,
    onSnapshot,
    writeBatch
} from "https://www.gstatic.com/firebasejs/11.10.0/firebase-firestore.js";

const projectListeners = new Map();
let listenerCounter = 0;

const settingsListeners = new Map();
let settingsListenerCounter = 0;

function getUserId() {
    const user = auth.currentUser;
    if (!user) {
        throw new Error("[Firebase] No authenticated user.");
    }
    return user.uid;
}

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
            return { success: true };
        } catch (error) {
            return { success: false, error: error.message || "Unknown error" };
        }
    },

    listenToProjects: (kotlinCallback) => {
        const listenerId = `listener_${listenerCounter++}`;
        const projectsCollection = collection(firestore, "users", getUserId(), "projects");
        const unsubscribe = onSnapshot(projectsCollection, (snapshot) => {
            const projects = [];
            snapshot.forEach(doc => {
                projects.push({ id: doc.id, ...doc.data() });
            });
            kotlinCallback(JSON.stringify(projects));
        }, (error) => {
            console.error("[Firebase] Projects snapshot error:", error);
        });

        projectListeners.set(listenerId, unsubscribe);
        return listenerId;
    },

    removeProjectListener: (listenerId) => {
        const unsubscribe = projectListeners.get(listenerId);
        if (unsubscribe) {
            unsubscribe();
            projectListeners.delete(listenerId);
        }
    },

    saveProject: async (projectJson) => {
        try {
            const project = JSON.parse(projectJson);
            const projectRef = doc(firestore, "users", getUserId(), "projects", project.id);
            await setDoc(projectRef, project);
            return { success: true };
        } catch (error) {
            return { success: false, error: error.message || "Unknown error" };
        }
    },

    removeProjectById: async (projectId) => {
        try {
            const projectRef = doc(firestore, "users", getUserId(), "projects", projectId);
            await deleteDoc(projectRef);
            return { success: true };
        } catch (error) {
            return { success: false, error: error.message || "Unknown error" };
        }
    },

    getProjectById: async (projectId) => {
        try {
            const projectRef = doc(firestore, "users", getUserId(), "projects", projectId);
            const docSnap = await getDoc(projectRef);

            if (docSnap.exists()) {
                return { success: true, projectJson: JSON.stringify({ id: docSnap.id, ...docSnap.data() }) };
            } else {
                return { success: false, error: "Project not found." };
            }
        } catch (error) {
            return { success: false, error: error.message || "Unknown error" };
        }
    },

    updateProjects: async (projectsJson) => {
        try {
            const projects = JSON.parse(projectsJson);
            const batch = writeBatch(firestore);
            const projectsCollectionRef = collection(firestore, "users", getUserId(), "projects");

            projects.forEach((project) => {
                if (!project.id) {
                    throw new Error("Project missing id");
                }
                const projectDocRef = doc(projectsCollectionRef, project.id);
                batch.set(projectDocRef, project);
            });

            await batch.commit();
            return { success: true };
        } catch (error) {
            return { success: false, error: error.message || "Unknown error" };
        }
    },

    getTimeSettings: (kotlinCallback) => {
        const listenerId = `settingsListener_${settingsListenerCounter++}`;
        const docRef = doc(firestore, "users", getUserId(), "settings", "timeSettings");

        const unsubscribe = onSnapshot(docRef, (docSnap) => {
            if (docSnap.exists()) {
                const json = JSON.stringify(docSnap.data());
                kotlinCallback(json);
            } else {
                kotlinCallback(null);
            }
        }, () => {
            kotlinCallback(null);
        });

        settingsListeners.set(listenerId, unsubscribe);
        return listenerId;
    },

    removeTimeSettingsListener: (listenerId) => {
        const unsubscribe = settingsListeners.get(listenerId);
        if (unsubscribe) {
            unsubscribe();
            settingsListeners.delete(listenerId);
        }
    },

    setTimeSettings: async (timeSettingsJson) => {
        try {
            const data = JSON.parse(timeSettingsJson);
            const docRef = doc(firestore, "users", getUserId(), "settings", "timeSettings");
            await setDoc(docRef, data);
            return { success: true };
        } catch (error) {
            return { success: false, error: error.message || "Unknown error" };
        }
    },

    getAlarmSound: (kotlinCallback) => {
        const listenerId = `alarmSoundListener_${settingsListenerCounter++}`;
        const docRef = doc(firestore, "users", getUserId(), "settings", "alarmSound");

        const unsubscribe = onSnapshot(docRef, (docSnap) => {
            if (docSnap.exists()) {
                const data = docSnap.data();
                const soundName = data?.sound || "STANDARD";
                kotlinCallback(soundName);
            } else {
                kotlinCallback("STANDARD");
            }
        }, () => {
            kotlinCallback("STANDARD");
        });

        settingsListeners.set(listenerId, unsubscribe);
        return listenerId;
    },

    removeAlarmSoundListener: (listenerId) => {
        const unsubscribe = settingsListeners.get(listenerId);
        if (unsubscribe) {
            unsubscribe();
            settingsListeners.delete(listenerId);
        }
    },

    setAlarmSound: async (soundName) => {
        try {
            const data = { sound: soundName };
            const docRef = doc(firestore, "users", getUserId(), "settings", "alarmSound");
            await setDoc(docRef, data);
            return { success: true };
        } catch (error) {
            return { success: false, error: error.message || "Unknown error" };
        }
    }
};

console.log("JS: Firebase bridge for Kotlin initialized.");
