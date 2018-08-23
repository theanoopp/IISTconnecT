package `in`.rgpvnotes.alert.myresource.model

import android.net.Uri

data class GiveWorkModel(

        var uri: Uri? = null,
        var name: String? = null){


    constructor():this(null,null)

}