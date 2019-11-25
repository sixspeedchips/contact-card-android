
## Introduction

Those who work in professional settings may find it common to exchange business cards upon meeting someone new. Business cards provide a concise medium for exchanging useful information about what one does, where one works and how to be easily contacted. In today's world they often function as an advertisement of one's own personal brand. However, frequent exchanges often lead people to accumulation of business cards with no easy way to store or index them.

## Overview

This project is intended to provide an easy method for people to store the business cards they acquire as contacts in their phone. When a rectangle roughly matching the dimensions of a business card enters the frame of the camera a raw image is taken as input, transformed and text is extracted. The extracted text is parsed and offered to the user as a contact and the contact saved. Later implementations may add the ability to search the contact on popular social platforms by returning API queries based on the processed text, which then the user may attempt to add to their own accounts.

## Current State

Currently the functionality of the app is as follows, when the app is launched for the first time there are several data preloads that happen, one, the training data is stored on the machine and loaded, and the database is populated with a list of first and last names. Given this, it then launches immediately into a camera. From the Camera the app attempts to find any card matching a rectangular area by performing several image processing functions using open-cv function. An approximation is performed to determine the a bounding box for the image and then the image is cropped and saved. When the image is saved the app swaps to a review, so that the user may review their capture, if it is unsatisfactory they may swipe left and attempt another capture. If the capture is likely to yield positive results the user may swipe right to then process the image. Processing happens first by feeding the image into the tesseract engine which yields a string interpretation of the text in the image. These results are then fed into a parsing algorithm which then attemps to pull out the name, the phone number and the email in the text. These results are display on the screen in edit boxes which the user may change to fix as poor results. 

## Future Goals

Future goals include:
* Expanding on user options.
* Building the features in the gallery.
* Code refactoring.
* Processing optimizations.
* Adding options to find social media for a contact and send requests.
* Tagging contacts with location to help remember where they were met.
* Bug-fixing

## Requirements

* Java 8
* Android SDK build minimum 24.
* An android phone with decent specs to handle the image processing.

## Build Instructions

1. Clone the repository:
	`git clone git@github.com:swandivejack/contact-card-android.git`
2. Using an IDE such as intellij import the project.
3. Go to [google dev console](https://console.developers.google.com/). Create a new project and add an Oauth key. From intellij, in the gradle tab->app->tasks->android->signing report, get the sha-1 and then copy the base package name of the project into the oathkey fields in google.
4. Build the gradle file.
5. Add an android app run configuration and run the app. 


### Bugs

Several bugs are currently known.
* App crashes dues to images being read and saved too quickly from the stream, most commn when the lighting is superb and the app can build bounding boxes for the card very quickly.
* Threads may sometimes not terminate under certain conditions leading to high battery consumption.
* Sometimes the flag for capturing an image is not tripped and the app is left in a state in which capturing cannot happen. 

### Technical requirements & dependencies

* All testing was performed on an Samsung s8+ running android 9. The minimum version set in the build.gradle is Android API 24. Device orientation is locked in landscape to prevent issues with image rotation and Tesseract.
* Libraries:
    * Open-CV: image processing
    * Tesseract: OCR 
    * Picasso to facilitate image loading and caching
* Services:
    * Google sign-in

### Links
* [Documentation](docs/javadocs/index.html)
* [Licenses](docs/licenses/license.info)
* [Usages](docs/usages.md)
* [Source Code](docs/sources.md)
* [User Stories](docs/user-stories.md)
* [Intended Users](docs/intended-users.md)
* [Entity Relationship Diagrams](docs/erd.md)
* [Screenshots](docs/screenshots.md)
* [Wireframes](docs/wireframe.md)
* [Milestones](docs/milestones.md)

