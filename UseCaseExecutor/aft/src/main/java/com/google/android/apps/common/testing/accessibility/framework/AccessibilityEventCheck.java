/*
 * Copyright (C) 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.android.apps.common.testing.accessibility.framework;

import android.view.accessibility.AccessibilityEvent;

import java.util.Collections;
import java.util.List;

/**
 * An {@code AccessibilityEventCheck} is used as a mechanism for detecting accessibility issues
 * based on the {@link AccessibilityEvent}s dispatched by an application or UI component.
 * <p>
 * Extending classes should use {@link #shouldHandleEvent(AccessibilityEvent)} as a convenience
 * mechanism for filtering events which they are interested in evaluating. If {@code true} is
 * returned, the event will be passed to {@link #runCheckOnEvent(AccessibilityEvent)} for
 * evaluation. Much like other {@link AccessibilityCheck}s, results are returned through a single
 * object, in this case {@link AccessibilityEventCheckResult}, which may include the culprit
 * {@link AccessibilityEvent}.
 * <p>
 * {@code AccessibilityEventCheck}s are different from other {@link AccessibilityCheck}s in that
 * they may maintain state internal to the class. This is because {@code AccessibilityEventCheck}s
 * operate on a stream of {@link AccessibilityEvent}s, and ordering, timing, or comparison of
 * multiple events may be required to properly evaluate an accessibility issue. The class defines
 * several callback methods to simplify managing this state. The component responsible for executing
 * this check must invoke {@link #onExecutionStarted()} when a new logical "test run" begins. It
 * must also invoke {@link #onExecutionEnded()} as it ends. Extending classes may wish to clean up
 * any state and return any final results from this method.
 * <p>
 * NOTE: Although extending classes can access all AccessibilityEvents fired by the system during
 * the test run interval, no guarantees about stability of an underlying UI are made. Information
 * about the current view hierarchy state may be accessed via
 * {@link AccessibilityEvent#getSource()}, but implementations must take care when determining when
 * and how to maintain state related to this information across invocations of
 * {@link #runCheckOnEvent(AccessibilityEvent)}. A general recommendation is to only store
 * information related to the {@link AccessibilityEvent} stream as part of the state maintained by
 * an extension of this class. To write an {@link AccessibilityCheck} that verifies some properties
 * of a view hierarchy, use an {@link AccessibilityHierarchyCheck}.
 */
public abstract class AccessibilityEventCheck extends AccessibilityCheck {

  /**
   * Convenience method for easily filtering the {@link AccessibilityEvent}s to be dispatched to
   * {@link #runCheckOnEvent(AccessibilityEvent)} for evaluation.
   * <p>
   * NOTE: The default implementation accepts all incoming events.
   *
   * @param event The event to filter
   * @return {@code true} if this {@code AccessibilityEventCheck} should handle this event,
   *         {@code false} otherwise.
   */
  protected boolean shouldHandleEvent(AccessibilityEvent event) {
    return true;
  }

  /**
   * Invoked when a new logical test run is beginning. Implementing checks should use this method to
   * initialize resources or state needed for evaluation, if needed.
   * {@link #shouldHandleEvent(AccessibilityEvent)} and {@link #runCheckOnEvent(AccessibilityEvent)}
   * are guaranteed to not be invoked until execution of this method terminates.
   */
  public void onExecutionStarted() {}

  /**
   * Invoked by the component responsible for executing this {@code AccessibilityCheck} to dispatch
   * an {@link AccessibilityEvent} to this check's logic.
   *
   * @param event The event to dispatch
   * @return A {@link List} of {@link AccessibilityEventCheckResult}s generated by the check. If no
   *         such results are generated, an empty collection will be returned.
   */
  public final List<AccessibilityEventCheckResult> dispatchEvent(AccessibilityEvent event) {
    if (shouldHandleEvent(event)) {
      return runCheckOnEvent(event);
    }
    return Collections.emptyList();
  }

  /**
   * Mechanism by which {@link AccessibilityEvent}s are delivered for evaluation. Extending classes
   * should override this method and return {@link AccessibilityEventCheckResult}s to indicate
   * results, if appropriate.
   *
   * @param event The event to evaluate.
   * @return A List of {@link AccessibilityEventCheckResult}s indicating an accessibility issue, if
   *         any. If no issues are found, or if an issue cannot be identified from the stream of
   *         {@link AccessibilityEvent}s observed, an empty collection will be returned.
   */
  protected abstract List<AccessibilityEventCheckResult> runCheckOnEvent(AccessibilityEvent event);

  /**
   * Invoked when a logical test run has concluded. Implementing checks should use this to clear any
   * state relevant to the previous evaluation, if needed. It is guaranteed that
   * {@link #shouldHandleEvent(AccessibilityEvent)} or {@link #runCheckOnEvent(AccessibilityEvent)}
   * will not be invoked after execution of this method has begun until a new logical test run is
   * signaled by {@link #onExecutionStarted()};
   *
   * @return A List of {@link AccessibilityEventCheckResult}s indicating accessibility issues, if
   *         any. If no issues are found, or if an issue cannot be identified from the stream of
   *         {@link AccessibilityEvent}s observed, an empty collection will be returned.
   */
  public List<AccessibilityEventCheckResult> onExecutionEnded() {
    return Collections.emptyList();
  }
}
