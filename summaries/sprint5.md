# Week 5 sprint retrospective

## Scrum master (Louis)

## Bettens, Louis
During this week, I introduced dependency injection across the codebase using Hilt. This required a shallow but wide scope of work, and thus more care in order not to interfer with other patches. Overall it went well.

## Jaakik, Marouane
This Sprint, I have mainly worked on redesigning the Main activity for the Demo to allow for Learner and Admin Mode. I have also started researching and thinking about implementing a basic search activity which will be my main focus next week. 

## Konrad, Jonas Franz
During this sprint I prepared the SQL database which will be needed to allow downloading datasets. This was done using Room. I had a bit of trouble implementing relations due to the poor documentation but managed to do it. The next step will be to implement the corresponding databaseService. Images will be stored on the apps external storage and indexed by said database.
I also added some methods to the existing databaseService because they where needed by Nicolas this week. They are in his pull request.

## Lachat, Niels Marco
During this sprint I integrated the DatasetListFragment into the DatasetSelectionActivity, so that the user can choose a dataset on which to learn from the available datasets. I also adapted LearningSettingsActivity and LearningActivity to take into account the selected dataset. During the sprint, I traded a task with Martin because otherwise we would have stepped on each other's toes (he was supposed to adapt the learning to be able to use firestore, which I did as part of the adaptations for the dataset selection). Overall it went well I think, with the task taking about 8 hours to do. I'm very excited to see that we are approaching a version of the app where all our work is integrated together!

## Lenweiter, Martin
This sprint was a bit of an odd one for me. I started out with the task of integrating the learning UI with the firestore database, but Niels and I discovered that his task and mine overlapped, and so we decided I move on to another task: adding a navigation drawer to our app. After researching how to best do this, I stumbled upon Android Jetpack's Navigation Component, which seems like the cleanest and most robust way to implement this, along with the rest of the navigation, with which we're having some issues. This, however, necessitates to migrate all our activities except main to fragments. The team decided to go that route, as we still have about 6 sprints to add features to our app, and a robust navigation system is important so that all these features integrate smoothly together. I therefore did not submit any pull request this week, but instead started looking at migrating our activities to fragments, which will be a full task for the next sprint.

## Vial, Nicolas
During this sprint, I modified all the Dataset Management UIs to use the firestore database. Modifying the activities took some work but I didn't encounter any problems. The problems came when rewriting the tests, I had to familiarize myself with firestore and manage to make the tests work regardless of the state of the database used which took me a lot of time but I finally succeeded. I communicated a lot with Jonas during this sprint and we worked hand in hand to implement all the necessary functions for dataset management. I didn't think it would take me so long, so I couldn't start my 2nd issue which was about modifying the UIs to get a good display no matter the screen size. For the rest, everything went well on my side for this sprint.
## Overall Team: Lessons Learned
