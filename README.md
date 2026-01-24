<p align="center">
  <img 
    src="https://github.com/user-attachments/assets/6452ab1a-c820-44a4-ab38-bad7956a4299"
    width="120"
    height="120"
    alt="MealWay Logo"
  />
</p>

<h1 align="center">MealWay</h1>



**MealWay** is an Android mobile application that helps users plan their weekly meals easily and efficiently.  
It allows users to explore meals by categories, countries, and ingredients, save favorites for offline use, and plan meals for the current week — all powered by **TheMealDB API**.

---

## 📱 Screenshots

### Light Mode
| Home | Search | Meal Details |
|------|--------|--------------|
| ![Home](https://github.com/user-attachments/assets/4a0985f1-ec54-4eaf-9cec-8b64aedaac67) | ![Search](https://github.com/user-attachments/assets/13bf1fb2-0573-4773-a147-9265fb8871fe) | ![MealDetails](https://github.com/user-attachments/assets/48b8ed14-7a54-401c-8597-224185cb0128) |

| Favorites | Weekly Plan | Profile |
|-----------|------------|---------|
| ![MyFav](https://github.com/user-attachments/assets/9d403b13-0caf-4887-95ed-3f0ea7739748) | ![Appointment](https://github.com/user-attachments/assets/3ee2a3fa-d395-4ee9-8dc8-85400bd12733) | ![Profile](https://github.com/user-attachments/assets/6d90e062-26ca-44fb-b1c3-9a81747ee077) |
### Dark Mode

<table>
  <tr>
    <th>Home</th>
    <th>Search</th>
    <th>Meal Details</th>
  </tr>
  <tr>
    <td>
      <img src="https://github.com/user-attachments/assets/adf25b95-70bc-49e7-afb4-b7e35124177e" width="320" />
    </td>
    <td>
      <img src="https://github.com/user-attachments/assets/0e1dcdc7-3230-4516-8710-14f42e50cac0" width="320" />
    </td>
    <td>
      <img src="https://github.com/user-attachments/assets/c3316e94-c4b8-4999-9165-56efef79c8a2" width="320" />
    </td>
  </tr>
</table>

<table>
  <tr>
    <th>Favorites</th>
    <th>Weekly Plan</th>
    <th>Profile</th>
  </tr>
  <tr>
    <td>
      <img src="https://github.com/user-attachments/assets/1bb16b66-6750-4bdf-b577-b3ac4138fca6" width="320" />
    </td>
    <td>
      <img src="https://github.com/user-attachments/assets/779742d5-fdb2-4c2a-a200-0bb2f0e92d07" width="320" />
    </td>
    <td>
      <img src="https://github.com/user-attachments/assets/a68ba680-9743-4e45-80bd-55639a0decec" width="320" />
    </td>
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

- **Language:** Kotlin  
- **Architecture:** MVVM  
- **API:** [TheMealDB](https://www.themealdb.com/)  
- **Local Storage:** Room Database  
- **Authentication & Sync:** Firebase Authentication & Firebase Firestore  
- **Networking:** Retrofit  
- **Asynchronous:** Coroutines / Flow  
- **UI:** XML, Material Design  
- **State Management:** ViewModel, LiveData  

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
