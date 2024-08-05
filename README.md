# Finder - Roommate and Accommodation Finder App

Finder is an Android application designed to help users find roommates and accommodations with ease. The app provides a platform for users to search for available roommates, upload their own listings, manage their profiles, and interact with various features to enhance their experience.

## Features

- **Search Functionality**: Users can search for available roommates using a search bar. Search results are displayed in a RecyclerView in the SearchActivity.
- **Roommate Listings**: Users can view a list of available roommates and their details, including name, preferences, and contact information.
- **User Profiles**: Users can create and manage their profiles with personal details, preferences, and a profile picture.
- **Upload Roommate Listings**: Users can upload information about themselves or available rooms through an UploadActivity.
- **Navigation Drawer**: Provides easy access to different sections of the app, including home, settings, and profile.
- **Bottom Navigation**: Allows users to quickly navigate between the home screen, settings, and profile.
- **Firebase Integration**: Utilizes Firebase Realtime Database for storing and retrieving roommate listings and Firebase Authentication for user login and registration.
- **RecyclerView**: Efficiently displays lists of roommates and search results, supporting smooth scrolling and dynamic data updates.

## User Interface

- **Main Screen**: Contains a search bar, upload button, and a RecyclerView displaying roommate listings, integrated with a navigation drawer for easy access to various app sections.
- **Search Screen**: Displays search results in a RecyclerView based on user queries.
- **Profile Screen**: Allows users to view and update their profiles.
- **Upload Screen**: Provides a form for users to input details about themselves or available rooms for rent.

## Technical Details

- **XML Layouts**: Utilizes ConstraintLayout and LinearLayout for flexible UI design, and RecyclerView for displaying lists of items.
- **Kotlin Code**: Manages activities, data fetching, and user interactions, integrating with Firebase for data storage and user authentication.
- **Firebase**: Realtime Database for storing roommate listings and Authentication for secure user login and registration.

## Getting Started

### Prerequisites

- Android Studio
- Firebase Account

### Installation

1. **Clone the repository**:
   ```bash https://github.com/joshdei/FinderApp.git
