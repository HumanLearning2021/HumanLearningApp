# Week 6 sprint retrospective

## Scrum master (JAAKIK)

## Bettens, Louis

## Jaakik, Marouane

## Konrad, Jonas Franz

## Lachat, Niels Marco
This week I started by merging a PR that cleaned the project hierarchy and renamed a few files to have a more coherent project hierarchy.
Then, I refactored and documented the Learning side of the app. It took much longer than expected because I had to understand how to use Hilt. Luckily Louis helped me and I then managed to inject a different database for testing. There was also an unexpected bug I had to fix because the learning activity uses the contentDescription of a view to check that classification is correct, and another part of the code changed the contentDescription, which made for a pretty difficult to find bug. I will make this more robust (and not depend on contentDescription) in next sprint. Also, the tests are painfully slow right now, so I am very motivated to try and optimize them in next sprint. I think it will benefit everyone to have faster tests.

## Lenweiter, Martin
This week, I performed the refactoring of all the activities into fragments, which is the first step to being able to use the navigation component. I had (still having) some issues with merging my teammates' work into my PR, especially the dependency injection part. Other than that, the week went pretty smoothly, but I expect next week will be tricky when implementing the navigation component.

## Vial, Nicolas

## Overall Team: Lessons Learned
