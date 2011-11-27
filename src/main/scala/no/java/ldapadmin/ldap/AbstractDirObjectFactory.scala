package no.java.ldapadmin.ldap

import javax.naming.Context
import javax.naming.Name
import javax.naming.directory.Attribute
import javax.naming.directory.Attributes
import javax.naming.spi.DirObjectFactory
import java.util.HashSet
import java.util.Hashtable
import java.util.Set
import no.java.core.ldap.AttributeException


abstract class AbstractDirObjectFactory extends DirObjectFactory {
  protected def convert: AnyRef

  protected def addRequiredObjectClass(objectClass: String): Unit = {
    requiredObjectClasses.add(objectClass)
  }

  protected def addRequredAttribute(attribute: String): Unit = {
    requiredAttributes.add(attribute)
  }

  protected def addRequredAttribute(attributes: Array[String]): Unit = {
    attributes.foreach(attr => requiredAttributes.add(attr))
  }

  def getObject: AnyRef = {
    return `object`
  }

  def getName: Name = {
    return name
  }

  def getContext: Context = {
    return context
  }

  def getEnvironment: Hashtable[_, _] = {
    return environment
  }

  def getAttributes: Attributes = {
    return attributes
  }

  def getStringAttribute(name: String): String = {
    var attribute: Attribute = attributes.get(name)
    if (attribute == null) {
      throw new AttributeException("Missing required attribute '" + name + "' on object '" + this.name + "'.")
    }
    var o: AnyRef = attribute.get(0)
    if (!(o.isInstanceOf[String])) {
      throw new AttributeException("Attribute '" + name + "' on object '" + this.name + "' is not a string.")
    }
    return o.asInstanceOf[String]
  }

  def getStringAttribute(name: String, defaultValue: String): String = {
    var attribute: Attribute = attributes.get(name)
    if (attribute == null) {
      return defaultValue
    }
    var o: AnyRef = attribute.get(0)
    if (!(o.isInstanceOf[String])) {
      throw new AttributeException("Attribute '" + name + "' on object '" + this.name + "' is not a string.")
    }
    return o.asInstanceOf[String]
  }

  protected def setIgnoreAttributeExeptionDuringConvertion(ignoreAttributeExeptionDuringConvertion: Boolean): Unit = {
    this.ignoreAttributeExeptionDuringConvertion = ignoreAttributeExeptionDuringConvertion
  }

  def setWarnOfUnconvertableObjects(warnOfUnconvertableObjects: Boolean): Unit = {
    this.warnOfUnconvertableObjects = warnOfUnconvertableObjects
  }

  def getObjectInstance(`object` : AnyRef, name: Name, context: Context, environment: Hashtable[_, _]): AnyRef = {
    return null
  }

  def getObjectInstance(`object` : AnyRef, name: Name, context: Context, environment: Hashtable[_, _], attributes: Attributes): AnyRef = {
    this.`object` = `object`
    this.name = name
    this.context = context
    this.environment = environment
    this.attributes = attributes
    try {
      var o: AnyRef = convert(attributes)
      if (o == null) {
        if (warnOfUnconvertableObjects) {
          System.err.println("Converter '" + getClass.getName + "' could not convert " + name.toString)
        }
        return null
      }
      else {
        return o
      }
    }
    catch {
      case e: AttributeException => {
        if (ignoreAttributeExeptionDuringConvertion) {
          e.printStackTrace
          return null
        }
        throw e
      }
    }
  }

  private def convert(attributes: Attributes): AnyRef = {
    if (requiredObjectClasses.size > 0) {
      if (attributes == null) {
        return null
      }
      var objectClass: Attribute = attributes.get("objectClass")
      if (objectClass == null) {
        return null
      }
      import scala.collection.JavaConversions._
      for (requiredObjectClass <- requiredObjectClasses) {
        if (!objectClass.contains(requiredObjectClass)) {
          return null
        }
      }
    }
    if (requiredAttributes.size > 0) {
      if (attributes == null) {
        return null
      }
      import scala.collection.JavaConversions._
      for (requiredAttribute <- requiredAttributes) {
        if (attributes.get(requiredAttribute) == null) {
          return null
        }
      }
    }
    return convert
  }

  private var `object` : AnyRef = null
  private var name: Name = null
  private var context: Context = null
  private var environment: Hashtable[_, _] = null
  private var attributes: Attributes = null
  private var requiredObjectClasses: Set[String] = new HashSet[String]
  private var requiredAttributes: Set[String] = new HashSet[String]
  private var ignoreAttributeExeptionDuringConvertion: Boolean = false
  private var warnOfUnconvertableObjects: Boolean = false
}