# Week 1 sprint retrospective

## Scrum master (Lenweiter, Martin)

## Bettens, Louis
This week I worked on breaking ground for the cloud back-end subsystem of the application. I learned about Firebase and organized a project there. I could not get anything in the code for the lack of the model interface.

The knowledge and (small) codebase I obtained will be useful in later phases.

I spent much more time than alloted on this, and I hope to be able to focus more on a concrete increment rather than learning in the future.

This week I worked on breaking ground for the cloud back-end subsystem of the application. I learned about Firebase and organized a project there. I could not get anything in the code for the lack of the model interface.
The knowledge and (small) codebase I obtained will be useful in later phases.
I spent much more time than alloted on this, and I hope to be able to focus more on a concrete increment rather than learning in the future.

## Jaakik, Marouane

This week I have been implementing the user interface for creating datasets and associating the necessary categories names. Initially I thought of making a simple interface to chose whether to download or create a dataset consisting only of one single category object, However during the midweek standup there has been a change of plans so then I switched to implementing an interface to dynamically add the categories necessary when taking the picture

## Konrad, Jonas Franz

This week I started implementing the activity used to add a picture to a dataset. The activity manages the camera permission and informs the user that it can't use this functionality if it does not give permission to the app to use the camera. The activity also has three buttons ; take the picture, select the category and save. Taking the picture and saving are not yet functional but selecting the category is. I had trouble getting the tests to work but I feel confident that this will go better next week. I also underestimated the time it would take to implement this functionality which means that it will have to be carried over into the next sprint. For next time I mainly need to improve on estimating how long a task will take, this way I will be able to post a pull request earlier which in turn will make the entire process smoother.

## Lachat, Niels Marco

This week I desiged the learning UI. At first I had to learn about how to use Constraint Layouts so that the UI adapts well to different screen sizes. I also learned how to do a drag and drop mechanism in android. I think the UI design and the drag feature went quite well. What went wrong is that I underestimated the time to learn about android libraries so I did not have enough time to write the tests and thus be able to merge into main.

## Lenweiter, Martin

This week I created a first version of the interface to data sets. This is part of the model of the MVP pattern. The first implementation of the interface allows to retrieve an image from a dummy data set based on the category of the desired image. As for the challenges I faced, I underestimated the time it would take, and am still learning to work with development tools.

## Vial, Nicolas

During this sprint, I designed the UI that allows to display the images of a dataset and to display them in large size.
I first did some research about Gridview and some layout features.
As we had not yet created a precise model for a dataset and an image, I could not make a usable UI in the hand because I relied on "dummy classes" created to start implementing the UI. I think that the UI is a good base to make a good UI for viewing a dataset.
I clearly underestimated the total time to complete this task, especially because of the testing. I had to do a lot of reading and still need to do so to fully understand all the intricacies of testing an UI.
So I couldn't merge into main but now that the model is well defined, it will be possible to modify the UI to satisfy this model.

## Overall Team: Lessons Learned
* From now on, submit all pull requests by wednesday evening, to be able to do comprehensive code reviews on Thursday, merge the code and lower stress.
* Keep meetings short by doing only the stand-up all together. Specific issues need to be discussed only among concerned team members.
