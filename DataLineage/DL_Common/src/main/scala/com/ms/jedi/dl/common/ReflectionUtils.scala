package com.ms.jedi.dl.common

import scala.reflect.runtime.{universe => ru}

/**
  * Reflection utils
  */
object ReflectionUtils {

  private val mirror: ru.Mirror = ru.runtimeMirror(getClass.getClassLoader)

  /**
    * Lists all direct sub-classes of the given trait T
    *
    * @tparam T sealed trait type
    * @return List of Class[_] instances
    */
  def subClassesOf[T: ru.TypeTag]: List[Class[_]] = {
    val clazz: ru.ClassSymbol = ru.typeOf[T].typeSymbol.asClass
    require(clazz.isTrait && clazz.isSealed)
    clazz.knownDirectSubclasses.toList map ((s: ru.Symbol) => mirror runtimeClass s.asClass)
  }

  /**
    * The method returns value for any field utilized by the class business logic
 *
    * @param instance An instance the will be inspected
    * @param fieldName A name of the field
    * @tparam TValue A type of the returned value
    * @return A value for any field utilized by the class business logic
    */
  def getFieldValue[TValue](instance : AnyRef, fieldName : String): TValue =
  {
    val cls = instance.getClass
    val field = cls.getDeclaredField(fieldName)
    val defaultAccessibility = field.isAccessible
    field.setAccessible(true)
    val value = field.get(instance).asInstanceOf[TValue]
    field.setAccessible(defaultAccessibility)
    value
  }
}
