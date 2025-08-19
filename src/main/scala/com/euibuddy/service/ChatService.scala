package com.euibuddy.service

import com.euibuddy.model.*
import com.euibuddy.data.KnowledgeBase

object ChatService {
  
  // Input processing functions
  def tokenize(text: String): List[String] = {
    text.split("\\s+").toList.filter(_.nonEmpty)
  }
  
  def normalize(tokens: List[String]): List[String] = {
    tokens.map(_.toLowerCase.trim.replaceAll("[^a-zA-Z0-9]", ""))
      .filter(_.nonEmpty)
  }
  
  // Intent classification function
  def classifyIntent(normalizedTokens: List[String]): Intent = {
    val tokenSet = normalizedTokens.toSet
    
    // Greeting patterns
    if (tokenSet.intersect(Set("hello", "hi", "hey", "greetings", "good")).nonEmpty) {
      Greeting
    }
    // Help patterns
    else if (tokenSet.intersect(Set("help", "assist", "support", "guide")).nonEmpty) {
      Help
    }
    // Campus location patterns
    else if (tokenSet.intersect(Set("where", "location", "campus", "address", "find")).nonEmpty &&
             tokenSet.intersect(Set("campus", "university", "eui", "building")).nonEmpty) {
      CampusLocation
    }
    // Faculty information patterns
    else if (tokenSet.intersect(Set("faculty", "department", "college", "school")).nonEmpty ||
             tokenSet.intersect(Set("cis", "engineering", "business", "design", "informatics")).nonEmpty) {
      
      if (tokenSet.contains("cis") || tokenSet.intersect(Set("computer", "information", "systems")).nonEmpty) {
        FacultyInfo("cis")
      } else if (tokenSet.contains("engineering") || tokenSet.intersect(Set("civil", "mechanical", "electrical")).nonEmpty) {
        FacultyInfo("engineering")
      } else if (tokenSet.intersect(Set("business", "informatics", "management")).nonEmpty) {
        FacultyInfo("business")
      } else if (tokenSet.intersect(Set("design", "arts", "digital", "graphic", "animation")).nonEmpty) {
        FacultyInfo("design")
      } else {
        FacultyInfo("general")
      }
    }
    // Contact information patterns
    else if (tokenSet.intersect(Set("contact", "phone", "email", "hotline", "call", "reach")).nonEmpty) {
      Contacts
    }
    // Results guidance patterns
    else if (tokenSet.intersect(Set("results", "grades", "marks", "scores", "transcript")).nonEmpty) {
      ResultsGuidance
    }
    // Study plan patterns
    else if (tokenSet.intersect(Set("study", "plan", "curriculum", "courses", "prerequisites", "program")).nonEmpty) {
      StudyPlanGuidance
    }
    // GPA calculation patterns
    else if (tokenSet.intersect(Set("gpa", "calculate", "grade", "point", "average")).nonEmpty) {
      GPAQuery
    }
    // Analytics patterns
    else if (tokenSet.intersect(Set("analytics", "statistics", "stats", "interactions", "log")).nonEmpty) {
      AnalyticsQuery
    }
    // Fallback for unrecognized input
    else {
      Fallback
    }
  }
  
  // Response generation function
  def generateResponse(intent: Intent): String = {
    intent match {
      case Greeting =>
        "Hello! I'm EUIBuddy, your campus assistant. I can help you with information about EUI faculties, campus location, contacts, study plans, GPA calculations, and more. How can I assist you today?"
      
      case Help =>
        """I can help you with:
          |• Campus location and directions
          |• Faculty information (CIS, Engineering, Business Informatics, Digital Arts & Design)
          |• Contact information and official channels
          |• Study plan guidance and prerequisites
          |• Results and transcript information
          |• GPA calculations
          |• Interaction analytics
          |
          |Just ask me about any of these topics!""".stripMargin
      
      case CampusLocation =>
        s"${KnowledgeBase.campusInfo("location")} ${KnowledgeBase.campusInfo("address")} ${KnowledgeBase.disclaimer}"
      
      case FacultyInfo(facultyName) =>
        KnowledgeBase.facultyInfo.get(facultyName) match {
          case Some(info) =>
            s"""Faculty: ${info("name")}
               |Location: ${info("location")}
               |Programs: ${info("programs")}
               |Dean: ${info("dean")}
               |Contact: ${info("contact")}
               |${KnowledgeBase.disclaimer}""".stripMargin
          case None =>
            s"""EUI has four main faculties:
               |• Computer and Information Systems (CIS)
               |• Engineering
               |• Business Informatics
               |• Digital Arts & Design
               |${KnowledgeBase.disclaimer}""".stripMargin
        }
      
      case Contacts =>
        s"""EUI Contact Information:
           |Main Hotline: ${KnowledgeBase.contactInfo("main_hotline")}
           |Admissions: ${KnowledgeBase.contactInfo("admissions")}
           |Student Affairs: ${KnowledgeBase.contactInfo("student_affairs")}
           |Academic Office: ${KnowledgeBase.contactInfo("academic_office")}
           |Website: ${KnowledgeBase.contactInfo("website")}
           |Emergency: ${KnowledgeBase.contactInfo("emergency")}
           |${KnowledgeBase.disclaimer}""".stripMargin
      
      case ResultsGuidance =>
        s"""Results Information:
           |${KnowledgeBase.resultsInfo("access")}
           |${KnowledgeBase.resultsInfo("discrepancies")}
           |${KnowledgeBase.resultsInfo("transcripts")}
           |${KnowledgeBase.resultsInfo("gpa_policy")}
           |${KnowledgeBase.disclaimer}""".stripMargin
      
      case StudyPlanGuidance =>
        s"""Study Plan Information:
           |${KnowledgeBase.studyPlanInfo("structure")}
           |${KnowledgeBase.studyPlanInfo("prerequisites")}
           |${KnowledgeBase.studyPlanInfo("credit_hours")}
           |${KnowledgeBase.studyPlanInfo("advisor_contact")}
           |${KnowledgeBase.disclaimer}""".stripMargin
      
      case GPAQuery =>
        """GPA Calculation Help:
          |To calculate your GPA, provide your courses in this format:
          |Course Name, Credits, Grade (e.g., "Math101, 3, A")
          |
          |Supported grades: A+, A, A-, B+, B, B-, C+, C, C-, D+, D, F
          |Grade scale: A+ = 4.0, A = 3.7, A- = 3.4, B+ = 3.2, B = 3.0, B- = 2.8, C+ = 2.6, C = 2.4, C- = 2.2, D+ = 2.0, D = 1.5,D- = 1.0, F = 0.0
          |
          |Enter your courses one by one, or type 'done' when finished.""".stripMargin
      
      case AnalyticsQuery =>
        "Analytics feature will show interaction statistics. This requires processing the interaction log."
      
      case Fallback =>
        "I'm not sure I understand. I can help with campus information, faculty details, contacts, study plans, GPA calculations, and analytics. Type 'help' to see what I can do!"
    }
  }
  
  // State management functions
  def updateLog(log: InteractionLog, userText: String, botReply: String, sequenceNumber: Int, kind: String = "qna"): InteractionLog = {
    val newMessage = Message(sequenceNumber, userText, botReply, kind)
    log :+ newMessage
  }
  
  def updateState(state: ChatState, userText: String, botReply: String): ChatState = {
    val newSequenceNumber = state.sequenceCounter + 1
    val newLog = updateLog(state.interactionLog, userText, botReply, newSequenceNumber)
    state.copy(interactionLog = newLog, sequenceCounter = newSequenceNumber)
  }
  
  def updateStateWithGPA(state: ChatState, userText: String, botReply: String, newCourses: List[Course], cumulativeGPA: Option[Double]): ChatState = {
    val newSequenceNumber = state.sequenceCounter + 1
    val newLog = updateLog(state.interactionLog, userText, botReply, newSequenceNumber)
    state.copy(
      interactionLog = newLog, 
      sequenceCounter = newSequenceNumber,
      cumulativeCourses = state.cumulativeCourses ++ newCourses,
      cumulativeGPA = cumulativeGPA
    )
  }
  
  // GPA calculation functions
  def parseGradeInput(input: String): Option[Course] = {
    val parts = input.split(",").map(_.trim)
    if (parts.length == 3) {
      try {
        val name = parts(0)
        val credits = parts(1).toDouble
        val grade = parts(2).toUpperCase
        if (GPAFormula.gradeToPoints.contains(grade) && credits > 0) {
          Some(Course(name, credits, grade))
        } else None
      } catch {
        case _: NumberFormatException => None
      }
    } else None
  }
  
  def calculateGPA(courses: List[Course]): Option[Double] = {
    if (courses.isEmpty) return None
    
    val validCourses = courses.filter(course => GPAFormula.gradeToPoints.contains(course.grade))
    if (validCourses.isEmpty) return None
    
    val totalPoints = validCourses.map(course => 
      GPAFormula.gradeToPoints(course.grade) * course.credits
    ).sum
    
    val totalCredits = validCourses.map(_.credits).sum
    
    if (totalCredits > 0) {
      val gpa = totalPoints / totalCredits
      Some(GPAFormula.roundGPA(gpa))
    } else None
  }
  
  def calculateCumulativeGPA(currentCourses: List[Course], previousCourses: List[Course]): (Option[Double], Option[Double]) = {
    val termGPA = calculateGPA(currentCourses)
    val allCourses = previousCourses ++ currentCourses
    val cumulativeGPA = calculateGPA(allCourses)
    (termGPA, cumulativeGPA)
  }
  
  // Analytics functions
  def getTotalInteractions(log: InteractionLog): Int = log.length
  
  def getTopIntents(log: InteractionLog, count: Int): List[(String, Int)] = {
    // This is a simplified version - in a real implementation, we'd need to store the intent with each message
    // For now, we'll analyze the user text to infer intents
    val intentCounts = log
      .map(message => classifyIntent(normalize(tokenize(message.userText))))
      .groupBy(identity)
      .view.mapValues(_.length)
      .toList
      .sortBy(-_._2)
      .take(count)
      .map { case (intent, count) => (intent.toString, count) }
    
    intentCounts
  }
  
  def generateAnalytics(log: InteractionLog): String = {
    val totalInteractions = getTotalInteractions(log)
    val topIntents = getTopIntents(log, 5)
    
    val analyticsText = s"""Interaction Analytics:
                           |Total Interactions: $totalInteractions
                           |
                           |Top Intents:""".stripMargin
    
    val intentsList = topIntents.map { case (intent, count) =>
      s"• $intent: $count times"
    }.mkString("\n")
    
    if (topIntents.nonEmpty) {
      analyticsText + "\n" + intentsList
    } else {
      analyticsText + "\nNo interactions recorded yet."
    }
  }
}

