# Week 6 sprint retrospective

## Scrum master (LENWEITER)

## Bettens, Louis
This week I finished my work on uploading existing pictures to the app. I had to request help on a tricky UI problem from Nicolas, and he helped me.

## Jaakik, Marouane

## Konrad, Jonas Franz

## Lachat, Niels Marco
This week I worked on optimizing the UI tests. The main work was to make the tests use the dummy database management and to simplify tests that were needlessly complex because they used the firestore database. I also drastically reduced all wait times in the tests, because it turns out that the CI doesn't really need them (and I suspect that maybe the waits caused the problems that we thought it fixed). With these changes the tests take about 3 minutes locally and 13 minutes on the CI to run (down from 16 minutes and 1 hour respectively). This allowed us to have a *much* smoother merging thursday. It took a bit more time than expected so I didn't have time to work on other tasks, but I am very satisfied with the work I did, and I know it will help us in the future. 

## Lenweiter, Martin
This sprint I implemented the Navigation component, using the fragments I made last week. Most of my time was spent on 1) merging my teammate's PR's and converting them to fragments; 2) implementing the actual navigation component; 3) adapting the tests. I spent a lot of time this sprint, 4 days of work so north of 20 hours, but in the end I did not manage to merge, as there were still some glitches with the navigation and a couple tests were failing.

## Vial, Nicolas
