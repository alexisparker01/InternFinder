Original App Design Project
===

## App Name: InternFinder (working title)

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
InternFinder is an app where you can create a profile and meet and connect with fellow interns of your industry in your area. 

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category: Photo & Video / Social **
- **Mobile:** Users can post and scroll through a timeline, uses camera, location services(?), mobile first experience.
- **Story:**  Allows users to meet and connect with interns in their area. They can post events for interns to meet them at, and they can post text posts which can kind of serve as a groupchat.
- **Market:** Since this app is solely for interns, most of the users will be 18-22 year old college students. If I were to expand the app, I would include full time employees as well. Since we're in the pandemic and a lot of internships are still remote, this app is useful for connecting with interns in your area so you can have somewhat of a "in person" experience. It differs from LinkedIn, because there is more a push for meeting in person and going to events with one another as opposed to solely online connections.
- **Habit:** Users can post whenever they want to connect with people in their area as well as post about events to meet up with one another. This app would be for daily use, especially in the months leading to and during an intern's internship.
- **Scope:** I can complete this app in the given timeframe for FBU Engineers which is 4 weeks. 

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* [x]	Users can register for an account which requires email confirmation
* [x] Users who already have an account can login
* [x] Users can logout
* [x] Users can set up a profile
* [x] Users can post events using Google Maps and Places SDK
* [x] Users can post status updates
* [x] Users can post text posts
* [x] Users can post picture posts using the Camera
* [x] Each post has a comment section so the users can communicate with each other
* [x] Every profile has a followers and following list and Users can follow and follow each other
* [x] Users can search for other users
* [x] Gesture feature
* [x] Animation feature
* [x] External library to add visual polish
* [x] Navigation between views


**Optional Nice-to-have Stories**

* You can link your LinkedIn profile to your profile
* [x] Users can modify their profiles and edit teir responses to questions about work (for example: After work what is your favorite thing to do? On the weekends what do you like to do? What's your work from home set up like?) When you click on someone else's profile it'll show how alike you guys are based on how many questions you both answered
* [x] Users can filter feed to show posts from people they are following, people within a certain number of miles of them, and people of their industry
* [] You can see a list of people attending the event
* [x] There is a map on the search/explore page that shows events near you on a map. When you click on the marker for each event it takes you to the post of the event. There is also a list of Users in your area.
* [x] Autofill suggestions when searching for a location for your event post
* [x] For Profile Picture, there can be an option to upload picture from camera roll

### 2. Screen Archetypes

* Login/Register Page
   * Register for account or login
* Timeline
   * Users can see posts
   * Users can create posts by clicking on Icon in corner
    * Each post has a comment section which you can futher expand and which will take you to the CommentSection page
* Profile
    * Users can set up a profile after their email in confirmed. Profiles would include: Name (EditText), Bio (EditText), Industry (Pick from a list), and Profile Picture (ImageView)
* Create Post Page
    * Users can create a post for an event, status update, or intro post.

* Comment Section Page
    * Users can reply to other users in comment section underneath post

* Search for Interns
    * Users can search the accounts of other users

* Map of Events Near you
    * User can see a map of event posts near them

### 3. Navigation

**Tab Navigation** (Tab to Screen)

*All icons in upper bar of screen*
* Search (upper bar)
* Create a post (upper bar)
* Profile (upper bar)
* Timeline (upper bar)

## Wireframes
https://www.figma.com/proto/0yivHGyHXgUChY8IypclPc/InternFinder?node-id=1%3A2&scaling=scale-down&page-id=0%3A1

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 
Models

Post
| Property | Type | Description |
| -------- | -------- | -------- |
| objectID     | String     | unique id for the user post (default field)    |
| author     | Pointer to User     | image author   |
| image     | File     | image that user posts    |
| caption     | String     | image caption by author    |
| commentsCount     | Number     | number of comments that have been posted to image   |
| likesCount	| Number |	number of likes for the post
| createdAt	| DateTime	| date when post is created (default field)
| location | GeoPoint | Coordinate of location of event

User
| Property | Type | Description |
| -------- | -------- | -------- |
| objectID     | String     | unique id for the user (default field)    |
| author     | Pointer to User     | image author   |
| username     | string     | username   |
| password     | String     | password    |
| profilePic     | File     | profile picture for profile    |
| Bio     | String     | biography for profile    |
| currentLocation     | GeoPoint     | Coordinate of user's current location    |

Follow
| Property | Type | Description |
| -------- | -------- | -------- |
| objectID     | String     | unique id for the follow (default field)    |
| from     | Pointer to User     | pointer to the user that is following   |
| to     | Pointer to User     | pointer to the user that is being followed   |

Answers
| Property | Type | Description |
| -------- | -------- | -------- |
| objectID     | String     | unique id for the answers (default field)    |
| user     | Pointer to User     | pointer to the user of the answer  |
| question1     | String    | string answer to question1   |
| question2     | String    | string answer to question1   |
| question3    | String    | string answer to question1   |
| question4    | String    | string answer to question1   |

Comment
| Property | Type | Description |
| -------- | -------- | -------- |
| objectID     | String     | unique id for the comment (default field)    |
| user     | Pointer to User     | pointer to the user of the comment  |
| text     | String    | description of comment   |
| post     | Pointer to Post    | pointer to what post the comment is under   |


List of network requests by screen
- Home Feed Screen
(Read/GET) Query all posts where user is author
(Create/POST) Create a new like on a post
(Delete) Delete existing like
(Create/POST) Create a new comment on a post
(Delete) Delete existing comment

- Create Post Screen
(Create/POST) Create a new post object
Profile Screen
(Read/GET) Query logged in user object
(Update/PUT) Update user profile image
(Update/PUT) Update user bio

