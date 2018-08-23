package `in`.rgpvnotes.alert.myresource.model

import java.util.*

data class NotesModel(

        var noteId: String? = null,
        var title: String? = null,
        var branch: String? = null,
        var semester: String? = null,
        var year: String? = null,
        var subjectCode: String? = null,
        var subjectName: String? = null,
        var uploadDate: Date? = null,
        var fileSize: Long = 0,
        var byName: String? = null,
        var byId: String? = null,
        var verifiedBy: String? = null,
        var verified: Boolean = false,
        var version: Int = 0,
        var fileUrl: String? = null

) {

        constructor():this(null,null,null,null,null,null,null,null,0,null,null,null,false,0,null)

}