#  MyAnimeTracker

**MyAnimeTracker** is an Android application built in **Java** that allows users to search for anime, track their personal list (completed, watching, planned), and manage each anime’s status directly through the **official MyAnimeList API**.

---

##  Objective

The goal of this app is to provide a simple, smooth, and intuitive experience for anime fans, allowing them to:

-  Search for anime titles by name.
-  View detailed information (image, average score, number of episodes, genres, etc.).
-  Add, update, or remove anime from their personal **MyAnimeList** account.
-  Keep their progress synced in real-time using the API.

---

##  API Used

**MyAnimeList API v2**

- **Base URL:** `https://api.myanimelist.net/v2`
- **Supported Methods:** `GET`, `PUT`, `PATCH`, `DELETE`
- **Features:**
  - Retrieve anime details.
  - Add or update anime in the user’s list.
  - Partially update the anime watch status.
  - Remove anime from the personal list.

> ️ You must have a **MyAnimeList API Key** for authentication.

---

##  Main Features

-  **Search anime** by name and display details like image, score, and episodes.
-  **Manage personal list** (completed, watching, or planned).
- ️ **Update anime status** via `PUT` or `PATCH` requests.
-  **Delete anime** from the list (`DELETE`).
-  **Clean and intuitive interface** with easy navigation between search, details, and personal list screens.

---


##  Expected Benefits

-  Easy and centralized management of the user’s anime list.
-  Real-time status synchronization through the MyAnimeList API.
-  Interactive and user-friendly experience.
-  Future expansion possibilities:
  - Filters by genre or popularity.
  - New episode notifications.
  - Offline mode support.

---

##  Roadmap

- [ ] Implement MyAnimeList OAuth2 authentication.
- [ ] Add favorites system and search history.
- [ ] Implement image caching and offline data support.
- [ ] Add dark mode.
- [ ] Publish on Google Play Store.

---

##  Installation and Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/your-username/MyAnimeTracker.git
