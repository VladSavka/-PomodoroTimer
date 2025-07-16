// firebase-init.js

import { initializeApp } from "https://www.gstatic.com/firebasejs/11.10.0/firebase-app.js";
import { getAuth, GoogleAuthProvider } from "https://www.gstatic.com/firebasejs/11.10.0/firebase-auth.js";
import { getFirestore, enableIndexedDbPersistence } from "https://www.gstatic.com/firebasejs/11.10.0/firebase-firestore.js";

const firebaseConfig = {
  apiKey: "AIzaSyD3saA7h2rLIdRaFVWQFFRb0C5qo6KawDg",
  authDomain: "kittidoro-timer.firebaseapp.com",
  projectId: "kittidoro-timer",
  storageBucket: "kittidoro-timer.firebasestorage.app",
  messagingSenderId: "653658039232",
  appId: "1:653658039232:web:68b2f7592a783974735e25",
  measurementId: "G-GKDJDKRKES"
};

export const app = initializeApp(firebaseConfig);
export const auth = getAuth(app);
export const googleProvider = new GoogleAuthProvider();
export const firestore = getFirestore(app);

enableIndexedDbPersistence(firestore).catch((err) => {
  console.error("Persistence error:", err.code);
});
