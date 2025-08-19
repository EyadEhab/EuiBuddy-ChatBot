package com.euibuddy

import com.euibuddy.model.*
import com.euibuddy.service.ChatService
import scala.io.StdIn
import scala.annotation.tailrec

object Main {
  
  def main(args: Array[String]): Unit = {
    println("=" * 50)
    println("Welcome to EUIBuddy - Your Campus Assistant!")
    println("=" * 50)
    println("Type 'quit' or 'exit' to end the conversation.")
    println("Type 'analytics' to see interaction statistics.")
    println()
    
    val initialState = ChatState(List.empty, 0)
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
    
    if (userInput.trim.isEmpty) {
      runChatbot(state)
    } else {
      // Process the input through our pure functions
      val tokens = ChatService.tokenize(userInput)
      val normalizedTokens = ChatService.normalize(tokens)
      val intent = ChatService.classifyIntent(normalizedTokens)
      
      // Handle special cases
      val (response, newState) = intent match {
        case AnalyticsQuery =>
          val analyticsResponse = ChatService.generateAnalytics(state.interactionLog)
          val updatedState = ChatService.updateState(state, userInput, analyticsResponse)
          (analyticsResponse, updatedState)
          
        case GPAQuery =>
          handleGPACalculation(state, userInput)
          
        case _ =>
          val response = ChatService.generateResponse(intent)
          val updatedState = ChatService.updateState(state, userInput, response)
          (response, updatedState)
      }
      
      println(s"EUIBuddy: $response")
      println()
      
      runChatbot(newState)
    }
  }
  
  def handleGPACalculation(state: ChatState, userInput: String): (String, ChatState) = {
    val initialResponse = if (state.cumulativeCourses.isEmpty) {
      ChatService.generateResponse(GPAQuery)
    } else {
      s"""GPA Calculation (Cumulative: ${state.cumulativeGPA.getOrElse(0.0)} from ${state.cumulativeCourses.length} previous courses)
         |
         |To calculate GPA for new courses, provide them in this format:
         |Course Name, Credits, Grade (e.g., "Math101, 3, A")
         |
         |Supported grades: A+, A, A-, B+, B, B-, C+, C, C-, D+, D, F
         |Grade scale: A+ = 4.0, A = 3.7, A- = 3.4, B+ = 3.2, B = 3.0, B- = 2.8, C+ = 2.6, C = 2.4, C- = 2.2, D+ = 2.0, D = 1.5,D- = 1.0, F = 0.0
         |
         |Enter your courses one by one, or type 'done' when finished.""".stripMargin
    }
    
    val updatedState = ChatService.updateState(state, userInput, initialResponse)
    
    println(s"EUIBuddy: $initialResponse")
    println()
    
    @tailrec
    def collectCourses(courses: List[Course], currentState: ChatState): (String, ChatState) = {
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
            collectCourses(courses :+ course, newState)
          case None =>
            println("Invalid format. Please use: Course Name, Credits, Grade (e.g., Math101, 3, A)")
            collectCourses(courses, currentState)
        }
      }
    }
    
    collectCourses(List.empty, updatedState)
  }
}

