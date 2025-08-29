package com.euibuddy.data

trait KnowledgeBaseTrait {
  val disclaimer: String
  val campusInfo: Map[String, String]
  val facultyInfo: Map[String, Map[String, Any]]
  val contactInfo: Map[String, String]
  val studyPlanInfo: Map[String, String]
  val resultsInfo: Map[String, String]
}