package com.euibuddy.data

object KnowledgeBase extends KnowledgeBaseTrait {
  
  val disclaimer = "(Double-check the official EUI pages; details may change.)"
  
  val campusInfo: Map[String, String] = Map(
    "location" -> "EUI campus is located in Knowledge City, NAC (New Administrative Capital), Egypt.",
    "address" -> "Knowledge City, New Administrative Capital, Cairo, Egypt",
    "main_building" -> "The main campus building houses all faculties and administrative offices."
  )
  
  val facultyInfo: Map[String, Map[String, Any]] = Map(
    "cis" -> Map(
      "name" -> "Computer and Information Systems",
      "location" -> "Building A, Floors 2-4",
      "programs" -> List("Computer Science", "Information Systems", "Software Engineering"),
      "dean" -> "Dr. Hoda Mokhtar",
      "contact" -> "cis@eui.edu.eg"
    ),
    "engineering" -> Map(
      "name" -> "Engineering",
      "location" -> "Building B, Floors 1-3",
      "programs" -> List("Civil Engineering", "Mechanical Engineering", "Electrical Engineering"),
      "dean" -> "Dr. Ashraf Mahran",
      "contact" -> "engineering@eui.edu.eg"
    ),
    "business" -> Map(
      "name" -> "Business Informatics",
      "location" -> "Building C, Floor 2",
      "programs" -> List("Business Administration", "Management Information Systems", "E-Commerce"),
      "dean" -> "Dr. Mohamed Saleh",
      "contact" -> "business@eui.edu.eg"
    ),
    "design" -> Map(
      "name" -> "Digital Arts & Design",
      "location" -> "Building D, Floors 1-2",
      "programs" -> List("Graphic Design", "Digital Media", "Animation", "Game Design"),
      "dean" -> "Dr. Ashraf Zaki",
      "contact" -> "design@eui.edu.eg"
    )
  )
  
  val contactInfo: Map[String, String] = Map(
    "title" -> "EUI Contact Information",
    "main_hotline_label" -> "Main Hotline",
    "main_hotline" -> "+20 2 1234 5678",
    "admissions_label" -> "Admissions",
    "admissions" -> "admissions@eui.edu.eg",
    "student_affairs_label" -> "Student Affairs",
    "student_affairs" -> "studentaffairs@eui.edu.eg",
    "academic_office_label" -> "Academic Office",
    "academic_office" -> "academic@eui.edu.eg",
    "website_label" -> "Website",
    "website" -> "https://www.eui.edu.eg",
    "emergency_label" -> "Emergency",
    "emergency" -> "+20 2 1234 9999"
  )
  
  val studyPlanInfo: Map[String, String] = Map(
    "title" -> "Study Plan Information",
    "structure" -> "Most programs follow a 4-year structure with 8 semesters (Fall/Spring each year).",
    "prerequisites" -> "Prerequisites are courses that must be completed before enrolling in advanced courses. Check your program handbook for specific requirements.",
    "credit_hours" -> "Most bachelor programs require 120-140 credit hours for graduation.",
    "advisor_contact" -> "Contact your academic advisor through the Academic Office for personalized study plan guidance."
  )
  
  val resultsInfo: Map[String, String] = Map(
    "title" -> "Results Information",
    "access" -> "Access your official results through the EUI Student Portal at portal.eui.edu.eg",
    "discrepancies" -> "For grade discrepancies, contact the Academic Office within 2 weeks of result publication.",
    "transcripts" -> "Official transcripts can be requested from the Registrar's Office.",
    "gpa_policy" -> "GPA is calculated using a 4.0 scale. Minimum GPA for graduation is 2.0."
  )
  

}

