# Sprint 9 retrospective

## Scrum master (Niels Lachat)


## Bettens, Louis
This Sprint, I was tasked with implementing the back-end part of learner statistics. We can now record how well learners perform, but there is no UI to this yet. This took the expected amount of time and went smoothly.

## Jaakik, Marouane
This Sprint, I have implemented the Back-end changes to account for User vs Learner Modes. Implementation went smoothly, I have just struggled to get the coverage to the required threshold to merge. I hope to do This by the end of the Weekend.  I have worked together with Nicolas who who implemented the UI aspect of it, which was done and ready to merge but since it is dependent on mine, this couldn't be done. I also got Help from Louis and Jonas to better understand the backend code. All in All good week.

## Konrad, Jonas Franz
This week I integrate the image caching mechanism into the production app. This took much longer than expected because of changes made to the app since the caching methods were implemented. There also was an issue with IO operations being executed on the main thread that I did not discover earlier but it is now merged. I also started doing a bit of refactoring in response to the feedback we received (pr still a draft). My other task was to implement the ability to download a database but the implementation I made is quite honestly very bad which is why I left it as a draft pr.
In summary, I expected the caching integration to be non trivial despite all the code being ready in theory but not to encounter this many issues. My start to the week was not very good and my code reflects that which is why I did not finish my second task and rather to have something which is not very good I decided to take more time for my code reviews and improve the testing for the caching instead.

## Lachat, Niels Marco
This week I worked on making the learning fragment work with datasets with an arbitrary number of categories. I started by refactoring the learning to avoid relying on the contentDescription to verify that a sorting is correct. 

I could implement these changes and write tests for them, but I couldn't merge my PR, because some tests broke because I added datasets to the DummyDatabaseService. I didn't have the time fix the broken tests, but that will be my first task for the next sprint. 

Overall, I spent around 11 hours on the project which is more than I should have, but I feel that the work I did was meaningful.


## Lenweiter, Martin
This week, I integrated the search functionality into our app. This allows to perform a basic search through datasets, which can be done either for learning or dataset editing. It went pretty smoothly, and I spent only 5 hours of work instead of the 8 I had foreseen.

## Vial, Nicolas
This week I made the necessary changes to the interfaces to allow a separation between user and administrator.  There were fewer changes to make than I thought, so I worked a little less than usual this week. My task was dependent on Marouane's who was in charge of the backend, so we communicated a lot to agree on the model used. I thought I would have time to start writing the documentation for all dataset editing interfaces but I didn't find the time. I didn't have any problems except a problem of understanding about injections that Louis helped me to clarify.

## Overall Team: Lessons Learned
This sprint, we accomplished a lot of useful work, although some of it could not be merged due to late discovered issues (low coverage, broken tests, ...).
This is something we should improve by trying to test earlier, but it is certainly not an easy thing to do.

Next sprint we should try to merge all the remaining PRs so that we satisfy the requirements, as well as address some of the issues noted by the code review. We will also need to implement the destabilization task, but it shouldn't be too difficult, given that we already did some preparatory work for it (allow learning on arbitrarily many categories, and add a mechanism to store statistics).

