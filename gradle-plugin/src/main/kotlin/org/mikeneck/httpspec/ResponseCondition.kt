package org.mikeneck.httpspec

interface ResponseCondition {

  fun setStatus(status: Int)
  fun setBodyContains(fragment: String)
  fun setRetryStrategy(retryStrategy: RequestRetryStrategy)
  fun setRetryStrategy(intervals: Iterable<Long>) {
    setRetryStrategy(RequestRetryStrategy.fromIterable(intervals))
  }
}
