package com.lind.vladimir.gallery

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.io.Serializable

@JsonIgnoreProperties(ignoreUnknown = true)
data class Image(var id: String? = null,
                 var description: String? = null,
                 var instagram_username: String? = null,
                 var twitter_username: String? = null,
                 var portfolio_url: String? = null,
                 var color: String? = null,
                 var urls: Urls? = null) : Serializable

data class Urls(
        var raw: String? = null,
        var localCoverPath: String? = null,
        var full: String? = null,
        var regular: String? = null,
        var small: String? = null,
        var thumb: String? = null): Serializable