package `in`.rgpvnotes.alert.myresource.model

import java.io.Serializable
import java.util.Date

data class AssignmentModel(

        var title: String? = null,
        var description: String? = null,
        var type: String? = null,
        var questions: String? = null,
        var submitDate: Date? = null,
        var subjectCode: String? = null,
        var subjectName: String? = null,
        var byName: String? = null,
        var byId: String? = null,
        var classId: String? = null,
        var files: ArrayList<String>? = null) : Serializable {


    constructor():this(null,null,null,null,null,null,null,null,null,null,null)
}