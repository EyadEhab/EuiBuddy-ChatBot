package com.euibuddy

import com.euibuddy.model.*
import com.euibuddy.service.ChatService
import scala.io.StdIn
import scala.annotation.tailrec
import javax.swing.{JOptionPane, JDialog, JFrame, WindowConstants}
// Import specific classes from java.awt to avoid conflicts with scala.collection.immutable.List
import java.awt.{Font, Color, Dimension}
import java.awt.event.*

object Main {
  
  def selectLanguage(): String = {
    // Create a simple GUI dialog for language selection
    val options = Array[Object]("English", "العربية")
    val result = JOptionPane.showOptionDialog(
      null,
      "Please select your preferred language / الرجاء اختيار اللغة المفضلة",
      "Language Selection / اختيار اللغة",
      JOptionPane.DEFAULT_OPTION,
      JOptionPane.QUESTION_MESSAGE,
      null,
      options,
      options(0)
    )
    
    val language = result match {
      case 0 => 
        println("English selected.")
        // Set left-to-right orientation for English
        javax.swing.UIManager.put("OptionPane.componentOrientation", java.awt.ComponentOrientation.LEFT_TO_RIGHT)
        "english"
      case 1 => 
        println("Arabic selected. / تم اختيار اللغة العربية.")
        // Set right-to-left orientation for Arabic
        javax.swing.UIManager.put("OptionPane.componentOrientation", java.awt.ComponentOrientation.RIGHT_TO_LEFT)
        "arabic"
      case _ => 
        println("No selection made. Defaulting to English.")
        // Set left-to-right orientation for English
        javax.swing.UIManager.put("OptionPane.componentOrientation", java.awt.ComponentOrientation.LEFT_TO_RIGHT)
        "english"
    }
    
    // Display a confirmation message in the selected language
    val confirmMessage = language match {
      case "english" => "You selected English."
      case "arabic" => "لقد اخترت اللغة العربية."
    }
    
    JOptionPane.showMessageDialog(
      null,
      confirmMessage,
      "Language Confirmation / تأكيد اللغة",
      JOptionPane.INFORMATION_MESSAGE
    )
    
    language
  }

  def main(args: Array[String]): Unit = {
    // Set console encoding to UTF-8 for proper Arabic text display
    System.setProperty("file.encoding", "UTF-8")
    java.nio.charset.Charset.defaultCharset()
    
    // Set up UI manager to use a font that supports Arabic globally
    // Try to use a font that has good Arabic support
    val arabicFontName = if (Font.getFont("Tahoma") != null) {
      "Tahoma"
    } else if (Font.getFont("Segoe UI") != null) {
      "Segoe UI"
    } else if (Font.getFont("Lucida Sans Unicode") != null) {
      "Lucida Sans Unicode"
    } else {
      "Arial"
    }
    
    val arabicFont = new Font(arabicFontName, Font.PLAIN, 14)
    javax.swing.UIManager.put("OptionPane.messageFont", arabicFont)
    javax.swing.UIManager.put("TextField.font", arabicFont)
    javax.swing.UIManager.put("Button.font", arabicFont)
    javax.swing.UIManager.put("Label.font", arabicFont)
    
    // Enable input method support for Arabic text input
    System.setProperty("java.awt.im.style", "on-the-spot")
    
    // We'll set the component orientation based on the selected language in the selectLanguage method
    
    println("=" * 50)
    println("Welcome to EUIBuddy - Your Campus Assistant!")
    println("=" * 50)
    println("Type 'quit' or 'exit' to end the conversation.")
    println("Type 'analytics' to see interaction statistics.")
    println()

    // Print current encoding information
    println("Current file.encoding: " + System.getProperty("file.encoding"))
    println("Current Charset: " + java.nio.charset.Charset.defaultCharset())
    
    // Test Arabic text display
    println("Testing Arabic text display:")
    println("\u0645\u0631\u062D\u0628\u0627 \u0628\u0643 \u0641\u064A \u062C\u0627\u0645\u0639\u0629 \u0645\u0635\u0631 \u0644\u0644\u0645\u0639\u0644\u0648\u0645\u0627\u062A\u064A\u0629")
    println()

    val selectedLanguage = selectLanguage()
    val initialState = ChatState(List.empty, 0, language = selectedLanguage)
    runChatbot(initialState)
  }

  @tailrec
  def runChatbot(state: ChatState): Unit = {
    // Use GUI dialog for input to properly display Arabic text based on selected language
    val promptMessage = state.language match {
      case "english" => "Enter your message:"
      case "arabic" => "أدخل رسالتك:"
    }
    
    val dialogTitle = state.language match {
      case "english" => "EUIBuddy Chat"
      case "arabic" => "محادثة EUIBuddy"
    }
    
    // Create a custom text field for better Arabic input support
    val textField = new javax.swing.JTextField(30)
    textField.setComponentOrientation(state.language match {
      case "english" => java.awt.ComponentOrientation.LEFT_TO_RIGHT
      case "arabic" => java.awt.ComponentOrientation.RIGHT_TO_LEFT
    })
    
    // Create a custom option pane with the text field
    val optionPane = new JOptionPane(
      promptMessage,
      JOptionPane.QUESTION_MESSAGE,
      JOptionPane.DEFAULT_OPTION,
      null,
      null,
      null
    )
    optionPane.setWantsInput(true)
    optionPane.setInitialSelectionValue("")
    
    // Create a dialog with the option pane
    val dialog = optionPane.createDialog(null, dialogTitle)
    dialog.setVisible(true)
    
    // Get the user input from the option pane
    val userInput = optionPane.getInputValue match {
      case s: String => s
      case _ => null
    }
    
    val optionSelected = optionPane.getValue



    
    // Check for exit conditions
    val shouldExit = (userInput == null && (optionSelected == JOptionPane.CANCEL_OPTION || optionSelected == JOptionPane.CLOSED_OPTION)) || 
                    (userInput != null && (userInput.toLowerCase.trim == "quit" || 
                    userInput.toLowerCase.trim == "exit" || 
                    userInput.toLowerCase.trim == "q" ||
                    userInput.toLowerCase.trim == "خروج" ||
                    userInput.toLowerCase.trim == "انهاء"))
    
    if (shouldExit) {
      val exitMessage = state.language match {
        case "english" => "Thank you for using EUIBuddy! Have a great day!"
        case "arabic" => "شكرا لاستخدامك EUIBuddy! نتمنى لك يوما سعيدا!"
      }
      JOptionPane.showMessageDialog(
        null,
        exitMessage,
        "EUIBuddy",
        JOptionPane.INFORMATION_MESSAGE
    )
    System.exit(0) // Terminate the program
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
          // If GPAQuery is present, display other responses first, then handle GPA
          if (initialCombinedResponse.nonEmpty) {
            JOptionPane.showMessageDialog(
              null,
              initialCombinedResponse,
              "EUIBuddy",
              JOptionPane.INFORMATION_MESSAGE
            )
          }
          
          // Get the initial GPA prompt and state
          val (initialGPAResponse, stateAfterInitialPrompt) = handleGPACalculation(tempState, userInput)
          
          // Display the initial GPA prompt to the user
          JOptionPane.showMessageDialog(
            null,
            initialGPAResponse,
            "EUIBuddy - GPA Calculation",
            JOptionPane.DEFAULT_OPTION
          )
          println()
          
          // Now, start the interactive collection of courses and return its final result
          collectGPACourses(List.empty, stateAfterInitialPrompt)
        } else {
          // If no GPAQuery, just combine all responses
          (initialCombinedResponse, tempState)
        }
        
        // Display response using GUI dialog for proper Arabic text display
        if (response.nonEmpty) {
          JOptionPane.showMessageDialog(
            null,
            response,
            "EUIBuddy",
            JOptionPane.INFORMATION_MESSAGE
          )
        }
        
        newState
      }
    runChatbot(newState)
  }
  
  def handleGPACalculation(state: ChatState, userInput: String): (String, ChatState) = {
    val cumulativeInfo = if (state.cumulativeCourses.nonEmpty) {
      state.language match {
        case "english" => s" (Cumulative: ${state.cumulativeGPA.getOrElse(0.0)} from ${state.cumulativeCourses.length} previous courses)"
        case "arabic" => s" (التراكمي: ${state.cumulativeGPA.getOrElse(0.0)} من ${state.cumulativeCourses.length} مواد سابقة)"
      }
    } else {
      ""
    }

    val initialResponse = state.language match {
      case "english" => 
        s"""GPA Calculation$cumulativeInfo:
           |
           |To calculate your GPA, provide your courses in this format:
           |Course Name, Credits, Grade (e.g., "Math101, 3, A")
           |
           |Supported grades: A+, A, A-, B+, B, B-, C+, C, C-, D+, D, F
           |Grade scale: A+ = 4.0, A = 3.7, A- = 3.4, B+ = 3.2, B = 3.0, B- = 2.8, C+ = 2.6, C = 2.4, C- = 2.2, D+ = 2.0, D = 1.5, D- = 1.0, F = 0.0
           |
           |Enter your courses one by one, or type 'done' when finished.""".stripMargin
      case "arabic" => 
        s"""حساب المعدل التراكمي$cumulativeInfo:
           |
           |لحساب المعدل التراكمي، قم بإدخال المواد بهذه الصيغة:
           |اسم المادة، الساعات المعتمدة، الدرجة (مثال: "Math101, 3, A")
           |
           |الدرجات المدعومة: A+, A, A-, B+, B, B-, C+, C, C-, D+, D, F
           |مقياس الدرجات: A+ = 4.0, A = 3.7, A- = 3.4, B+ = 3.2, B = 3.0, B- = 2.8, C+ = 2.6, C = 2.4, C- = 2.2, D+ = 2.0, D = 1.5, D- = 1.0, F = 0.0
           |
           |أدخل المواد واحدة تلو الأخرى، أو اكتب 'done' عند الانتهاء.""".stripMargin
    }
    
    val updatedState = ChatService.updateState(state, userInput, initialResponse)
    (initialResponse, updatedState)
  }

  @tailrec
  def collectGPACourses(courses: List[Course], currentState: ChatState): (String, ChatState) = {
    // Use GUI dialog for input to properly display Arabic text
    val promptMessage = currentState.language match {
      case "english" => "Enter course (Name, Credits, Grade) or 'done':"
      case "arabic" => "أدخل المادة (الاسم، الساعات المعتمدة، الدرجة) أو 'done':"
    }
    
    // Create a custom option pane with better Arabic input support
    val optionPane = new JOptionPane(
      promptMessage,
      JOptionPane.QUESTION_MESSAGE,
      JOptionPane.DEFAULT_OPTION,
      null,
      null,
      null
    )
    optionPane.setWantsInput(true)
    optionPane.setInitialSelectionValue("")
    optionPane.setComponentOrientation(currentState.language match {
      case "english" => java.awt.ComponentOrientation.LEFT_TO_RIGHT
      case "arabic" => java.awt.ComponentOrientation.RIGHT_TO_LEFT
    })
    
    // Create a dialog with the option pane
    val dialog = optionPane.createDialog(null, "EUIBuddy - GPA Calculation")
    dialog.setVisible(true)
    
    // Get the user input from the option pane
    val courseInput = optionPane.getInputValue match {
      case s: String => s
      case _ => null
    }

    // Check for exit conditions in GPA mode
    val shouldExit = courseInput == null || 
                    courseInput.toLowerCase.trim == "done" ||
                    courseInput.toLowerCase.trim == "exit" ||
                    courseInput.toLowerCase.trim == "quit" ||
                    courseInput.toLowerCase.trim == "q" ||
                    courseInput.toLowerCase.trim == "خروج" ||
                    courseInput.toLowerCase.trim == "انهاء"
    
    if (shouldExit) {
      if (courses.nonEmpty) {
        val (termGPA, cumulativeGPA) = ChatService.calculateCumulativeGPA(courses, currentState.cumulativeCourses)
        
        val gpaResponse = currentState.language match {
          case "english" => 
            s"""GPA Calculation Results:
               |
               |New courses processed: ${courses.length}
               |Term GPA: ${termGPA.getOrElse("N/A")}
               |Cumulative GPA: ${cumulativeGPA.getOrElse("N/A")}
               |Total courses in record: ${currentState.cumulativeCourses.length + courses.length}""".stripMargin
          case "arabic" => 
            s"""نتائج حساب المعدل التراكمي:
               |
               |المواد الجديدة التي تمت معالجتها: ${courses.length}
               |المعدل الفصلي: ${termGPA.getOrElse("غير متاح")}
               |المعدل التراكمي: ${cumulativeGPA.getOrElse("غير متاح")}
               |إجمالي المواد المسجلة: ${currentState.cumulativeCourses.length + courses.length}""".stripMargin
        }
        
        val completionMessage = currentState.language match {
          case "english" => "GPA calculation completed"
          case "arabic" => "تم الانتهاء من حساب المعدل التراكمي"
        }
        
        val finalState = ChatService.updateStateWithGPA(currentState, completionMessage, gpaResponse, courses, cumulativeGPA)
        (gpaResponse, finalState)
      } else {
        val noCoursesResponse = currentState.language match {
          case "english" => "No new courses entered. GPA calculation cancelled."
          case "arabic" => "لم يتم إدخال مواد جديدة. تم إلغاء حساب المعدل التراكمي."
        }
        
        val cancelMessage = currentState.language match {
          case "english" => "GPA calculation cancelled"
          case "arabic" => "تم إلغاء حساب المعدل التراكمي"
        }
        
        val finalState = ChatService.updateState(currentState, cancelMessage, noCoursesResponse)
        (noCoursesResponse, finalState)
      }
    } else {
      ChatService.parseGradeInput(courseInput) match {
        case Some(course) =>
          // Show confirmation message in the selected language
          val confirmMessage = currentState.language match {
            case "english" => s"Added: ${course.name} (${course.credits} credits, ${course.grade})"
            case "arabic" => s"تمت الإضافة: ${course.name} (${course.credits} ساعة معتمدة، ${course.grade})"
          }
          
          JOptionPane.showMessageDialog(
            null,
            confirmMessage,
            "EUIBuddy - GPA Calculation",
            JOptionPane.INFORMATION_MESSAGE
          )
          
          val courseAddedMessage = currentState.language match {
            case "english" => s"Course added: ${course.name}"
            case "arabic" => s"تمت إضافة المادة: ${course.name}"
          }
          
          val newState = ChatService.updateState(currentState, courseInput, courseAddedMessage)
          collectGPACourses(courses :+ course, newState)
        case None =>
          // Show error message in the selected language
          val errorMessage = currentState.language match {
            case "english" => "Invalid format. Please use: Course Name, Credits, Grade (e.g., Math101, 3, A)"
            case "arabic" => "صيغة غير صحيحة. الرجاء استخدام: اسم المادة، الساعات المعتمدة، الدرجة (مثال: Math101, 3, A)"
          }
          
          JOptionPane.showMessageDialog(
            null,
            errorMessage,
            "EUIBuddy - GPA Calculation",
            JOptionPane.ERROR_MESSAGE
          )
          
          collectGPACourses(courses, currentState)
      }
    }
  }
}

