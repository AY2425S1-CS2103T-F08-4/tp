---
layout: page
title: Developer Guide
---
* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* {list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well}

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

<div markdown="span" class="alert alert-primary">

:bulb: **Tip:** The `.puml` files used to create diagrams in this document `docs/diagrams` folder. Refer to the [_PlantUML Tutorial_ at se-edu/guides](https://se-education.org/guides/tutorials/plantUml.html) to learn how to create and edit diagrams.
</div>

### Architecture

<img src="images/ArchitectureDiagram.png" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<img src="images/ArchitectureSequenceDiagram.png" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<img src="images/ComponentManagers.png" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

![Structure of the UI Component](images/UiClassDiagram.png)

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `ReminderListDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object and `Reminder` residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<img src="images/LogicClassDiagram.png" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

![Interactions Inside the Logic Component for the `delete 1` Command](images/DeleteSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</div>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<img src="images/ParserClasses.png" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<img src="images/ModelClassDiagram.png" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the reminder address book data i.e., all `Reminder` objects (which are contained in a `UniqueReminderList` object).
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<div markdown="span" class="alert alert-info">:information_source: **Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<img src="images/BetterModelClassDiagram.png" width="450" />

</div>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<img src="images/StorageClassDiagram.png" width="550" />

The `Storage` component,
* can save address book data, reminder address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from `AddressBookStorage`, `ReminderAddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

![UndoRedoState0](images/UndoRedoState0.png)

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

![UndoRedoState1](images/UndoRedoState1.png)

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

![UndoRedoState2](images/UndoRedoState2.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</div>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

![UndoRedoState3](images/UndoRedoState3.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</div>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Logic.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</div>

Similarly, how an undo operation goes through the `Model` component is shown below:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Model.png)

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</div>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

![UndoRedoState4](images/UndoRedoState4.png)

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

![UndoRedoState5](images/UndoRedoState5.png)

The following activity diagram summarizes what happens when a user executes a new command:

<img src="images/CommitActivityDiagram.png" width="250" />

#### Design considerations:

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command are correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**: School of Computing students

* has a need to manage a significant number of contacts of other students, recruiters, potential employers in
networking events
* prefer desktop apps over other types
* can type fast due to the nature of their course
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps

**Value proposition**: Allows SoC students to quickly and easily manage the relationships they form with others in
school and on their journey to acquire internships. SoC students are likely to prefer a CLI due to their background 
in computing. Also, they can most likely type fast.



### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​                              | I want to …​                                                           | So that I can…​                                    |
| -------- | ------------------------------------ | -------------------------------------------------------------------- | ------------------------------------------------- |
| `* * *`  | computer science student             | have a CLI                                                           | navigate around the app easier                    |
| `* * *`  | school of computing student          | have an option of adding new contacts quickly                        | quickyl track people while socializing            |
| `* * *`  | user                                 | be able to edit contact information                                  | so that I can change lines quickly                |
| `* * *`  | user                                 | be able to delete contacts                                           |                                                   |
| `* * *`  | first time user                      | have a user guide for the app                                        | navigate around easily                            |
| `* * *`  | computing student                    | be able to save the contact's Github username                        |                                                   |
| `* * *`  | user trying to make connections      | be able to label a contact as a "follow-up"                          | remember to contact them                          |  
| `* * *`  | student looking for an internship    | have a feature to rank the priority of my contacts                   | focus more on internships with a higher chance    |
| `* * *`  | student trying to make connections   | have a feature to add potential meeting date with my contact         | keep track of my schedule                         | 
| `* *`    | user trying to make connections      | be able to tag the contacts by association                           | know how I met them                               |
| `* *`    | user trying to make connections      | be able to assign the current connection (eg. connected on LinkedIn) | know how close I am to each contact               |
| `* *`    | user                                 | be able to mark each action (e.g. connected on LinkedIn) with a date | keep track of these details                       |
| `* *`    | user                                 | be able to filter my contact list by association                     |                                                   |
| `* *`    | user                                 | be able to filter my contact list by current stage                   |                                                   |
| `* *`    | user                                 | be able to filter my contact list by last action date                |                                                   |
| `*`      | student                              | be able to create contacts with empty fields                         | remember people who I am starting to get to know  |
| `*`      | frequent user                        | be able to change my default sort                                    | keep my preferred sort                            |
| `*`      | user                                 | be able to use CLI to open up a new window                           | show all details for a given contact              |
| `*`      | user                                 | be able to archive all my current contacts                           | save less-contacted                               |
| `*`      | lazy user                            | use short commands                                                   | obtain results quicker                            |
| `*`      | user who is not strong at connecting | get prompted for potential actions                                   | connect with my contacts easier                   |
| `*`      | user looking to expand my network    | be able to view other student's email                                | contact them                                      |
| `*`      | student looking for an internship    | be able to set reminders                                             | remember to follow up with people I meet          |
| `*`      | advanced user                        | have a visualisation of my connections progress                      | have a gauge of my network connections            |


*{More to be added}*

### Use cases
### **System:** NetBook  
***
**Use case:** UCO1 - Add New Contact  
**Actor:** User

**Main Success Scenario (MSS)**
1. User requests to add a new contact
2. NetBook validates the inputs from the user
3. NetBook adds the new contact to the list of contacts
4. NetBook displays the new contact on the GUI

Use case ends.

**Extensions**

* 2a. A duplicate contact was added
  * 2a1. NetBook displays an error message
  * **Use case ends.**


* 2b. Invalid or missing data
  * 2b1. NetBook displays an error message
  * 2b2. User requests to add a contact again
  * 2b3. Steps 2b1 and 2b2 are repeated until the information entered is valid
  * **Use case resumes from Step 3**
***
**Use case:** UCO2 - Delete Contact  
**Actor:** User

**Main Success Scenario (MSS)**
1. User requests to delete a contact
2. NetBook validates the input from the user
3. NetBook deletes the specified contact
4. NetBook removes the specified contact from the GUI

Use case ends.

**Extensions**

* 2a. Invalid index number
    * 2a1. NetBook displays an error message stating that the given index is invalid
    * 2a2. User re-enters delete command with valid index
    * 2a3. Steps 2b1 and 2b2 are repeated until the information entered is valid
    * **Use case resumes from Step 3**

***
**Use case:** UCO3 - Find Contact  
**Actor:** User

**Main Success Scenario (MSS)**
1. User requests to find a contact through a keyword
2. NetBook validates the input from the user
3. NetBook identifies users with the keyword in their name or organisation
4. NetBook displays the relevant users in the GUI, and displays a message stating the number of contacts displayed

Use case ends.
***
**Use case:** UCO4 - List All Contacts  
**Actor:** User

**Main Success Scenario (MSS)**
1. User types in `list` command
2. NetBook displays the all registered contacts in the GUI, and displays a message
informing users that all contacts have been displayed

Use case ends.
***

**Use case:** UCO5 - Open Help Page  
**Actor:** New User

**Main Success Scenario (MSS)**
1. User types in `help` command
2. NetBook displays the help page to the user

Use case ends.
***
**Use case:** UCO6 - Assign Priority To Contact   
**Actor:** User

**Main Success Scenario (MSS)**
1. User requests to assign a priority to a contact
2. NetBook validates the input from the user
3. NetBook assigns the stated priority to the stated contact
4. NetBook displays the priority for the contact on the GUI

Use case ends.

**Extensions**

* 2a. Invalid or missing fields
    * 2a1. NetBook displays an error message
    * 2a2. User requests to add a priority to a contact again
    * 2a3. Steps 2bi and 2bii are repeated until the information entered is valid
    * **Use case resumes from Step 3**
***
**Use case:** UCO7 - Edit Contact 
**Actor:** User

**Main Success Scenario (MSS)**
1. User requests to edit the details of a specific contact
2. NetBook validates the input from the user
3. NetBook edits the specified contact
4. NetBook updates the information in the GUI

Use case ends.

**Extensions**

* 2a. Invalid index number
    * 2a1. NetBook displays an error message stating that the given index is invalid
    * 2a2. User re-enters delete command with valid index
    * 2a3. Steps 2a1 and 2a2 are repeated until the information entered is valid
    * **Use case resumes from Step 3**
* 2b. Invalid Data
    * 2b1. NetBook displays an error message informing the user of the correct format
    * 2b2. User re-enters data in the correct format
    * 2b3. Steps 2b1 and 2b2 are repeated until the data entered is valid
    * **Use case resumes from Step 3**

***
**Use case:** UCO8 - Sort Contacts
**Actor:** User

**Main Success Scenario (MSS)**
1. User requests to sort contacts by a specific sorting preference
2. NetBook validates the input from the user
3. NetBook updates the GUI so that the contacts are arranged according to the sorting preference

Use case ends.

**Extensions**

* 2a. Invalid sorting preference
    * 2a1. NetBook displays an error message stating that the given sorting preference is invalid
    * 2a2. User re-enters delete command with a valid sorting preference
    * 2a3. Steps 2a1 and 2a2 are repeated until the sorting preference entered is valid
    * **Use case resumes from Step 3**

***
**Use case:** UCO9 - Save sorting preference
**Actor:** User

**Main Success Scenario (MSS)**
1. User requests to save a specific sorting preference
2. NetBook validates the input from the user
3. NetBook updates the saved sorting preference

Use case ends.

**Extensions**

* 2a. Invalid sorting preference
    * 2a1. NetBook displays an error message stating that the given sorting preference is invalid
    * 2a2. User re-enters delete command with a valid sorting preference
    * 2a3. Steps 2a1 and 2a2 are repeated until the sorting preference entered is valid
    * **Use case resumes from Step 3**

***
**Use case:** UC10 - Add a remark to a contact
**Actor:** User

**Main Success Scenario (MSS)**
1. User requests to add a remark to a contact
2. NetBook validates the input from the user
3. NetBook adds the given remark to the contact
4. NetBook displays the remark in the GUI

Use case ends.

**Extensions**

* 2a. Invalid index number
    * 2a1. NetBook displays an error message stating that the given index is invalid
    * 2a2. User re-enters delete command with valid index
    * 2a3. Steps 2a1 and 2a2 are repeated until the information entered is valid
    * **Use case resumes from Step 3**

***
**Use case:** UC11 - Create a reminder
**Actor:** User

**Main Success Scenario (MSS)**
1. User requests to create a reminder about a contact
2. NetBook validates the input from the user
3. NetBook creates the given reminder
4. NetBook displays the remark in the GUI

Use case ends.

**Extensions**

* 2a. Invalid index number
    * 2a1. NetBook displays an error message stating that the given index is invalid
    * 2a2. User re-enters delete command with valid index
    * 2a3. Steps 2a1 and 2a2 are repeated until the information entered is valid
    * **Use case resumes from Step 3**
* 2b. Invalid Data
    * 2b1. NetBook displays an error message informing the user of the correct format
    * 2b2. User re-enters data in the correct format
    * 2b3. Steps 2b1 and 2b2 are repeated until the data entered is valid
    * **Use case resumes from Step 3**

***
**Use case:** UC12 - Delete a reminder
**Actor:** User

**Main Success Scenario (MSS)**
1. User requests to delete a specified reminder
2. NetBook validates the input from the user
3. NetBook deletes the given reminder
4. NetBook removes the deleted reminder from the GUI

Use case ends.

**Extensions**

* 2a. Invalid index number
    * 2a1. NetBook displays an error message stating that the given index is invalid
    * 2a2. User re-enters delete command with valid index
    * 2a3. Steps 2a1 and 2a2 are repeated until the information entered is valid
    * **Use case resumes from Step 3**

***


### Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `17` or above installed.
2.  Should be able to hold up to 1000 persons without a noticeable sluggishness in performance for typical usage.
3.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
4.  Most commands should result in a visible change within 2 seconds.
5.  Should not contain languages and images that are deemed offensive to the target audience.
6.  The GUI should contain high contrast themes, which allows users to view the contents without straining their eyes.
7.  The GUI should work well in different resolutions.
8.  The system should be intuitive and easy to use for new users.
9.  The date formats for fields that require dates must align with the cultural preferences of SoC students. 
10. Should have a friendly pdf for both the user and developer guides.
11. Should adhere to a strict schedule that delivers a feature set or update by the end of every milestone.
12. NetBook should only take up a small storage space of less than 100MB.



### Glossary

* **Mainstream OS**: Windows, Linux, Unix, MacOS
* **Private contact detail**: A contact detail that is not meant to be shared with others

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<div markdown="span" class="alert alert-info">:information_source: **Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</div>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

1. _{ more test cases …​ }_

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command. Multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: First contact is deleted from the list. Details of the deleted contact shown in the status message. Timestamp in the status bar is updated.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. Error details shown in the status message. Status bar remains the same.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases …​ }_

### Sorting the list

1. Sorting the list while all persons are being shown

    1. Test case: `sort recent`<br>
       Expected: List is sorted by last seen date from recent to distant. Timestamp in the status bar is updated.

    1. Test case: `sort high`<br>
       Expected: List is sorted by priority from high to low. Timestamp in the status bar is updated.

    1. Test case: `sort name`<br>
       Expected: List doesn't change. Error details shown in the status message. Status bar remains the same.

    1. Other incorrect delete commands to try: `sort`, `sort s`, `...` (where s is not one of the four default parameters)<br>
       Expected: Similar to previous.

1. _{ more test cases …​ }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{explain how to simulate a missing/corrupted file, and the expected behavior}_

1. _{ more test cases …​ }_
