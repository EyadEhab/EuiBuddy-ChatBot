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
      .map { token =>
        // Map Arabic commands to English equivalents
        token match {
          case "خروج" | "انهاء" | "انتهيت" => "quit"
          case "تغييراللغة" | "تغيير_اللغة" | "تغييرلغة" => "change_language"
          case "مرحبا" | "اهلا" | "السلامعليكم" | "السلام_عليكم" => "hi"
          case "حاسبات" | "كليةالحاسبات" | "كلية_الحاسبات" => "cis"
          case "تجارة" | "كليةالتجارة" | "كلية_التجارة" => "business"
          case "هندسة" | "كليةالهندسة" | "كلية_الهندسة" => "engineering"
          case "تصميم" | "فنون" | "فنونتصميم" => "design"
          case _ => token
        }
      }
  }
  
  // Intent classification function
  def classifyIntent(normalizedTokens: List[String]): List[Intent] = {
    val tokenSet = normalizedTokens.toSet
    var intents = List.empty[Intent]

    // Greeting patterns
    if (tokenSet.intersect(Set("hello", "hi", "hey", "greetings", "good", "مرحبا", "اهلا", "السلامعليكم")).nonEmpty) {
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
        tokenSet.intersect(Set("cis", "engineering", "business", "design", "informatics", "art", "digital", "bis", "management", "حاسبات", "تجارة", "هندسة", "تصميم")).nonEmpty) {

      if (tokenSet.contains("cis") || tokenSet.contains("computer") || tokenSet.intersect(Set("information", "systems", "حاسبات")).nonEmpty) {
        intents = intents :+ FacultyInfo("cis")
      }
      if (tokenSet.contains("engineering") || tokenSet.intersect(Set("civil", "mechanical", "electrical", "هندسة")).nonEmpty) {
        intents = intents :+ FacultyInfo("engineering")
      }
      if (tokenSet.intersect(Set("business", "informatics", "management", "bis", "تجارة")).nonEmpty) {
        intents = intents :+ FacultyInfo("business")
      }
      // Specific check for "Digital Arts & Design"
      if (tokenSet.contains("design") || tokenSet.contains("digital") || tokenSet.contains("arts") || tokenSet.intersect(Set("graphic", "animation", "تصميم")).nonEmpty) {
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
    val knowledgeBase = if (chatState.language == "arabic") KnowledgeBaseArabic else KnowledgeBase
    
    intent match {
      case Greeting =>
        if (chatState.language == "arabic") {
          "مرحباً! أنا EUIBuddy، مساعدك الجامعي. يمكنني مساعدتك بمعلومات عن كليات EUI، موقع الحرم الجامعي، جهات الاتصال، خطط الدراسة، حساب المعدل التراكمي، والمزيد. كيف يمكنني مساعدتك اليوم؟"
        } else {
          "Hello! I'm EUIBuddy, your campus assistant. I can help you with information about EUI faculties, campus location, contacts, study plans, GPA calculations, and more. How can I assist you today?"
        }
      
      case Help =>
        if (chatState.language == "arabic") {
          """يمكنني مساعدتك في:
            |• موقع الحرم الجامعي والاتجاهات
            |• معلومات الكليات (الحاسبات، الهندسة، معلوماتية الأعمال، الفنون الرقمية والتصميم)
            |• معلومات الاتصال والقنوات الرسمية
            |• إرشادات خطة الدراسة والمتطلبات الأساسية
            |• معلومات النتائج والسجلات الرسمية
            |• حساب المعدل التراكمي
            |• تحليلات التفاعل
            |
            |فقط اسألني عن أي من هذه المواضيع!""".stripMargin
        } else {
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
        }
      
      case CampusLocation =>
        if (chatState.language == "arabic") {
          s"${knowledgeBase.campusInfo("location")} ${knowledgeBase.campusInfo("address")} ${knowledgeBase.disclaimer}"
        } else {
          s"${knowledgeBase.campusInfo("location")} ${knowledgeBase.campusInfo("address")} ${knowledgeBase.disclaimer}"
        }
      
      case FacultyInfo(facultyName) =>
        knowledgeBase.facultyInfo.get(facultyName) match {
          case Some(info) =>
            if (chatState.language == "arabic") {
              s"""الكلية: ${info("name")}
                 |الموقع: ${info("location")}
                 |البرامج: ${info("programs").asInstanceOf[List[String]].mkString(", ")}
                 |العميد: ${info("dean")}
                 |الاتصال: ${info("contact")}
                 |${knowledgeBase.disclaimer}""".stripMargin
            } else {
              s"""Faculty: ${info("name")}
                 |Location: ${info("location")}
                 |Programs: ${info("programs").asInstanceOf[List[String]].mkString(", ")}
                 |Dean: ${info("dean")}
                 |Contact: ${info("contact")}
                 |${knowledgeBase.disclaimer}""".stripMargin
            }
          case None =>
            if (chatState.language == "arabic") {
              s"""جامعة EUI لديها أربع كليات رئيسية:
                 |• كلية الحاسبات ونظم المعلومات (CIS)
                 |• كلية الهندسة
                 |• كلية معلوماتية الأعمال
                 |• كلية الفنون الرقمية والتصميم
                 |${knowledgeBase.disclaimer}""".stripMargin
            } else {
              s"""EUI has four main faculties:
                 |• Computer and Information Systems (CIS)
                 |• Engineering
                 |• Business Informatics
                 |• Digital Arts & Design
                 |${knowledgeBase.disclaimer}""".stripMargin
            }
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
        if (chatState.language == "arabic") {
          """مساعدة حساب المعدل التراكمي:
            |لحساب معدلك التراكمي، أدخل مقرراتك بهذا التنسيق:
            |اسم المقرر، الساعات المعتمدة، الدرجة (مثال: "رياضيات101، 3، A")
            |
            |الدرجات المدعومة: ممتاز (A)، جيد جداً (B)، جيد (C)، مقبول (D)، راسب (F)
            |مقياس الدرجات: ممتاز = 4.0، جيد جداً = 3.0، جيد = 2.0، مقبول = 1.0، راسب = 0.0
            |
            |أدخل مقرراتك واحداً تلو الآخر، أو اكتب 'done' عند الانتهاء.""".stripMargin
        } else {
          """GPA Calculation Help:
            |To calculate your GPA, provide your courses in this format:
            |Course Name, Credits, Grade (e.g., "Math101, 3, A")
            |
            |Supported grades: A+, A, A-, B+, B, B-, C+, C, C-, D+, D, F
            |Grade scale: A+ = 4.0, A = 3.7, A- = 3.4, B+ = 3.2, B = 3.0, B- = 2.8, C+ = 2.6, C = 2.4, C- = 2.2, D+ = 2.0, D = 1.5,D- = 1.0, F = 0.0
            |
            |Enter your courses one by one, or type 'done' when finished.""".stripMargin
        }
      
      case AnalyticsQuery =>
        if (chatState.language == "arabic") {
          "سيعرض ميزة التحليلات إحصائيات التفاعل. هذا يتطلب معالجة سجل التفاعلات."
        } else {
          "Analytics feature will show interaction statistics. This requires processing the interaction log."
        }
      
      case Fallback =>
        if (chatState.language == "arabic") {
          "أعتذر، لم أفهم سؤالك. يمكنني مساعدتك بمعلومات عن الحرم الجامعي، تفاصيل الكليات، جهات الاتصال، خطط الدراسة، حساب المعدل التراكمي، والتحليلات. اكتب 'help' لترى ما يمكنني فعله!"
        } else {
          "I'm not sure I understand. I can help with campus information, faculty details, contacts, study plans, GPA calculations, and analytics. Type 'help' to see what I can do!"
        }
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
        
        // Map Arabic grades to English equivalents
        val mappedGrade = grade match {
          case "ممتاز" => "A"
          case "جيدجدا" | "جيد_جدا" => "B"
          case "جيد" => "C"
          case "مقبول" => "D"
          case "راسب" => "F"
          case _ => grade
        }
        
        if (GPAFormula.gradeToPoints.contains(mappedGrade) && credits > 0) {
          Some(Course(name, credits, mappedGrade))
        } else {
          None
        }
      } catch {
        case _: NumberFormatException => None
      }
    } else {
      None
    }
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

