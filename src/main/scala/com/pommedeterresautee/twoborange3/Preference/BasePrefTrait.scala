package com.pommedeterresautee.twoborange3.Preference

import android.content.{SharedPreferences, Context}
import android.preference.PreferenceManager
import scala.Boolean
import scala.Long

/**
 * Manage the application preferences
 */
trait BasePrefTrait {

  private var mContext: Context = _
  private var pref: SharedPreferences = _

  def register(context: Context) {
    mContext = context.getApplicationContext
    require(mContext != null, "Context registration failed.")
    pref = PreferenceManager.getDefaultSharedPreferences(mContext)
    require(pref!= null, classOf[SharedPreferences].getSimpleName + " is null.")
  }

  protected def getBoolean(keyID: Int): Option[Boolean] = {
    get(keyID, classOf[Boolean]).asInstanceOf[Option[Boolean]]
  }

  protected def getLong(keyID: Int): Option[Long] = {
    get(keyID, classOf[Long]).asInstanceOf[Option[Long]]
  }

  protected def getInteger(keyID: Int): Option[Int] = {
    get(keyID, classOf[Int]).asInstanceOf[Option[Int]]
  }
  protected def getFloat(keyID: Int): Option[Float] = {
    get(keyID, classOf[Float]).asInstanceOf[Option[Float]]
  }

  protected def getString(keyID: Int): Option[String] = {
    get(keyID, classOf[String]).asInstanceOf[Option[String]]
  }

  private def get[T](keyID: Int, returnType: T): Option[T] = {
    require(mContext != null, "Context is not registered.")
    require(pref != null, classOf[SharedPreferences].getSimpleName + " is null.")
    val key: String = mContext.getString(keyID)
    require(key != null && key.size > 0, "Preference ID key doesn't exist: " + keyID)
    val result = returnType match {
      case t if t == classOf[String] => pref.getString(key, "").asInstanceOf[T]
      case t if t == classOf[Int] => pref.getInt(key, -1).asInstanceOf[T]
      case t if t == classOf[Long] => pref.getLong(key, -1).asInstanceOf[T]
      case t if t == classOf[Boolean] => pref.getBoolean(key, false).asInstanceOf[T]
      case t if t == classOf[Float] => pref.getFloat(key, -1).asInstanceOf[T]
      case _ => throw new IllegalArgumentException("Preference return type is unknown: " + returnType.getClass.getSimpleName)
    }
    result match {
      case r if r == -1l || r == -1f || r == -1 || r == "" => None
      case _ => Option(result)
    }
  }

  /**
   * Erase all preferences.
   * Use commit instead of apply to be sure it has been applied before next operation.
   */
  protected def resetPreference() {
    pref.edit.clear.commit
  }

  /**
   * Save data related to a key in Android Pref
   *
   * @param keyID the ID of the pref as defined in the XML
   * @param value use None to delete the value attached to the provided key
   */
  protected def saveIt(keyID: Int, value: Any) {
    require(mContext != null, "Context is not registered.")
    val editor: SharedPreferences.Editor = pref.edit
    val key: String = mContext.getString(keyID)
    require(key != null && key.size > 0, "Preference ID key doesn't exist: " + keyID)
    value match {
      case v:String => editor.putString(key, v)
      case v:Long => editor.putLong(key, v)
      case v:Int => editor.putInt(key, v)
      case v:Boolean => editor.putBoolean(key, v)
      case None => editor.remove(key)
      case _ => throw new IllegalArgumentException("Argument provided in preference save method is not from a supported class: " + value.getClass.getSimpleName)
    }
    editor.commit
  }

  protected def getContext: Context = {
    mContext
  }
}
