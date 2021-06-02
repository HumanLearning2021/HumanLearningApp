# HumanLearningApp

[![Maintainability](https://api.codeclimate.com/v1/badges/88c3d9e8784c8fa76af6/maintainability)](https://codeclimate.com/github/HumanLearning2021/HumanLearningApp/maintainability)

[![Test Coverage](https://api.codeclimate.com/v1/badges/88c3d9e8784c8fa76af6/test_coverage)](https://codeclimate.com/github/HumanLearning2021/HumanLearningApp/test_coverage)

## The application
Human Learning is an application whose objective is to facilitate learning of abstraction by matching images. There are two main aspects to the application: learning on a dataset and managing datasets. Traditionnaly one would use this learning methods with printed images, but this takes a lot of time to prepare and update. Here you can do all of this digitally.

The application's database is organized in datasets who contain categories. Pictures are assigned to categories and each category has a picture which is set as its representative.

## Features
- Login with Google accounts (either as an administrator who can alter datasets or as a learner)
- Cloud storage for the database
- Offline mode for the entire database (changes made when in offline mode are not reflected in the cloud)
- Learning on a dataset of choice from the database and with visual as well as audio feedback. Three different learning modes are available:
    - Presentation where images must be classified against an exact copy
    - Representation where images must be classified against the representative images of their respective category
    - Evaluation where the learner performs a [ComFor](https://www.researchgate.net/publication/6886535_The_ComFor_An_instrument_for_the_indication_of_augmentative_communication_in_people_with_autism_and_intellectual_disability)-like evaluation whose results are displayed at the end
- Database management utilities:
    - Add and remove datasets
    - Add and remove categories inside a dataset
    - Add and remove pictures using either the phone's camera or by choosing a picture present on the device
    - Modify the representative picture of a category
    - Modify dataset names
- Most layouts are adapted for use with a tablet

## Installation instructions
To compile the code from source you need to put the cleartext copy of the build secrets at the root of the repository. Please contact the development team to get access to them.

If you want to run the unit tests you need to first run the Firebase emulators:
- To install the emulators follow the instructions [here](https://firebase.google.com/docs/cli?authuser=5&hl=en#install_the_firebase_cli). There is no need to run `firebase init`, everything is already setup in the project. You only have to do `firebase login`.
- Depending on your os, run the command `firebase emulators:start --import ./firestore_state_windows` or `firebase emulators:start --import ./firestore_state_linux` from within the root directory of the project before running any tests (including through gradle)
