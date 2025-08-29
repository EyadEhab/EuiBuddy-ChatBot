package com.euibuddy

import com.euibuddy.model.*
import com.euibuddy.service.ChatService
import scala.io.StdIn
import scala.annotation.tailrec

object Main {
  
  def selectLanguage(): String = {
    println("Please select your preferred language:")
    println("1. English")

    println("2. Arabic")
    print("Enter 1 or 2: ")
    val choice = StdIn.readLine().trim
    choice match {
      case "1" =>
        println("English selected.")
        "english"
      case "2" =>
        println("اللغة العربية تم اختيارها.")
        "arabic"
      case _ =>
        println("Invalid choice. Defaulting to English.")
        "english"
    }
  }

  def main(args: Array[String]): Unit = {
    println("=" * 50)
    println("Welcome to EUIBuddy - Your Campus Assistant!")
    println("=" * 50)
    println("Type 'quit' or 'exit' to end the conversation.")
    println("Type 'analytics' to see interaction statistics.")
    println()

    val selectedLanguage = selectLanguage()
    val initialState = ChatState(List.empty, 0, language = selectedLanguage)
    runChatbot(initialState)
  }

  @tailrec
  def runChatbot(state: ChatState): Unit = {
    print("You: ")
    val userInput = try {
      StdIn.readLine()
    } catch {
      case _: Exception => null
    }
    
    if (userInput == null || userInput.toLowerCase.trim == "quit" || userInput.toLowerCase.trim == "exit"|| userInput.toLowerCase.trim == "q") {
      println("\nEUIBuddy: Thank you for using EUIBuddy! Have a great day!")
      return
    }
    
    val newState = 
      if (userInput.toLowerCase.trim == "change language") {
        val newLanguage = selectLanguage()
        println(s"EUIBuddy: Language changed to $newLanguage.")
        state.copy(language = newLanguage)
      } else if (userInput.trim.isEmpty) {
        state
      } else {
        // Process the input through our pure functions
        val tokens = ChatService.tokenize(userInput)
        val normalizedTokens = ChatService.normalize(tokens)
        val intents = ChatService.classifyIntent(normalizedTokens)

        // Handle special cases and generate responses for all identified intents
        val (gpaIntents, otherIntents) = intents.partition(_ == GPAQuery)

        val (otherResponses, tempState) = otherIntents.foldLeft((List.empty[String], state)) {
          case ((accResponses, currentState), currentIntent) =>
            currentIntent match {
              case AnalyticsQuery =>
                val analyticsResponse = ChatService.generateAnalytics(currentState.interactionLog)
                val updatedState = ChatService.updateState(currentState, userInput, analyticsResponse)
                (accResponses :+ analyticsResponse, updatedState)
              case _ =>
                val response = ChatService.generateResponse(currentIntent, currentState)
                val updatedState = ChatService.updateState(currentState, userInput, response)
                (accResponses :+ response, updatedState)
            }
        }

        val initialCombinedResponse = otherResponses.mkString("\n\n")

        val (response, newState) = if (gpaIntents.nonEmpty) {
          // If GPAQuery is present, print other responses first, then handle GPA
          if (initialCombinedResponse.nonEmpty) {
            println(s"EUIBuddy: $initialCombinedResponse")
            println()
          }
          
          // Get the initial GPA prompt and state
          val (initialGPAResponse, stateAfterInitialPrompt) = handleGPACalculation(tempState, userInput)
          
          // Print the initial GPA prompt to the user
          println(s"EUIBuddy: $initialGPAResponse")
          println()
          
          // Now, start the interactive collection of courses and return its final result
          collectGPACourses(List.empty, stateAfterInitialPrompt)
        } else {
          // If no GPAQuery, just combine all responses
          (initialCombinedResponse, tempState)
        }
        
        println(s"EUIBuddy: $response")
        println()
        
        newState
      }
    runChatbot(newState)
  }
  
  def handleGPACalculation(state: ChatState, userInput: String): (String, ChatState) = {
    val cumulativeInfo = if (state.cumulativeCourses.nonEmpty) {
      s" (Cumulative: ${state.cumulativeGPA.getOrElse(0.0)} from ${state.cumulativeCourses.length} previous courses)"
    } else {
      ""
    }

    val initialResponse = s"""GPA Calculation$cumulativeInfo:
         |
         |To calculate your GPA, provide your courses in this format:
         |Course Name, Credits, Grade (e.g., "Math101, 3, A")
         |
         |Supported grades: A+, A, A-, B+, B, B-, C+, C, C-, D+, D, F
         |Grade scale: A+ = 4.0, A = 3.7, A- = 3.4, B+ = 3.2, B = 3.0, B- = 2.8, C+ = 2.6, C = 2.4, C- = 2.2, D+ = 2.0, D = 1.5,D- = 1.0, F = 0.0
         |
         |Enter your courses one by one, or type 'done' when finished.""".stripMargin
    
    val updatedState = ChatService.updateState(state, userInput, initialResponse)
    (initialResponse, updatedState)
  }

  @tailrec
  def collectGPACourses(courses: List[Course], currentState: ChatState): (String, ChatState) = {
    print("Enter course (Name, Credits, Grade) or 'done': ")
    val courseInput = StdIn.readLine()

    if (courseInput == null || courseInput.toLowerCase.trim == "done") {
      if (courses.nonEmpty) {
        val (termGPA, cumulativeGPA) = ChatService.calculateCumulativeGPA(courses, currentState.cumulativeCourses)
        
        val gpaResponse = s"""GPA Calculation Results:
                             |
                             |New courses processed: ${courses.length}
                             |Term GPA : ${termGPA.getOrElse("N/A")}
                             |Cumulative GPA : ${cumulativeGPA.getOrElse("N/A")}
                             |Total courses in record: ${currentState.cumulativeCourses.length + courses.length}""".stripMargin
        val finalState = ChatService.updateStateWithGPA(currentState, "GPA calculation completed", gpaResponse, courses, cumulativeGPA)
        (gpaResponse, finalState)
      } else {
        val noCoursesResponse = "No new courses entered. GPA calculation cancelled."
        val finalState = ChatService.updateState(currentState, "GPA calculation cancelled", noCoursesResponse)
        (noCoursesResponse, finalState)
      }
    } else {
      ChatService.parseGradeInput(courseInput) match {
        case Some(course) =>
          println(s"Added: ${course.name} (${course.credits} credits, ${course.grade})")
          val newState = ChatService.updateState(currentState, courseInput, s"Course added: ${course.name}")
          collectGPACourses(courses :+ course, newState)
        case None =>
          println("Invalid format. Please use: Course Name, Credits, Grade (e.g., Math101, 3, A)")
          collectGPACourses(courses, currentState)
      }
    }
  }
}

