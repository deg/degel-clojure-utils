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


(defproject degel/degel-clojure-utils "0.1.21"
  :description "A collection of Clojure utilities and extensions that I have found useful."
  :url "https://github.com/deg/degel-clojure-utils"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo}

  :dependencies [;; Clojure itself
                 [org.clojure/clojure "1.5.1"]

                 ;; Dev utilities to trace functions.
                 [org.clojure/tools.trace "0.7.5"]

                 ;; Random number generator for both Clojure and ClojureScript
                 [com.cemerick/pprng "0.0.2"
                  ;; [TODO] pprng uses clojure 1.6.0-alpha. We should move up too, but not yet,
                  ;;        while we fix other problems.
                  :exclusions [org.clojure/clojure]]

                 ;; Unit testing
                 ;; [TODO] This version of midje has transitive dependencies on both
                 ;; joda-time 2.0 and 2.2. Kludge here for now; upgrade when fixed.
                 [midje "1.6.0" :exclusions [joda-time]]
                 [joda-time "2.2"]

                 ;; Utility to get our version at runtime
                 [trptcolin/versioneer "0.1.0"]

                 ;; REPL interface for Emacs.
                 [org.clojure/tools.nrepl "0.2.3"]

                 ;; Ring/Compojure RPC ([TODO] Are these both needed?)
                 [shoreleave/shoreleave-remote-ring "0.3.0"
                  ;; [TODO] shoreleave-remote-ring uses org.clojure/tools.reader 0.7.0, conflicting
                  ;; with later versions used, e.g. by pprng (which uses 0.7.10)
                  :exclusions [org.clojure/tools.reader]]
                 [shoreleave/shoreleave-remote "0.3.0"]]

  :min-lein-version "2.0.0"
  ;:pedantic? :abort

  :main degel.cljutil.main

  :profiles {:dev {:plugins [[lein-midje "2.0.4"]
                             [lein-marginalia "0.7.1"]]}}

  :plugins [[lein-pprint "1.1.1"]])
