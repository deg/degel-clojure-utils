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
;; These next two fail because I have implemented match-all direclty with map. Perhaps fix someday.
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