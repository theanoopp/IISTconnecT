package `in`.rgpvnotes.alert.myresource.model

import android.net.Uri

data class AssignmentFile(
        var uri: Uri? = null,
        var name: String? = null){

    constructor() : this(null,null)

}