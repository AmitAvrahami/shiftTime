# 🚀 תיעוד אלגוריתם השיבוץ האוטומטי - ShiftTime

## 📋 סקירה כללית של מה שנבנה היום

בפיתוח שנעשה היום הושלמה **מערכת אלגוריתמית מתקדמת** לשיבוץ אוטומטי של עובדים למשמרות. המערכת מבוססת על **Backtracking Algorithm** עם מנוע כללים חכם וטיפול באילוצים מורכבים.

---

## 🏗️ ארכיטקטורת המערכת

### 1. **BestEffortBacktrackingAlgorithm** 
**מיקום**: `com.example.shiftime.algorithm.BestEffortBacktrackingAlgorithm`

**תפקיד**: הליבה המרכזית של המערכת - מבצע שיבוץ אוטומטי באמצעות אלגוריתם backtracking מתקדם.

**עקרונות עבודה**:
- מנסה למלא כל משמרת עם העובד הכי מתאים
- אם נתקע, חוזר אחורה ומנסה אפשרויות אחרות
- שומר את הפתרון הטוב ביותר שמצא
- מוכן להשאיר "חורים" במשמרות שלא ניתן למלא

**מאפיינים טכניים**:
```kotlin
class BestEffortBacktrackingAlgorithm(
    private val ruleEngine: SchedulingRuleEngine
) {
    private var bestSolutionSoFar: MutableList<ShiftAssignment>? = null
    private var bestScore = -1
    private var iterationCount = 0
    private val maxIterations = 100000
}
```

### 2. **SchedulingRuleEngine**
**מיקום**: `com.example.shiftime.algorithm.SchedulingRuleEngine`

**תפקיד**: מנוע הכללים החכם - קובע איזה עובד יכול לעבוד באיזה משמרת.

**סוגי כללים**:
- **כללי חובה** (Mandatory Rules): חייבים להתקיים, אחרת השיבוץ אסור
- **כללי העדפה** (Preference Rules): רצוי שיתקיימו, אבל לא חובה

**פונקציות מרכזיות**:
```kotlin
// בדיקה בסיסית - האם מותר לשבץ?
fun canAssignEmployee(): Boolean

// בדיקה מתקדמת - האם השיבוץ גם מועדף?
fun isPreferredAssignment(): Boolean

// חלוקת עובדים לרשימות
fun getPreferredEmployees(): List<Employee>
fun getPossibleEmployees(): List<Employee>
```

### 3. **SchedulingRules**
**מיקום**: `com.example.shiftime.algorithm.SchedulingRules`

**תפקיד**: מימוש הכללים הספציפיים לשיבוץ.

---

## 🎯 כללי השיבוץ שיושמו

### כללי חובה (Mandatory Rules):

#### 1. **NoMultipleShiftsPerDayRule**
```kotlin
// מונע שיבוץ של עובד ליותר ממשמרת אחת באותו יום
// דוגמה: אם עמית עובד בוקר ביום ראשון, הוא לא יכול לעבוד גם צהריים
```

#### 2. **MaxShiftsPerEmployeeRule**
```kotlin
// מוודא שעובד לא חורג ממכסת המשמרות המקסימלית שלו
// דוגמה: אם למשה יש מקסימום 5 משמרות בשבוע, הוא לא יקבל יותר
```

#### 3. **EmployeeConstraintsRule**
```kotlin
// מכבד את האילוצים שהעובד הגדיר במערכת
// דוגמה: אם שרה ציינה שהיא לא יכולה לעבוד ביום רביעי לילה - לא נשבץ אותה
```

### כללי העדפה (Preference Rules):

#### 1. **AvoidConsecutive8to8Rule**
```kotlin
// מונע מצבים של פחות מ-8 שעות מנוחה בין משמרות
// דוגמאות בעייתיות:
// - צהריים (עד 22:45) ← בוקר למחרת (מ-6:45) = 8 שעות בלבד
// - לילה (עד 6:45) ← צהריים (מ-14:45) = 8 שעות בלבד
```

---

## 🔄 זרימת עבודה של האלגוריתם

### שלב 1: הכנת נתונים
```
📊 אסיפת נתונים:
├── רשימת משמרות שלא מולאו
├── רשימת עובדים זמינים  
├── שיבוצים קיימים
└── אילוצי עובדים
```

### שלב 2: תהליך Backtracking
```
🔍 לכל משמרת:
├── 🌟 ראשית: נסה עובדים "מועדפים"
│   └── (עוברים גם כללי חובה גם כללי העדפה)
├── ⚠️  שנית: נסה עובדים "אפשריים" 
│   └── (עוברים רק כללי חובה)
├── 🕳️  שלישית: השאר "חור" במשמרת
└── 🔙 אם תקוע - תחזור אחורה ונסה מסלול אחר
```

### שלב 3: יצירת תוצאה
```
🎯 תוצאות אפשריות:
├── Perfect: כל המשמרות מולאו ✨
├── BestEffort: חלק מהמשמרות מולאו + רשימת "חורים" 📊
└── Failed: לא הצליח למלא אף משמרת ❌
```

---

## 📊 מודלי נתונים

### SchedulingResult
```kotlin
sealed class SchedulingResult {
    data class Perfect(
        val message: String,
        val assignments: List<ShiftAssignment>,
        val statistics: ScheduleStatistics,
        val holes: List<ShiftHole>
    ) : SchedulingResult()

    data class BestEffort(
        val message: String,
        val assignments: List<ShiftAssignment>,
        val statistics: ScheduleStatistics,
        val holes: List<ShiftHole>,
        val holesCount: Int
    ) : SchedulingResult()

    data class Failed(
        val message: String
    ) : SchedulingResult()
}
```

### ShiftHole (חור במשמרת)
```kotlin
data class ShiftHole(
    val shift: Shift,
    val reason: String,
    val suggestedActions: List<String>
)
```

### ScheduleStatistics
```kotlin
data class ScheduleStatistics(
    val totalShiftsToFill: Int,
    val shiftsSuccessfullyFilled: Int,
    val totalEmployeesInvolved: Int,
    val remainingUnfilledShifts: Int,
    val algorithmScore: Double,
    val executionTimeMs: Long
)
```

---

## 🎮 דוגמאות שימוש

### דוגמה 1: הרצת אלגוריתם בסיסי
```kotlin
// יצירת מנוע כללים
val constraints = getEmployeeConstraints() // מהמסד נתונים
val ruleEngine = SchedulingRuleEngine(constraints)

// יצירת אלגוריתם
val algorithm = BestEffortBacktrackingAlgorithm(ruleEngine)

// הכנת נתונים
val schedulingData = SchedulingData(
    shifts = allShifts,
    employees = availableEmployees,
    existingAssignments = currentAssignments
)

// הרצה!
val result = algorithm.assignShifts(schedulingData)

// טיפול בתוצאה
when (result) {
    is SchedulingResult.Perfect -> {
        println("🎉 ${result.message}")
        saveAssignments(result.assignments)
    }
    is SchedulingResult.BestEffort -> {
        println("✅ ${result.message}")
        saveAssignments(result.assignments)
        showHolesToManager(result.holes)
    }
    is SchedulingResult.Failed -> {
        println("❌ ${result.message}")
    }
}
```

### דוגמה 2: בדיקת כללים ידנית
```kotlin
val ruleEngine = SchedulingRuleEngine(constraints)

// בדיקה האם עמית יכול לעבוד ביום ראשון בוקר
val canAssign = ruleEngine.canAssignEmployee(
    employee = amit,
    shift = sundayMorningShift,
    currentAssignments = existingAssignments,
    allShifts = allShifts
)

if (!canAssign) {
    val reasons = ruleEngine.getViolationReasons(amit, sundayMorningShift, existingAssignments, allShifts)
    println("❌ לא ניתן לשבץ את עמית:")
    reasons.forEach { println("   • $it") }
}
```

---

## 🎯 הישגים טכניים

### ✅ מה הושג היום:

1. **אלגוריתם Backtracking מלא** - מסוגל למצוא פתרונות אופטימליים
2. **מנוע כללים גמיש** - קל להוסיף כללים חדשים  
3. **טיפול בחורים** - לא נכשל כשלא ניתן למלא הכל
4. **סטטיסטיקות מפורטות** - מידע שימושי למנהלים
5. **לוגינג מפורט** - קל לדבג ולהבין מה קורה
6. **הפרדת אחריות ברורה** - כל קלאס עם תפקיד ספציפי

### 🏆 נקודות חוזק:

- **גמישות**: מנוע הכללים מאפשר להוסיף בקלות כללים חדשים
- **ביצועים**: מגבלת 100,000 איטרציות מונעת תקיעות אינסופיות  
- **אמינות**: אלגוריתם Best-Effort מבטיח תוצאה אפילו במקרים קשים
- **שקיפות**: לוגינג מפורט לכל שלב בתהליך
- **תחזוקה**: קוד נקי ומובנה לפי עקרונות Clean Architecture

---

## 🚧 שלבים הבאים (מה שנדרש להשלמה)

### 1. **UseCase לאינטגרציה**
```kotlin
// צריך ליצור: CreateAutoScheduleUseCase
// תפקיד: לחבר בין השכבות ולהפעיל את האלגוריתם
```

### 2. **חיבור ל-ViewModel**
```kotlin
// הוספת פונקציה ל-ShiftViewModel:
// fun generateAutoSchedule(assignmentStyle: AssignmentStyle)
```

### 3. **UI להצגת תוצאות**
```kotlin
// יצירת דיאלוג/מסך להצגת תוצאות השיבוץ
// SchedulingResultDialog.kt
```

### 4. **טיפול בסגנונות שיבוץ**
```kotlin
// יישום ההבדלים בין:
// - BALANCED (מאוזן)
// - EMPLOYEE_PREFERENCE (העדפות עובדים)  
// - EFFICIENCY (יעילות מקסימלית)
```

### 5. **טסטים ובדיקות**
```kotlin
// כתיבת Unit Tests לכל הכללים
// טסטים לאלגוריתם עם מקרי קצה
```

---

## 📈 מדדי איכות ושיפורים אפשריים

### ביצועים נוכחיים:
- **מגבלת איטרציות**: 100,000 (מונע תקיעות)
- **זיכרון**: שמירת פתרון טוב ביותר בלבד
- **מורכבות**: O(n!) במקרה הגרוע (backtracking)

### שיפורים עתידיים:
- **הוריסטיקות**: סידור חכם של משמרות לפי קושי
- **Pruning**: זיהוי מוקדם של ענפים חסרי תועלת
- **פתרונות מקבילים**: חלוקת העבודה בין threads
- **מטמון**: שמירת תוצאות בדיקות חוזרות

---

## 🎉 סיכום

היום הושלמה בהצלחה **הליבה האלגוריתמית** של מערכת השיבוץ האוטומטי ב-ShiftTime. 

המערכת מדגימה:
- **עיצוב אלגוריתמי מתקדם** עם backtracking
- **הפרדת אחריות נקייה** בין רכיבים
- **גמישות ומודולריות** להוספת תכונות
- **טיפול בבעיות אמיתיות** (חורים, אילוצים, אופטימיזציה)

**זהו בסיס חזק ומקצועי** שמוכן להשלמה ולאינטגרציה עם שאר המערכת! 🚀

---

*תיעוד נוצר על ידי: עמית אברהמי*  
*תאריך: יוני 2025*  
*פרויקט: ShiftTime - מערכת ניהול משמרות*
