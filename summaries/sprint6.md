# Week 6 sprint retrospective

## Scrum master (JAAKIK)

## Bettens, Louis
 this week  i worked on implementing support for adding already existing photos from the user's device to the database. I was able to accomplish the task, but could not merge in time because of the Firebase situation.
## Jaakik, Marouane
This sprint I have implemented a basic Search activity where the user can search by the dataset name, further improvements could be brought to the Database management to make it easier to perform more complicated search such as by category or tags, that is why I decided not to merge it this week and work on this aspect of the search activity during next week after discussing with the Team. 

## Konrad, Jonas Franz
This week I implemented caching and the ability to download databases. I had some trouble integrating dependency injection to write cleaner code but otherwise it went ok. We had trouble with our tests because we hit the quota limit on Firestore which means that they could no longer run. I explored the possibility to use the Firebase Emulator to test instead of the "real" Firebase such as to not have this problem again. Some testing yielded positive results which means that we will probably integrate this after this sprint.

## Lachat, Niels Marco
This week I started by merging a PR that cleaned the project hierarchy and renamed a few files to have a more coherent project hierarchy.
Then, I refactored and documented the Learning side of the app. It took much longer than expected because I had to understand how to use Hilt. Luckily Louis helped me and I then managed to inject a different database for testing. There was also an unexpected bug I had to fix because the learning activity uses the contentDescription of a view to check that classification is correct, and another part of the code changed the contentDescription, which made for a pretty difficult to find bug. I will make this more robust (and not depend on contentDescription) in next sprint. Also, the tests are painfully slow right now, so I am very motivated to try and optimize them in next sprint. I think it will benefit everyone to have faster tests.

## Lenweiter, Martin
This week, I performed the refactoring of all the activities into fragments, which is the first step to being able to use the navigation component. I had (still having) some issues with merging my teammates' work into my PR, especially the dependency injection part. Other than that, the week went pretty smoothly, but I expect next week will be tricky when implementing the navigation component.

## Vial, Nicolas
This week I first updated DatasetsOverview and then linked it to other activities. I also made some changes to tests that were not complete and fixed some others. Then I started working on my 2nd issue which is about having nice interfaces on phone as well as on tablet. I clearly didn't have time to finish this part because it takes me much more time than I thought. So I will finish this issue next week by adding the interface changes when the phone or tablet is in landscape mode.
## Overall Team: Lessons Learned
