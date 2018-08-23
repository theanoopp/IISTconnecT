package `in`.rgpvnotes.alert.myresource.model

import java.io.Serializable
import java.util.*

data class Module(

        var courseName: String? = null,
        var courseBrief: String? = null,
        var location: String? = null,
        var startDate: Date? = null,
        var endDate: Date? = null,
        var facultyName: String? = null,
        var facultyEmail: String? = null

        ) : Serializable {

    constructor():this(null,null,null,null,null,null,null)

}