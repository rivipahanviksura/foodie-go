# 🍔 FoodieGo

FoodieGo is a full-featured Android food ordering application built with **Java** and **XML** in Android Studio. It provides separate experiences for customers and administrators, with Firebase-powered authentication and data management, food browsing, cart and order handling, location features, online payments, profile management, and an admin dashboard.

---

## ✨ Features

### 👤 Customer Features

- User registration and login
- Firebase Authentication
- Browse food categories
- View available food items
- Search for food
- View food details
- Add items to cart
- Update and manage cart items
- Place food orders
- View order confirmation
- User profile management
- Save and use current location
- Google Maps integration
- Contact restaurant by phone
- Online payment integration
- Food images loaded dynamically
- Wi-Fi/network related handling
- In-app user experience with fragments and navigation

### 🛠️ Admin Features

- Admin dashboard
- View total customers
- View total orders
- View food count
- View revenue information
- Manage customers
- Add and manage food items
- View and manage customer orders
- Update order status
- View order timestamps and customer details
- Dashboard charts and statistics

---

## 🧰 Technologies Used

| Technology | Purpose |
|---|---|
| Java | Main application development |
| XML | Android UI layouts |
| Android Studio | Development environment |
| Firebase Authentication | User authentication |
| Firebase Firestore | Cloud database |
| Firebase Storage | Cloud file/image storage support |
| Google Maps SDK | Map display |
| Google Play Services Location | Device location |
| Glide | Image loading |
| OkHttp | HTTP/network requests |
| Cloudinary Android | Image/media handling |
| PayHere Android SDK | Payment integration |
| MPAndroidChart | Dashboard charts |
| Gson | JSON processing |
| Material Components | Android UI components |
| View Binding | Safer view access |

---

## 📱 Android Configuration

- **Application ID:** `lk.foodie.foodiego`
- **Minimum SDK:** 24
- **Target SDK:** 35
- **Compile SDK:** 35
- **Java Compatibility:** Java 11
- **Version:** 1.0
- **View Binding:** Enabled

---

## 📂 Project Structure

```text
foodie-go/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/lk/foodie/foodiego/
│   │   │   │   ├── activities/
│   │   │   │   ├── adapters/
│   │   │   │   ├── fragments/
│   │   │   │   ├── models/
│   │   │   │   ├── ui/
│   │   │   │   └── ...
│   │   │   ├── res/
│   │   │   │   ├── drawable/
│   │   │   │   ├── layout/
│   │   │   │   ├── menu/
│   │   │   │   ├── navigation/
│   │   │   │   ├── values/
│   │   │   │   └── ...
│   │   │   └── AndroidManifest.xml
│   │   ├── androidTest/
│   │   └── test/
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites

Before running the project, install:

- Android Studio
- JDK 11 or a compatible JDK supported by your Android Studio installation
- Android SDK with API 35
- Git
- A Firebase project
- An Android emulator or physical Android device

---

## 📥 Installation

### 1. Clone the repository

```bash
git clone https://github.com/rivipahanviksura/foodie-go.git
```

### 2. Open the project

Open **Android Studio** and select:

```text
Open → foodie-go
```

Allow Android Studio to complete the Gradle sync.

### 3. Configure Firebase

This repository does not include the Firebase `google-services.json` configuration file.

To connect the application to Firebase:

1. Open the Firebase Console.
2. Create or select your Firebase project.
3. Add an Android application with the package name:

```text
lk.foodie.foodiego
```

4. Download `google-services.json`.
5. Place the file inside:

```text
app/google-services.json
```

6. Enable the Firebase services required by the application, including:
   - Authentication
   - Cloud Firestore
   - Storage, where required

> `google-services.json` is intentionally excluded from Git tracking.

---

## 🗺️ Google Maps Setup

The application uses Google Maps and device location services.

Configure a valid Google Maps API key for the Android application and ensure the required Maps SDK is enabled in your Google Cloud project.

For security, keep API credentials outside public source control whenever possible and apply appropriate API restrictions in Google Cloud.

---

## 💳 Payment Integration

FoodieGo includes integration with the **PayHere Android SDK** for payment functionality.

A valid PayHere merchant configuration may be required before payment-related features can be used in a production environment.

---

## 🔥 Firebase Data

The application uses Firebase services for application data and authentication.

Main application data includes areas such as:

- Users
- Food categories
- Food items
- Shopping carts
- Orders
- Customer information

Firebase security rules should be configured appropriately before deploying the application to production.

---

## 🏗️ Build and Run

After completing Firebase and required API configuration:

1. Sync the project with Gradle.
2. Select an Android emulator or connected physical device.
3. Click **Run** in Android Studio.

You can also build from the command line.

### Windows

```powershell
.\gradlew.bat assembleDebug
```

### macOS / Linux

```bash
./gradlew assembleDebug
```

The generated debug APK will normally be available under:

```text
app/build/outputs/apk/debug/
```

---

## 📦 Main Dependencies

FoodieGo uses libraries and services including:

- AndroidX AppCompat
- Material Components
- ConstraintLayout
- Android Navigation Components
- Lifecycle ViewModel and LiveData
- Firebase Authentication
- Firebase Firestore
- Firebase Storage
- Google Maps
- Google Play Services Location
- Glide
- OkHttp
- Cloudinary Android
- PayHere Android SDK
- Gson
- MPAndroidChart
- AndroidSVG
- RoundedImageView

---

## 🔐 Security Notes

Sensitive or machine-specific files are excluded from source control through `.gitignore`, including files such as:

```text
local.properties
app/google-services.json
*.jks
*.keystore
secrets.properties
.env
```

Never commit private signing keys, passwords, secret tokens, merchant secrets, or unrestricted API credentials to a public repository.

---

## 🎯 Project Purpose

FoodieGo was developed as an Android food ordering and restaurant management application that demonstrates practical mobile application development concepts including:

- Authentication
- Cloud database integration
- RecyclerView-based interfaces
- Fragment navigation
- Shopping cart management
- Order processing
- Location and map services
- Online payment integration
- Image loading and media handling
- Administrative management
- Dashboard statistics and charts

---

## 👨‍💻 Developer

**Rivipahan Viksura**

GitHub: [@rivipahanviksura](https://github.com/rivipahanviksura)

Repository: [FoodieGo](https://github.com/rivipahanviksura/foodie-go)

---

## 📄 License

This project is provided for educational and portfolio purposes.

Unless a separate license file is added to this repository, no additional open-source license is granted.

---

<p align="center">
  <strong>FoodieGo</strong><br>
  Android Food Ordering & Management Application
</p>
