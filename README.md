<p align="center">
  <img 
    src="https://github.com/user-attachments/assets/6452ab1a-c820-44a4-ab38-bad7956a4299"
    width="120"
    height="120"
    alt="MealWay Logo"
  />
</p>

<h1 align="center">MealWay</h1>

<p align="center">
  <b>Plan your meals smarter, easier, and offline-ready 🍽️</b>
</p>

---

## 📱 About MealWay

**MealWay** is an Android application that helps users plan their weekly meals easily and efficiently.  
Users can explore meals by categories, countries, and ingredients, save favorites for offline use, and plan meals for the current week — all powered by **TheMealDB API**.

---

## 📸 Screenshots

### 🚀 Onboarding

<table>
  <tr>
    <th>Onboarding 1</th>
    <th>Onboarding 2</th>
    <th>Onboarding 3</th>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/b7f1c4ed-bcb9-4e18-983c-051f4e737708" width="300"/></td>
    <td><img src="https://github.com/user-attachments/assets/12e9f7a0-b586-49ee-8f35-b76c00a2d6ef" width="300"/></td>
    <td><img src="https://github.com/user-attachments/assets/546a8a40-4edd-4977-a575-41f98e5f1f55" width="300"/></td>
  </tr>
</table>

---

### ☀️ Light Mode

<table>
  <tr>
    <th>Home</th>
    <th>Search</th>
    <th>Meal Details</th>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/25a4f33c-69b5-4578-bb5c-417ad24c405e" width="300"/></td>
    <td><img src="https://github.com/user-attachments/assets/96925e9e-6bf5-4e6e-90a7-f29924149f56" width="300"/></td>
    <td><img src="https://github.com/user-attachments/assets/d09e162e-5a0e-4c08-8da8-2f7651bda44c" width="300"/></td>
  </tr>
</table>

<table>
  <tr>
    <th>Favorites</th>
    <th>Weekly Plan</th>
    <th>Profile</th>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/ccd843ba-273c-42e9-a053-de2b262555fd" width="300"/></td>
    <td><img src="https://github.com/user-attachments/assets/f9b2742e-e47c-4214-92cf-d01f322e9415" width="300"/></td>
    <td><img src="https://github.com/user-attachments/assets/406dad14-c575-481d-a006-02c77dbc763d" width="300"/></td>
  </tr>
</table>

---

### 🌙 Dark Mode

<table>
  <tr>
    <th>Home</th>
    <th>Search</th>
    <th>Meal Details</th>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/65d89c6a-e3d2-4731-9d9f-9ceb43ea55a7" width="300"/></td>
    <td><img src="https://github.com/user-attachments/assets/4d3daecd-1804-489e-8c70-c5e78aa301e7" width="300"/></td>
    <td><img src="https://github.com/user-attachments/assets/ae9e27d7-0032-49b1-a90a-911159de823f" width="300"/></td>
  </tr>
</table>

<table>
  <tr>
    <th>Favorites</th>
    <th>Weekly Plan</th>
    <th>Profile</th>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/ce611dff-91cc-4805-bb51-c5b8db412dd9" width="300"/></td>
    <td><img src="https://github.com/user-attachments/assets/fbccf72a-e3aa-4590-9e41-2edc6dd068c9" width="300"/></td>
    <td><img src="https://github.com/user-attachments/assets/5164ce79-0cfd-455c-af6e-f7c736fc1200" width="300"/></td>
  </tr>
</table>


---

## ✨ Features

- 🍽️ **Meal of the Day**  
  Get a randomly selected meal for daily inspiration.

- 🔍 **Advanced Search**  
  Search meals by:
  - Country  
  - Ingredient  
  - Category  

- 📂 **Browse Categories**  
  Explore a list of available meal categories.

- 🌍 **Meals by Country**  
  View popular meals from different countries.

- ❤️ **Favorite Meals**  
  Add or remove meals from favorites.  
  - Stored locally using **Room Database**
  - Available **offline**
  - Firebase is used for favorites storage

- 📅 **Weekly Meal Planning**  
  Add and view meals for the current week.

- 📡 **Offline Support**  
  - View favorite meals
  - View weekly meal plan  
  even when there is no network connection.

- ☁️ **Data Synchronization & Backup**
  - Sync and restore user data using **Firebase**
  - Access archived data after login

- 🔐 **Authentication**
  - Login & Sign Up
  - Social Authentication by Google 
  - Firebase Authentication
  - Auto-login using **SharedPreferences**

---

## 🛠️ Tech Stack & Libraries

- **Language:** Java  
- **Architecture:** MVP
- **API:** [TheMealDB](https://www.themealdb.com/)  
- **Local Storage:** Room Database  
- **Authentication & Sync:** Firebase Authentication & Firebase Firestore  
- **Networking:** Retrofit  
- **Asynchronous:**  RxJava 3  
- **UI:** XML, Material Design  
- **State Management: RxJava (Single, Observable, Completable) 

---

## 🚀 Getting Started

### Prerequisites
- Android Studio
- Android SDK
- Internet connection (for API & Firebase)

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/MealWay.git
