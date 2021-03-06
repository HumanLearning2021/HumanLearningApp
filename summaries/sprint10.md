# Sprint 10 retrospective

## Scrum master (Nicolas Vial)


## Bettens, Louis
During this sprint, I worked on refactoring the data storage backend of the application. I was able to apply most suggestions made by the code review, including simplifying the Class hierarchy and transitioning to the Repository pattern. I was also able to squash a couple bugs.

## Jaakik, Marouane
During this sprint, I have finally managed to merge the required backend changes for User/Admin privileges. I had to discuss a lot with Jonas who helped me to better  understand the Databases Injections and the offline mode to the app, then I figured out how to write more meaningful tests to get the required coverage. I spent 15 hours not including reviews this week going back and forth between on different implementation and test ideas for the backend changes. 

## Konrad, Jonas Franz
This week I added the ability to download the database. This way the app can be used without an internet connection. I also resolved a bug where setting a picture as the representative picture for a category would remove it from storage, hence leading to an inconsistent state in the database. Overall it went ok, some parallelism difficulties where encountered unexpectedly, but I managed to solve them in the end.

## Lachat, Niels Marco
This week I started by fixing the tests that prevented me from merging last week. This took up quite some time, but I think I made useful changes to make the tests more robust. 
After that, I started implementing the evaluation mode for the learning. This went quite well and was not too hard to integrate, but I think that the code could benefit from a little refactoring to abstract the model inside the presenter.
Overall I am quite happy with the final result of my task this week. I spent around 10 hours on the project.

## Lenweiter, Martin
This week I did one the 2 parts of the destabilization sprint (Niels did the other) which consists of an evaluation of a user following the comVoor method. I implemented the UI that displays the result of an evaluation. The main difficulty I had was to get familiar with `MPAndroidChart`, a graph library. I spent about 8 hours of work including reviews.

## Vial, Nicolas
This week, my goal was to fix several minor bugs as well as to improve the user interface so that some interfaces become much more intuitive. I didn't encounter any particular problem and I think the application has become more pleasant and intuitive to use. This week I worked a little bit more than 8 hours with the reviews but I think my time estimation for this week was better than the past weeks.
## Overall Team: Lessons Learned
During this sprint, we finally managed to satisfy all the project requirements as we had requested last week. We also managed to merge all the pull requests for the second time even though there were only 5 of us to review many PRs. Our organization during this sprint was excellent, thanks in part to great communication and responsiveness. We finalized a first version of the task imposed by the destabilization sprint which adds an evaluation that ends with learner statistics. Another part of our time was spent modifying our code following the comments of the code review.
The next sprint will surely be mainly dedicated to make changes to satisfy the code review and to improve some existing features.  
