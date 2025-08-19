package com.euibuddy.model

// Sealed trait for user intents
sealed trait Intent
case object Greeting extends Intent
case object Help extends Intent
case object CampusLocation extends Intent
case class FacultyInfo(facultyName: String) extends Intent
case object Contacts extends Intent
case object ResultsGuidance extends Intent
case object StudyPlanGuidance extends Intent
case object GPAQuery extends Intent
case object AnalyticsQuery extends Intent
case object Fallback extends Intent

// Case class for individual messages/interactions
case class Message(
  sequenceNumber: Int,
  userText: String,
  botReply: String,
  kind: String = "qna"
)

// Type alias for interaction log
type InteractionLog = List[Message]

// Case class for course information (used in GPA calculation)
case class Course(
  name: String,
  credits: Double,
  grade: String
)

// Case class for the overall chatbot state
case class ChatState(
  interactionLog: InteractionLog,
  sequenceCounter: Int = 0,
  cumulativeCourses: List[Course] = List.empty,
  cumulativeGPA: Option[Double] = None
)

// Object for GPA calculation constants
object GPAFormula {
  val gradeToPoints: Map[String, Double] = Map(
    "A+" -> 4.0,
    "A" -> 3.7,
    "A-" -> 3.4,
    "B+" -> 3.2,
    "B" -> 3.0,
    "B-" -> 2.8,
    "C+" -> 2.6,
    "C" -> 2.4,
    "C-" -> 2.2,
    "D+" -> 2,
    "D" -> 1.5,
    "D-" -> 1,
    "F" -> 0.0
  )
  
  def roundGPA(gpa: Double): Double = {
    Math.round(gpa * 100.0) / 100.0
  }
}

