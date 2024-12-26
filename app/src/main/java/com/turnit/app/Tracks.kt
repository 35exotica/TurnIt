package com.turnit.app

import com.backendless.files.BackendlessFile

data class Tracks(
    var name: String? = null,
    var creator: String? = null,
    var coverImage: BackendlessFile? = null,
    var music: BackendlessFile? = null
)
