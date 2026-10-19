# Aurorah CAN Digital Hub 🌟

[![Android SDK](https://img.shields.io/badge/Android-SDK%2034-brightgreen.svg)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin%201.9+-blue.svg)](https://kotlinlang.org/)
[![Database](https://img.shields.io/badge/Database-MySQL%20%7C%20MariaDB-orange.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-purple.svg)](LICENSE)

**Aurorah CAN Digital Hub** is a comprehensive, community-focused mobile platform designed for the **Aurorah Community Action Network (CAN)**. The application connects volunteers, beneficiaries, and donors to efficiently report, filter, and respond to community needs, facilitate transparent financial donations, and coordinate local social aid projects.

---

## 🚀 Key Features

### 🔐 1. Authentication
- **Email/Password Login & Registration**: Secure authentication with real-time input validation (RFC 5322 compliant emails & strong password enforcement).
- **Role-Based Access Control**: Differentiates between `Volunteer`, `Beneficiary`, and `Admin` user personas.
- **Encrypted Session Management**: Local persistent session handling using `SessionManager` with secure auth tokens.

### 📋 2. Community Needs & Assistance Portal
- **Real-Time Needs Stream**: Browse community aid requests categorized by Food, Clothing, Education, Healthcare, and Infrastructure.
- **Priority Badging**: Visual indication of urgency (`Critical`, `High`, `Medium`, `Low`).
- **Category Filtering**: Filter community requests dynamically using tabbed chips or category selectors.
- **Offer Assistance Workflow**: Interactive Bottom Sheet dialog allowing volunteers to submit skill offers, availability, and fulfillment notes directly to aid requests.

### 💳 3. Financial Donation Gateway
- **Multi-Category Allocation**: Direct donations toward specific causes (e.g., General Community Fund, Youth Education, Emergency Relief).
- **Payment Method Integration**: Support for Card, Credit, EFT/Bank Transfer, and Mobile Pay options.
- **Transaction Generation**: Instant, unique transaction reference generation (`TXN-YYYYMMDD-XXXX`) with full transaction receipt details.

### 📰 4. Home Dashboard & News Stream
- **Personalized Welcome**: Dynamic greeting displaying active volunteer name and role status.
- **Community News Feed**: Stay updated with local aid developments, press releases, and success stories.
- **Quick Action Links**: Rapid navigation to report needs, donate, or browse volunteer opportunities.

### ⚙️ 5. Settings & User Controls
- **Dark Mode Support**: Seamless light/dark theme toggling backed by `AppCompatDelegate` and persistent preferences.
- **Password Management**: Secure dialog to update user passwords with complexity checks and mock API synchronization.
- **Notification Preferences**: Customizable push/alert toggles.
- **Secure Logout**: Complete session clearance returning users to the login activity.

---

## 🗄️ Database Architecture & Backend Schema

The project includes a robust relational database schema (`database/schema_and_seed.sql`) optimized for MySQL / MariaDB engine.

```
       +-------------------+
       |       Users       |
       +-------------------+
         /               \
   (1:1) /                 \ (1:1)
        v                   v
+---------------+   +-------------------+
|  Volunteers   |   |   Beneficiaries   |
+---------------+   +-------------------+

       +-------------------+           +-------------------+
       |  CommunityNeeds   |           |     Donations     |
       +-------------------+           +-------------------+
```

### Table Definitions
1. **`Users`**: Primary account repository storing `UserID`, `FullName`, `Email`, `PasswordHash` (BCrypt encrypted), `Role`, and `CreatedAt`.
2. **`Volunteers`**: Profiles linked 1:1 with `Users` capturing phone contact, skills list, and availability.
3. **`Beneficiaries`**: Profiles linked 1:1 with `Users` tracking household size, address, and primary need category.
4. **`CommunityNeeds`**: Central register of aid requests storing `Title`, `Description`, `Location`, `Priority`, `Category`, and `Status` (`Open`, `In Progress`, `Fulfilled`).
5. **`Donations`**: Transaction log linking users to financial contributions with `Amount`, `PaymentMethod`, and unique `TransactionRef`.

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin 1.9+
- **Architecture**: MVVM (Model-View-ViewModel) with Repository Pattern
- **UI Framework**: Android Jetpack, Material Design 3, ViewBinding, Navigation Component
- **Networking**: Retrofit 2, OkHttp 4, Gson Converter, MockApiRepository
- **Authentication**: Firebase Authentication, Google Play Services Auth
- **Local Storage**: SharedPreferences, Encrypted Security Preferences
- **Testing**:
  - **Unit Testing**: JUnit 4, Mockito
  - **UI Testing**: Espresso, AndroidJUnit4 Test Runner

---

## 📁 Project Structure

```
AurorahCANDigitalHub/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/aurorah/can/
│   │   │   │   ├── data/
│   │   │   │   │   ├── api/          # ApiService & MockApiRepository
│   │   │   │   │   ├── local/        # SessionManager
│   │   │   │   │   └── model/        # Data Models (User, CommunityNeed, Donation, etc.)
│   │   │   │   ├── ui/
│   │   │   │   │   ├── auth/         # LoginActivity & RegisterActivity
│   │   │   │   │   ├── donation/     # DonationFragment
│   │   │   │   │   ├── home/         # HomeFragment & NewsAdapter
│   │   │   │   │   ├── needs/        # NeedsFragment, NeedsAdapter & OfferAssistanceBottomSheet
│   │   │   │   │   └── settings/     # SettingsFragment & ChangePasswordDialog
│   │   │   │   ├── util/             # InputValidator & Helpers
│   │   │   │   └── MainActivity.kt   # Host Activity with Bottom Navigation
│   │   │   └── res/                  # Layouts, Drawables, Menus & Nav Graph
│   │   ├── test/                     # Local Unit Tests (InputValidatorTest)
│   │   └── androidTest/              # Instrumentation Tests (LoginActivityTest)
│   └── build.gradle.kts
├── database/
│   └── schema_and_seed.sql           # MySQL Relational Schema & 30+ Seed Records
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## ⚙️ Getting Started

### Prerequisites
- **Android Studio**: Hedgehog (2023.1.1) or newer / Ladybug
- **JDK**: Java Development Kit 11+
- **Android SDK**: API Level 34 (Compile & Target)
- **Minimum SDK**: API Level 26 (Android 8.0 Oreo)

### Build & Run
1. Clone the repository:
   ```bash
   git clone https://github.com/AurorahCAN/AurorahCANDigitalHub.git
   cd AurorahCANDigitalHub
   ```
2. Open the project in **Android Studio**.
3. Sync Gradle dependencies:
   ```bash
   ./gradlew build
   ```
4. Run the debug application on an emulator or physical device:
   ```bash
   ./gradlew assembleDebug
   ```

### Running Tests
- **Unit Tests**:
  ```bash
  ./gradlew test
  ```
- **UI Instrumentation Tests**:
  ```bash
  ./gradlew connectedAndroidTest
  ```

---

## 📄 License

This project is open-source under the [MIT License](LICENSE). Developed for the Aurorah Community Action Network.
