# Week 4 sprint retrospective

## Bettens, Louis


## Jaakik, Marouane


## Konrad, Jonas Franz
This sprint I finished implementing all the necessary elements to allow the management of a database stored in Firestore. I had a bit of trouble with asynchrony which was improved with the very helpful pr review by Louis. This took way more time than I expected but since the issue linked to this was open for the second sprint in a row I wanted to finish it. It is not perfect but it is good enough to be used in my opinion. For next sprint I really need to be more careful with my worload estimate.

## Lachat, Niels Marco
This sprint I refactored and redesigned the DataOverviewFragment (now callled DatasetListFragment). Initially my task was to use the already existing fragment in an activity allowing to select the dataset on which to learn, but it turned out that the DatasetListFragment needed to be reworked to be able to indicate which dataset is selected to the host activity. I also decided to add the representative pictures of the categories in the list. This took more time than expected because I also needed to integrate the fragment with the new DatabaseManagement API. But in the end I managed to adapt the fragment to suit my needs, which means that it will be easy to make the activity which I was supposed to make this sprint. (I still need to test the displaying of representative pictures in the fragment, because I didn't have time to test it this sprint).


## Lenweiter, Martin
This sprint I added the option to use the learning UI in representation mode (having to classify pictures against representative pictures of the category to which the image to classify belongs). After adding this feature, many tests broke because adding a representative picture to the categories introduced a circular relationship between `Categories` and `CategorizedPictures`. I wasted a lot of time until I discovered that this was the problem, and then we fixed it by moving the bookkeeping of representative pictures to `DatabaseService`. Further, I struggled with the UI Testing, as I couldn't  find a way to test whether the picture held by the views in the learning activity was part of one set and not another.

## Vial, Nicolas
This week I made the link between all the UIs that are part of the management of a dataset. I also made some modifications to use the model to communicate with the database.Making the link and all the modifications took me a lot of time because there were a lot of small details about what we wanted to be possible when using the application and details about the model that allows the UIs to communicate with the database. I didn't encounter any big problems during the implementation but I had some issues with some tests that I was able to fix after discussing with some team members.  I clearly worked a lot more than usual (about 20 hours) but since we decided to do a 1.5 week sprint, it didn't cause any big problems.
## Overall Team: Lessons Learned (Scrum master : Vial, Nicolas)
