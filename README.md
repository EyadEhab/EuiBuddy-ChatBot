# EUIBuddy - Campus & Faculties Helper Chatbot

A lightweight, rule-based chatbot built in Scala 3 that helps new students with campus basics and information about all EUI faculties (CIS, Engineering, Business Informatics, Digital Arts & Design).

## Overview

EUIBuddy is a functional programming-focused chatbot that demonstrates the use of Scala 3's advanced features including:
- Pattern matching and sealed ADTs for intent classification
- Immutable data structures for state management
- Pure functions for all core logic
- Higher-order functions for analytics and data processing
- Option types for safe error handling

## Features

- **Campus Information**: Location, directions, and general campus details
- **Faculty Information**: Details about all four EUI faculties including programs, locations, and contacts
- **Contact Directory**: Comprehensive contact information for various departments
- **Study Plan Guidance**: General guidance on academic structure and prerequisites
- **Results Information**: Guidance on accessing official results and handling discrepancies
- **GPA Calculator**: Interactive GPA calculation with support for standard grade scales
- **Analytics**: Interaction statistics and intent analysis
- **Robust Input Processing**: Handles typos, synonyms, and flexible input formats

## Architecture

The chatbot follows a purely functional architecture with clear separation of concerns:

### Core Components

1. **Models** (`model/Models.scala`): ADTs and case classes for data representation
2. **Knowledge Base** (`data/KnowledgeBase.scala`): Immutable maps containing factual information
3. **Chat Service** (`service/ChatService.scala`): Pure functions for all core logic
4. **Main Application** (`Main.scala`): Application entry point and I/O handling

### Data Flow

```
User Input → Tokenization → Normalization → Intent Classification → Response Generation → State Update → Output
```

All transformations are performed using pure functions, ensuring predictability and testability.

## Requirements

- Java 11 or higher
- Scala 3.4.1
- sbt (Scala Build Tool)

## Installation & Setup

1. **Install Scala and sbt** (if not already installed):
   Install the latest LTS java jdk from <br>https://adoptium.net/temurin/releases<br>
   and the latest sbt from <br>https://www.scala-sbt.org/download/

3. **Clone or extract the project**:
   <br>open cmd
   ```bash
   git clone https://github.com/EyadEhab/EuiBuddy-ChatBot.git
   cd EuiBuddy-ChatBot
   ```
   
## Usage

### Running the Chatbot ( It will take time in the first run only )
   ```bash
   sbt run
   ```

### Interactive Commands

Once running, you can interact with EUIBuddy using natural language. Examples:

- **Greetings**: "hello", "hi", "good morning"
- **Help**: "help", "what can you do"
- **Campus Location**: "where is campus", "campus location"
- **Faculty Information**: "cis faculty", "engineering department", "business programs"
- **Contacts**: "contact information", "phone numbers"
- **Study Plans**: "study plan", "prerequisites", "curriculum"
- **Results**: "how to check results", "transcript information"
- **GPA Calculation**: "calculate gpa", "grade point average"
- **Analytics**: "analytics", "statistics", "interaction stats"
- **Exit**: "quit", "exit"

### GPA Calculation Example

```
You: calculate gpa
EUIBuddy: [GPA calculation instructions]

Enter course (Name, Credits, Grade) or 'done': Math101, 3, A
Added: Math101 (3.0 credits, A)

Enter course (Name, Credits, Grade) or 'done': CS101, 4, B+
Added: CS101 (4.0 credits, B+)

Enter course (Name, Credits, Grade) or 'done': done
EUIBuddy: GPA Calculation Results:
Courses processed: 2
Term GPA: 3.57
Cumulative GPA: 3.57
```

## Testing

The project includes comprehensive unit tests covering all pure functions:

```bash
sbt test
```

Test coverage includes:
- Input processing (tokenization, normalization)
- Intent classification
- Response generation
- GPA calculations
- State management
- Analytics functions

## Project Structure

```
euibuddy-chatbot/
├── build.sbt                          # Build configuration
├── src/
│   ├── main/scala/com/euibuddy/
│   │   ├── Main.scala                 # Application entry point
│   │   ├── model/
│   │   │   └── Models.scala           # ADTs and case classes
│   │   ├── service/
│   │   │   └── ChatService.scala      # Core pure functions
│   │   └── data/
│   │       └── KnowledgeBase.scala    # Hard-coded knowledge base
│   └── test/scala/com/euibuddy/
│       └── ChatServiceSpec.scala      # Unit tests
├── README.md                          # This file
└── test_input.txt                     # Sample test input
```

## Functional Programming Concepts Used

### Algebraic Data Types (ADTs)
```scala
sealed trait Intent
case object Greeting extends Intent
case object Help extends Intent
case class FacultyInfo(facultyName: String) extends Intent
// ... more intents
```

### Pattern Matching
```scala
def classifyIntent(normalizedTokens: List[String]): Intent = {
  val tokenSet = normalizedTokens.toSet
  
  if (tokenSet.intersect(Set("hello", "hi", "hey")).nonEmpty) {
    Greeting
  } else if (tokenSet.intersect(Set("help", "assist")).nonEmpty) {
    Help
  }
  // ... more patterns
}
```

### Immutable Data Structures
```scala
case class ChatState(
  interactionLog: InteractionLog,
  sequenceCounter: Int = 0
)

def updateState(state: ChatState, userText: String, botReply: String): ChatState = {
  val newSequenceNumber = state.sequenceCounter + 1
  val newLog = updateLog(state.interactionLog, userText, botReply, newSequenceNumber)
  state.copy(interactionLog = newLog, sequenceCounter = newSequenceNumber)
}
```

### Higher-Order Functions
```scala
def getTopIntents(log: InteractionLog, count: Int): List[(String, Int)] = {
  log
    .map(message => classifyIntent(normalize(tokenize(message.userText))))
    .groupBy(identity)
    .view.mapValues(_.length)
    .toList
    .sortBy(-_._2)
    .take(count)
}
```

### Option Types for Error Handling
```scala
def calculateGPA(courses: List[Course]): Option[(Double, Double)] = {
  if (courses.isEmpty) return None
  
  val validCourses = courses.filter(course => GPAFormula.gradeToPoints.contains(course.grade))
  if (validCourses.isEmpty) return None
  
  // ... calculation logic
  Some((roundedGPA, roundedGPA))
}
```

## Knowledge Base

The chatbot uses a hard-coded, immutable knowledge base containing:

- **Campus Information**: Location, address, building details
- **Faculty Details**: Four faculties with programs, locations, deans, and contacts
- **Contact Directory**: Phone numbers, emails, and official channels
- **Academic Information**: Study plan structure, prerequisites, GPA policies
- **Results Guidance**: Portal access, discrepancy procedures, transcript requests

All factual responses include a disclaimer: "(Double-check the official EUI pages; details may change.)"
