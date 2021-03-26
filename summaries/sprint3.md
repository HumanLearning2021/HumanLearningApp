# Week 3 sprint retrospective

## Bettens, Louis

During this week I worked on two tasks. The first was to implement the ability for app users to modify data in the FireStore database, which required code changes, setting up unit tests, and adding and documenting configuration to the back-end. The second one was to allow users to authenticate using Google accounts, which required adding an SSL app signing certificate to the project and ensuring the team could safely access it.

I was able to finish the first task early, which allowed others to potentially build up on it, and allowed me to focus on the second one more. I discussed with Jonas and Martin the design of the database service interface and the database schema. Coordinating the sharing of secrets with the CI and the rest of the team required special attention. In total I spent 9-10 hours working this week.


## Jaakik, Marouane

During this 3rd sprint, I have implemented the DataOverviewActivity that displays the available datasets. After discussing with the team about the reusability of Displaying the datasets in other contexts of our App, I opted to implement it as a Fragment consisting of a recyclerview . For now this uses some dummy data and will be fully integrated with the rest of the app in the coming sprints. This time around I have managed my time better and it me an overall 9 hours for implementation and testing. 



## Konrad, Jonas Franz

This week I updated the AddPictureActivity to use the Category instead of Strings. I also updated the database model and it's dummy version to include all that is necessary to be able to maintain the database. This required a redesign of multiple classes and their interactions. The changes to the interfaces were merged but the changes to the dummy implementation were not. The reason being that they use the Uri class which can't be used in tests and I could not find a way around it. I did not expect to have to redesign some of the already existing classes to be able to implement the new functionality but the biggest time sink this week was by far trying to get the tests to work with the Uri class. Time spent was 1 hour redesign AddPictureActivity updating, 1 hour redesign , 5 hours implementation and 6 hours trying to get the tests to work and battling the CI.

## Lachat, Niels Marco
This week I first integrated the learning UI with the DummyDataset so that it will be easy to switch to learning with real datasets from the database once that is ready. 
I also implemented an audio feedback in the learning UI so that it is clear when the classification is correct or not. The testing of the audio feedback was hard because I couldn't find a testing API that allows to verify that an audio output is produced on the device. So I resorted to testing directly the media player's states, but it's not ideal.
Concerning time management, I think I improved my estimates of how long a task will take, which allowed me to spend a more moderate amount of time on the project, around 10 hours.


## Lenweiter, Martin
This week I first wrote additional functions on the model interface as well as its dummy implementation. Them, with Jonas and Louis, we redesigned part of this interface, which makes it now clearer to use. The second part of my week was spent on writing the first part of the learning settings, which now consist in choosing the learning mode (presentation vs representation). I worked a bit less this week than the previous 2, around 9 hours.



## Vial, Nicolas
This week I updated DisplayDatasetActivity by adding a menu to change the categories and by adding an editable text to change the name of the dataset displayed. Clicking on an image that represents a category will display all the images in that category in the dataset. Clicking on one of these images displays it in large size and a button allows you to remove this image from the dataset. I didn't encounter any big problems during the implementation but we are still using the dummyDataset, so we will have to make some changes to make it work with the final model. It still takes me a long time to implement tests but it's getting better. The communication with the group members allowed us to move forward quickly and without problems. CI and Codeclimate are still taking us a lot of time.


## Overall Team: Lessons Learned (Scrum master : Lachat, Niels)
Overall the team greatly improved in time management this week. We are starting to make more realistic estimates for how long a task takes to complete. Also, we decreased the end of sprint stress by defining a clear timeline for the week. For example, we defined that no PR submitted after wednesday 23:59 would be considered for merging during thursday, so that we put our entire focus on reviewing and merging on thursday. This worked quite well (even though it took a bit more time than expected to merge everything). 

We are also getting more familiar with the CI environment, which allows to spend less time trying to make it work (although there are still issues). 
So overall, we are on the right track!
