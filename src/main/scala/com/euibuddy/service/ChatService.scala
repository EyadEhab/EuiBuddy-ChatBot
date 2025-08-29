package com.euibuddy.service

import com.euibuddy.model.*
import com.euibuddy.data.{KnowledgeBase, KnowledgeBaseArabic}

object ChatService {
  // Added a comment to force recompilation
  
  // Input processing functions
  def tokenize(text: String): List[String] = {
    // Split by whitespace, preserving Arabic characters
    text.split("\\s+").toList.filter(_.nonEmpty)
  }
  
  def normalize(tokens: List[String]): List[String] = {
    // Normalize tokens: convert to lowercase, trim, and remove non-alphanumeric characters
    // For Arabic, we need to be careful not to remove Arabic characters. 
    // Scala's toLowerCase works for Arabic, and we'll adjust regex to keep Arabic letters.
    tokens.map(_.toLowerCase.trim.replaceAll("[^a-zA-Z0-9\\u0600-\\u06FF]", "")) // \\u0600-\\u06FF is the Unicode range for Arabic characters
      .filter(_.nonEmpty)
  }
  
  // Intent classification function
  def classifyIntent(normalizedTokens: List[String]): List[Intent] = {
    val tokenSet = normalizedTokens.toSet
    var intents = List.empty[Intent]

    // Greeting patterns
    if (tokenSet.intersect(Set("hello", "hi", "hey", "greetings", "good")).nonEmpty) {
      intents = intents :+ Greeting
    }
    // Help patterns
    if (tokenSet.intersect(Set("help", "assist", "support", "guide")).nonEmpty) {
      intents = intents :+ Help
    }
    // Campus location patterns
    if (tokenSet.intersect(Set("where", "location", "campus", "address", "find")).nonEmpty &&
        tokenSet.intersect(Set("campus", "university", "eui", "building")).nonEmpty) {
      intents = intents :+ CampusLocation
    }
    // Faculty information patterns
    if (tokenSet.intersect(Set("faculty", "department", "college", "school")).nonEmpty ||
        tokenSet.intersect(Set("cis", "engineering", "business", "design", "informatics", "art", "digital", "bis", "management")).nonEmpty) {

      if (tokenSet.contains("cis") || tokenSet.contains("computer") || tokenSet.intersect(Set("information", "systems")).nonEmpty) {
        intents = intents :+ FacultyInfo("cis")
      }
      if (tokenSet.contains("engineering") || tokenSet.intersect(Set("civil", "mechanical", "electrical")).nonEmpty) {
        intents = intents :+ FacultyInfo("engineering")
      }
      if (tokenSet.intersect(Set("business", "informatics", "management" ,"bis")).nonEmpty) {
        intents = intents :+ FacultyInfo("business")
      }
      // Specific check for "Digital Arts & Design"
      if (tokenSet.contains("design") || tokenSet.contains("digital") || tokenSet.contains("arts") || tokenSet.intersect(Set("graphic", "animation")).nonEmpty) {
        intents = intents :+ FacultyInfo("design")
      }
      
      // General faculty info if no specific faculty is mentioned but faculty-related terms are present
      if (intents.filter(_.isInstanceOf[FacultyInfo]).isEmpty && tokenSet.intersect(Set("faculty", "department", "college", "school")).nonEmpty) {
        intents = intents :+ FacultyInfo("general")
      }
    }
    // Contact information patterns
    if (tokenSet.intersect(Set("contact", "phone", "email", "hotline", "call", "reach")).nonEmpty) {
      intents = intents :+ ContactInfo("general")
    }
    // Results guidance patterns
    if (tokenSet.intersect(Set("results", "grades", "marks", "scores", "transcript")).nonEmpty) {
      intents = intents :+ ResultsInfo("general")
    }
    // Study plan patterns
    if (tokenSet.intersect(Set("study", "plan", "curriculum", "courses", "prerequisites", "program")).nonEmpty) {
      intents = intents :+ StudyPlanInfo("general")
    }
    // GPA calculation patterns
    if (tokenSet.intersect(Set("gpa", "calculate", "grade", "point", "average")).nonEmpty) {
      intents = intents :+ GPAQuery
    }
    // Analytics patterns
    if (tokenSet.intersect(Set("analytics", "statistics", "stats", "interactions", "log")).nonEmpty) {
      intents = intents :+ AnalyticsQuery
    }

    if (intents.isEmpty) {
      List(Fallback)
    } else {
      intents
    }
  }
  
  // Response generation function
  def generateResponse(intent: Intent, chatState: ChatState): String = {
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
               |Programs: ${info("programs").asInstanceOf[List[String]].mkString(", ")}
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
      
      case ContactInfo(infoType) =>
        (if (chatState.language == "arabic") KnowledgeBaseArabic else KnowledgeBase).contactInfo.get(infoType) match {
          case Some(info) =>
            val label = (if (chatState.language == "arabic") KnowledgeBaseArabic else KnowledgeBase).contactInfo.getOrElse(s"${infoType}_label", infoType.replace("_", " "))
            s"${(if (chatState.language == "arabic") KnowledgeBaseArabic else KnowledgeBase).contactInfo("title")}\n$label: $info"
          case None => (if (chatState.language == "arabic") KnowledgeBaseArabic else KnowledgeBase).resultsInfo("not_found")
        }
      
      case ResultsInfo(infoType) =>
        (if (chatState.language == "arabic") KnowledgeBaseArabic else KnowledgeBase).resultsInfo.get(infoType) match {
          case Some(info) => s"${(if (chatState.language == "arabic") KnowledgeBaseArabic else KnowledgeBase).resultsInfo("title")}\n${info}"
          case None => (if (chatState.language == "arabic") KnowledgeBaseArabic else KnowledgeBase).resultsInfo("not_found")
        }
      
      case StudyPlanInfo(infoType) =>
        (if (chatState.language == "arabic") KnowledgeBaseArabic else KnowledgeBase).studyPlanInfo.get(infoType.toString) match {
          case Some(studyInfo) =>
            (if (chatState.language == "arabic") KnowledgeBaseArabic else KnowledgeBase).studyPlanInfo("title") + "\n" + studyInfo.toString
          case None => (if (chatState.language == "arabic") KnowledgeBaseArabic else KnowledgeBase).resultsInfo("not_found")
        }
      
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
      .map(message => ChatService.classifyIntent(ChatService.normalize(ChatService.tokenize(message.userText))))
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

