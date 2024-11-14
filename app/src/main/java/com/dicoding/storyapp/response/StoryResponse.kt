package com.dicoding.storyapp.response

import android.os.Parcel
import android.os.Parcelable

data class StoryResponse(
	val listStory: List<ListStoryItem?>? = null,
	val error: Boolean? = null,
	val message: String? = null
)

data class ListStoryItem(
	val photoUrl: String? = null,
	val createdAt: String? = null,
	val name: String? = null,
	val description: String? = null,
	val lon: Any? = null,
	val id: String? = null,
	val lat: Any? = null
) : Parcelable {

	constructor(parcel: Parcel) : this(
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readValue(Any::class.java.classLoader),
		parcel.readString(),
		parcel.readValue(Any::class.java.classLoader)
	)

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(photoUrl)
		parcel.writeString(createdAt)
		parcel.writeString(name)
		parcel.writeString(description)
		parcel.writeValue(lon)
		parcel.writeString(id)
		parcel.writeValue(lat)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<ListStoryItem> {
		override fun createFromParcel(parcel: Parcel): ListStoryItem {
			return ListStoryItem(parcel)
		}

		override fun newArray(size: Int): Array<ListStoryItem?> {
			return arrayOfNulls(size)
		}
	}
}
