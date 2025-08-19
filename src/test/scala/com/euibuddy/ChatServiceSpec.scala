package com.euibuddy

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import com.euibuddy.model.*
import com.euibuddy.service.ChatService

class ChatServiceSpec extends AnyFlatSpec with Matchers {
  
  "tokenize" should "split text into words" in {
    ChatService.tokenize("Hello EUIBuddy") should be(List("Hello", "EUIBuddy"))
    ChatService.tokenize("  multiple   spaces  ") should be(List("multiple", "spaces"))
    ChatService.tokenize("") should be(List())
  }
  
  "normalize" should "convert to lowercase and remove punctuation" in {
    ChatService.normalize(List("Hello", "EUIBuddy!")) should be(List("hello", "euibuddy"))
    ChatService.normalize(List("Where's", "the", "campus?")) should be(List("wheres", "the", "campus"))
  }
  
  "classifyIntent" should "correctly identify greeting intents" in {
    ChatService.classifyIntent(List("hello")) should be(Greeting)
    ChatService.classifyIntent(List("hi", "there")) should be(Greeting)
    ChatService.classifyIntent(List("good", "morning")) should be(Greeting)
  }
  
  it should "correctly identify help intents" in {
    ChatService.classifyIntent(List("help")) should be(Help)
    ChatService.classifyIntent(List("i", "need", "assist")) should be(Help)
  }
  
  it should "correctly identify campus location intents" in {
    ChatService.classifyIntent(List("where", "is", "the", "campus")) should be(CampusLocation)
    ChatService.classifyIntent(List("campus", "location")) should be(CampusLocation)
  }
  
  it should "correctly identify faculty intents" in {
    ChatService.classifyIntent(List("cis", "faculty")) should be(FacultyInfo("cis"))
    ChatService.classifyIntent(List("engineering", "department")) should be(FacultyInfo("engineering"))
    ChatService.classifyIntent(List("business", "informatics")) should be(FacultyInfo("business"))
    ChatService.classifyIntent(List("digital", "arts", "design")) should be(FacultyInfo("design"))
  }
  
  it should "correctly identify contact intents" in {
    ChatService.classifyIntent(List("contact", "information")) should be(Contacts)
    ChatService.classifyIntent(List("phone", "number")) should be(Contacts)
  }
  
  it should "correctly identify GPA intents" in {
    ChatService.classifyIntent(List("calculate", "gpa")) should be(GPAQuery)
    ChatService.classifyIntent(List("grade", "point", "average")) should be(GPAQuery)
  }
  
  it should "return fallback for unrecognized input" in {
    ChatService.classifyIntent(List("random", "words")) should be(Fallback)
  }
  
  "parseGradeInput" should "correctly parse valid course input" in {
    val result = ChatService.parseGradeInput("Math101, 3, A")
    result should be(Some(Course("Math101", 3.0, "A")))
  }
  
  it should "return None for invalid input" in {
    ChatService.parseGradeInput("invalid input") should be(None)
    ChatService.parseGradeInput("Math101, invalid, A") should be(None)
    ChatService.parseGradeInput("Math101, 3, Z") should be(None)
  }
  
  "calculateGPA" should "correctly calculate GPA for valid courses" in {
    val courses = List(
      Course("Math101", 3.0, "A"),
      Course("CS101", 4.0, "B+"),
      Course("ENG101", 3.0, "A-")
    )
    val result = ChatService.calculateGPA(courses)
    result should be(defined)
    result.get should be(3.57 +- 0.01) // More precise calculation
  }

  it should "return None for empty course list" in {
    ChatService.calculateGPA(List()) should be(None)
  }

  "calculateCumulativeGPA" should "calculate both term and cumulative GPA" in {
    val previousCourses = List(
      Course("HIST101", 3.0, "B"),
      Course("PHYS101", 4.0, "A-")
    )
    val currentCourses = List(
      Course("MATH101", 3.0, "A"),
      Course("CS101", 4.0, "B+")
    )
    
    val (termGPA, cumulativeGPA) = ChatService.calculateCumulativeGPA(currentCourses, previousCourses)
    termGPA should be(defined)
    cumulativeGPA should be(defined)
    termGPA.get should be(3.57 +- 0.01)
    cumulativeGPA.get should be(3.47 +- 0.01) // Combined calculation
  }

  it should "handle empty previous courses correctly" in {
    val currentCourses = List(
      Course("MATH101", 3.0, "A")
    )
    val (termGPA, cumulativeGPA) = ChatService.calculateCumulativeGPA(currentCourses, List())
    termGPA should be(defined)
    cumulativeGPA should be(defined)
    termGPA shouldBe cumulativeGPA // Should be the same when no previous courses
  }
  
  "updateLog" should "add a new message to the log" in {
    val initialLog = List(Message(1, "Hello", "Hi there", "qna"))
    val updatedLog = ChatService.updateLog(initialLog, "How are you?", "I'm fine", 2)
    updatedLog should have length 2
    updatedLog.last.userText should be("How are you?")
    updatedLog.last.botReply should be("I'm fine")
    updatedLog.last.sequenceNumber should be(2)
  }
  
  "updateState" should "increment sequence counter and update log" in {
    val initialState = ChatState(List(), 0)
    val updatedState = ChatService.updateState(initialState, "Hello", "Hi there")
    updatedState.sequenceCounter should be(1)
    updatedState.interactionLog should have length 1
    updatedState.interactionLog.head.sequenceNumber should be(1)
  }
  
  "getTotalInteractions" should "return correct count" in {
    val log = List(
      Message(1, "Hello", "Hi", "qna"),
      Message(2, "Help", "I can help", "qna")
    )
    ChatService.getTotalInteractions(log) should be(2)
  }
  
  "generateResponse" should "return appropriate responses for different intents" in {
    val greetingResponse = ChatService.generateResponse(Greeting)
    greetingResponse should include("EUIBuddy")
    greetingResponse should include("campus assistant")
    
    val helpResponse = ChatService.generateResponse(Help)
    helpResponse should include("Campus location")
    helpResponse should include("Faculty information")
    
    val fallbackResponse = ChatService.generateResponse(Fallback)
    fallbackResponse should include("not sure")
    fallbackResponse should include("help")
  }
}

