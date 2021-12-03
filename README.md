<h1 align="center">How Yo</h1>

<img src="readme_asset/howyo_topic.png" width="100%">

A comprehensive App for all travelers, which accompanies everyone from scheduling to the end of the trip.

<a href="https://play.google.com/store/apps/details?id=com.yuchen.howyo"><img src="https://github.com/ione0213/HowYo/blob/master/readme-asset/google_play_badge.png?raw=true" width="230" height="90"></a>

## Flows

### Test Account
- User:	HowYoGuest@gmail.com / Password: @@HowYo2021

### Planning Flow
The Planning flow is for the user who would like to start a new trip, edit upcoming plan or do the final check before vacation. It includes: 
* Start up the plan
    
    Users could find the plan which is suitable for the reference

    <img src="readme-asset/plan_browsing.gif" width="250"><br/>

* Interact with the author of plan
    
    When browsing the plans, users could send a like, leave a comment, collect current plan

    <img src="readme-asset/plan_Interact.gif" width="250"><br/>

* Create / Copy plan
    
    Users could copy all contents of the plan from others to a new plan. Besides, they could create a new plan and define the basic information of the trip themselves

    Edit / Reset cover image

    <img src="readme-asset/edit_reset_cover_image.gif" width="250"><br/>

    Define plan name and duration of traveling

  <img src="readme-asset/plan_name_duration.gif" width="250"><br/>

    Copy plan from other users

    <img src="readme-asset/copy_plan.gif" width="250"><br/>

* Details in the plan
    
    The author could edit the details for the new or existing plan

    Invite friends as the companions

    <img src="readme-asset/invite_companions.gif" width="250"><br/>
    
    Add / Edit schedule in one of days
  
    <img src="readme-asset/add_edit_schedule.gif" width="250"><br/>

    move / delete schedule

    <img src="readme-asset/move_delete_schedule.gif" width="250"><br/>
    
    Add / move / delete days in the plan

    <img src="readme-asset/add_move_delete_day.gif" width="250"><br/>

    Co-edit plan with companions

    <img src="readme-asset/coedit_plan.gif" width="450"><br/>
    
    Checking and shopping list

    <img src="readme-asset/shopping_check_list.gif" width="250"><br/>

### Traveling Flow
While users are on the journey, traveling flow provides them to check the schedules, get locations, track their spending, and contact companions with IM.

* Details in schedule
    
    Review the image data

    <img src="readme-asset/review_image.gif" width="250"><br/>

    Confirm the location of schedules

    <img src="readme-asset/schedule_location.gif" width="250"><br/>
    
* Get locations
    
    Check companions' current destination

    <img src="readme-asset/companion_location.gif" width="250"><br/>

* Spending
    
    Track users' spending in the trip, calculate the rate that every members should pay

    <img src="readme-asset/track_spending.gif" width="250"><br/>
    
* Instant Messaging
    
    The users in the same traveling plan could send message to each other with group message

    <img src="readme-asset/instant_messaging.gif" width="450"><br/>

## Techniques

* Implement **MVVM** pattern with **LiveData**, **ViewModel**, and **Data Binding**
* Use **CollapsingToolbarLayout** to increase the viewing space of data
* Implement **ItemTouchHelper** for moving and swiping items in **RecyclerView**
* Perform Firebase **Authentication** with Facebook and Google Sign-In to ensure account safety. Provide the one-click sign-in for a better user experience
* Accomplish immediate and co-editable data with **Firestore**
* Build instant messaging by Firestore **Snapshot Listener**
* Upload images to the Firebase **Storage** and store the URL to the associated documents
* Load, present, and cache images from URL by implementing **Glide**
* Get user location data and update it to the database via **Service** in the background continually
* With **Google Maps SDK**, establish the information of location and custom marker icons for displaying avatars from URL
* Initiate **Crashlytics**. Analyze the report of crashing and fix the root cause

## IDE

* Android Studio - Arctic Fox
* Android SDK - 26+
* Gradle - 7.0.1

## Release Notes

| Version | Data | Description |
| :-----: | ------ | ------ | 
| 1.2 |Nov/21/2021 | Replace displaying user id with user name in every pages |
| 1.2 | Nov/15/2021  |Launch in Goole Play Store |

## Contact
Yu Chen ione0213@gmail.com

## License
MIT