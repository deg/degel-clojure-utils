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


(defproject degel-clojure-utils "0.1.1-SNAPSHOT"
  :description "A collection of Clojure utilities and extensions that I have found useful."
  :url "https://github.com/deg/degel-clojure-utils"
  :license {:name "MIT License"
            :url "http://www.opensource.org/licenses/mit-license.php"}

  :dependencies [;; Clojure itself
                 [org.clojure/clojure "1.4.0"]

                 ;; Dev utilities to trace functions.
                 [org.clojure/tools.trace "0.7.5"]

                 ;; Unit testing
                 [midje "1.4.0"]

                 ;; REPL interface for Emacs.
                 [org.clojure/tools.nrepl "0.2.0-RC2"]]

  :profiles {:dev {:plugins [[lein-midje "2.0.4"]
                             [lein-marginalia "0.7.1"]]}}

  :plugins [[lein-pprint "1.1.1"]
            [lein-pedantic "0.0.5"]])
