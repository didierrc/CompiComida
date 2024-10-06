# 🥐 CompiComida 🥐

## General Description
Get rid of the grocery list stress with the CompiComida App. Designed to simplify the meals planning and pantry management.
It'll help you to create and organize your shopping list, discover new and delicious meals and to keep track of your pantry
resources like expire dates! And all in just one place.

![Grocery List Simpson](https://media2.giphy.com/media/l0G16KpPfcmdN1G1O/200w.gif?cid=6c09b952mnojk36mo1ony6m0eanf37mxp3klovbeeyy3yt24&ep=v1_gifs_search&rid=200w.gif&ct=g)

- **Project Duration**: 3 Months
- **Total Hours**: 60 group hours + 30 individual hours per person
- **Meet the APK Droids Team** 🤖

|  Name                       | UO         |
|-----------------------------|------------|
|  Didier Yamil Reyes Castro  |  UO287866  | 
|  Raúl Mera Soto             |  UO287827  |
|  Yago Fernández López       |  UO289549  |

## Sprint Planning
Here you will find a detailed breakdown of what each team member have done (or will have to do) along the creation of this Project.

---

### 🛒 Sprint 1: Basic Grocery List Management 🥘
**Duration:** 3 weeks  
**Total Hours:** 20h group work + 10h individual work

#### Goals:
- Develop a basic user interface for managing grocery lists.
- Implement functionality to add, remove, and categorize grocery items.

#### Key Concepts:
- **Activity and Fragments**: UI creation using activities and fragments.
- **RecyclerView**: Display a list of grocery items.
- **Data Persistence**: Store grocery lists with SharedPreferences or SQLite.

#### Tasks and Responsibilities:
- **Member A: UI Design**
  - Design the main interface (XML layout).
  - Create mockups for user experience.
  
- **Member B: Data Management**
  - Implement data persistence using SharedPreferences or SQLite.
  - Develop methods to add and remove items from the list.
  
- **Member C: RecyclerView Implementation**
  - Implement RecyclerView to display the grocery list.
  - Manage item clicks to remove items.

---

### 🥩 Sprint 2: Meal Planning and Price Comparison 💲
**Duration:** 3 weeks  
**Total Hours:** 20h group work + 10h individual work

#### Goals:
- Introduce meal planning functionality.
- Implement a basic price comparison feature.

#### Key Concepts:
- **Networking**: Fetch data from external APIs for price comparison.
- **Fragments**: Separate screens for meal planning using fragments.
- **ViewModel**: Manage UI-related data with ViewModel.

#### Tasks and Responsibilities:
- **Member A: Meal Planning UI**
  - Design the meal planning interface.
  - Create a layout for displaying meal options.
  
- **Member B: API Integration**
  - Research and implement APIs for price comparison.
  - Handle JSON parsing for external data.
  
- **Member C: ViewModel Implementation**
  - Implement ViewModel to manage data related to meal plans.
  - Integrate meal planning with the grocery list functionality.

---

### ⚠️ Sprint 3: Expiry Notifications and Voice Input 📢
**Duration:** 3 weeks  
**Total Hours:** 20h group work + 10h individual work

#### Goals:
- Implement expiry notifications for pantry items.
- Add voice input functionality for adding items.

#### Key Concepts:
- **AlarmManager**: Set notifications for expiry dates.
- **Speech Recognition**: Implement voice input for adding items.
- **BroadcastReceiver**: Handle notifications with BroadcastReceiver.

#### Tasks and Responsibilities:
- **Member A: Notification System**
  - Implement the notification system using AlarmManager.
  - Set up logic for tracking expiry dates.
  
- **Member B: Voice Input Implementation**
  - Integrate speech recognition for adding items.
  - Create a user interface for voice input.
  
- **Member C: Testing and Optimization**
  - Test the app for bugs and usability.
  - Optimize performance based on user feedback.

---
### 🚀 Future Features (if time allows):
- **Shopping History**: View past purchases and trends.
- **Recipe Suggestions**: Suggest recipes based on pantry items.
- **List Sharing**: Share grocery lists with others.
- **Dietary Preferences**: Filter items by dietary needs (e.g., vegan, gluten-free).
- **Inventory Management**: Track pantry inventory and suggest restocks.
- **Barcode Scanning**: Add items by scanning barcodes.
- **Gamification**: Challenges or rewards for completing lists or meal planning.
- **Automatic photos**: Ability to add photos automatically for regular items like bread, water, milk, ...
- **Price tracking charts**: Apart from setting a price for an item. It would be beneficial to see charts on the fluctuation of price along time.
---

## Initial schema

![db_schema](https://github.com/user-attachments/assets/56ac97f9-6a77-4a3a-84c1-1ddc9f98bace)

