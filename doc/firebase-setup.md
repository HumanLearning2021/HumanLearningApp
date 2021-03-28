# Firebase Setup

## Introduction

Human Learning is hosted on [Google Firebase Cloud].
This document explains how this was set up in a way that can hopefully be reproduced.

The development project is called [human-learning-app].
Unit tests in this repository rely on it.
Do not use it in production! Please create and administer your own.

[Google Firebase Cloud]: https://firebase.google.com/
[human-learning-app]: https://console.firebase.google.com/u/0/project/human-learning-app

## Databases

Currently, the project contains two separate databases: *demo* and *scratch*. *demo* cannot be modified within the app, which means its contents aRe predictable. In *scratch*, anybody can edit anything.

## Cloud Storage

Firestore documents are used for most of the data.
They are not suitable for storing images so we use a Cloud Storage Bucket and keep references into it with ``gs://`` urls.

## Security rules

Current Firestore rules:
```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
  // {database} is always "(default)"
  // hence, we fake having distinct databases by creating subcollections
  match /databases/{dbName}/{documentPath=**} {
      allow read;
      allow write: if dbName == "scratch";
  }
  }
}
```

Current Storage rules:
```
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /{db}/{allPaths=**} {
      allow read;
      allow write: if db == "scratch";
    }
  }
}
```
