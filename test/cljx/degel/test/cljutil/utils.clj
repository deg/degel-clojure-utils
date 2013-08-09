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


(ns degel.test.cljutil.utils
  (:use [degel.cljutil.utils])
  (:use [clojure.test])
  (:use [midje.sweet]))


(fact "take-until: numbers"
      (take-until odd? [2 4 6 8 9 10]) => [2 4 6 8 9])
(fact "take-until: empty"
      (take-until odd? []) => [])
(fact "take-until: no-match"
      (take-until odd? [2 4 6 8 10]) => [2 4 6 8 10])

(fact "match-all: simple check"
      (match-all [#"1" #"2"] ["1" "2"]) => true)
(fact "match-all: wrong order"
      (match-all [#"1" #"2"] ["2" "1"]) => false)
;; These next two fail because I have implemented match-all directly with map. Perhaps fix someday.
(future-fact "match-all: too many patterns"
      (match-all [#"1" #"2" #"3"] ["1" "2"]) => false)
(future-fact "match-all: too many targets"
      (match-all [#"1" #"2" #"3"] ["1" "2"]) => false)

(fact "match-any: single target"
      (match-any #".*123.*" ["123"]) => true)
(fact "match-any: multiple targets"
      (match-any #".*123.*" ["abc" "xx123xx" "abc"]) => true)
(fact "match-any: no match"
      (match-any #".*123.*" ["12" "23" "321"]) => false)


(fact "heads: simple test"
      (heads (range 3)) => '([] [0] [0 1] [0 1 2]))


(fact "group-results: basic test"
      (group-results identity [:a :a :a :b :b :a :a :a :b :b :b :b])
      => [[:a 3] [:b 2] [:a 3] [:b 4]])
(fact "group-results: empty"
      (group-results identity []) => ())
(fact "group-results: one"
      (group-results identity [:a :a :a]) => [[:a 3]])
(fact "group-results: two"
      (group-results identity [:a :a :a :b :b]) => [[:a 3] [:b 2]])
(fact "group-results: square"
      (group-results #(* % %) [2 -2 3 -3 4 5 -6 6])
      => [[4 2] [9 2] [16 1] [25 1] [36 2]])


(fact "group-value-by-keys: simple"
      (group-values-by-keys [[:o 1] [:e 2] [:o 3] [:e 4] [:o 5] [:e 6]]
                            first second)
      => '{:e (2 4 6), :o (1 3 5)})
(fact "group-value-by-keys: ignoring third"
      (group-values-by-keys [[1 2 3] [4 5 6] [7 8 9] [3 2 1] [6 5 4] [9 8 7]]
					   second first)
      => '{2 (1 3), 5 (4 6), 8 (7 9)})
(fact "group-value-by-keys: complex funcs"
      (group-values-by-keys [[1 -2 3] [4 -1 6] [7 0 9] [3 1 1] [6 2 4] [9 3 7]]
                            #(* (second %) (second %))
                            (juxt first third))
      => '{0 ([7 9]), 1 ([4 6] [3 1]), 4 ([1 3] [6 4]), 9 ([9 7])})


(fact "first-difference: simple test"
      (first-difference [1 2 3 4 5]) => [1 1 1 1])
(fact "first-difference: 2nd-order"
      ((comp first-difference first-difference) [1 2 3 4 5]) => [0 0 0])


(def wrnh #'degel.cljutil.utils/weighted-rand-nth-helper)
(fact "weighted-rand-nth: simple test"
     (wrnh [[:a 1] [:b 2] [:c 1]] second 2) => [:b 2])
(fact "weighted-rand-nth: computed weight"
      (wrnh [1 2 3 4] #(* % %) 10) => 3)
(fact "weighted-rand-nth: lowest rand"
     (wrnh [[:a 1.0] [:b 2.0] [:c 1.0]] second 0.0) => [:a 1.0])
(fact "weighted-rand-nth: high rand"
     (wrnh [[:a 1.0] [:b 2.0] [:c 1.0]] second 3.999999) => [:c 1.0])
(fact "weighted-rand-nth: lowest rand"
     (wrnh [[:a 1.0] [:b 2.0] [:c 1.0]] second 0.0) => [:a 1.0])
(fact "weighted-rand-nth: high rand"
     (wrnh [[:a 1.0] [:b 2.0] [:c 1.0]] second 3.999999) => [:c 1.0])
(fact "weighted-rand-nth: too low"
     (wrnh [[:a 1.0] [:b 2.0] [:c 1.0]] second -0.00001) => [:a 1.0])
(future-fact "weighted-rand-nth: too high"
     (wrnh [[:a 1.0] [:b 2.0] [:c 1.0]] second 4.00001) => [:c 1.0])
