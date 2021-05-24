# FBLA Quiz

Created by Varun Unnithan, from Morris Hills High School, for the FBLA 
Coding and Programming Competitive Event 2021.

All source code and images, with the exceptions of those related to the 
third-party libraries listed below, are original works by Varun Unnithan.

## Use

To run the program, open the FBLA Quiz.jar file within the program folder.

Upon starting, options are available to either start a new 5-question quiz, 
view the results of previous quizzes taken, or view the database of all 
current questions and answers. The last feature is meant only for teachers or
test administrators, and so it is locked behind a password. By default the 
password to access it is `password`, though this can be changed within the 
program.

In a quiz, there are five different types of questions, which are randomly chosen 
and administered in a random order. Each question may be flagged for review before 
submitting the quiz. The question types are as follows:

#### Multiple Choice
The single best choice of the four options for the given question must be selected 
at the time of the  quiz's submission. The eliminate tool, when enabled, can 
help cross off and disable choices which the user sees as incorrect.

#### Multiple Select
The best choices of the five options for the given question must be selected at 
the time of the quiz's submission. There may be anywhere from one correct answer 
among the options, to all five being correct. The eliminate tool, when enabled, 
can help cross off and disable choices which the user sees as incorrect.

#### True or False
The given statement will either be true or false, with the correct option needing
to be selected at the time of the quiz's submission.

#### Fill-in-the-Blank
The given question will have an answer that the user will need to type in and 
input at the time of the quiz's submission. This input may be either in the form
of a number or a typed word/phrase, with invalid inputs being voided.

#### Matching
The given direction will have the boxes underneath it be matched up with one another
in a specific combination. The left-hand side column of boxes must be connected to 
their corresponding boxes on the right hand side at the time of submission. Each box
will have exactly one matching box, and each box will have a pair. To connect boxes, 
simply drag the dark colored circles on the right hand side and connect them with the
corresponding light colored circles for the left hand side boxes.


## Directories

### Score Report
Score reports that have been exported as PDFs can be found in the Score Reports 
folder within the project's main directory.

### Documentation
Program documentation can be found within the javadoc folder within the project's 
main directory, which was created using Java's Javadoc documentation generator.


## Dependencies

This program also makes use of third-party libraries or other resources that 
may be distributed under their own licenses.

### JSON Simple 1.1.1

https://code.google.com/archive/p/json-simple/

Licensed by Apache License Version 2.0

### Apache PDFBox 2.0.2

https://pdfbox.apache.org/

Licensed by Apache License Version 2.0


## Build

Built with Apache Maven 3.6.3 

https://maven.apache.org/index.html

Licensed by Apache License Version 2.0


## Apache License

Below are details regarding the Apache License for the libraries used in this project.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
