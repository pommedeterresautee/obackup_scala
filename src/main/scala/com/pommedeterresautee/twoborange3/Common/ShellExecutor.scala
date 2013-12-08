package com.pommedeterresautee.twoborange3.Common

import rx.lang.scala.{Subscription, Observer, Observable}
import java.io.DataOutputStream
import scala.Predef.String
import scala.io.Source


object ShellExecutor {
  private final val CHAR_OUT_OF_UNICODE: Int = 65535
  private final val CHAR_BACKSPACE: Int = 8
  private final val CHAR_NEW_LINE: Int = 10

  /**
   * Execute some code in the shell.
   *
   * @param commands to be executed. Provided as a list of String
   * @param useRoot if true, commands are sent to Su, otherwise to the Shell
   * @return an Observable for an Async Execution
   */
  def apply(commands:Traversable[String], useRoot: Boolean = false) = Observable{
    (observer: Observer[NewLine]) => {
      val mCommandResult = StringBuilder.newBuilder
      var mPreviousBackSpace = false

      val builder: ProcessBuilder = new ProcessBuilder(if (useRoot) "su" else "sh")
      builder.redirectErrorStream(true)
      val process = builder.start
      val os = new DataOutputStream(process.getOutputStream)

      val input = Source.fromInputStream(process.getInputStream)
      commands.foreach{command => os.writeBytes(command + "\n"); os.flush()}

      Iterator.continually(input.next())
        .takeWhile(c => c != -1 || c != CHAR_OUT_OF_UNICODE)
        .foreach {
        case CHAR_BACKSPACE =>
          if(!mPreviousBackSpace) observer.onNext(NewLine(mCommandResult.toString(), add = false))
          mPreviousBackSpace = true
          mCommandResult.setLength(mCommandResult.length - 1)
        case CHAR_NEW_LINE =>
          observer.onNext(NewLine(mCommandResult.toString(), add = true))
          mCommandResult.clear()
          mPreviousBackSpace = true
        case c => mCommandResult += c.toChar
          mPreviousBackSpace = false
      }

    val code = process.waitFor
    if (code != 0) observer.onError(new Exception("Execution error code:" + code))

    os.close()
    input.close()
    process.destroy()
    observer.onCompleted()
    Subscription()
    }
  }
}

case class NewLine(line:String, add:Boolean)
