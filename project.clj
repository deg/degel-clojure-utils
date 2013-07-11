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


(defproject degel-clojure-utils "0.1.4"
  :description "A collection of Clojure utilities and extensions that I have found useful."
  :url "https://github.com/deg/degel-clojure-utils"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo}

  :repositories {"clojars" {:url "https://clojars.org/degel-clojure-utils"
                            :username "deg" :password :gpg}}

  :dependencies [;; Clojure itself
                 [org.clojure/clojure "1.5.1"]

                 ;; Dev utilities to trace functions.
                 [org.clojure/tools.trace "0.7.5"]

                 ;; Unit testing
                 [midje "1.4.0"]

                 ;; REPL interface for Emacs.
                 [org.clojure/tools.nrepl "0.2.3"]

                 ;; For CLJS REPL
                 [com.cemerick/piggieback "0.0.4"]
                 ]

  :profiles {:dev {:plugins [[lein-midje "2.0.4"]
                             [lein-marginalia "0.7.1"]]}}

  :plugins [[lein-pprint "1.1.1"]
            ;[lein-pedantic "0.0.5"]
            ])
