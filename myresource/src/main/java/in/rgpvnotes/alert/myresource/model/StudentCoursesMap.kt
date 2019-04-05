package `in`.rgpvnotes.alert.myresource.model

import java.io.Serializable
import java.util.*

data class StudentCoursesMap(

        var courseId : String? = null  ,
        var studentId : String? = null

) : Serializable {

    constructor() : this(null,null)


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
        val attendance = other as StudentCoursesMap?
        return Objects.equals(this.studentId, attendance!!.studentId)
    }

}