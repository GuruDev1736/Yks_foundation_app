package com.taskease.yksfoundation.Model.ResponseModel

data class GetAllPostResponseModel(
    val CONTENT: AllContent,
    val MSG: String,
    val STS: String
)

data class AllContent(
    val content: List<GetAllPost>,
    val empty: Boolean,
    val first: Boolean,
    val last: Boolean,
    val number: Int,
    val numberOfElements: Int,
    val pageable: Pageable,
    val size: Int,
    val sort: SortX,
    val totalElements: Int,
    val totalPages: Int
)

data class GetAllPost(
    val content: String,
    val createdDate: List<Int>,
    val enabled: Boolean,
    val id: Int,
    val imageUrls: List<String>,
    val likeCount: Int,
    val likedBy: List<Int>,
    val postImage: String,
    val savedBy: List<Int>,
    val superAdmin: Boolean,
    val title: String,
    val updatedDate: Any,
    val user: User
)

data class Pageable(
    val offset: Int,
    val pageNumber: Int,
    val pageSize: Int,
    val paged: Boolean,
    val sort: SortX,
    val unpaged: Boolean
)

data class SortX(
    val empty: Boolean,
    val sorted: Boolean,
    val unsorted: Boolean
)