# Sprint 9 retrospective

## Scrum master (Niels Lachat)


## Bettens, Louis


## Jaakik, Marouane

## Konrad, Jonas Franz
This week I integrate the image caching mechanism into the production app. This took much longer than expected because of changes made to the app since the caching methods were implemented. There also was an issue with IO operations being executed on the main thread that I did not discover earlier but it is now merged. I also started doing a bit of refactoring in response to the feedback we received (pr still a draft). My other task was to implement the ability to download a database but the implementation I made is quite honestly very bad which is why I left it as a draft pr.
In summary, I expected the caching integration to be non trivial despite all the code being ready in theory but not to encounter this many issues. My start to the week was not very good and my code reflects that which is why I did not finish my second task and rather to have something which is not very good I decided to take more time for my code reviews and improve the testing for the caching instead.

## Lachat, Niels Marco


## Lenweiter, Martin
This week, I integrated the search functionality into our app. This allows to perform a basic search through datasets, which can be done either for learning or dataset editing. It went pretty smoothly, and I spent only 5 hours of work instead of the 8 I had foreseen.

## Vial, Nicolas
This week I made the necessary changes to the interfaces to allow a separation between user and administrator.  There were fewer changes to make than I thought, so I worked a little less than usual this week. My task was dependent on Marouane's who was in charge of the backend, so we communicated a lot to agree on the model used. I thought I would have time to start writing the documentation for all dataset editing interfaces but I didn't find the time. I didn't have any problems except a problem of understanding about injections that Louis helped me to clarify.
## Overall Team: Lessons Learned
