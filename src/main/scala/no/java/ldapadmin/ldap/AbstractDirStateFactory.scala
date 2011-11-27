package no.java.ldapadmin.ldap

import javax.naming.Context
import javax.naming.Name
import javax.naming.directory.Attribute
import javax.naming.directory.Attributes
import javax.naming.directory.BasicAttributes
import javax.naming.directory.BasicAttribute
import javax.naming.spi.DirStateFactory
import java.util.ArrayList
import java.util.Hashtable
import java.util.List

object AbstractDirStateFactory {
  val OBJECT_CLASS = "objectClass"
}

abstract class AbstractDirStateFactory[T] extends DirStateFactory {
  protected def this(requiredType: Class[T]) {
    this ()
    this.requiredType = requiredType
  }

  protected def convert(t: T, attributes: Attributes): Attributes

  protected def addObjectClass(objectClass: String) {
    objectClasses.add(objectClass)
  }

  protected def putIfSet(attribute: String, string: String) {
    if (string == null) {
      return
    }
    attributes.put(attribute, string)
  }

  protected def putIfNotEmpty(attribute: String, string: String) {
    if (string == null || string.trim.length == 0) {
      return
    }
    attributes.put(attribute, string)
  }

  def getStateToBind(o: AnyRef, name: Name, context: Context, hashtable: Hashtable[_, _]): AnyRef = {
    return null
  }

  def getStateToBind(o: AnyRef, name: Name, context: Context, hashtable: Hashtable[_, _], a: Attributes): DirStateFactory.Result = {
    if (o == null) {
      return null
    }
    if (requiredType != null) {
      if (!requiredType.isAssignableFrom(o.getClass)) {
        return null
      }
    }
    if (a != null) {
//      attributes = a.clone.asInstanceOf[Attributes]
      attributes = a
    }
    else {
      attributes = new BasicAttributes
    }
    var objectClass: Attribute = attributes.get(AbstractDirStateFactory.OBJECT_CLASS)
    if (objectClass != null) {
      import scala.collection.JavaConversions._
      for (oc <- objectClasses) {
        if (!objectClass.contains(oc)) {
          objectClass.add(oc)
        }
      }
    }
    else {
      objectClass = new BasicAttribute(AbstractDirStateFactory.OBJECT_CLASS)
      import scala.collection.JavaConversions._
      for (oc <- objectClasses) {
        objectClass.add(oc)
      }
      attributes.put(objectClass)
    }
    attributes = convert(o.asInstanceOf[T], attributes)
    if (attributes == null) {
      return null
    }
    return new DirStateFactory.Result(null, attributes)
  }

  private var requiredType: Class[_] = null
  private var objectClasses: List[String] = new ArrayList[String]
  private var attributes: Attributes = null
}

