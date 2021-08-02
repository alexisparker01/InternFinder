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

* Users can register for an account which requires email confirmation [x]
* Users who already have an account can login [x]
* Users can logout [x]
* Users can set up a profile after their email in confirmed. Profiles would include: Name (EditText), Bio (EditText), Industry (Pick from a list), and Profile Picture (ImageView) [x]
* Users can post events (event post) using Google Maps and Places SDK [x]
* Users can post status updates (text post) [x]
* Users can post intro posts (text post) [x]
* Users can post picture posts (picture post) using the Camera [x]
* Each post has a comment section so the users can communicate with each other [x]
* Every profile has a followers and following list
* Users can search for other users [x]
* Gesture feature
* Animation feature
* External library to add visual polish
* Navigation [x]


**Optional Nice-to-have Stories**

* You can link your LinkedIn profile to your profile
* Bio has little prompts to answer (the answers will be multiple choice) so you can get to know someone like for example: After work what is your favorite thing to do? On the weekends what do you like to do? What's your work from home set up like?) Based on how many of the little bio questions that you have similar answers to, it'll show how alike you guys are like (you and this person are 50% compatable)
* Users can filter feed to show posts from people they are following, and people within a certain number of miles of them [x]
* You can see a list of people attending the event
* There is a map on the search/explore page that shows events near you on a map [x]
* Autofill suggestions when searching event locations [x]
* Search page will display interns that have the same industry as you and in the same area [x]
* For Profile Picture, there can be an option to upload picture from camera roll or make your profile picture a picture you take on your camera then and there. [x]

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
[Add picture of your hand sketched wireframes in this section]
<img src="https://www.figma.com/proto/0yivHGyHXgUChY8IypclPc/InternFinder?node-id=1%3A2&scaling=scale-down&page-id=0%3A1" width=600>

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
| objectID     | String     | unique id for the user post (default field)    |
| author     | Pointer to User     | image author   |
| username     | string     | username   |
| password     | String     | password    |
| profilePic     | File     | profile picture for profile    |
| Bio     | String     | biography for profile    |


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

