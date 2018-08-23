package `in`.rgpvnotes.alert.myresource.model

import java.io.Serializable

data class MyClassModel(


        var classId: String? = null,
        var facultyId: String? = null,
        var facultyName: String? = null,
        var subjectCode: String? = null,
        var subjectName: String? = null


) : Serializable {

    constructor():this(null,null,null,null,null)

}