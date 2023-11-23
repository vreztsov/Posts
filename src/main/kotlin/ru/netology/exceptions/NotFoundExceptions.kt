package ru.netology.exceptions

class PostNotFoundException(message: String) : RuntimeException(message)

class ReasonNotFoundException(message: String) : RuntimeException(message)

class CommentNotFoundException(message: String) : RuntimeException(message)