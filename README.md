# README #

This app is a remake of the currency exchange screen of the Revolut android app.
 
### The idea ###
 
 Because the fixer.io endpoint only returns the exchange rates for one currency at a time we shouldn't
 poll all three currencies at the same time but only the one the user selected and when the user changes 
 the selected base currency we should start polling that. This makes polling more complicated because the webservice query changes.
 We should keep all the collected rates in memory so the user can change currencies and get back the
 previously collected rates.
 
 Use pure java everywhere where it's feasible to make it easily testable.
 
 To support rotation keep everything in memory so when the user rotates the phone the ui can just reattach and 
 get all the data back.
 
### Technical Approach ###

* The app is a simplified version of the clean architecture (less abstraction).
* Organised into 3 modules
    * Domain: Holds the apps domain logic (rate polling). Pure java. Defines it's own dependencies.
    * Data: For accessing data
    * Presentation: Showing data to the user
* Uses dagger to wire the dependencies
* Uses RxJava for handling data flow