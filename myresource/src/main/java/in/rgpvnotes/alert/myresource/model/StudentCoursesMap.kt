package `in`.rgpvnotes.alert.myresource.model

import java.io.Serializable

data class StudentCoursesMap(

        var courseId : String? = null  ,
        var studentId : String? = null

) : Serializable {

    constructor() : this(null,null)

}