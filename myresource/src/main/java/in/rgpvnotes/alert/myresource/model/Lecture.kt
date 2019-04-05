package `in`.rgpvnotes.alert.myresource.model

import java.io.Serializable
import java.util.Date

data class Lecture(

        var lectureId: String? = null,
        var courseId: String? = null,
        var timestamp: Date? = null,
        var facultyId: String? = null,
        val timeSlot : String? = null,
        val remark : String? = null,
        val topic :  String ? = null,
        val active : Boolean = false

): Serializable {

    constructor() : this(null,null,null,null,null,null,null,false)
}