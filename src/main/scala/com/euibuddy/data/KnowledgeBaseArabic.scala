package com.euibuddy.data

object KnowledgeBaseArabic extends KnowledgeBaseTrait {

  val disclaimer = "(يرجى مراجعة صفحات الجامعة الرسمية؛ قد تتغير التفاصيل.)"

  val campusInfo: Map[String, String] = Map(
    "location" -> "يقع حرم الجامعة المصرية للتعلم الإلكتروني (EUI) في مدينة المعرفة، العاصمة الإدارية الجديدة، مصر.",
    "address" -> "مدينة المعرفة، العاصمة الإدارية الجديدة، القاهرة، مصر",
    "main_building" -> "يضم المبنى الرئيسي للجامعة جميع الكليات والمكاتب الإدارية."
  )

  val facultyInfo: Map[String, Map[String, Any]] = Map(
    "cis" -> Map(
      "name" -> "كلية الحاسبات ونظم المعلومات",
      "location" -> "مبنى أ، الطوابق 2-4",
      "programs" -> List("علوم الحاسب", "نظم المعلومات", "هندسة البرمجيات"),
      "dean" -> "د. أحمد حسن",
      "contact" -> "cis@eui.edu.eg"
    ),
    "engineering" -> Map(
      "name" -> "كلية الهندسة",
      "location" -> "مبنى ب، الطوابق 1-3",
      "programs" -> List("الهندسة المدنية", "الهندسة الميكانيكية", "الهندسة الكهربائية"),
      "dean" -> "د. محمد علي",
      "contact" -> "engineering@eui.edu.eg"
    ),
    "business" -> Map(
      "name" -> "كلية معلوماتية الأعمال",
      "location" -> "مبنى ج، الطابق 2",
      "programs" -> List("إدارة الأعمال", "نظم معلومات إدارية", "التجارة الإلكترونية"),
      "dean" -> "د. سارة أحمد",
      "contact" -> "business@eui.edu.eg"
    ),
    "design" -> Map(
      "name" -> "كلية الفنون الرقمية والتصميم",
      "location" -> "مبنى د، الطوابق 1-2",
      "programs" -> List("التصميم الجرافيكي", "الوسائط الرقمية", "الرسوم المتحركة", "تصميم الألعاب"),
      "dean" -> "د. ليلى إبراهيم",
      "contact" -> "design@eui.edu.eg"
    )
  )

  val contactInfo: Map[String, String] = Map(
    "main_hotline" -> "+20 2 1234 5678",
    "admissions" -> "admissions@eui.edu.eg",
    "student_affairs" -> "studentaffairs@eui.edu.eg",
    "academic_office" -> "academic@eui.edu.eg",
    "website" -> "https://www.eui.edu.eg",
    "emergency" -> "+20 2 1234 9999"
  )

  val studyPlanInfo: Map[String, String] = Map(
    "structure" -> "تتبع معظم البرامج هيكلًا مدته 4 سنوات مع 8 فصول دراسية (خريف/ربيع كل عام).",
    "prerequisites" -> "المتطلبات الأساسية هي المقررات التي يجب إكمالها قبل التسجيل في المقررات المتقدمة. تحقق من دليل برنامجك للمتطلبات المحددة.",
    "credit_hours" -> "تتطلب معظم برامج البكالوريوس 120-140 ساعة معتمدة للتخرج.",
    "advisor_contact" -> "اتصل بمرشدك الأكاديمي من خلال المكتب الأكاديمي للحصول على إرشادات مخصصة لخطة الدراسة."
  )

  val resultsInfo: Map[String, String] = Map(
    "access" -> "يمكنك الوصول إلى نتائجك الرسمية من خلال بوابة الطالب بجامعة EUI على portal.eui.edu.eg",
    "discrepancies" -> "في حالة وجود أي اختلافات في الدرجات، يرجى الاتصال بالمكتب الأكاديمي في غضون أسبوعين من تاريخ إعلان النتائج.",
    "transcripts" -> "يمكن طلب السجلات الرسمية من مكتب المسجل.",
    "gpa_policy" -> "يتم حساب المعدل التراكمي (GPA) باستخدام مقياس 4.0. الحد الأدنى للمعدل التراكمي للتخرج هو 2.0."
  )
}