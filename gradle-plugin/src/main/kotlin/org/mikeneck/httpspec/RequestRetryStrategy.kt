package org.mikeneck.httpspec

fun interface RequestRetryStrategy {
  fun retryInterval(): Iterator<Long>

  companion object {
    fun fromIterable(iterable: Iterable<Long>): RequestRetryStrategy {
      return RequestRetryStrategy { iterable.iterator() }
    }

    fun of(vararg intervals: Long): RequestRetryStrategy {
      val list: MutableList<Long> = mutableListOf()
      for (interval in intervals) {
        list.add(interval)
      }
      return fromIterable(list.toList())
    }
  }
}
