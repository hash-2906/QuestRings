# Google Play Store Listing & Release Package

This document contains all metadata, visual specifications, descriptions, and commands needed to publish **QuestRings** on the Google Play Console.

---

## 1. Store Listing Metadata

### App Title (30 characters max)
```text
QuestRings: Habit RPG & Arena
```

### Short Description (80 characters max)
```text
Conquer daily habits with concentric rings, XP quests & multiplayer chambers.
```

### Full Description (4000 characters max)
```text
Make today legendary with QuestRings — the premium cyberpunk productivity app that turns daily self-mastery into an immersive RPG adventure.

⚡ THE CONCENTRIC RING ENGINE
Track your core pillars through three synchronized concentric rings:
• Vitality (Red): Physical resilience, workouts, hydration, and recovery.
• Intellect (Green): Deep work, coding, reading, and mental acuity.
• Discipline (Blue): Daily routines, morning momentum, and focus habits.
Close all three rings before midnight to activate the Tri-Ring Multiplier and trigger dazzling particle celebrations!

⚔️ SCHEDULED QUESTS & LIVE COUNTDOWN
Manage real-world obligations with high-stakes urgency:
• Easy, Side, Main, and Boss Quest priority tiers with scalable XP rewards.
• Dynamic live countdown timers with a forgiving 24-hour grace period.
• Dual-view interface: seamlessly switch between an actionable agenda list and a monthly calendar breakdown.

🛡️ COLLABORATIVE ROOMS & LIVE ARENA
Don't grind alone. Form syndicates with friends, colleagues, or fitness partners:
• Real-time rooms with instant 6-character join codes and QR code scanning.
• Post dual-type assignments (simple deliverables or step-by-step checklists).
• Live speed & rank scoring engine: fast completions unlock massive speed bonuses.
• Per-assignment live leaderboards updated across all devices in real time.

🏆 XP ECONOMY & MEDAL ACHIEVEMENTS
Every habit check, task completion, and room victory feeds into a unified, auditable XP ledger:
• Advance through Bronze, Silver, Gold, Platinum, and Obsidian tiers.
• Full-screen medal reveal celebrations with particle bursts.
• Detailed audit ledger with category breakdown and streak multipliers.

🌐 SOCIAL LAYER & GLOBAL LEADERBOARDS
Connect with mutual friends and compete on the global stage:
• Community activity feed with animated tactile reactions (👏, 🔥, ⚡).
• Weekly, Monthly, and All-Time global rankings.
• Comprehensive public dossiers featuring habit ring distributions and shared chambers.

💎 CRAFTED FOR PEAK PERFORMANCE
• 100% Offline-First: Smooth Room SQLite local storage with 100MB Firestore cloud caching.
• Tactile Haptics: Distinct tactile feedback for tabs, checklist items, and milestone victories.
• Skeleton Shimmer: Zero generic spinners; sleek dark cyberpunk skeletons.
• Full Privacy Compliance: Instant, self-serve account and data deletion in-app.

Forge your aura. Close your rings. Conquer your day.
```

---

## 2. Graphic Asset Specifications

### A. App Icon (512 x 512 PNG, 32-bit color, max 1MB)
- **Visuals:** Concentric triple rings (Red, Green, Blue) surrounding a radiant Gold lightning bolt against a pitch-black OLED background (`#0A0A0E`).
- **Adaptive Icon XML:** Ready in `res/mipmap-anydpi-v26/ic_launcher.xml` with monochrome Material You support for Android 13+.

### B. Feature Graphic (1024 x 500 PNG/JPEG, 24-bit, max 15MB)
- **Dimensions:** 1024 px width × 500 px height.
- **Visual Design:**
  - Background: Cyberpunk subtle grid with radial purple glow (`#7E5DFF15`).
  - Left Center: Floating 3D-angled phone showing the glowing triple concentric rings (`#FF2D55`, `#30D158`, `#0A84FF`).
  - Right Center: Bold typography: "MAKE TODAY LEGENDARY" in Playfair italics and DM Sans.
  - Accents: Floating Gold Guardian medal 🥇 and Obsidian Sovereign badge ⬛.

### C. Screenshots (Minimum 4 high-res 1080x2400 PNGs)
1. **Screenshot 1 — Personal Habit Rings**
   - Top Headline: *"CLOSE YOUR RINGS • BUILD UNBROKEN MOMENTUM"*
   - Screen: Home screen with 3 concentric rings glowing, streak badges, and active habit cards.
2. **Screenshot 2 — Scheduled Quests & Timers**
   - Top Headline: *"PRIORITY QUESTS • REAL-TIME DEADLINES"*
   - Screen: Task scheduler showing Boss Quest countdown, grace period badges, and XP pills.
3. **Screenshot 3 — Room Chambers & Live Leaderboards**
   - Top Headline: *"MULTIPLAYER ROOMS • CLIMB THE PODIUM"*
   - Screen: Cyber Valkyries chamber with checklist assignments and #1 gold crown podium.
4. **Screenshot 4 — Medals & XP Ledger**
   - Top Headline: *"PRESTIGE MEDALS • AUDITABLE XP ECONOMY"*
   - Screen: Full-screen medal unlock card and weekly gains XP bar chart.

---

## 3. Signing & Build Commands

### Step 1: Generate Release Keystore (One-Time)
Run this command in a secure location (e.g., your local user directory outside Git):
```bash
keytool -genkey -v \
  -keystore ~/questrings_release.jks \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -alias questrings_key
```

### Step 2: Configure `keystore.properties`
Create `keystore.properties` in the project root:
```properties
storeFile=/Users/your_username/questrings_release.jks
storePassword=YourKeystorePassword
keyAlias=questrings_key
keyPassword=YourKeyPassword
```
*(Note: `keystore.properties` is protected in `.gitignore` and will never be committed).*

### Step 3: Build Production Android App Bundle (AAB)
```bash
./gradlew clean bundleRelease
```
The optimized, minified, and R8-obfuscated bundle will be generated at:
```text
app/build/outputs/bundle/release/app-release.aab
```

### Step 4: Build Release APK (For Direct Testing)
```bash
./gradlew assembleRelease
```
The signed APK will be generated at:
```text
app/build/outputs/apk/release/app-release.apk
```

---

## 4. Google Play Console Pre-Launch Checklist

- [x] **App Signing by Google Play:** Enabled.
- [x] **Target SDK 34+:** Configured in `app/build.gradle.kts`.
- [x] **64-bit Architecture:** Fully supported by Kotlin/JVM.
- [x] **ProGuard / R8 Enabled:** `isMinifyEnabled = true`, `isShrinkResources = true`.
- [x] **Account Deletion Flow:** Present in Profile -> Danger Zone.
- [x] **Privacy Policy URL:** Ready (`docs/privacy_policy.md`).
- [x] **App Content Declarations:** No ads, productivity & health categorization.
