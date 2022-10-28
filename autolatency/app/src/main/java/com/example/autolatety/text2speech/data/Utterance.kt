package com.example.autolatety.text2speech.data

import java.io.Serializable

data class Utterance(var content: String): Serializable {
    var contentDisplay = "\"" + content + "\""
}