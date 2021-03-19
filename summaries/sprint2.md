# Week 2 sprint retrospective

## Scrum master (Konrad, Jonas)
This week we vastly improved on time management in meetings. They were much shorter and a lot more efficient.
We had two short standups (Monday and Thursday) during which everyone updated the group on their current status and their dependencies on work from other people. After the meeting these dependencies where resolved by the concerned parties.
Average meeting duration was 15 minutes.
Pull request were there faster but not entirely ready for merging on Wednesday because of troubles writing the tests.

## Bettens, Louis
This week I provided a well-modularised implementation of the cloud database back-end of the app. This required discussing the interface of the module with the rest of the team, in particular with Martin who provided it. I also had to fight the CI to get acceptable test coverage on my work due to odd issues with the Glide library, which took a lot of time at the end of the process and almost caused a failure to deliver on time.

I noted that, since Martin and I miscommunicated, it might be preferable in the future to structure such discussions around trying to write down a decision in an electronic document, so as to make sure everything is clear and can be referred back to.

## Jaakik, Marouane
During This week, I have been able to implement a dynamic interface to enter the required label names for the creation of the Dataset, where the user can add and remove and edit the labels before submitting those. The main challenges I faced this week were in regard of testing my interface and actually making the tests pass the CI, I reached a 90%coverage. In general, this week has been positive as I feel more comfortable with the many developpement tools that we have to use except Cirrus which proved to be a little bit to difficult to reason about and get consistent results from. 

## Konrad, Jonas Franz
This week I implemented the AddPictureActivity completely. I had to resolve how this activity would communicate with the rest of the app and how it will return the result (the picture which was taken and the assigned category). This was done using an ActivityResultContract.
I could not find out how to deny permissions during testing which means that I can't test the activity properly. These problems during testing led my pull request to only exit the draft phase on thursday evening, which is suboptimal.

## Lachat, Niels Marco
This week I added unit tests for the learning UI that I implemented in the previous week. I had to use UI Automator because espresso doesn't have an easy way to perform a drag and drop operation. This meant that I had to learn how to use UI Automator. I didn't have the time to integrate the learning UI with the dummy dataset, but it will probably be quite simple to integrate. This week's workload was a bit lighter for me because I was unavailable for two days (I had to go to the recruitment), but I will try to compensate this in next week's sprint.

## Lenweiter, Martin
This sprint, I first changed the interface of the model, making it more minimal and compatible with the requirements of the actual Firestore implementation. I also adapted the dummy dataset implementation to reflect the new interface. This allowed to merge the PR that wasn't done last week. I then added functions to the interface to allow to put pictures into the dataset, as well as retrieve and add categories. Finally, I wrote the corresponding dummy implementations. Lessons learned: (1) should have communicated better with Louis to get on the same page regarding the design of the dataset interface earlier, which would have avoided having to rewrite most of the code. (2) Estimating time needed to do the work definitely still an issue.

## Vial, Nicolas
During this sprint I finished the UI to display a dataset and the UI that displays the picture on which we click and I made it work with the actual model. I also learned how to do certain type of tests to be able to do add some more tests next week. i think that those UI are working great and we are now able to use them. What went wrong was when I had to do the tests pass with CI which causes a lot of weird errors but I finally managed to pass the tests. It is still really hard for me to estimate how much time it takes to finish a certain task (Probably due to the fact that I need a lot of time to write good tests).

## Overall Team: Lessons Learned

Time management during meetings was much better this week as well as team coordination. Testing seems to be the part taking up by far the most time but hopefully this will improve in later sprints.
