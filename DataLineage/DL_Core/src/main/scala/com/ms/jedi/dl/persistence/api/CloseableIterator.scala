/**
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

package com.ms.jedi.dl.persistence.api

import java.io.Closeable

/**
 * A wrapper for Iterable that can be closed by the client when not needed any more.
 *
 * @param iterator      an iterator of type T
 * @param closeFunction close callback
 * @tparam T item type
 */
class CloseableIterable[T](val iterator: Iterator[T], closeFunction: => Unit) extends Closeable {
  override def close(): Unit = closeFunction

  def ++(otherIterable: CloseableIterable[T]): CloseableIterable[T] =
    new CloseableIterable[T](
      iterator = this.iterator ++ otherIterable.iterator,
      try this.close()
      finally otherIterable.close())

  def map[U](fn: T => U): CloseableIterable[U] =
    new CloseableIterable[U](iterator.map(fn), closeFunction)

  def flatMap[U](fn: T => Iterable[U]): CloseableIterable[U] =
    new CloseableIterable[U](iterator.flatMap(fn), closeFunction)

  def filter(p: T => Boolean): CloseableIterable[T] =
    new CloseableIterable[T](iterator.filter(p), closeFunction)
}

object CloseableIterable {
  def empty[T]: CloseableIterable[T] = new CloseableIterable[T](Iterator.empty, () => {})

  def chain[T](iters: CloseableIterable[T]*): CloseableIterable[T] = {
    (empty[T] /: iters) {
      case (accIter, nextIter) => accIter ++ nextIter
    }
  }
}