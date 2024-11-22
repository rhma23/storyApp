package com.dicoding.storyapp.response

import java.io.Serializable

data class Response(
	val error: Boolean? = null,
	val message: String? = null,
	val story: Story? = null
): Serializable

data class Story(
	val photoUrl: String? = null,
	val createdAt: String? = null,
	val name: String? = null,
	val description: String? = null,
	val lon: String? = null,
	val id: String? = null,
	val lat: String? = null
): Serializable
