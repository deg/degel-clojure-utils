;;; Copyright (c) 2012-2013 David Goldfarb. All rights reserved.
;;; Contact info: deg@degel.com
;;;
;;; The use and distribution terms for this software are covered by the Eclipse
;;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php) which can
;;; be found in the file epl-v10.html at the root of this distribution.
;;; By using this software in any fashion, you are agreeing to be bound by the
;;; terms of this license.
;;;
;;; You must not remove this notice, or any other, from this software.


(ns degel.cljutil.utils
  (:require [cemerick.pprng :as rng]))


(defn third
  "Third element of a sequence"
  [x]
  (-> x next next first))


(defn fourth
  "Fourth element of a sequence"
  [x]
  (-> x next next next first))


(defn fifth
  "Fifth element of a sequence"
  [x]
  (-> x next next next next first))


(defn take-until
  "Returns a lazy sequence of successive items from coll until and including
   when (pred item) returns true. pred must be free of side-effects."
  [pred coll]
  (lazy-seq
    (when-let [s (seq coll)]
      (if (pred (first s))
        (list (first s))
        (cons (first s) (take-until pred (rest s)))))))


(defn match-all
  "True if each target matches the corresponding pattern. Caution: this is not symmetric with match-any."
  [patterns targets]
  (every? identity (map re-matches patterns targets)))


(defn match-any
  "True if any target matches the pattern. Caution: this is not symmetric with match-all."
  [pattern targets]
  (if (some #(re-matches pattern %) targets)
    true false))


(defn take-n-or-all
  "Take the first n items, or all if n is nil."
  [n-or-nil coll]
  ((if n-or-nil (partial take n-or-nil) identity) coll))


(defn heads
  "Return the partial heads of a sequence.

   ex: `(heads (range 3))`

  -> `([] [0] [0 1] [0 1 2])`"
  [l]
  (reductions conj [] l))


(defn group-results
  "Group a set of results by clustering and counting adjacent results with matching key.
  That is (group-results identity [:a :a :a :b :b :a :a :a :b :b :b :b]) =>
          ([:a 3] [:b 2] [:a 3] [:b 4])"
  [keyfn results]
  (map (juxt first count)
       (partition-by identity
                     (map keyfn results))))


(defn group-values-by-keys
  "Organize a sequence of items, each of which contains a key and a value.
   All the values with a common key are grouped together into a sequence that is
   the value of that key in the returned map.

   Perhaps best explained by an example:

   ex: `(group-values-by-keys [[:o 1] [:e 2] [:o 3] [:e 4] [:o 5] [:e 6]])`

   -> ` {:e (2 4 6), :o (1 3 5)}`"
  [l f-key f-val]
  (into {} (map (juxt (comp f-key first) #(map f-val %))
                (partition-by f-key (sort-by f-key l)))))


(defn first-difference
  "Return a sequence of differences of the sequence elements."
  [seq]
  (map (fn [[a b]] (- b a))
       (partition 2 1 seq)))


(declare weighted-rand-nth-helper repeatable-rand)
(defn weighted-rand-nth
  "Like rand-nth, but gives a weighted probability to each item in the sequence. The weight of each
   is determined by calling weight-fn on the item."
  [coll weight-fn]
  ;; This code is optimized for a 10% speed gain at the expense of some
  ;; readability. Unfortunately, this seems to have a negligible effect on the
  ;; total runtime of codachrom. So, I'm not at all certain it is worth
  ;; keeping this optimization.
  ;; Here is the original code (also, see below):
  ;; (weighted-rand-nth-helper coll weight-fn (rand (reduce + (map weight-fn coll)))))
  ;; Note that the profiler shows this function to be a hot spot in codachrom,
  ;; but it's lying. Actually, I think that the time is coming from reduce
  ;; forcing the realization of lazy sequences.
  (weighted-rand-nth-helper coll weight-fn
    (repeatable-rand (reduce (fn [^double a b] (+ a ^double (weight-fn b))) 0.0 coll))))


(def ^:dynamic ;;^java.util.Random  [TODO] (Does pprng already hint appropriately?)
  *random-object*  (rng/rng))

(defn with-random-seed
  "Apply fcn in a dynamic context where repeatable-rand starts with a fixed seed."
  [[seed] fcn]
  (binding [*random-object* (if seed
                              (rng/rng seed)
                              (rng/rng))]
    ;; The first number returned by java.util.Random seems to be confined to
    ;; a very narrow range, almost independent of the seed. So, throw one away.
    (rng/double *random-object*)
    (fcn)))


(defn repeatable-rand
  "Variant of rand that can be reproducibly seeded."
 [n]
 (* n (rng/double *random-object*)))

(defn repeatable-rand-int
  "Variant of rand-int that can be reproducibly seeded."
 [n]
 (mod (rng/int *random-object*) n))

(defn- weighted-rand-nth-helper
  "Helper function, pulled out to ease testing. This way we can test the logic here,
   without rand getting in the way."
  ;; Second line of optimization (see above). Original: [coll weight-fn rand-val]
  [coll weight-fn ^double rand-val]
  (loop [rand-ptr rand-val
         [item & rest] (seq coll)]
    (let [item-weight (weight-fn item)]
      (if (>= item-weight rand-ptr)
        item
        (recur (- rand-ptr item-weight) rest)))))


(defn tee
  "Apply f-effector to the value of f-body, presumably for
   side-effect, then return f-body's value. Useful in many debugging
   and output-generation scenarios."
    [f-effector f-body]
    (let [val (f-body)]
      (f-effector val)
      val))
