package `in`.rgpvnotes.alert.myresource.model

import java.io.Serializable

data class StudentModel(

        var studentName: String? = null,
        var studentEmail: String? = null,
        var studentBranch: String? = null,
        var profileImage: String? = null,
        var enrollmentNumber: String? = null,
        var batch: String? = null,
        var verified: String? = null,
        var section: String? = null,
        var currentYear: String? = null,
        var classId: String? = null,
        var currentSemester: String? = null,
        var program: String? = null): Serializable {

    constructor():this(null,null,null,null,null,null,null,null,null,null,null,null)

}