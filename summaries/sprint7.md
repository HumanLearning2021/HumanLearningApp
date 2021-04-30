# Week 6 sprint retrospective

## Scrum master (LENWEITER)

## Bettens, Louis
This week I finished my work on uploading existing pictures to the app. I had to request help on a tricky UI problem from Nicolas, and he helped me.

## Jaakik, Marouane

## Konrad, Jonas Franz
This week I integrated the firebase emulator into our project. This way we no longer use the production firebase. Making it work locally was pretty straight forward but installing it on the CI took a lot of trial an error because the documentation for Cirrus is very poor in my opinion.  Also I had a problem where the emulator would start on the CI but if I wanted to start it with a given state for the database it would crash despite working locally. This was due to a bug in the emulator; if a state is exported on Windows it can't be imported in Unix, which took a long time to figure out. In the end it all works well but took way longer than expected because I had to figure out how to configure the CI, how to best install the emulator on it and how to build Docker images.
After all this was done I updated last sprints task, but after the test refactor which was done this week and the emulator integration there is a bug with some tests which work if run individually but no via connectedCheck and I did not find the time to resolve this this week, which means that I need to postpone the merge yet again.
In conclusion I did not expect to have to learn how to use Docker images and the CI configuration was more confusing than expected but managed to get this weeks task done in the end.

## Lachat, Niels Marco
This week I worked on optimizing the UI tests. The main work was to make the tests use the dummy database management and to simplify tests that were needlessly complex because they used the firestore database. I also drastically reduced all wait times in the tests, because it turns out that the CI doesn't really need them (and I suspect that maybe the waits caused the problems that we thought it fixed). With these changes the tests take about 3 minutes locally and 13 minutes on the CI to run (down from 16 minutes and 1 hour respectively). This allowed us to have a *much* smoother merging thursday. It took a bit more time than expected so I didn't have time to work on other tasks, but I am very satisfied with the work I did, and I know it will help us in the future. 

## Lenweiter, Martin
This sprint I implemented the Navigation component, using the fragments I made last week. Most of my time was spent on 1) merging my teammate's PR's and converting them to fragments; 2) implementing the actual navigation component; 3) adapting the tests. I spent a lot of time this sprint, 4 days of work so north of 20 hours, but in the end I did not manage to merge, as there were still some glitches with the navigation and a couple tests were failing. I don't think this could have been done differently though, as the navigation component needed to be implemented atomically since it touched all of the UI. It was enjoyable to work on it and I got to read all of the UI code which was nice. Lastly, I got some nice help from my teammates when I needed it, which I appreciate.

## Vial, Nicolas
This week reworked all the interfaces so that we can use the application on phone and tablet in portrait or landscape mode while keeping a nice interface. I didn't encounter any big problems for this task. I then used a lot of time on Thursday to help Martin with his PR which is complicated to merge but we are finally very close to get it right. 
