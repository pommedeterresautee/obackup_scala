package com.pommedeterresautee.twoborange3.Common

import rx.lang.scala.Observable
import rx.lang.scala.concurrency.Schedulers
import rx.lang.scala.ImplicitFunctionConversions._
import rx.android.concurrency.AndroidSchedulers

/**
 * Do all the registration / observation stuff:<br>
 *
 * - observe on main thread<br>
 * - subscribe on new thread<br>
 * - make it compliant with Scala notification<br>
 * 
 * @param o the observable to transform
 * @tparam T the type of the observable
 */
class RxThread[T](o:Observable[T]) {

  def execAsync[T] = {
    o.subscribeOn(Schedulers.newThread)
      .observeOn(AndroidSchedulers.mainThread())
      .materialize
  }
}

/**
 * Convert implicitly in the new format as required by the pimp my library pattern.
 */
object RxThread {
  implicit def Observable2Notification[T](o: Observable[T]) = new RxThread(o)
}
