# Week 5 sprint retrospective

## Scrum master (Louis)

## Bettens, Louis

## Jaakik, Marouane

## Konrad, Jonas Franz
During this sprint I prepared the SQL database which will be needed to allow downloading datasets. This was done using Room. I had a bit of trouble implementing relations due to the poor documentation but managed to do it. The next step will be to implement the corresponding databaseService. Images will be stored on the apps external storage and indexed by said database.
I also added some methods to the existing databaseService because they where needed by Nicolas this week. They are in his pull request.

## Lachat, Niels Marco
During this sprint I integrated the DatasetListFragment into the DatasetSelectionActivity, so that the user can choose a dataset on which to learn from the available datasets. I also adapted LearningSettingsActivity and LearningActivity to take into account the selected dataset. During the sprint, I traded a task with Martin because otherwise we would have stepped on each other's toes (he was supposed to adapt the learning to be able to use firestore, which I did as part of the adaptations for the dataset selection). Overall it went well I think, with the task taking about 8 hours to do. I'm very excited to see that we are approaching a version of the app where all our work is integrated together!

## Lenweiter, Martin

## Vial, Nicolas
During this sprint, I modified all the Dataset Management UIs to use the firestore database. Modifying the activities took some work but I didn't encounter any problems. The problems came when rewriting the tests, I had to familiarize myself with firestore and manage to make the tests work regardless of the state of the database used which took me a lot of time but I finally succeeded. I communicated a lot with Jonas during this sprint and we worked hand in hand to implement all the necessary functions for dataset management. I didn't think it would take me so long, so I couldn't start my 2nd issue which was about modifying the UIs to get a good display no matter the screen size. For the rest, everything went well on my side for this sprint.
## Overall Team: Lessons Learned
