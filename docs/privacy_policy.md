# Privacy Policy for QuestRings

**Last Updated:** October 5, 2026  
**Effective Date:** October 5, 2026  

QuestRings ("we", "our", or "the App") is committed to protecting your privacy. This Privacy Policy describes how we collect, use, store, and delete your information when you use our mobile application.

---

## 1. Information We Collect

### A. Information You Provide
- **Account Information:** When you register or sign in via Email/Password or Google Sign-In, we collect your email address, display name, user identifier (UID), and chosen profile avatar color.
- **Productivity & Quest Data:** Habits, daily completions, scheduled task quests, priority levels, checklist progress, room memberships, and room quest submissions.
- **Social Data:** Activity milestones, reactions (emojis), and mutual friend connections.

### B. Automatically Collected Information
- **Local Device Progress:** Stored in your device's local database (Room SQLite) for zero-latency offline operation.
- **Analytics & Crash Reports:** Basic diagnostics and performance metrics to ensure app stability and prevent crashes.

---

## 2. How We Use Your Information
We use your data strictly to:
1. Render and calculate your concentric habit rings (Vitality, Intellect, Discipline) and XP economy.
2. Synchronize your progress in real time across devices via Google Firebase.
3. Power collaborative Room chambers, join codes, and live assignment leaderboards.
4. Broadcast activity achievements to mutual friends.

We **do not** sell your personal data or share it with third-party advertisers.

---

## 3. Data Storage & Security
Your data is securely stored using:
- **Cloud Firestore & Firebase Authentication:** Governed by Google Cloud's enterprise security protocols and encryption in transit (HTTPS/TLS) and at rest (AES-256).
- **On-Device SQLite (Room):** Encrypted according to standard Android application sandboxing.
- **Offline Cache:** Stored with a strict 100MB quota for resilient offline performance.

---

## 4. User Rights & Account Deletion (Google Play Compliance)
Google Play requires clear and accessible mechanisms for users to delete their account and associated data.

### How to Delete Your Account:
You can permanently delete your entire account and all associated records directly from within the app at any time:
1. Open the QuestRings app and navigate to the **Profile** tab.
2. Scroll to the **Danger Zone** section at the bottom.
3. Tap **"DELETE ACCOUNT & ERASE DATA"**.
4. Confirm your decision by entering your password or confirming your authentication session.

### What Happens When You Delete Your Account:
- All personal documents in Google Cloud Firestore (`users/{uid}`) are permanently deleted.
- All room memberships and submission records associated with your UID are purged.
- All local Room SQLite tables on your device are wiped immediately.
- Your Firebase Authentication credentials are deleted from Google servers.
- This action is irreversible.

Alternatively, you may request data erasure by emailing us at **privacy@questrings.app** with your registered email address.

---

## 5. Children's Privacy
QuestRings does not knowingly collect personal identifiable information from children under the age of 13.

---

## 6. Contact Us
If you have any questions or concerns regarding this Privacy Policy, please reach out to us:
- **Email:** support@questrings.app
- **Website:** https://questrings.app
