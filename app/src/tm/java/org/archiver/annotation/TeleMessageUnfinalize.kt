package org.archiver.annotation

@Target(allowedTargets = [AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.FIELD, AnnotationTarget.CONSTRUCTOR])
annotation class TeleMessageUnfinalize()
