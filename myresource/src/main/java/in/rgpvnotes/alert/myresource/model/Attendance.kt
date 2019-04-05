package `in`.rgpvnotes.alert.myresource.model

import java.util.*

data class Attendance(

        var courseId :String ? = null,
        var lectureId :String ? = null,
        var studentId :String ? = null,
        var timestamp :Date ? = null

) {

    constructor():this(null,null,null,null)

    override fun hashCode(): Int {
        var hash = 3
        hash = 43 * hash + Objects.hashCode(this.studentId)
        return hash
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null) {
            return false
        }
        if (javaClass != other.javaClass) {
            return false
        }
        val attendance = other as Attendance?
        return Objects.equals(this.studentId, attendance!!.studentId)
    }
}