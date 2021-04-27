# Firebase emulator setup

## Installation

Follow the instructions [here](https://firebase.google.com/docs/cli?authuser=5&hl=en#install_the_firebase_cli).

## Usage

Depending on your os, run the command `firebase emulators:start --import ./firestore_state_windows` or `firebase emulators:start --import ./firestore_state_linux` from within the root directory of the project before running any tests (including through gradle).
After the tests are finished you can shut it down. The emulator state will be kept until it is shut down, so keep that in mind when writing tests.