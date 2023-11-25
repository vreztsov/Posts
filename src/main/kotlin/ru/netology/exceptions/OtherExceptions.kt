package ru.netology.exceptions

class CommentException(message: String): RuntimeException(message)

class WrongCodeOfComparatorException(message: String): RuntimeException(message)