package com.hanmajid.yggr.androidx.appsearch

import androidx.appsearch.annotation.Document
import androidx.appsearch.app.AppSearchSchema

@Document
data class Note(

    // Required field for a document class. All documents MUST have a namespace.
    @Document.Namespace
    val namespace: String,

    // Required field for a document class. All documents MUST have an Id.
    @Document.Id
    val id: String,

    // Optional field for a document class, used to set the score of the
    // document. If this is not included in a document class, the score is set
    // to a default of 0.
    @Document.Score
    val score: Int,

    // Optional field for a document class, used to index a note's text for this
    // document class.
    @Document.StringProperty(indexingType = AppSearchSchema.StringPropertyConfig.INDEXING_TYPE_PREFIXES)
    val text: String
)