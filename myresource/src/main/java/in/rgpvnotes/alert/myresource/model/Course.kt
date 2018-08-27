package `in`.rgpvnotes.alert.myresource.model

import java.io.Serializable

data class Course(

        var courseName: String? = null,
        var courseBrief: String? = null,
        var location: String? = null,
        var facultyName: String? = null,
        var facultyId: String? = null,
        var facultyEmail: String? = null,
        var courseId: String? = null

) : Serializable

{

    constructor() : this(null,null,null,null,null,null,null)

}