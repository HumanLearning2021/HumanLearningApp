# Week 1 sprint retrospective

## Scrum master (Konrad, Jonas)
This week we vastly improved on time management in meetings. They were much shorter and a lot more efficient.
We had two short standups (Monday and Thursday) during which everyone updated the group on their current status and their dependencies on work from other people. After the meeting these dependencies where resolved by the concerned parties.
Average meeting duration was 15 minutes.
Pull request were there faster but not entirely ready for merging on Wednesday because of troubles writing the tests.

## Bettens, Louis

## Jaakik, Marouane

## Konrad, Jonas Franz
This week I implemented the AddPictureActivity completely. I had to resolve how this activity would communicate with the rest of the app and how it will return the result (the picture which was taken and the assigned category). This was done using an ActivityResultContract.
I could not find out how to deny permissions during testing. This lead to the 80% test coverage being unachievable and the merge to be impossible due to the CI. I also don't know how to test the aforementioned contract correctly.

## Lachat, Niels Marco

## Lenweiter, Martin
This sprint, I first changed the interface of the model, making it more minimal and compatible with the requirements of the actual Firestore implementation. I also adapted the dummy dataset implementation to reflect the new interface. This allowed to merge the PR that wasn't done last week. I then added functions to the interface to allow to put pictures into the dataset, as well as retrieve and add categories. Finally, I wrote the corresponding dummy implementations. Lessons learned: (1) should have communicated better with Louis to get on the same page regarding the design of the dataset interface earlier, which would have avoided having to rewrite most of the code. (2) Estimating time needed to do the work definitely still an issue.

## Vial, Nicolas
During this sprint I finished the UI to display a dataset and the UI that displays the picture on which we click and I made it work with the actual model. I also learned how to do certain type of tests to be able to do add some more tests next week. i think that those UI are working great and we are now able to use them. What went wrong was when I had to do the tests pass with CI which causes a lot of weird errors but I finally managed to pass the tests. It is still really hard for me to estimate how much time it takes to finish a certain task (Probably due to the fact that I need a lot of time to write good tests).
## Overall Team: Lessons Learned
