package `in`.rgpvnotes.alert.myresource.model

import java.util.*

data class AttendanceModel(

        var studentName: String? = null,
        var enrollmentNumber: String? = null,
        var present: Boolean = false,
        var date: Date? = null,
        var classId: String? = null,
        var subjectCode: String? = null,
        var subjectName: String? = null,
        var semester: String? = null,
        var facultyId: String? = null,
        var facultyName: String? = null,
        var timeSlot: String? = null) {

    constructor():this(null,null,false,null,null,null,null,null,null,null,null)
}