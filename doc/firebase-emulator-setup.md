# Firebase emulator setup

## Installation

Follow the instructions [here](https://firebase.google.com/docs/cli?authuser=5&hl=en#install_the_firebase_cli). There is no need to run `firebase init`, everything is already setup in the project. You only have to do `firebase login`.

## Usage

Depending on your os, run the command `firebase emulators:start --import ./firestore_state_windows` or `firebase emulators:start --import ./firestore_state_linux` from within the root directory of the project before running any tests (including through gradle). Please do not try to run the import for another os than the one you are running, it seems to break importing for some reason.
After the tests are finished you can shut it down. The emulator state will be kept until it is shut down, so keep that in mind when writing tests.
