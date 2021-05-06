# Week 6 sprint retrospective

## Scrum master (Jonas Konrad)

## Bettens, Louis
This week I was responsible for refactoring the `DatabaseManagement` layer of the application to avoid code duplication. I was able to rely on our test suite to check for regressions. I was also assigned the task of merging or clarifying the purpose of the "demo" and "demo2" databases but did not have the time to get to this task.

## Jaakik, Marouane
This, I finaly managed to merge the Search activity and make it work with the firestore Database. I have also looked into my other task With Martin and Louis, about having different user accounts for learner and administrator and discussed some ideas. Because This task needed Martin's PR as a starting point we decided to leave it for next sprint. 
## Konrad, Jonas Franz
This week I fixed the testing issues on the pr #101 which was that some tests wanted Demo2 to be injected as the dummy database and some as the scratch database. I managed to get the two injections to coexist. This pr was finally merged this week. I also added some methods to the databaseServices and managements as requested by Nicolas. Some housekeeping was done as well (e.g add database rules to the emulator).

## Lachat, Niels Marco
This week I worked on making the learning UI clearer to the learner. I had very little time because I had a lot of work for other courses, so I didn't manage to do the whole task (and I didn't start the other task I was assigned). I addded an issue so I don't forget to finish the work I started in a future sprint. What I managed to do was add a colored border to images in the learning UI, to make it clear what the user should interact with. What still needs to be done is to change the color of the border according to what the learning needs to interact with dynamically. I spent around 6 hours on the project this week (counting reviews). 

## Lenweiter, Martin
This week, I first merged the Navigation component PR. I'm happy about this because it makes the app more responsive, and also its UI more modular and so easier to modify. In the second part of the week, I implemented some changes to the UI. Namely, we now have a bottom and side navigation drawer. The bottom one allows to choose between the learning and the datasets editing part of the app, while the side one allows for the login only so far. The past couple of weeks were rough because I had to get up to speed both with the UI part of the code, as well as the Jetpack Navigation library, but this week was definitely easier. I spent around 12 hours this week (8 on the Navigation component PR and 3 on the navigation drawer one and 1 on reviews).

## Vial, Nicolas
This week, I worked on improving the dataset management interface to make it more logical and simple to use. I also added the possibility to put a new representative picture with the help of Jonas. Everything went very well for these tasks. There are still one or two graphical details that can be improved but I didn't have time to refine them. I also added a default picture in cases where no representative picture was selected before.
## Overall Team: Lessons Learned
