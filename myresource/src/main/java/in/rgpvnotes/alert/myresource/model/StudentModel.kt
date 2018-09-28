package `in`.rgpvnotes.alert.myresource.model

import java.io.Serializable

data class StudentModel(

        var studentName: String? = null,
        var studentEmail: String? = null,
        var studentBranch: String? = null,
        var studentId: String? = null,
        var profileImage: String? = null,
        var enrollmentNumber: String? = null): Serializable {

    constructor():this(null,null,null,null,null,null)

}