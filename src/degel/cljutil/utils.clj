;;; Copyright (c) 2012, David Goldfarb. All rights reserved.
;;;
;;; Permission is hereby granted, free of charge, to any person obtaining
;;; a copy of this software and associated documentation
;;; files (the "Software"), to deal in the Software without restriction,
;;; including without limitation the rights to use, copy, modify, merge,
;;; publish, distribute, sublicense, and/or sell copies of the Software,
;;; and to permit persons to whom the Software is furnished to do so,
;;; subject to the following conditions:
;;;
;;; The above copyright notice and this permission notice shall be
;;; included in all copies or substantial portions of the Software.
;;;
;;; THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
;;; EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
;;; MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
;;; NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
;;; LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
;;; OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
;;; WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.


(ns degel.cljutil.utils
  (:require [degel.cljutil.devutils :as dev]))
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

(defn group-results
  "Group a set of results by clustering and counting adjacent results with matching key.
  That is (group-results identity [:a :a :a :b :b :a :a :a :b :b :b :b]) =>
          ([:a 3] [:b 2] [:a 3] [:b 4])"
  [keyfn results]
  (map (juxt first count)
       (partition-by identity
                     (map keyfn results))))


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
  ;; This code is optimized for a 10% speed gain at the expense of some readability. Unfortunately,
  ;; this seems to have a negligible effect on the total runtime of the full program. So, I'm not
  ;; at all certain it is worth keeping this optimization.
  ;; Here is the original code (also, see below):
  ;; (weighted-rand-nth-helper coll weight-fn (rand (reduce + (map weight-fn coll)))))
  ;; Note that the profiler shows this function to be a hot spot, but it's lying. Actually, I
  ;; think that the time is coming from reduce forcing the realization of lazy sequences.
  (weighted-rand-nth-helper coll weight-fn
    (repeatable-rand (reduce (fn [^double a b] (+ a ^double (weight-fn b))) 0.0 coll))))

(def ^:dynamic *random-object*  (java.util.Random.))

(defmacro with-random-seed
  "Apply fcn in a dynamic context where repeatable-rand starts with a fixed seed."
  [[seed] & body]
  `(let [the-seed# ~seed]
     (binding [*random-object* (if the-seed#
                                 (java.util.Random. the-seed#)
                                 (java.util.Random.))]
       ;; The first number returned by java.util.Random seems to be confined to
       ;; a very narrow range, almost independent of the seed. So, throw one away.
       (.nextDouble *random-object*)
       ~@body)))


(defn repeatable-rand
  "Variant of rand that can be reproducibly seeded."
 [n]
 (* n (.nextDouble *random-object*)))

(defn- weighted-rand-nth-helper
  "Helper function, pulled out just to ease testing. This way we can test the logic here, without
rand getting in the way."
  ;; [TODO] I'm not sure this function is always safe, in the face of evil floating point rounding.
  ;; Needs some real testing, and probably also a fallback strategy.  Maybe just return the last
  ;; item if we fall off the end of the list.
  ;;
  ;; Second line of optimization. Here is the original:
  ;; [coll weight-fn rand-val]
  [coll weight-fn ^double rand-val]
  (loop [rand-ptr rand-val
         [item & rest] (seq coll)]
    (let [item-weight (weight-fn item)]
      (if (>= item-weight rand-ptr)
        item
        (recur (- rand-ptr item-weight) rest)))))


(defmacro tee [fcn & body]
  "Apply fcn to the value of body, presumably for side-effect, then return body's value. Useful in many
   debugging and output-generation scenarios."
  `(let [rslt# (do ~@body)]
     (~fcn rslt#)
     rslt#))
